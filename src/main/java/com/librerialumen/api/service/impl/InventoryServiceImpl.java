package com.librerialumen.api.service.impl;

import com.librerialumen.api.domain.enums.InventoryMovementType;
import com.librerialumen.api.domain.model.InventoryMovement;
import com.librerialumen.api.domain.model.Product;
import com.librerialumen.api.exception.BusinessException;
import com.librerialumen.api.repository.InventoryMovementRepository;
import com.librerialumen.api.repository.ProductRepository;
import com.librerialumen.api.service.AuditService;
import com.librerialumen.api.service.InventoryService;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryServiceImpl implements InventoryService {

  private final ProductRepository productRepository;
  private final InventoryMovementRepository inventoryMovementRepository;
  private final AuditService auditService;

  @Override
  public int adjustStock(UUID productId, int delta, String reason, UUID actorUserId) {
    if (delta == 0) {
      throw new BusinessException("INVALID_STOCK_DELTA", "Stock adjustment must be non-zero");
    }

    Product product = productRepository.findById(productId)
        .orElseThrow(() -> new BusinessException("PRODUCT_NOT_FOUND", "Product not found"));

    InventoryMovementType type = delta > 0 ? InventoryMovementType.IN : InventoryMovementType.OUT;

    InventoryMovement movement = new InventoryMovement();
    movement.setProduct(product);
    movement.setMovementType(type);
    movement.setQuantity(Math.abs(delta));
    movement.setMovementDate(Instant.now());
    movement.setReference(reason);
    movement.setNotes(actorUserId != null ? "actor=" + actorUserId : null);

    inventoryMovementRepository.save(movement);

    int stock = getCurrentStock(productId);
    auditService.record("Inventory", productId.toString(), "ADJUST_STOCK",
        actorUserId != null ? actorUserId.toString() : null,
        Map.of(
            "delta", delta,
            "movementId", movement.getId(),
            "stock", stock,
            "reason", reason
        ));
    return stock;
  }

  @Override
  @Transactional(readOnly = true)
  public int getCurrentStock(UUID productId) {
    productRepository.findById(productId)
        .orElseThrow(() -> new BusinessException("PRODUCT_NOT_FOUND", "Product not found"));
    Integer stock = inventoryMovementRepository.calculateCurrentStock(productId);
    return stock != null ? stock : 0;
  }
}
