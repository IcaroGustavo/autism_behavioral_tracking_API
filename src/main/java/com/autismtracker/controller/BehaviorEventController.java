package com.autismtracker.controller;

import com.autismtracker.dto.BehaviorEventDtos;
import com.autismtracker.model.User;
import com.autismtracker.service.BehaviorEventService;
import com.autismtracker.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/events")
public class BehaviorEventController {

	private final BehaviorEventService behaviorEventService;
	private final UserService userService;

	public BehaviorEventController(BehaviorEventService behaviorEventService, UserService userService) {
		this.behaviorEventService = behaviorEventService;
		this.userService = userService;
	}

	@PostMapping
	public ResponseEntity<BehaviorEventDtos.EventResponse> create(@RequestBody BehaviorEventDtos.CreateEventRequest request,
	                                                              Authentication authentication) {
		User user = userService.findByEmail(authentication.getName()).orElseThrow();
		var saved = behaviorEventService.create(user, request);
		return ResponseEntity.ok(toResponse(saved));
	}

	@GetMapping
	public ResponseEntity<List<BehaviorEventDtos.EventResponse>> list(Authentication authentication) {
		User user = userService.findByEmail(authentication.getName()).orElseThrow();
		return ResponseEntity.ok(behaviorEventService.list(user));
	}

	@PutMapping("/{id}")
	public ResponseEntity<BehaviorEventDtos.EventResponse> update(@PathVariable UUID id,
	                                                              @RequestBody BehaviorEventDtos.UpdateEventRequest request,
	                                                              Authentication authentication) {
		User user = userService.findByEmail(authentication.getName()).orElseThrow();
		return behaviorEventService.update(user, id, request)
			.map(ResponseEntity::ok)
			.orElse(ResponseEntity.notFound().build());
	}

	private BehaviorEventDtos.EventResponse toResponse(com.autismtracker.model.BehaviorEvent event) {
		BehaviorEventDtos.EventResponse resp = new BehaviorEventDtos.EventResponse();
		resp.setId(event.getId());
		resp.setEventDateTime(event.getEventDateTime());
		resp.setIntensity(event.getIntensity());
		resp.setDurationMinutes(event.getDurationMinutes());
		resp.setAntecedent(event.getAntecedent());
		resp.setBehavior(event.getBehavior());
		resp.setConsequence(event.getConsequence());
		return resp;
	}
}

