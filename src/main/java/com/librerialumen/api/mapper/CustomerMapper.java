package com.librerialumen.api.mapper;

import com.librerialumen.api.domain.model.Customer;
import com.librerialumen.api.web.dto.customer.CustomerCreateDTO;
import com.librerialumen.api.web.dto.customer.CustomerUpdateDTO;
import com.librerialumen.api.web.dto.customer.CustomerViewDTO;
import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface CustomerMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  Customer toEntity(CustomerCreateDTO dto);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateEntity(CustomerUpdateDTO dto, @MappingTarget Customer customer);

  CustomerViewDTO toView(Customer customer);

  List<CustomerViewDTO> toViewList(List<Customer> customers);
}
