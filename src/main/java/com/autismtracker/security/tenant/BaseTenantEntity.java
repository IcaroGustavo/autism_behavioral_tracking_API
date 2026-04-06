package com.autismtracker.security.tenant;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import java.time.OffsetDateTime;

@MappedSuperclass
@FilterDef(name = "tenantFilter", parameters = @ParamDef(name = "tenantId", type = String.class))
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
public abstract class BaseTenantEntity {

	@Column(name = "tenant_id", nullable = false, updatable = false, length = 64)
	private String tenantId;

	@Column(name = "created_at", nullable = false, updatable = false)
	private OffsetDateTime createdAt = OffsetDateTime.now();

	public String getTenantId() { return tenantId; }
	public void setTenantId(String tenantId) { this.tenantId = tenantId; }
	public OffsetDateTime getCreatedAt() { return createdAt; }

	@PrePersist
	void onPrePersist() {
		if (this.tenantId == null) {
			this.tenantId = TenantContext.getCurrentTenant()
				.orElseThrow(() -> new IllegalStateException("Tenant ausente no contexto"));
		}
	}
}

