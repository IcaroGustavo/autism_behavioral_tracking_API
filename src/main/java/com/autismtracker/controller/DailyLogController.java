package com.autismtracker.controller;

import com.autismtracker.dto.DailyLogDtos;
import com.autismtracker.model.UserRole;
import com.autismtracker.model.User;
import com.autismtracker.service.DailyLogService;
import com.autismtracker.service.UserService;
import com.autismtracker.repository.ProfessionalAssignmentRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/daily-logs")
public class DailyLogController {

	private final DailyLogService dailyLogService;
	private final UserService userService;
	private final ProfessionalAssignmentRepository assignmentRepository;

	public DailyLogController(DailyLogService dailyLogService, UserService userService, ProfessionalAssignmentRepository assignmentRepository) {
		this.dailyLogService = dailyLogService;
		this.userService = userService;
		this.assignmentRepository = assignmentRepository;
	}

	@PostMapping
	public ResponseEntity<DailyLogDtos.DailyLogResponse> create(@RequestBody DailyLogDtos.CreateDailyLogRequest request,
	                                                            Authentication authentication) {
		User user = userService.findByEmail(authentication.getName()).orElseThrow();
		var saved = dailyLogService.create(user, request);
		var resp = new DailyLogDtos.DailyLogResponse();
		resp.setId(saved.getId());
		resp.setDate(saved.getDate());
		resp.setSleepHours(saved.getSleepHours());
		resp.setDietQuality(saved.getDietQuality());
		resp.setNotes(saved.getNotes());
		return ResponseEntity.ok(resp);
	}

	@GetMapping
	public ResponseEntity<List<DailyLogDtos.DailyLogResponse>> list(
		@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
		@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
		@RequestParam(required = false) UUID parentId,
		Authentication authentication) {
		User requester = userService.findByEmail(authentication.getName()).orElseThrow();

		User target = requester;
		if (parentId != null && requester.getRole() == UserRole.PROFESSIONAL) {
			boolean allowed = assignmentRepository.existsByProfessionalIdAndParentIdAndActiveTrue(requester.getId(), parentId);
			if (allowed) {
				target = userService.findById(parentId).orElseThrow();
			}
		}

		return ResponseEntity.ok(dailyLogService.list(target, startDate, endDate));
	}
}

