package com.librerialumen.api.mapper;

import com.librerialumen.api.domain.model.Reservation;
import com.librerialumen.api.web.dto.reservation.ReservationCreateDTO;
import com.librerialumen.api.web.dto.reservation.ReservationViewDTO;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    uses = {ReservationItemMapper.class},
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface ReservationMapper {

  @Mapping(target = "customer", ignore = true)
  @Mapping(target = "items", ignore = true)
  @Mapping(target = "code", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "reservationDate", ignore = true)
  @Mapping(target = "totalAmount", ignore = true)
  @Mapping(target = "notes", source = "notes")
  @Mapping(target = "expirationDate", source = "pickupDeadline")
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  Reservation toEntity(ReservationCreateDTO dto);

  @Mapping(target = "pickupDeadline", source = "expirationDate")
  @Mapping(target = "customerId", source = "customer.id")
  @Mapping(target = "customerFirstName", source = "customer.firstName")
  @Mapping(target = "customerLastName", source = "customer.lastName")
  @Mapping(target = "customerDni", source = "customer.dni")
  @Mapping(target = "customerEmail", source = "customer.email")
  @Mapping(target = "customerPhone", source = "customer.phone")
  ReservationViewDTO toView(Reservation reservation);

  List<ReservationViewDTO> toViewList(List<Reservation> reservations);
}
