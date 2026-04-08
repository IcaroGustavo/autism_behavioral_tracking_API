package com.autismtracker.repository;

import com.autismtracker.model.ProfessionalAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProfessionalAssignmentRepository extends JpaRepository<ProfessionalAssignment, UUID> {
	List<ProfessionalAssignment> findByProfessionalIdAndActiveTrue(UUID professionalId);
	boolean existsByProfessionalIdAndParentIdAndActiveTrue(UUID professionalId, UUID parentId);
	Optional<ProfessionalAssignment> findByProfessionalIdAndParentId(UUID professionalId, UUID parentId);
}

