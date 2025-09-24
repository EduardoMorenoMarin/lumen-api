package com.librerialumen.api.service;

import com.librerialumen.api.web.dto.reservation.ReservationCreateDTO;
import com.librerialumen.api.web.dto.reservation.ReservationViewDTO;
import java.util.UUID;

public interface ReservationService {

  ReservationViewDTO create(ReservationCreateDTO dto, UUID actorUserId);

  ReservationViewDTO accept(UUID reservationId, UUID actorUserId);

  ReservationViewDTO confirmPickup(UUID reservationId, UUID actorUserId, boolean createSale);

  ReservationViewDTO cancel(UUID reservationId, UUID actorUserId, String reason);

  ReservationViewDTO get(UUID reservationId);
}
