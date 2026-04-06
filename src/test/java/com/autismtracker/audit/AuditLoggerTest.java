package com.autismtracker.audit;

import com.autismtracker.security.tenant.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AuditLoggerTest {

	@AfterEach
	void cleanup() {
		TenantContext.clear();
	}

	@Test
	void shouldSaveAuditLogWithTenant() {
		AuditLogRepository repo = mock(AuditLogRepository.class);
		AuditLogger logger = new AuditLogger(repo);
		TenantContext.setCurrentTenant("tenant-xyz");

		logger.log("EntityX", "123", "READ", "user@example.com", "details");

		ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
		verify(repo, times(1)).save(captor.capture());
		AuditLog saved = captor.getValue();
		assertEquals("tenant-xyz", saved.getTenantId());
		assertEquals("EntityX", saved.getEntityName());
		assertEquals("123", saved.getEntityId());
		assertEquals("READ", saved.getAction());
		assertEquals("user@example.com", saved.getWho());
	}
}

