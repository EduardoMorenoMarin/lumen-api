package com.librerialumen.api.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "audit_logs")
public class AuditLog extends BaseEntity {

  @Column(name = "entity_name", nullable = false, length = 150)
  private String entityName;

  @Column(name = "entity_id", nullable = false, length = 64)
  private String entityId;

  @Column(name = "action", nullable = false, length = 50)
  private String action;

  @Column(name = "performed_by", length = 320)
  private String performedBy;

  @Column(name = "performed_at", nullable = false)
  private Instant performedAt;

  @Column(name = "details", length = 2000)
  private String details;

  @PrePersist
  void ensurePerformedAt() {
    if (performedAt == null) {
      performedAt = Instant.now();
    }
  }
}
