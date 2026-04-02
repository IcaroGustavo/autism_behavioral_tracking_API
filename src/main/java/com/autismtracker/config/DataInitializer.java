package com.autismtracker.config;

import com.autismtracker.model.User;
import com.autismtracker.model.UserRole;
import com.autismtracker.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

	@Bean
	public CommandLineRunner seedDefaultUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {
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
		};
	}
}

