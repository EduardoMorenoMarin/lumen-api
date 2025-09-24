package com.librerialumen.api.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.librerialumen.api.domain.model.AuditLog;
import com.librerialumen.api.repository.AuditLogRepository;
import com.librerialumen.api.service.AuditService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuditServiceImpl implements AuditService {

  private final AuditLogRepository auditLogRepository;
  private final ObjectMapper objectMapper;

  @Override
  public void record(String entityName, String entityId, String action, String performedBy,
      Map<String, Object> details) {
    AuditLog log = new AuditLog();
    log.setEntityName(entityName);
    log.setEntityId(entityId);
    log.setAction(action);
    log.setPerformedBy(performedBy);
    log.setDetails(serialize(details));
    auditLogRepository.save(log);
  }

  private String serialize(Map<String, Object> details) {
    if (details == null || details.isEmpty()) {
      return null;
    }
    try {
      return objectMapper.writeValueAsString(details);
    } catch (JsonProcessingException e) {
      return details.toString();
    }
  }
}
