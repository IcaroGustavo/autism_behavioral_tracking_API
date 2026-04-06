package com.autismtracker.security.tenant;

import jakarta.persistence.EntityManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.Session;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class TenantFilterActivationFilter extends OncePerRequestFilter {

	private final EntityManager entityManager;

	public TenantFilterActivationFilter(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {
		try {
			String tenantId = TenantContext.getCurrentTenant().orElse(null);
			if (tenantId != null) {
				Session session = entityManager.unwrap(Session.class);
				org.hibernate.Filter filter = session.enableFilter("tenantFilter");
				filter.setParameter("tenantId", tenantId);
			}
			filterChain.doFilter(request, response);
		} finally {
			TenantContext.clear();
		}
	}
}

