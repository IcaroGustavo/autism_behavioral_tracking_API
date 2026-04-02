package com.autismtracker.repository;

import com.autismtracker.model.BehaviorEvent;
import com.autismtracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BehaviorEventRepository extends JpaRepository<BehaviorEvent, UUID> {
	List<BehaviorEvent> findByUser(User user);
}

