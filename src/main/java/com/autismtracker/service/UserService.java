package com.autismtracker.service;

import com.autismtracker.model.User;
import com.autismtracker.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
	private final UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public Optional<User> findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Transactional
	public User save(User user) {
		return userRepository.save(user);
	}

	public Optional<User> findById(UUID id) {
		return userRepository.findById(id);
	}
}

