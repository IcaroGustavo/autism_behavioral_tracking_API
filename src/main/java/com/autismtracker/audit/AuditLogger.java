package com.autismtracker.audit;

import com.autismtracker.security.tenant.TenantContext;
import org.springframework.stereotype.Component;

@Component
public class AuditLogger {

	private final AuditLogRepository repository;

	public AuditLogger(AuditLogRepository repository) {
		this.repository = repository;
	}

	public void log(String entityName, String entityId, String action, String who, String details) {
		AuditLog log = new AuditLog();
		log.setTenantId(TenantContext.getCurrentTenant().orElse("UNKNOWN"));
		log.setEntityName(entityName);
		log.setEntityId(entityId);
		log.setAction(action);
		log.setWho(who);
		log.setDetails(details);
		repository.save(log);
	}
}

