package com.autismtracker.security;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class JwtServiceTest {

	@Test
	void shouldGenerateAndParseTenantIdClaim() {
		JwtService svc = new JwtService();
		// Injetando campos via reflexão para teste simples
		TestUtils.setField(svc, "secretKeyBase64", "3q2+7wAAAAAAAAAAAAAAAAAAAAAAAABhYmNkZWZnaGlqa2xtbm9wcQ==");
		TestUtils.setField(svc, "jwtExpirationMillis", 900000L);

		Map<String, Object> claims = new HashMap<>();
		claims.put("tenant_id", "tenant-abc");
		String token = svc.generateToken("user@example.com", claims);

		assertEquals("user@example.com", svc.extractUsername(token));
		assertEquals("tenant-abc", svc.extractTenantId(token));
		assertTrue(svc.isTokenValid(token, "user@example.com"));
	}
}

package com.autismtracker.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

	private JwtService jwtService;

	@BeforeEach
	void setUp() {
		jwtService = new JwtService();
		String base64Secret = "YWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWE=";
		ReflectionTestUtils.setField(jwtService, "secretKeyBase64", base64Secret);
		ReflectionTestUtils.setField(jwtService, "jwtExpirationMillis", 60_000L);
	}

	@Test
	void generateAndValidateToken_shouldWork() {
		String username = "user@example.com";

		String token = jwtService.generateToken(username, java.util.Map.of());
		boolean valid = jwtService.isTokenValid(token, username);
		String extracted = jwtService.extractUsername(token);

		
		assertThat(token).isNotBlank();
		assertThat(valid).isTrue();
		assertThat(extracted).isEqualTo(username);
	}
}

