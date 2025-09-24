package com.librerialumen.api.mapper;

import com.librerialumen.api.domain.model.Category;
import com.librerialumen.api.web.dto.category.CategoryCreateDTO;
import com.librerialumen.api.web.dto.category.CategoryUpdateDTO;
import com.librerialumen.api.web.dto.category.CategoryViewDTO;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  Category toEntity(CategoryCreateDTO dto);

  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  void updateEntity(CategoryUpdateDTO dto, @MappingTarget Category category);

  CategoryViewDTO toView(Category category);

  List<CategoryViewDTO> toViewList(List<Category> categories);
}