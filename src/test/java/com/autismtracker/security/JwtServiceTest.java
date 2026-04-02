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

