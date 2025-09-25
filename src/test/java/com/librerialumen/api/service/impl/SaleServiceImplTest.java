package com.librerialumen.api.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.librerialumen.api.common.CustomerTestBuilder;
import com.librerialumen.api.common.ProductTestBuilder;
import com.librerialumen.api.common.UserTestBuilder;
import com.librerialumen.api.domain.model.Customer;
import com.librerialumen.api.domain.model.Product;
import com.librerialumen.api.domain.model.Sale;
import com.librerialumen.api.domain.model.SaleItem;
import com.librerialumen.api.domain.model.User;
import com.librerialumen.api.exception.BusinessException;
import com.librerialumen.api.mapper.SaleItemMapper;
import com.librerialumen.api.mapper.SaleMapper;
import com.librerialumen.api.repository.CustomerRepository;
import com.librerialumen.api.repository.ProductRepository;
import com.librerialumen.api.repository.SaleRepository;
import com.librerialumen.api.repository.UserRepository;
import com.librerialumen.api.service.AuditService;
import com.librerialumen.api.service.InventoryService;
import com.librerialumen.api.web.dto.sale.SaleCreateDTO;
import com.librerialumen.api.web.dto.sale.SaleItemCreateDTO;
import com.librerialumen.api.web.dto.sale.SaleViewDTO;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SaleServiceImplTest {

  @Mock
  private SaleRepository saleRepository;
  @Mock
  private ProductRepository productRepository;
  @Mock
  private CustomerRepository customerRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private SaleMapper saleMapper;
  @Mock
  private SaleItemMapper saleItemMapper;
  @Mock
  private InventoryService inventoryService;
  @Mock
  private AuditService auditService;

  @InjectMocks
  private SaleServiceImpl saleService;

  @Test
  void create_shouldComputeTotalsAndLinkCashier() {
    UUID actor = UUID.randomUUID();
    Product product = ProductTestBuilder.aProduct().withPrice(BigDecimal.valueOf(20)).build();
    Customer customer = CustomerTestBuilder.aCustomer().build();
    User cashier = UserTestBuilder.aUser().withRole("EMPLOYEE").build();

    SaleItemCreateDTO itemDto = SaleItemCreateDTO.builder()
        .productId(product.getId())
        .quantity(2)
        .build();
    SaleCreateDTO dto = SaleCreateDTO.builder()
        .items(List.of(itemDto))
        .paymentMethod("CASH")
        .customerId(customer.getId())
        .build();

    Sale saleEntity = new Sale();
    when(saleMapper.toEntity(dto)).thenReturn(saleEntity);
    SaleItem saleItem = new SaleItem();
    saleItem.setQuantity(itemDto.getQuantity());
    when(saleItemMapper.toEntity(itemDto)).thenReturn(saleItem);
    when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
    when(inventoryService.getCurrentStock(product.getId())).thenReturn(5);
    when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
    when(userRepository.findById(actor)).thenReturn(Optional.of(cashier));
    when(saleRepository.save(saleEntity)).thenAnswer(invocation -> {
      Sale sale = invocation.getArgument(0);
      sale.setId(UUID.randomUUID());
      return sale;
    });
    SaleViewDTO viewDTO = new SaleViewDTO();
    when(saleMapper.toView(any(Sale.class))).thenReturn(viewDTO);

    SaleViewDTO result = saleService.create(dto, actor);

    assertSame(viewDTO, result);

    ArgumentCaptor<Sale> saleCaptor = ArgumentCaptor.forClass(Sale.class);
    verify(saleRepository).save(saleCaptor.capture());
    Sale saved = saleCaptor.getValue();
    assertEquals(0, BigDecimal.valueOf(40).compareTo(saved.getTotalAmount()));
    assertEquals("CASH", saved.getNotes());
    assertEquals(cashier, saved.getCashier());
    assertEquals(customer, saved.getCustomer());
    assertEquals(1, saved.getItems().size());
    assertEquals(saved, saved.getItems().get(0).getSale());

    verify(inventoryService).adjustStock(product.getId(), -2, "SALE:" + saved.getId(), actor);
    verify(auditService).record(eq("Sale"), anyString(), eq("CREATE"), anyString(), any());
  }

  @Test
  void create_shouldFailWhenStockInsufficient() {
    Product product = ProductTestBuilder.aProduct().withPrice(BigDecimal.TEN).build();
    SaleItemCreateDTO itemDto = SaleItemCreateDTO.builder()
        .productId(product.getId())
        .quantity(3)
        .build();
    SaleCreateDTO dto = SaleCreateDTO.builder()
        .items(List.of(itemDto))
        .paymentMethod("CARD")
        .build();

    when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
    when(inventoryService.getCurrentStock(product.getId())).thenReturn(2);

    BusinessException ex = assertThrows(BusinessException.class,
        () -> saleService.create(dto, UUID.randomUUID()));
    assertEquals("INSUFFICIENT_STOCK", ex.getCode());
    verify(saleRepository, never()).save(any());
  }

  @Test
  void create_shouldRequireAuthenticatedCashier() {
    UUID productId = UUID.randomUUID();
    Product product = ProductTestBuilder.aProduct().build();
    product.setId(productId);
    when(productRepository.findById(productId)).thenReturn(Optional.of(product));
    when(inventoryService.getCurrentStock(productId)).thenReturn(10);
    SaleItemCreateDTO itemDto = SaleItemCreateDTO.builder()
        .productId(productId)
        .quantity(1)
        .build();
    SaleCreateDTO dto = SaleCreateDTO.builder()
        .items(List.of(itemDto))
        .paymentMethod("CARD")
        .build();
    SaleItem saleItem = new SaleItem();
    saleItem.setQuantity(1);
    when(saleItemMapper.toEntity(itemDto)).thenReturn(saleItem);
    when(saleMapper.toEntity(dto)).thenReturn(new Sale());

    BusinessException ex = assertThrows(BusinessException.class,
        () -> saleService.create(dto, null));
    assertEquals("CASHIER_REQUIRED", ex.getCode());
  }
}

