package com.autismtracker.service;

import com.autismtracker.dto.DailyLogDtos;
import com.autismtracker.model.DailyLog;
import com.autismtracker.model.DietQuality;
import com.autismtracker.model.User;
import com.autismtracker.repository.DailyLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class DailyLogServiceTest {

	private DailyLogRepository dailyLogRepository;
	private DailyLogService dailyLogService;

	@BeforeEach
	void setUp() {
		dailyLogRepository = Mockito.mock(DailyLogRepository.class);
		dailyLogService = new DailyLogService(dailyLogRepository);
	}

	@Test
	void create_shouldPersistDailyLog_withDefaultsWhenDateNull() {
		
		User user = new User();
		user.setEmail("u@example.com");

		DailyLogDtos.CreateDailyLogRequest req = new DailyLogDtos.CreateDailyLogRequest(null, 7, DietQuality.GOOD, "ok");
		when(dailyLogRepository.save(any(DailyLog.class))).thenAnswer(inv -> {
			DailyLog saved = inv.getArgument(0);
			return saved;
		});

		
		DailyLog saved = dailyLogService.create(user, req);

		
		assertThat(saved.getUser()).isEqualTo(user);
		assertThat(saved.getDate()).isNotNull();
		assertThat(saved.getSleepHours()).isEqualTo(7);
		assertThat(saved.getDietQuality()).isEqualTo(DietQuality.GOOD);
		assertThat(saved.getNotes()).isEqualTo("ok");

		ArgumentCaptor<DailyLog> captor = ArgumentCaptor.forClass(DailyLog.class);
		verify(dailyLogRepository, times(1)).save(captor.capture());
		assertThat(captor.getValue().getUser()).isEqualTo(user);
	}

	@Test
	void list_shouldFilterByDateRange_whenProvided() {
		
		User user = new User();
		user.setEmail("u@example.com");
		LocalDate start = LocalDate.of(2026, 1, 1);
		LocalDate end = LocalDate.of(2026, 1, 31);

		DailyLog log = new DailyLog();
		log.setDate(LocalDate.of(2026, 1, 10));
		when(dailyLogRepository.findByUserAndDateBetween(user, start, end)).thenReturn(List.of(log));

		
		var result = dailyLogService.list(user, start, end);

		
		assertThat(result).hasSize(1);
		assertThat(result.get(0).getDate()).isEqualTo(LocalDate.of(2026, 1, 10));
	}
}

