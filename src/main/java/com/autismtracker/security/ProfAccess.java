package com.autismtracker.security;

import com.autismtracker.model.ProfessionalProfile;
import com.autismtracker.repository.ProfessionalProfileRepository;
import com.autismtracker.service.UserService;
import org.springframework.stereotype.Component;

@Component("profAccess")
public class ProfAccess {

	private final ProfessionalProfileRepository profileRepository;
	private final UserService userService;

	public ProfAccess(ProfessionalProfileRepository profileRepository, UserService userService) {
		this.profileRepository = profileRepository;
		this.userService = userService;
	}

	public boolean hasSpecialty(String email, String specialtyName) {
		return userService.findByEmail(email)
			.flatMap(u -> profileRepository.findByUserId(u.getId()))
			.map(ProfessionalProfile::getSpecialty)
			.map(Enum::name)
			.filter(n -> n.equalsIgnoreCase(specialtyName))
			.isPresent();
	}
}

