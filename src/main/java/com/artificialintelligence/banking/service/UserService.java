package com.artificialintelligence.banking.service;

import com.artificialintelligence.banking.domain.Role;
import com.artificialintelligence.banking.domain.User;
import com.artificialintelligence.banking.repository.UserRepository;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
    }

    public User register(String username, String rawPassword, String email, String fullName) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already taken");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered");
        }
        User user = new User(username, passwordEncoder.encode(rawPassword), email, fullName, Role.ROLE_USER);
        return userRepository.save(user);
    }

    public User createAdmin(String username, String rawPassword, String email, String fullName) {
        if (userRepository.existsByUsername(username)) {
            return userRepository.findByUsername(username).get();
        }
        User user = new User(username, passwordEncoder.encode(rawPassword), email, fullName, Role.ROLE_ADMIN);
        return userRepository.save(user);
    }
}