package com.librerialumen.api.mapper;

import com.librerialumen.api.domain.model.SaleItem;
import com.librerialumen.api.web.dto.sale.SaleItemCreateDTO;
import com.librerialumen.api.web.dto.sale.SaleItemViewDTO;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface SaleItemMapper {

  @Mapping(target = "sale", ignore = true)
  @Mapping(target = "product", ignore = true)
  @Mapping(target = "totalPrice", ignore = true)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  SaleItem toEntity(SaleItemCreateDTO dto);

  List<SaleItem> toEntityList(List<SaleItemCreateDTO> dtoList);

  @Mapping(target = "productId", source = "product.id")
  @Mapping(target = "productTitle", source = "product.title")
  SaleItemViewDTO toView(SaleItem item);

  List<SaleItemViewDTO> toViewList(List<SaleItem> items);
}
