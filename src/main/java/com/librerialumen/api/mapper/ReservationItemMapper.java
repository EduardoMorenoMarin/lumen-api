package com.librerialumen.api.mapper;

import com.librerialumen.api.domain.model.ReservationItem;
import com.librerialumen.api.web.dto.reservation.ReservationItemCreateDTO;
import com.librerialumen.api.web.dto.reservation.ReservationItemViewDTO;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface ReservationItemMapper {

  @Mapping(target = "reservation", ignore = true)
  @Mapping(target = "product", ignore = true)
  @Mapping(target = "unitPrice", ignore = true)
  @Mapping(target = "totalPrice", ignore = true)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  ReservationItem toEntity(ReservationItemCreateDTO dto);

  List<ReservationItem> toEntityList(List<ReservationItemCreateDTO> items);

  @Mapping(target = "productId", source = "product.id")
  @Mapping(target = "productTitle", source = "product.title")
  ReservationItemViewDTO toView(ReservationItem item);

  List<ReservationItemViewDTO> toViewList(List<ReservationItem> items);
}
