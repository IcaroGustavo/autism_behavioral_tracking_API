package com.autismtracker.controller;

import com.autismtracker.dto.ProfessionalDtos;
import com.autismtracker.model.ProfessionalAssignment;
import com.autismtracker.model.ProfessionalInvitation;
import com.autismtracker.model.ProfessionalProfile;
import com.autismtracker.model.User;
import com.autismtracker.model.UserRole;
import com.autismtracker.repository.ProfessionalAssignmentRepository;
import com.autismtracker.repository.ProfessionalInvitationRepository;
import com.autismtracker.repository.ProfessionalProfileRepository;
import com.autismtracker.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/professionals")
public class ProfessionalController {

	private final ProfessionalAssignmentRepository assignmentRepository;
	private final ProfessionalInvitationRepository invitationRepository;
	private final ProfessionalProfileRepository profileRepository;
	private final UserService userService;

	public ProfessionalController(ProfessionalAssignmentRepository assignmentRepository, ProfessionalInvitationRepository invitationRepository, ProfessionalProfileRepository profileRepository, UserService userService) {
		this.assignmentRepository = assignmentRepository;
		this.invitationRepository = invitationRepository;
		this.profileRepository = profileRepository;
		this.userService = userService;
	}

	@GetMapping("/patients")
	@PreAuthorize("hasRole('PROFESSIONAL')")
	public ResponseEntity<Page<ProfessionalDtos.PatientSummary>> listPatients(
		@PageableDefault(size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable,
		Authentication authentication) {
		User professional = userService.findByEmail(authentication.getName()).orElseThrow();
		var page = assignmentRepository.findByProfessionalIdAndActiveTrue(professional.getId(), pageable)
			.map(a -> {
				User p = a.getParent();
				var dto = new ProfessionalDtos.PatientSummary();
				dto.setId(p.getId());
				dto.setName(p.getName());
				dto.setEmail(p.getEmail());
				return dto;
			});
		return ResponseEntity.ok(page);
	}

	@GetMapping("/me/profile")
	@PreAuthorize("hasRole('PROFESSIONAL')")
	public ResponseEntity<?> myProfile(Authentication authentication) {
		User professional = userService.findByEmail(authentication.getName()).orElseThrow();
		return profileRepository.findByUserId(professional.getId())
			.<ResponseEntity<?>>map(p -> ResponseEntity.ok(Map.of(
				"userId", professional.getId(),
				"name", professional.getName(),
				"email", professional.getEmail(),
				"specialty", p.getSpecialty().name(),
				"registrationNumber", p.getRegistrationNumber()
			)))
			.orElse(ResponseEntity.ok(Map.of(
				"userId", professional.getId(),
				"name", professional.getName(),
				"email", professional.getEmail(),
				"specialty", null,
				"registrationNumber", null
			)));
	}

	@PutMapping("/me/profile")
	@PreAuthorize("hasRole('PROFESSIONAL')")
	public ResponseEntity<?> updateMyProfile(
		@RequestParam String specialty,
		@RequestParam(required = false) String registrationNumber,
		Authentication authentication
	) {
		User professional = userService.findByEmail(authentication.getName()).orElseThrow();
		var profile = profileRepository.findByUserId(professional.getId())
			.orElseGet(() -> {
				var p = new ProfessionalProfile();
				p.setUser(professional);
				return p;
			});
		try {
			profile.setSpecialty(Enum.valueOf(com.autismtracker.model.ProfessionalSpecialty.class, specialty.toUpperCase()));
		} catch (IllegalArgumentException ex) {
			return ResponseEntity.badRequest().body(Map.of("error", "Especialidade inválida"));
		}
		profile.setRegistrationNumber(registrationNumber);
		profileRepository.save(profile);
		return ResponseEntity.ok(Map.of(
			"userId", professional.getId(),
			"name", professional.getName(),
			"email", professional.getEmail(),
			"specialty", profile.getSpecialty().name(),
			"registrationNumber", profile.getRegistrationNumber()
		));
	}

	// CONVITES
	@GetMapping("/invitations/for-professional")
	@PreAuthorize("hasRole('PROFESSIONAL')")
	public ResponseEntity<Page<ProfessionalInvitation>> listInvitationsForProfessional(
		@PageableDefault(size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable,
		Authentication authentication) {
		User professional = userService.findByEmail(authentication.getName()).orElseThrow();
		autoExpireOldInvites();
		return ResponseEntity.ok(invitationRepository.findByProfessionalEmailAndStatus(
			professional.getEmail(), ProfessionalInvitation.Status.PENDING, pageable
		));
	}

	@GetMapping("/invitations/by-parent")
	@PreAuthorize("hasRole('PARENT')")
	public ResponseEntity<Page<ProfessionalInvitation>> listInvitationsByParent(
		@PageableDefault(size = 20, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable,
		Authentication authentication) {
		User parent = userService.findByEmail(authentication.getName()).orElseThrow();
		autoExpireOldInvites();
		return ResponseEntity.ok(invitationRepository.findByInviterParentId(parent.getId(), pageable));
	}

	@PostMapping("/invitations")
	@PreAuthorize("hasRole('PARENT')")
	public ResponseEntity<?> createInvitation(
		@RequestParam String professionalEmail,
		@RequestParam(required = false) String message,
		Authentication authentication
	) {
		User parent = userService.findByEmail(authentication.getName()).orElseThrow();
		ProfessionalInvitation inv = new ProfessionalInvitation();
		inv.setInviterParent(parent);
		inv.setProfessionalEmail(professionalEmail.trim().toLowerCase());
		inv.setMessage(message);
		inv.setStatus(ProfessionalInvitation.Status.PENDING);
		invitationRepository.save(inv);
		return ResponseEntity.ok(Map.of("status", "invited", "invitationId", inv.getId()));
	}

	@PostMapping("/invitations/{id}/accept")
	@PreAuthorize("hasRole('PROFESSIONAL')")
	public ResponseEntity<?> acceptInvitation(@PathVariable UUID id, Authentication authentication) {
		User professional = userService.findByEmail(authentication.getName()).orElseThrow();
		return invitationRepository.findById(id)
			.filter(inv -> inv.getStatus() == ProfessionalInvitation.Status.PENDING)
			.filter(inv -> inv.getProfessionalEmail().equalsIgnoreCase(professional.getEmail()))
			.map(inv -> {
				if (isExpired(inv)) {
					inv.setStatus(ProfessionalInvitation.Status.EXPIRED);
					invitationRepository.save(inv);
					return ResponseEntity.status(410).body(Map.of("error", "Convite expirado"));
				}
				// Cria vínculo se não existir
				if (!assignmentRepository.existsByProfessionalIdAndParentIdAndActiveTrue(professional.getId(), inv.getInviterParent().getId())) {
					ProfessionalAssignment a = new ProfessionalAssignment();
					a.setProfessional(professional);
					a.setParent(inv.getInviterParent());
					assignmentRepository.save(a);
				}
				inv.setStatus(ProfessionalInvitation.Status.ACCEPTED);
				invitationRepository.save(inv);
				return ResponseEntity.ok(Map.of("status", "accepted"));
			})
			.orElse(ResponseEntity.status(404).body(Map.of("error", "Convite não encontrado ou inválido")));
	}

	@PostMapping("/invitations/{id}/reject")
	@PreAuthorize("hasRole('PROFESSIONAL')")
	public ResponseEntity<?> rejectInvitation(@PathVariable UUID id, Authentication authentication) {
		User professional = userService.findByEmail(authentication.getName()).orElseThrow();
		return invitationRepository.findById(id)
			.filter(inv -> inv.getStatus() == ProfessionalInvitation.Status.PENDING)
			.filter(inv -> inv.getProfessionalEmail().equalsIgnoreCase(professional.getEmail()))
			.map(inv -> {
				if (isExpired(inv)) {
					inv.setStatus(ProfessionalInvitation.Status.EXPIRED);
				} else {
					inv.setStatus(ProfessionalInvitation.Status.REJECTED);
				}
				invitationRepository.save(inv);
				return ResponseEntity.ok(Map.of("status", "updated", "newStatus", inv.getStatus().name()));
			})
			.orElse(ResponseEntity.status(404).body(Map.of("error", "Convite não encontrado ou inválido")));
	}

	@org.springframework.beans.factory.annotation.Value("${app.invitation.expireDays:30}")
	private int invitationExpireDays;

	private boolean isExpired(ProfessionalInvitation inv) {
		OffsetDateTime threshold = OffsetDateTime.now().minus(invitationExpireDays, ChronoUnit.DAYS);
		return inv.getCreatedAt().isBefore(threshold);
	}

	private void autoExpireOldInvites() {
		OffsetDateTime threshold = OffsetDateTime.now().minus(invitationExpireDays, ChronoUnit.DAYS);
		var all = invitationRepository.findAll();
		boolean changed = false;
		for (var inv : all) {
			if (inv.getStatus() == ProfessionalInvitation.Status.PENDING && inv.getCreatedAt().isBefore(threshold)) {
				inv.setStatus(ProfessionalInvitation.Status.EXPIRED);
				changed = true;
			}
		}
		if (changed) {
			invitationRepository.saveAll(all);
		}
	}
	@PostMapping("/assignments")
	@PreAuthorize("hasAnyRole('PROFESSIONAL','PARENT')")
	public ResponseEntity<?> createAssignment(
		@RequestParam UUID parentId,
		Authentication authentication
	) {
		User current = userService.findByEmail(authentication.getName()).orElseThrow();

		UUID professionalId;
		UUID targetParentId;

		if (current.getRole() == UserRole.PROFESSIONAL) {
			professionalId = current.getId();
			targetParentId = parentId;
		} else {
			// PARENT vinculando um profissional a si mesmo (parentId deve ser dele)
			if (!current.getId().equals(parentId)) {
				return ResponseEntity.status(403).build();
			}
			return ResponseEntity.badRequest().body(Map.of("error", "Fluxo de convite de profissional não implementado."));
		}

		if (assignmentRepository.existsByProfessionalIdAndParentIdAndActiveTrue(professionalId, targetParentId)) {
			return ResponseEntity.ok(Map.of("status", "already_assigned"));
		}

		User professional = userService.findById(professionalId).orElseThrow();
		User parent = userService.findById(targetParentId).orElseThrow();

		ProfessionalAssignment assignment = new ProfessionalAssignment();
		assignment.setProfessional(professional);
		assignment.setParent(parent);
		assignmentRepository.save(assignment);

		return ResponseEntity.ok(Map.of("status", "assigned"));
	}

	@DeleteMapping("/assignments")
	@PreAuthorize("hasRole('PROFESSIONAL')")
	public ResponseEntity<?> removeAssignment(
		@RequestParam UUID parentId,
		Authentication authentication
	) {
		User professional = userService.findByEmail(authentication.getName()).orElseThrow();
		return assignmentRepository.findByProfessionalIdAndParentId(professional.getId(), parentId)
			.map(a -> {
				a.setActive(false);
				assignmentRepository.save(a);
				return ResponseEntity.ok(Map.of("status", "unassigned"));
			})
			.orElse(ResponseEntity.notFound().build());
	}
}

