package com.autismtracker.repository;

import com.autismtracker.model.ProfessionalProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProfessionalProfileRepository extends JpaRepository<ProfessionalProfile, UUID> {
	Optional<ProfessionalProfile> findByUserId(UUID userId);
}

