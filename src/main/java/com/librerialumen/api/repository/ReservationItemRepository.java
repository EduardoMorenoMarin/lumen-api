package com.librerialumen.api.repository;

import com.librerialumen.api.domain.model.ReservationItem;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationItemRepository extends JpaRepository<ReservationItem, UUID> {
}
