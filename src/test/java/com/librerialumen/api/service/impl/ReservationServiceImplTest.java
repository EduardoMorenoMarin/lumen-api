package com.librerialumen.api.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.librerialumen.api.common.CustomerTestBuilder;
import com.librerialumen.api.common.ProductTestBuilder;
import com.librerialumen.api.common.ReservationItemTestBuilder;
import com.librerialumen.api.common.ReservationTestBuilder;
import com.librerialumen.api.domain.enums.ReservationStatus;
import com.librerialumen.api.domain.model.Customer;
import com.librerialumen.api.domain.model.Product;
import com.librerialumen.api.domain.model.Reservation;
import com.librerialumen.api.domain.model.ReservationItem;
import com.librerialumen.api.exception.BusinessException;
import com.librerialumen.api.mapper.ReservationItemMapper;
import com.librerialumen.api.mapper.ReservationMapper;
import com.librerialumen.api.repository.CustomerRepository;
import com.librerialumen.api.repository.ProductRepository;
import com.librerialumen.api.repository.ReservationRepository;
import com.librerialumen.api.service.AuditService;
import com.librerialumen.api.service.InventoryService;
import com.librerialumen.api.service.SaleService;
import com.librerialumen.api.web.dto.reservation.ReservationCreateDTO;
import com.librerialumen.api.web.dto.reservation.ReservationCustomerDataDTO;
import com.librerialumen.api.web.dto.reservation.ReservationItemCreateDTO;
import com.librerialumen.api.web.dto.reservation.ReservationViewDTO;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReservationServiceImplTest {

  @Mock
  private ReservationRepository reservationRepository;
  @Mock
  private ProductRepository productRepository;
  @Mock
  private CustomerRepository customerRepository;
  @Mock
  private ReservationMapper reservationMapper;
  @Mock
  private ReservationItemMapper reservationItemMapper;
  @Mock
  private InventoryService inventoryService;
  @Mock
  private SaleService saleService;
  @Mock
  private AuditService auditService;

  @InjectMocks
  private ReservationServiceImpl reservationService;

  private Product product;

  @BeforeEach
  void setUp() {
    product = ProductTestBuilder.aProduct().withPrice(BigDecimal.valueOf(50)).build();
  }

  @Test
  void create_shouldReuseCustomerAndSetTotals() {
    UUID productId = product.getId();
    ReservationItemCreateDTO itemDto = ReservationItemCreateDTO.builder()
        .productId(productId)
        .quantity(2)
        .build();
    ReservationCustomerDataDTO customerData = ReservationCustomerDataDTO.builder()
        .dni("12345678")
        .firstName("Ana")
        .lastName("Doe")
        .email("ana@lumen.test")
        .phone("123456789")
        .build();
    ReservationCreateDTO dto = ReservationCreateDTO.builder()
        .customerData(customerData)
        .items(List.of(itemDto))
        .pickupDeadline(Instant.now().plusSeconds(3600))
        .notes("Leave at counter")
        .build();

    Reservation mappedReservation = new Reservation();
    when(reservationMapper.toEntity(dto)).thenReturn(mappedReservation);

    Customer existingCustomer = CustomerTestBuilder.aCustomer().withDni("12345678").build();
    when(customerRepository.findByDni("12345678")).thenReturn(Optional.of(existingCustomer));
    when(customerRepository.save(existingCustomer)).thenReturn(existingCustomer);

    when(productRepository.findById(productId)).thenReturn(Optional.of(product));
    ReservationItem mappedItem = new ReservationItem();
    when(reservationItemMapper.toEntity(itemDto)).thenReturn(mappedItem);

    when(reservationRepository.save(mappedReservation)).thenAnswer(invocation -> {
      Reservation entity = invocation.getArgument(0);
      entity.setId(UUID.randomUUID());
      return entity;
    });

    ReservationViewDTO viewDTO = new ReservationViewDTO();
    when(reservationMapper.toView(any(Reservation.class))).thenReturn(viewDTO);

    ReservationViewDTO result = reservationService.create(dto, null);

    assertSame(viewDTO, result);

    ArgumentCaptor<Reservation> reservationCaptor = ArgumentCaptor.forClass(Reservation.class);
    verify(reservationRepository).save(reservationCaptor.capture());
    Reservation saved = reservationCaptor.getValue();

    assertEquals(ReservationStatus.PENDING, saved.getStatus());
    assertEquals(existingCustomer, saved.getCustomer());
    assertEquals(0, BigDecimal.valueOf(100).compareTo(saved.getTotalAmount()));
    assertEquals(1, saved.getItems().size());
    assertEquals(product, saved.getItems().get(0).getProduct());
    assertNotNull(saved.getReservationDate());
    verify(auditService).record(eq("Reservation"), anyString(), eq("CREATE"), any(), any());
  }

  @Test
  void accept_shouldTransitionPendingToReserved() {
    Reservation reservation = ReservationTestBuilder.aReservation()
        .withStatus(ReservationStatus.PENDING)
        .build();
    UUID reservationId = reservation.getId();

    when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
    when(reservationRepository.save(reservation)).thenReturn(reservation);
    ReservationViewDTO view = new ReservationViewDTO();
    when(reservationMapper.toView(reservation)).thenReturn(view);

    ReservationViewDTO response = reservationService.accept(reservationId, UUID.randomUUID());

    assertSame(view, response);
    assertEquals(ReservationStatus.RESERVED, reservation.getStatus());
    verify(auditService).record(eq("Reservation"), anyString(), eq("ACCEPT"), any(), any());
  }

  @Test
  void accept_shouldFailWhenNotPending() {
    Reservation reservation = ReservationTestBuilder.aReservation()
        .withStatus(ReservationStatus.RESERVED)
        .build();
    when(reservationRepository.findById(reservation.getId())).thenReturn(Optional.of(reservation));

    BusinessException ex = assertThrows(BusinessException.class,
        () -> reservationService.accept(reservation.getId(), UUID.randomUUID()));
    assertEquals("RESERVATION_NOT_PENDING", ex.getCode());
    verify(reservationRepository, never()).save(any());
  }

  @Test
  void confirmPickup_withSale_ShouldCompleteWithoutAdjustingStock() {
    ReservationItem item = ReservationItemTestBuilder.aReservationItem()
        .withQuantity(1)
        .withProduct(product)
        .build();
    Reservation reservation = ReservationTestBuilder.aReservation()
        .withStatus(ReservationStatus.RESERVED)
        .withItem(item)
        .build();

    when(reservationRepository.findById(reservation.getId())).thenReturn(Optional.of(reservation));
    when(inventoryService.getCurrentStock(product.getId())).thenReturn(5);
    when(reservationRepository.save(reservation)).thenReturn(reservation);
    ReservationViewDTO view = new ReservationViewDTO();
    when(reservationMapper.toView(reservation)).thenReturn(view);

    ReservationViewDTO response = reservationService.confirmPickup(reservation.getId(), UUID.randomUUID(), true);

    assertSame(view, response);
    assertEquals(ReservationStatus.COMPLETED, reservation.getStatus());
    verify(saleService).create(any(), any());
    verify(inventoryService, never()).adjustStock(any(), anyInt(), anyString(), any());
    verify(auditService).record(eq("Reservation"), anyString(), eq("CONFIRM_PICKUP"), any(), any());
  }

  @Test
  void confirmPickup_withoutSale_ShouldAdjustStock() {
    ReservationItem item = ReservationItemTestBuilder.aReservationItem()
        .withQuantity(3)
        .withProduct(product)
        .build();
    Reservation reservation = ReservationTestBuilder.aReservation()
        .withStatus(ReservationStatus.RESERVED)
        .withItem(item)
        .build();
    UUID actor = UUID.randomUUID();

    when(reservationRepository.findById(reservation.getId())).thenReturn(Optional.of(reservation));
    when(inventoryService.getCurrentStock(product.getId())).thenReturn(10);
    when(reservationRepository.save(reservation)).thenReturn(reservation);
    when(reservationMapper.toView(reservation)).thenReturn(new ReservationViewDTO());

    reservationService.confirmPickup(reservation.getId(), actor, false);

    verify(inventoryService).adjustStock(product.getId(), -item.getQuantity(), "RESERVATION_PICKUP", actor);
    assertEquals(ReservationStatus.COMPLETED, reservation.getStatus());
    verify(auditService).record(eq("Reservation"), anyString(), eq("CONFIRM_PICKUP"), any(), any());
  }

  @Test
  void confirmPickup_shouldFailWhenStockMissing() {
    ReservationItem item = ReservationItemTestBuilder.aReservationItem()
        .withQuantity(2)
        .withProduct(product)
        .build();
    Reservation reservation = ReservationTestBuilder.aReservation()
        .withStatus(ReservationStatus.RESERVED)
        .withItem(item)
        .build();

    when(reservationRepository.findById(reservation.getId())).thenReturn(Optional.of(reservation));
    when(inventoryService.getCurrentStock(product.getId())).thenReturn(1);

    BusinessException ex = assertThrows(BusinessException.class,
        () -> reservationService.confirmPickup(reservation.getId(), UUID.randomUUID(), false));
    assertEquals("INSUFFICIENT_STOCK", ex.getCode());
    verify(reservationRepository, never()).save(any());
    verify(saleService, never()).create(any(), any());
  }

  @Test
  void cancel_shouldRejectCompletedOrExpired() {
    for (ReservationStatus status : List.of(ReservationStatus.COMPLETED, ReservationStatus.EXPIRED)) {
      Reservation reservation = ReservationTestBuilder.aReservation()
          .withStatus(status)
          .build();
      when(reservationRepository.findById(reservation.getId())).thenReturn(Optional.of(reservation));

      BusinessException ex = assertThrows(BusinessException.class,
          () -> reservationService.cancel(reservation.getId(), UUID.randomUUID(), "no"));
      assertEquals("RESERVATION_ALREADY_COMPLETED", ex.getCode());
    }
  }
}



