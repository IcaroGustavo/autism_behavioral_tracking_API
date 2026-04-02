package com.autismtracker.repository;

import com.autismtracker.model.DailyLog;
import com.autismtracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface DailyLogRepository extends JpaRepository<DailyLog, UUID> {
	List<DailyLog> findByUserAndDateBetween(User user, LocalDate start, LocalDate end);
	List<DailyLog> findByUser(User user);
}

