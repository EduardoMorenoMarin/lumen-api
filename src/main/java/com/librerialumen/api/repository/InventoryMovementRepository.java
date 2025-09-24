package com.librerialumen.api.repository;

import com.librerialumen.api.domain.model.InventoryMovement;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, UUID> {

  @Query("SELECT COALESCE(SUM(CASE "
      + "WHEN m.movementType = com.librerialumen.api.domain.enums.InventoryMovementType.IN THEN m.quantity "
      + "WHEN m.movementType = com.librerialumen.api.domain.enums.InventoryMovementType.OUT THEN -m.quantity "
      + "ELSE 0 END), 0) "
      + "FROM InventoryMovement m WHERE m.product.id = :productId")
  Integer calculateCurrentStock(@Param("productId") UUID productId);
}
