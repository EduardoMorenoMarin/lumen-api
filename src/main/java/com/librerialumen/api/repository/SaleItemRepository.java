package com.librerialumen.api.repository;

import com.librerialumen.api.domain.model.SaleItem;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleItemRepository extends JpaRepository<SaleItem, UUID> {
}
