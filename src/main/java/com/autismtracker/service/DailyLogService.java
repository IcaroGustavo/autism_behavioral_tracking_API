package com.autismtracker.service;

import com.autismtracker.dto.DailyLogDtos;
import com.autismtracker.model.DailyLog;
import com.autismtracker.model.User;
import com.autismtracker.repository.DailyLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DailyLogService {
	private final DailyLogRepository dailyLogRepository;

	public DailyLogService(DailyLogRepository dailyLogRepository) {
		this.dailyLogRepository = dailyLogRepository;
	}

	@Transactional
	public DailyLog create(User user, DailyLogDtos.CreateDailyLogRequest request) {
		DailyLog log = new DailyLog();
		log.setUser(user);
		log.setDate(request.getDate() != null ? request.getDate() : LocalDate.now());
		log.setSleepHours(request.getSleepHours());
		log.setDietQuality(request.getDietQuality());
		log.setNotes(request.getNotes());
		return dailyLogRepository.save(log);
	}

	public List<DailyLogDtos.DailyLogResponse> list(User user, LocalDate start, LocalDate end) {
		List<DailyLog> logs;
		if (start != null && end != null) {
			logs = dailyLogRepository.findByUserAndDateBetween(user, start, end);
		} else {
			logs = dailyLogRepository.findByUser(user);
		}
		return logs.stream().map(this::toResponse).collect(Collectors.toList());
	}

	private DailyLogDtos.DailyLogResponse toResponse(DailyLog log) {
		DailyLogDtos.DailyLogResponse resp = new DailyLogDtos.DailyLogResponse();
		resp.setId(log.getId());
		resp.setDate(log.getDate());
		resp.setSleepHours(log.getSleepHours());
		resp.setDietQuality(log.getDietQuality());
		resp.setNotes(log.getNotes());
		return resp;
	}
}

