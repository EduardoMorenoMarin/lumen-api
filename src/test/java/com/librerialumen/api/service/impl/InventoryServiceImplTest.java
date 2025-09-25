package com.librerialumen.api.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.librerialumen.api.domain.enums.InventoryMovementType;
import com.librerialumen.api.domain.model.InventoryMovement;
import com.librerialumen.api.domain.model.Product;
import com.librerialumen.api.exception.BusinessException;
import com.librerialumen.api.repository.InventoryMovementRepository;
import com.librerialumen.api.repository.ProductRepository;
import com.librerialumen.api.service.AuditService;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InventoryServiceImplTest {

  @Mock
  private ProductRepository productRepository;
  @Mock
  private InventoryMovementRepository inventoryMovementRepository;
  @Mock
  private AuditService auditService;

  @InjectMocks
  private InventoryServiceImpl inventoryService;

  @Test
  void adjustStock_shouldPersistMovementAndReturnStock() {
    UUID productId = UUID.randomUUID();
    Product product = new Product();
    product.setId(productId);
    when(productRepository.findById(productId)).thenReturn(Optional.of(product));
    when(inventoryMovementRepository.save(any(InventoryMovement.class))).thenAnswer(invocation -> {
      InventoryMovement movement = invocation.getArgument(0);
      movement.setId(UUID.randomUUID());
      return movement;
    });
    when(inventoryMovementRepository.calculateCurrentStock(productId)).thenReturn(5);

    int stock = inventoryService.adjustStock(productId, 3, "restock", UUID.randomUUID());

    assertEquals(5, stock);
    ArgumentCaptor<InventoryMovement> movementCaptor = ArgumentCaptor.forClass(InventoryMovement.class);
    verify(inventoryMovementRepository).save(movementCaptor.capture());
    InventoryMovement movement = movementCaptor.getValue();
    assertEquals(Integer.valueOf(3), movement.getQuantity());
    assertEquals(InventoryMovementType.IN, movement.getMovementType());
    verify(inventoryMovementRepository).calculateCurrentStock(productId);
    verify(auditService).record(eq("Inventory"), anyString(), eq("ADJUST_STOCK"), any(), any());
  }

  @Test
  void adjustStock_shouldCreateOutMovementForNegativeDelta() {
    UUID productId = UUID.randomUUID();
    Product product = new Product();
    product.setId(productId);
    when(productRepository.findById(productId)).thenReturn(Optional.of(product));
    when(inventoryMovementRepository.save(any(InventoryMovement.class))).thenAnswer(invocation -> {
      InventoryMovement movement = invocation.getArgument(0);
      movement.setId(UUID.randomUUID());
      return movement;
      });
    when(inventoryMovementRepository.calculateCurrentStock(productId)).thenReturn(2);

    inventoryService.adjustStock(productId, -4, "sale", null);

    ArgumentCaptor<InventoryMovement> captor = ArgumentCaptor.forClass(InventoryMovement.class);
    verify(inventoryMovementRepository).save(captor.capture());
    InventoryMovement movement = captor.getValue();
    assertEquals(Integer.valueOf(4), movement.getQuantity());
    assertEquals(InventoryMovementType.OUT, movement.getMovementType());
  }

  @Test
  void adjustStock_shouldRejectZeroDelta() {
    BusinessException ex = assertThrows(BusinessException.class,
        () -> inventoryService.adjustStock(UUID.randomUUID(), 0, "noop", null));
    assertEquals("INVALID_STOCK_DELTA", ex.getCode());
  }

  @Test
  void getCurrentStock_shouldReturnZeroWhenNull() {
    UUID productId = UUID.randomUUID();
    Product product = new Product();
    when(productRepository.findById(productId)).thenReturn(Optional.of(product));
    when(inventoryMovementRepository.calculateCurrentStock(productId)).thenReturn(null);

    int stock = inventoryService.getCurrentStock(productId);

    assertEquals(0, stock);
  }
}


