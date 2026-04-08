package com.autismtracker.controller;

import com.autismtracker.dto.ProfessionalDtos;
import com.autismtracker.model.ProfessionalAssignment;
import com.autismtracker.model.User;
import com.autismtracker.model.UserRole;
import com.autismtracker.repository.ProfessionalAssignmentRepository;
import com.autismtracker.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/professionals")
public class ProfessionalController {

	private final ProfessionalAssignmentRepository assignmentRepository;
	private final UserService userService;

	public ProfessionalController(ProfessionalAssignmentRepository assignmentRepository, UserService userService) {
		this.assignmentRepository = assignmentRepository;
		this.userService = userService;
	}

	@GetMapping("/patients")
	@PreAuthorize("hasRole('PROFESSIONAL')")
	public ResponseEntity<List<ProfessionalDtos.PatientSummary>> listPatients(Authentication authentication) {
		User professional = userService.findByEmail(authentication.getName()).orElseThrow();
		var list = assignmentRepository.findByProfessionalIdAndActiveTrue(professional.getId()).stream()
			.map(a -> {
				User p = a.getParent();
				var dto = new ProfessionalDtos.PatientSummary();
				dto.setId(p.getId());
				dto.setName(p.getName());
				dto.setEmail(p.getEmail());
				return dto;
			}).collect(Collectors.toList());
		return ResponseEntity.ok(list);
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

