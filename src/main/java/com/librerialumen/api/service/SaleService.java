package com.librerialumen.api.service;

import com.librerialumen.api.web.dto.sale.SaleCreateDTO;
import com.librerialumen.api.web.dto.sale.SaleViewDTO;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface SaleService {

  SaleViewDTO create(SaleCreateDTO dto, UUID actorUserId);

  SaleViewDTO get(UUID saleId);

  List<SaleViewDTO> getDailySales(Instant start, Instant end);
}
