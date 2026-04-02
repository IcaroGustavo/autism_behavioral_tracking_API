package com.autismtracker.service;

import com.autismtracker.dto.BehaviorEventDtos;
import com.autismtracker.model.BehaviorEvent;
import com.autismtracker.model.EventIntensity;
import com.autismtracker.model.User;
import com.autismtracker.repository.BehaviorEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BehaviorEventServiceTest {

	private BehaviorEventRepository behaviorEventRepository;
	private BehaviorEventService behaviorEventService;

	@BeforeEach
	void setUp() {
		behaviorEventRepository = mock(BehaviorEventRepository.class);
		behaviorEventService = new BehaviorEventService(behaviorEventRepository);
	}

	@Test
	void create_shouldPersistWithDefaults() {
		
		User user = new User();
		user.setEmail("u@example.com");
		BehaviorEventDtos.CreateEventRequest req = new BehaviorEventDtos.CreateEventRequest(
			null, null, null, "noise", null, null);
		when(behaviorEventRepository.save(any(BehaviorEvent.class))).thenAnswer(inv -> inv.getArgument(0));

		
		BehaviorEvent saved = behaviorEventService.create(user, req);

		
		assertThat(saved.getUser()).isEqualTo(user);
		assertThat(saved.getEventDateTime()).isNotNull();
		assertThat(saved.getAntecedent()).isEqualTo("noise");
	}

	@Test
	void list_shouldMapToResponses() {
		
		User user = new User();
		BehaviorEvent e = new BehaviorEvent();
		e.setEventDateTime(LocalDateTime.now());
		when(behaviorEventRepository.findByUser(user)).thenReturn(List.of(e));

		
		var responses = behaviorEventService.list(user);

		
		assertThat(responses).hasSize(1);
		assertThat(responses.get(0).getEventDateTime()).isNotNull();
	}

	@Test
	void update_shouldUpdateWhenOwnerMatches() {
		
		UUID id = UUID.randomUUID();
		User user = new User();
		user.setEmail("owner@example.com");
		user.setId(UUID.randomUUID());
		BehaviorEvent existing = new BehaviorEvent();
		existing.setId(id);
		existing.setUser(user);
		when(behaviorEventRepository.findById(id)).thenReturn(Optional.of(existing));

		BehaviorEventDtos.UpdateEventRequest req = new BehaviorEventDtos.UpdateEventRequest(
			EventIntensity.MODERATE, 12, "A", "B", "C"
		);

		
		var updated = behaviorEventService.update(user, id, req);

		
		assertThat(updated).isPresent();
		assertThat(updated.get().getIntensity()).isEqualTo(EventIntensity.MODERATE);
		assertThat(updated.get().getDurationMinutes()).isEqualTo(12);
		assertThat(updated.get().getAntecedent()).isEqualTo("A");
	}

	@Test
	void update_shouldReturnEmptyWhenOwnerMismatch() {
		
		UUID id = UUID.randomUUID();
		User owner = new User();
		owner.setId(UUID.randomUUID());
		User other = new User();
		other.setId(UUID.randomUUID());

		BehaviorEvent existing = new BehaviorEvent();
		existing.setId(id);
		existing.setUser(owner);
		when(behaviorEventRepository.findById(id)).thenReturn(Optional.of(existing));

		BehaviorEventDtos.UpdateEventRequest req = new BehaviorEventDtos.UpdateEventRequest(
			EventIntensity.MILD, 5, null, null, null
		);

		
		var updated = behaviorEventService.update(other, id, req);

		
		assertThat(updated).isNotPresent();
	}
}

