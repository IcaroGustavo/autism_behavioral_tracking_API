package com.autismtracker.dto;

import com.autismtracker.model.EventIntensity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

public class BehaviorEventDtos {
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CreateEventRequest {
		private LocalDateTime eventDateTime;
		private EventIntensity intensity;
		private Integer durationMinutes;
		private String antecedent;
		private String behavior;
		private String consequence;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class UpdateEventRequest {
		private EventIntensity intensity;
		private Integer durationMinutes;
		private String antecedent;
		private String behavior;
		private String consequence;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class EventResponse {
		private UUID id;
		private LocalDateTime eventDateTime;
		private EventIntensity intensity;
		private Integer durationMinutes;
		private String antecedent;
		private String behavior;
		private String consequence;
	}
}

