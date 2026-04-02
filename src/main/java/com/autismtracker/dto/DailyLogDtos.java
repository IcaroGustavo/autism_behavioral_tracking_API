package com.autismtracker.dto;

import com.autismtracker.model.DietQuality;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

public class DailyLogDtos {
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CreateDailyLogRequest {
		private LocalDate date;
		private Integer sleepHours;
		private DietQuality dietQuality;
		private String notes;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class DailyLogResponse {
		private UUID id;
		private LocalDate date;
		private Integer sleepHours;
		private DietQuality dietQuality;
		private String notes;
	}
}

