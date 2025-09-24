package com.librerialumen.api.service;

import com.librerialumen.api.web.dto.catalog.PublicCategoryViewDTO;
import com.librerialumen.api.web.dto.catalog.PublicProductViewDTO;
import java.util.List;
import java.util.UUID;

public interface PublicCatalogService {

  List<PublicProductViewDTO> listProducts();

  PublicProductViewDTO getProduct(UUID productId);

  List<PublicCategoryViewDTO> listCategories();

  PublicCategoryViewDTO getCategory(UUID categoryId);
}
