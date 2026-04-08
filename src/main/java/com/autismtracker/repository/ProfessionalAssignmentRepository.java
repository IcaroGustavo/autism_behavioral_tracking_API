package com.autismtracker.repository;

import com.autismtracker.model.ProfessionalAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProfessionalAssignmentRepository extends JpaRepository<ProfessionalAssignment, UUID> {
	List<ProfessionalAssignment> findByProfessionalIdAndActiveTrue(UUID professionalId);
	Page<ProfessionalAssignment> findByProfessionalIdAndActiveTrue(UUID professionalId, Pageable pageable);
	boolean existsByProfessionalIdAndParentIdAndActiveTrue(UUID professionalId, UUID parentId);
	Optional<ProfessionalAssignment> findByProfessionalIdAndParentId(UUID professionalId, UUID parentId);
}

