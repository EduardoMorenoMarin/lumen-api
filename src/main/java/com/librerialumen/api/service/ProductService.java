package com.librerialumen.api.service;

import com.librerialumen.api.web.dto.product.ProductCreateDTO;
import com.librerialumen.api.web.dto.product.ProductUpdateDTO;
import com.librerialumen.api.web.dto.product.ProductViewDTO;
import java.util.List;
import java.util.UUID;

public interface ProductService {

  ProductViewDTO create(ProductCreateDTO dto);

  ProductViewDTO update(UUID productId, ProductUpdateDTO dto);

  ProductViewDTO patch(UUID productId, ProductUpdateDTO dto);

  void delete(UUID productId);

  ProductViewDTO get(UUID productId);

  List<ProductViewDTO> list();
}
