package com.librerialumen.api.service.impl;

import com.librerialumen.api.domain.model.Category;
import com.librerialumen.api.exception.BusinessException;
import com.librerialumen.api.mapper.CategoryMapper;
import com.librerialumen.api.repository.CategoryRepository;
import com.librerialumen.api.service.AuditService;
import com.librerialumen.api.service.CategoryService;
import com.librerialumen.api.web.dto.category.CategoryCreateDTO;
import com.librerialumen.api.web.dto.category.CategoryUpdateDTO;
import com.librerialumen.api.web.dto.category.CategoryViewDTO;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

  private final CategoryRepository categoryRepository;
  private final CategoryMapper categoryMapper;
  private final AuditService auditService;

  @Override
  public CategoryViewDTO create(CategoryCreateDTO dto) {
    Category category = categoryMapper.toEntity(dto);
    category.setActive(dto.getActive() == null ? true : dto.getActive());
    return saveAndAudit(category, "CREATE");
  }

  @Override
  public CategoryViewDTO update(UUID categoryId, CategoryUpdateDTO dto) {
    Category category = loadCategory(categoryId);
    if (dto.getActive() != null) {
      category.setActive(dto.getActive());
    }
    categoryMapper.updateEntity(dto, category);
    return saveAndAudit(category, "UPDATE");
  }

  @Override
  public CategoryViewDTO patch(UUID categoryId, CategoryUpdateDTO dto) {
    Category category = loadCategory(categoryId);
    if (dto.getName() != null) {
      category.setName(dto.getName());
    }
    if (dto.getDescription() != null) {
      category.setDescription(dto.getDescription());
    }
    if (dto.getActive() != null) {
      category.setActive(dto.getActive());
    }
    return saveAndAudit(category, "PATCH");
  }

  @Override
  public void delete(UUID categoryId) {
    Category category = loadCategory(categoryId);
    try {
      categoryRepository.delete(category);
      categoryRepository.flush();
    } catch (DataIntegrityViolationException ex) {
      throw new BusinessException("CATEGORY_DELETE_CONSTRAINT",
          "Unable to delete category because it is referenced by other records.", ex);
    }
    auditService.record("Category", category.getId().toString(), "DELETE", null,
        buildAuditDetails(category));
  }

  @Override
  @Transactional(readOnly = true)
  public CategoryViewDTO get(UUID categoryId) {
    return categoryMapper.toView(loadCategory(categoryId));
  }

  @Override
  @Transactional(readOnly = true)
  public List<CategoryViewDTO> list() {
    return categoryMapper.toViewList(categoryRepository.findAll());
  }

  private Category loadCategory(UUID categoryId) {
    return categoryRepository.findById(categoryId)
        .orElseThrow(() -> new BusinessException("CATEGORY_NOT_FOUND", "Category not found"));
  }

  private CategoryViewDTO saveAndAudit(Category category, String action) {
    Category saved = categoryRepository.save(category);
    auditService.record("Category", saved.getId().toString(), action, null,
        buildAuditDetails(saved));
    return categoryMapper.toView(saved);
  }

  private Map<String, Object> buildAuditDetails(Category category) {
    Map<String, Object> details = new HashMap<>();
    if (category.getName() != null) {
      details.put("name", category.getName());
    }
    if (category.getDescription() != null) {
      details.put("description", category.getDescription());
    }
    details.put("active", category.isActive());
    return details;
  }
}
