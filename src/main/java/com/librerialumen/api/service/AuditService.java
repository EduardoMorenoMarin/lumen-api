package com.librerialumen.api.service;

import java.util.Map;

public interface AuditService {

  void record(String entityName, String entityId, String action, String performedBy, Map<String, Object> details);
}
