package com.librerialumen.api.service;

import com.librerialumen.api.web.dto.category.CategoryCreateDTO;
import com.librerialumen.api.web.dto.category.CategoryUpdateDTO;
import com.librerialumen.api.web.dto.category.CategoryViewDTO;
import java.util.List;
import java.util.UUID;

public interface CategoryService {

  CategoryViewDTO create(CategoryCreateDTO dto);

  CategoryViewDTO update(UUID categoryId, CategoryUpdateDTO dto);

  CategoryViewDTO patch(UUID categoryId, CategoryUpdateDTO dto);

  void delete(UUID categoryId);

  CategoryViewDTO get(UUID categoryId);

  List<CategoryViewDTO> list();
}
