package com.librerialumen.api.mapper;

import com.librerialumen.api.domain.model.Product;
import com.librerialumen.api.web.dto.product.ProductCreateDTO;
import com.librerialumen.api.web.dto.product.ProductUpdateDTO;
import com.librerialumen.api.web.dto.product.ProductViewDTO;
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
public interface ProductMapper {

  @Mapping(target = "category", ignore = true)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  Product toEntity(ProductCreateDTO dto);

  @Mapping(target = "category", ignore = true)
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateEntity(ProductUpdateDTO dto, @MappingTarget Product product);

  @Mapping(target = "categoryId", source = "category.id")
  @Mapping(target = "categoryName", source = "category.name")
  ProductViewDTO toView(Product product);

  List<ProductViewDTO> toViewList(List<Product> products);
}
