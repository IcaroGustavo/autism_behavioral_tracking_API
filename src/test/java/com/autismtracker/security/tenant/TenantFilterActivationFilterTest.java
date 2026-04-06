package com.autismtracker.security.tenant;

import jakarta.persistence.EntityManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class TenantFilterActivationFilterTest {

	@AfterEach
	void cleanup() { TenantContext.clear(); }

	@Test
	void shouldEnableHibernateFilterWhenTenantPresent() throws Exception {
		EntityManager em = mock(EntityManager.class);
		Session session = mock(Session.class);
		Filter filter = mock(Filter.class);

		when(em.unwrap(Session.class)).thenReturn(session);
		when(session.enableFilter("tenantFilter")).thenReturn(filter);

		TenantFilterActivationFilter tfilter = new TenantFilterActivationFilter(em);
		HttpServletRequest req = mock(HttpServletRequest.class);
		HttpServletResponse res = mock(HttpServletResponse.class);
		FilterChain chain = mock(FilterChain.class);

		TenantContext.setCurrentTenant("tenant-1");
		tfilter.doFilterInternal(req, res, chain);

		verify(session, times(1)).enableFilter("tenantFilter");
		verify(filter, times(1)).setParameter(eq("tenantId"), eq("tenant-1"));
		verify(chain, times(1)).doFilter(req, res);
	}
}

