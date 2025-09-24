package com.librerialumen.api.mapper;

import com.librerialumen.api.domain.model.Sale;
import com.librerialumen.api.web.dto.sale.SaleCreateDTO;
import com.librerialumen.api.web.dto.sale.SaleViewDTO;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    uses = {SaleItemMapper.class},
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface SaleMapper {

  @Mapping(target = "customer", ignore = true)
  @Mapping(target = "cashier", ignore = true)
  @Mapping(target = "items", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "saleDate", ignore = true)
  @Mapping(target = "totalAmount", ignore = true)
  @Mapping(target = "taxAmount", ignore = true)
  @Mapping(target = "discountAmount", ignore = true)
  @Mapping(target = "notes", ignore = true)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  Sale toEntity(SaleCreateDTO dto);

  @Mapping(target = "customerId", source = "customer.id")
  @Mapping(target = "customerFirstName", source = "customer.firstName")
  @Mapping(target = "customerLastName", source = "customer.lastName")
  @Mapping(target = "cashierId", source = "cashier.id")
  @Mapping(target = "cashierEmail", source = "cashier.email")
  SaleViewDTO toView(Sale sale);

  List<SaleViewDTO> toViewList(List<Sale> sales);
}
