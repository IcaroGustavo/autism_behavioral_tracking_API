package com.autismtracker.controller;

import com.autismtracker.model.User;
import com.autismtracker.service.AnalysisService;
import com.autismtracker.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/events/analysis")
public class AnalysisController {

	private final AnalysisService analysisService;
	private final UserService userService;

	public AnalysisController(AnalysisService analysisService, UserService userService) {
		this.analysisService = analysisService;
		this.userService = userService;
	}

	@GetMapping("/triggers")
	@PreAuthorize("!hasRole('PROFESSIONAL') or @profAccess.hasSpecialty(authentication.name, 'NEUROPEDIATRIA')")
	public ResponseEntity<Map<String, Long>> triggers(@RequestParam(defaultValue = "10") int limit,
	                                                  Authentication authentication) {
		User user = userService.findByEmail(authentication.getName()).orElseThrow();
		return ResponseEntity.ok(analysisService.mostFrequentAntecedentWords(user, limit));
	}
}

