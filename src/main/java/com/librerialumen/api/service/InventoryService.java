package com.librerialumen.api.service;

import java.util.UUID;

public interface InventoryService {

  int adjustStock(UUID productId, int delta, String reason, UUID actorUserId);

  int getCurrentStock(UUID productId);
}
