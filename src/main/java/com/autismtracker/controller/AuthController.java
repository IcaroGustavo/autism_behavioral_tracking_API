package com.autismtracker.controller;

import com.autismtracker.dto.AuthDtos;
import com.autismtracker.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;

	public AuthController(AuthenticationManager authenticationManager, JwtService jwtService) {
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
	}

	@PostMapping("/login")
	public ResponseEntity<AuthDtos.LoginResponse> login(@RequestBody AuthDtos.LoginRequest request,
	                                                   @RequestHeader(name = "X-Tenant-ID", required = false) String tenantId) {
		Authentication authentication = authenticationManager.authenticate(
			new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
		);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		Map<String, Object> claims = new HashMap<>();
		// Apenas metadados mínimos: tenant_id e roles (sem PHI)
		if (tenantId != null && !tenantId.isBlank()) {
			claims.put("tenant_id", tenantId);
		}
		String[] roles = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toArray(String[]::new);
		claims.put("roles", roles);
		String token = jwtService.generateToken(request.getEmail(), claims);
		return ResponseEntity.ok(new AuthDtos.LoginResponse(token));
	}
}

