package com.autismtracker.audit;

import com.autismtracker.security.tenant.BaseTenantEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(name = "audit_logs", indexes = {
	@Index(name = "idx_audit_tenant_time", columnList = "tenant_id, occurred_at")
})
public class AuditLog extends BaseTenantEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "entity_name", nullable = false, length = 128)
	private String entityName;

	@Column(name = "entity_id", nullable = false, length = 64)
	private String entityId;

	@Column(name = "action", nullable = false, length = 16)
	private String action;

	@Column(name = "who", nullable = false, length = 128)
	private String who;

	@Column(name = "occurred_at", nullable = false)
	private OffsetDateTime occurredAt = OffsetDateTime.now();

	@Column(name = "details", columnDefinition = "text")
	private String details;

	public Long getId() { return id; }
	public String getEntityName() { return entityName; }
	public void setEntityName(String entityName) { this.entityName = entityName; }
	public String getEntityId() { return entityId; }
	public void setEntityId(String entityId) { this.entityId = entityId; }
	public String getAction() { return action; }
	public void setAction(String action) { this.action = action; }
	public String getWho() { return who; }
	public void setWho(String who) { this.who = who; }
	public OffsetDateTime getOccurredAt() { return occurredAt; }
	public void setOccurredAt(OffsetDateTime occurredAt) { this.occurredAt = occurredAt; }
	public String getDetails() { return details; }
	public void setDetails(String details) { this.details = details; }
}

