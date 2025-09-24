package com.librerialumen.api.service.impl;

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
import com.librerialumen.api.service.ReservationService;
import com.librerialumen.api.service.SaleService;
import com.librerialumen.api.web.dto.reservation.ReservationCreateDTO;
import com.librerialumen.api.web.dto.reservation.ReservationCustomerDataDTO;
import com.librerialumen.api.web.dto.reservation.ReservationItemCreateDTO;
import com.librerialumen.api.web.dto.reservation.ReservationViewDTO;
import com.librerialumen.api.web.dto.sale.SaleCreateDTO;
import com.librerialumen.api.web.dto.sale.SaleItemCreateDTO;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationServiceImpl implements ReservationService {

  private final ReservationRepository reservationRepository;
  private final ProductRepository productRepository;
  private final CustomerRepository customerRepository;
  private final ReservationMapper reservationMapper;
  private final ReservationItemMapper reservationItemMapper;
  private final InventoryService inventoryService;
  private final SaleService saleService;
  private final AuditService auditService;

  @Override
  public ReservationViewDTO create(ReservationCreateDTO dto, UUID actorUserId) {
    if (dto.getItems() == null || dto.getItems().isEmpty()) {
      throw new BusinessException("RESERVATION_ITEMS_REQUIRED", "Reservation must include items");
    }

    Reservation reservation = reservationMapper.toEntity(dto);
    reservation.setReservationDate(Instant.now());
    reservation.setStatus(ReservationStatus.PENDING);
    reservation.setCode(generateCode());

    Customer customer = resolveCustomer(dto);
    reservation.setCustomer(customer);

    List<ReservationItem> items = buildReservationItems(reservation, dto.getItems());
    reservation.setItems(items);

    BigDecimal total = items.stream()
        .map(ReservationItem::getTotalPrice)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
    reservation.setTotalAmount(total);

    Reservation saved = reservationRepository.save(reservation);
    Map<String, Object> auditDetails = new HashMap<>();
    auditDetails.put("total", saved.getTotalAmount());
    auditDetails.put("status", saved.getStatus().name());
    auditDetails.put("customerId", saved.getCustomer().getId());
    auditDetails.put("customerDni", saved.getCustomer().getDni());
    auditDetails.put("source", actorUserId != null ? "INTERNAL" : "PUBLIC");
    recordAudit("Reservation", saved.getId(), "CREATE", actorUserId, auditDetails);

    return reservationMapper.toView(saved);
  }

  @Override
  public ReservationViewDTO accept(UUID reservationId, UUID actorUserId) {
    Reservation reservation = reservationRepository.findById(reservationId)
        .orElseThrow(() -> new BusinessException("RESERVATION_NOT_FOUND", "Reservation not found"));

    if (reservation.getStatus() != ReservationStatus.PENDING) {
      throw new BusinessException("RESERVATION_NOT_PENDING",
          "Only pending reservations can be accepted");
    }

    ReservationStatus previousStatus = reservation.getStatus();
    reservation.setStatus(ReservationStatus.RESERVED);

    Reservation updated = reservationRepository.save(reservation);

    Map<String, Object> auditDetails = new HashMap<>();
    auditDetails.put("previousStatus", previousStatus.name());
    auditDetails.put("newStatus", updated.getStatus().name());
    auditDetails.put("customerId", updated.getCustomer().getId());
    auditDetails.put("customerDni", updated.getCustomer().getDni());
    recordAudit("Reservation", updated.getId(), "ACCEPT", actorUserId, auditDetails);

    return reservationMapper.toView(updated);
  }

  @Override
  public ReservationViewDTO confirmPickup(UUID reservationId, UUID actorUserId, boolean createSale) {
    Reservation reservation = reservationRepository.findById(reservationId)
        .orElseThrow(() -> new BusinessException("RESERVATION_NOT_FOUND", "Reservation not found"));

    ReservationStatus previousStatus = reservation.getStatus();

    if (reservation.getStatus() == ReservationStatus.CANCELLED
        || reservation.getStatus() == ReservationStatus.COMPLETED
        || reservation.getStatus() == ReservationStatus.EXPIRED) {
      throw new BusinessException("RESERVATION_ALREADY_CLOSED", "Reservation already closed");
    }

    if (reservation.getStatus() != ReservationStatus.RESERVED) {
      throw new BusinessException("RESERVATION_NOT_RESERVED",
          "Reservation must be accepted before confirmation");
    }


    reservation.getItems().forEach(item -> {
      int available = inventoryService.getCurrentStock(item.getProduct().getId());
      if (available < item.getQuantity()) {
        throw new BusinessException("INSUFFICIENT_STOCK",
            "Not enough stock for product " + item.getProduct().getSku());
      }
    });

    if (createSale) {
      saleService.create(buildSaleFromReservation(reservation), actorUserId);
    } else {
      reservation.getItems().forEach(item ->
          inventoryService.adjustStock(item.getProduct().getId(), -item.getQuantity(),
              "RESERVATION_PICKUP", actorUserId));
    }

    reservation.setStatus(ReservationStatus.COMPLETED);
    Reservation updated = reservationRepository.save(reservation);

    Map<String, Object> auditDetails = new HashMap<>();
    auditDetails.put("createSale", createSale);
    auditDetails.put("previousStatus", previousStatus.name());
    auditDetails.put("newStatus", updated.getStatus().name());
    auditDetails.put("customerId", updated.getCustomer().getId());
    auditDetails.put("customerDni", updated.getCustomer().getDni());
    recordAudit("Reservation", updated.getId(), "CONFIRM_PICKUP", actorUserId, auditDetails);

    return reservationMapper.toView(updated);
  }

  @Override
  public ReservationViewDTO cancel(UUID reservationId, UUID actorUserId, String reason) {
    Reservation reservation = reservationRepository.findById(reservationId)
        .orElseThrow(() -> new BusinessException("RESERVATION_NOT_FOUND", "Reservation not found"));

    if (reservation.getStatus() == ReservationStatus.COMPLETED
        || reservation.getStatus() == ReservationStatus.EXPIRED) {
      throw new BusinessException("RESERVATION_ALREADY_COMPLETED", "Reservation already completed");
    }

    ReservationStatus previousStatus = reservation.getStatus();
    reservation.setStatus(ReservationStatus.CANCELLED);
    reservation.setNotes(reason);
    Reservation updated = reservationRepository.save(reservation);

    Map<String, Object> auditDetails = new HashMap<>();
    auditDetails.put("reason", reason);
    auditDetails.put("previousStatus", previousStatus.name());
    auditDetails.put("newStatus", updated.getStatus().name());
    auditDetails.put("customerId", updated.getCustomer().getId());
    auditDetails.put("customerDni", updated.getCustomer().getDni());
    recordAudit("Reservation", updated.getId(), "CANCEL", actorUserId, auditDetails);

    return reservationMapper.toView(updated);
  }

  @Override
  @Transactional(readOnly = true)
  public ReservationViewDTO get(UUID reservationId) {
    Reservation reservation = reservationRepository.findById(reservationId)
        .orElseThrow(() -> new BusinessException("RESERVATION_NOT_FOUND", "Reservation not found"));
    return reservationMapper.toView(reservation);
  }

  private Customer resolveCustomer(ReservationCreateDTO dto) {
    if (dto.getCustomerId() != null) {
      return customerRepository.findById(dto.getCustomerId())
          .orElseThrow(() -> new BusinessException("CUSTOMER_NOT_FOUND", "Customer not found"));
    }

    if (dto.getCustomerData() == null) {
      throw new BusinessException("CUSTOMER_DATA_REQUIRED",
          "Customer information is required when customerId is not provided");
    }

    ReservationCustomerDataDTO data = dto.getCustomerData();
    Customer customer = customerRepository.findByDni(data.getDni()).orElse(null);
    if (customer == null) {
      customer = new Customer();
    }

    customer.setDni(data.getDni());
    customer.setFirstName(data.getFirstName());
    customer.setLastName(data.getLastName());
    customer.setEmail(data.getEmail());
    customer.setPhone(data.getPhone());
    return customerRepository.save(customer);
  }
  private List<ReservationItem> buildReservationItems(Reservation reservation,
      List<ReservationItemCreateDTO> itemDtos) {
    List<ReservationItem> items = new ArrayList<>();
    for (ReservationItemCreateDTO itemDto : itemDtos) {
      Product product = productRepository.findById(itemDto.getProductId())
          .orElseThrow(() -> new BusinessException("PRODUCT_NOT_FOUND", "Product not found"));

      ReservationItem item = reservationItemMapper.toEntity(itemDto);
      item.setReservation(reservation);
      item.setProduct(product);
      item.setUnitPrice(product.getPrice());
      item.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity())));
      items.add(item);
    }
    return items;
  }

  private String generateCode() {
    return "RSV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
  }

  private SaleCreateDTO buildSaleFromReservation(Reservation reservation) {
    List<SaleItemCreateDTO> saleItems = new ArrayList<>();
    reservation.getItems().forEach(item -> saleItems.add(SaleItemCreateDTO.builder()
        .productId(item.getProduct().getId())
        .quantity(item.getQuantity())
        .unitPrice(item.getUnitPrice())
        .build()));

    return SaleCreateDTO.builder()
        .items(saleItems)
        .paymentMethod("RESERVATION")
        .cashierId(null)
        .customerId(reservation.getCustomer() != null ? reservation.getCustomer().getId() : null)
        .build();
  }

  private void recordAudit(String entity, UUID id, String action, UUID actor,
      Map<String, Object> details) {
    Map<String, Object> payload = new HashMap<>(details != null ? details : Map.of());
    payload.put("reservationId", id);
    auditService.record(entity, id.toString(), action,
        actor != null ? actor.toString() : null, payload);
  }
}
