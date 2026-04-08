package com.autismtracker.repository;

import com.autismtracker.model.ProfessionalInvitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ProfessionalInvitationRepository extends JpaRepository<ProfessionalInvitation, UUID> {
	List<ProfessionalInvitation> findByProfessionalEmailAndStatus(String email, ProfessionalInvitation.Status status);
	Page<ProfessionalInvitation> findByProfessionalEmailAndStatus(String email, ProfessionalInvitation.Status status, Pageable pageable);
	List<ProfessionalInvitation> findByInviterParentId(UUID inviterParentId);
	Page<ProfessionalInvitation> findByInviterParentId(UUID inviterParentId, Pageable pageable);
}

