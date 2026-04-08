package com.autismtracker.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class JwtServiceTest {

	private JwtService jwtService;

	@BeforeEach
	void setUp() {
		jwtService = new JwtService();
		// chave base64 válida (32 bytes) apenas para teste
		String base64Secret = "3q2+7wAAAAAAAAAAAAAAAAAAAAAAAABhYmNkZWZnaGlqa2xtbm9wcQ==";
		ReflectionTestUtils.setField(jwtService, "secretKeyBase64", base64Secret);
		ReflectionTestUtils.setField(jwtService, "jwtExpirationMillis", 60_000L);
	}

	@Test
	void generateAndValidateToken_shouldWork() {
		String username = "user@example.com";
		String token = jwtService.generateToken(username, Map.of());

		assertThat(token).isNotBlank();
		assertThat(jwtService.isTokenValid(token, username)).isTrue();
		assertEquals(username, jwtService.extractUsername(token));
	}

	@Test
	void shouldGenerateAndParseTenantIdClaim() {
		String token = jwtService.generateToken("user@example.com", Map.of("tenant_id", "tenant-abc"));
		assertEquals("tenant-abc", jwtService.extractTenantId(token));
	}
}
