package com.autismtracker.service;

import com.autismtracker.dto.BehaviorEventDtos;
import com.autismtracker.model.BehaviorEvent;
import com.autismtracker.model.User;
import com.autismtracker.repository.BehaviorEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BehaviorEventService {
	private final BehaviorEventRepository behaviorEventRepository;

	public BehaviorEventService(BehaviorEventRepository behaviorEventRepository) {
		this.behaviorEventRepository = behaviorEventRepository;
	}

	@Transactional
	public BehaviorEvent create(User user, BehaviorEventDtos.CreateEventRequest request) {
		BehaviorEvent event = new BehaviorEvent();
		event.setUser(user);
		event.setEventDateTime(request.getEventDateTime() != null ? request.getEventDateTime() : LocalDateTime.now());
		event.setIntensity(request.getIntensity());
		event.setDurationMinutes(request.getDurationMinutes());
		event.setAntecedent(request.getAntecedent());
		event.setBehavior(request.getBehavior());
		event.setConsequence(request.getConsequence());
		return behaviorEventRepository.save(event);
	}

	public List<BehaviorEventDtos.EventResponse> list(User user) {
		return behaviorEventRepository.findByUser(user)
			.stream().map(this::toResponse).collect(Collectors.toList());
	}

	@Transactional
	public Optional<BehaviorEventDtos.EventResponse> update(User user, UUID id, BehaviorEventDtos.UpdateEventRequest request) {
		return behaviorEventRepository.findById(id)
			.filter(e -> e.getUser() != null && e.getUser().getId().equals(user.getId()))
			.map(event -> {
				if (request.getIntensity() != null) event.setIntensity(request.getIntensity());
				if (request.getDurationMinutes() != null) event.setDurationMinutes(request.getDurationMinutes());
				if (request.getAntecedent() != null) event.setAntecedent(request.getAntecedent());
				if (request.getBehavior() != null) event.setBehavior(request.getBehavior());
				if (request.getConsequence() != null) event.setConsequence(request.getConsequence());
				return toResponse(event);
			});
	}

	private BehaviorEventDtos.EventResponse toResponse(BehaviorEvent event) {
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

