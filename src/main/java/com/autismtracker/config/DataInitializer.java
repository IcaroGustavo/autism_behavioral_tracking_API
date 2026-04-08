package com.autismtracker.config;

import com.autismtracker.model.User;
import com.autismtracker.model.UserRole;
import com.autismtracker.repository.UserRepository;
import com.autismtracker.model.ProfessionalProfile;
import com.autismtracker.model.ProfessionalSpecialty;
import com.autismtracker.repository.ProfessionalProfileRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.autismtracker.security.tenant.TenantContext;

@Configuration
public class DataInitializer {

	@Bean
	public CommandLineRunner seedDefaultUsers(UserRepository userRepository, ProfessionalProfileRepository profileRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			// Define um tenant padrão para o seed inicial (evita falha no @PrePersist)
			TenantContext.setCurrentTenant("default");
			if (!userRepository.existsByEmail("parent@example.com")) {
				User parent = new User();
				parent.setName("Default Parent");
				parent.setEmail("parent@example.com");
				parent.setPassword(passwordEncoder.encode("password"));
				parent.setRole(UserRole.PARENT);
				userRepository.save(parent);
			}
			if (!userRepository.existsByEmail("therapist@example.com")) {
				User therapist = new User();
				therapist.setName("Default Therapist");
				therapist.setEmail("therapist@example.com");
				therapist.setPassword(passwordEncoder.encode("password"));
				therapist.setRole(UserRole.THERAPIST);
				userRepository.save(therapist);
			}
			if (!userRepository.existsByEmail("pro@example.com")) {
				User professional = new User();
				professional.setName("Default Professional");
				professional.setEmail("pro@example.com");
				professional.setPassword(passwordEncoder.encode("password"));
				professional.setRole(UserRole.PROFESSIONAL);
				userRepository.save(professional);

				ProfessionalProfile profile = new ProfessionalProfile();
				profile.setUser(professional);
				profile.setSpecialty(ProfessionalSpecialty.FONOAUDIOLOGIA);
				profile.setRegistrationNumber("REG-12345");
				profileRepository.save(profile);
			}
			TenantContext.clear();
		};
	}
}

