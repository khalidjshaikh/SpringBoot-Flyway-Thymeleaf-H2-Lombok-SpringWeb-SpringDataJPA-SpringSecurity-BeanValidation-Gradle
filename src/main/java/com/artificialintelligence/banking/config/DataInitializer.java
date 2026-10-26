package com.artificialintelligence.banking.config;

import com.artificialintelligence.banking.domain.Role;
import com.artificialintelligence.banking.domain.User;
import com.artificialintelligence.banking.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (!userRepository.existsByUsername("admin")) {
                User admin = new User("admin", passwordEncoder.encode("Admin@123"),
                        "admin@banking.local", "Administrator", Role.ROLE_ADMIN);
                userRepository.save(admin);
            }
            if (!userRepository.existsByUsername("user")) {
                User user = new User("user", passwordEncoder.encode("User@123"),
                        "user@banking.local", "Demo User", Role.ROLE_USER);
                userRepository.save(user);
            }
        };
    }
}