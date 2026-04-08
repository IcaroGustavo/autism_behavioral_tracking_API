package com.autismtracker.repository;

import com.autismtracker.model.ProfessionalInvitation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProfessionalInvitationRepository extends JpaRepository<ProfessionalInvitation, UUID> {
	List<ProfessionalInvitation> findByProfessionalEmailAndStatus(String email, ProfessionalInvitation.Status status);
	List<ProfessionalInvitation> findByInviterParentId(UUID inviterParentId);
}

