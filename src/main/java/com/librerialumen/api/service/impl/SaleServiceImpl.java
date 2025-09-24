package com.librerialumen.api.service.impl;

import com.librerialumen.api.domain.enums.SaleStatus;
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
import com.librerialumen.api.service.SaleService;
import com.librerialumen.api.web.dto.sale.SaleCreateDTO;
import com.librerialumen.api.web.dto.sale.SaleItemCreateDTO;
import com.librerialumen.api.web.dto.sale.SaleViewDTO;
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
public class SaleServiceImpl implements SaleService {

  private final SaleRepository saleRepository;
  private final ProductRepository productRepository;
  private final CustomerRepository customerRepository;
  private final UserRepository userRepository;
  private final SaleMapper saleMapper;
  private final SaleItemMapper saleItemMapper;
  private final InventoryService inventoryService;
  private final AuditService auditService;

  @Override
  public SaleViewDTO create(SaleCreateDTO dto, UUID actorUserId) {
    if (dto.getItems() == null || dto.getItems().isEmpty()) {
      throw new BusinessException("SALE_ITEMS_REQUIRED", "Sale must contain at least one item");
    }

    List<SaleItem> saleItems = new ArrayList<>();
    BigDecimal total = BigDecimal.ZERO;

    for (SaleItemCreateDTO itemDto : dto.getItems()) {
      if (itemDto.getQuantity() == null || itemDto.getQuantity() <= 0) {
        throw new BusinessException("INVALID_QUANTITY", "Quantity must be greater than zero");
      }
      Product product = productRepository.findById(itemDto.getProductId())
          .orElseThrow(() -> new BusinessException("PRODUCT_NOT_FOUND", "Product not found"));

      int available = inventoryService.getCurrentStock(product.getId());
      if (available < itemDto.getQuantity()) {
        throw new BusinessException("INSUFFICIENT_STOCK",
            "Not enough stock for product " + product.getSku());
      }

      SaleItem saleItem = saleItemMapper.toEntity(itemDto);
      saleItem.setProduct(product);
      saleItem.setSale(null);
      BigDecimal unitPrice = itemDto.getUnitPrice() != null ? itemDto.getUnitPrice() : product.getPrice();
      saleItem.setUnitPrice(unitPrice);
      saleItem.setTotalPrice(unitPrice.multiply(BigDecimal.valueOf(itemDto.getQuantity())));
      total = total.add(saleItem.getTotalPrice());
      saleItems.add(saleItem);
    }

    Sale sale = saleMapper.toEntity(dto);
    sale.setStatus(SaleStatus.COMPLETED);
    sale.setSaleDate(Instant.now());
    sale.setTotalAmount(total);
    sale.setTaxAmount(BigDecimal.ZERO);
    sale.setDiscountAmount(BigDecimal.ZERO);
    sale.setNotes(dto.getPaymentMethod());

    if (dto.getCashierId() != null) {
      User cashier = userRepository.findById(dto.getCashierId())
          .orElseThrow(() -> new BusinessException("CASHIER_NOT_FOUND", "Cashier not found"));
      sale.setCashier(cashier);
    } else if (actorUserId != null) {
      userRepository.findById(actorUserId).ifPresent(sale::setCashier);
    }

    if (dto.getCustomerId() != null) {
      Customer customer = customerRepository.findById(dto.getCustomerId())
          .orElseThrow(() -> new BusinessException("CUSTOMER_NOT_FOUND", "Customer not found"));
      sale.setCustomer(customer);
    }

    sale.setItems(saleItems);
    saleItems.forEach(item -> item.setSale(sale));

    Sale saved = saleRepository.save(sale);

    saleItems.forEach(item ->
        inventoryService.adjustStock(item.getProduct().getId(), -item.getQuantity(),
            "SALE:" + saved.getId(), actorUserId));

    recordAudit(saved, actorUserId);

    return saleMapper.toView(saved);
  }

  @Override
  @Transactional(readOnly = true)
  public SaleViewDTO get(UUID saleId) {
    Sale sale = saleRepository.findById(saleId)
        .orElseThrow(() -> new BusinessException("SALE_NOT_FOUND", "Sale not found"));
    return saleMapper.toView(sale);
  }

  @Override
  @Transactional(readOnly = true)
  public List<SaleViewDTO> getDailySales(Instant start, Instant end) {
    List<Sale> sales = saleRepository.findByCreatedAtBetween(start, end);
    return saleMapper.toViewList(sales);
  }

  private void recordAudit(Sale sale, UUID actorUserId) {
    Map<String, Object> details = new HashMap<>();
    details.put("total", sale.getTotalAmount());
    details.put("items", sale.getItems().size());
    auditService.record("Sale", sale.getId().toString(), "CREATE",
        actorUserId != null ? actorUserId.toString() : null, details);
  }
}

