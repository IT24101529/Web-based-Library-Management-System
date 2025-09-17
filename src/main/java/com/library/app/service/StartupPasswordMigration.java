package com.library.app.service;

import com.library.app.entity.Role;
import com.library.app.entity.User;
import com.library.app.repository.RoleRepository;
import com.library.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StartupPasswordMigration implements CommandLineRunner {
    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        List<User> users = userRepo.findAll();
        boolean changed = false;

        for (User u : users) {
            // One-time encode if not BCrypt
            String ph = u.getPasswordHash();
            if (ph != null && !ph.matches("^\\$2[aby]\\$\\d{2}\\$.*")) {
                u.setPasswordHash(passwordEncoder.encode(ph));
                changed = true;
            }

            // Ensure roles relation is populated based on Users.role
            if (u.getRoles() == null || u.getRoles().isEmpty()) {
                Role r = roleRepo.findByRoleName(u.getRole()).orElse(null);
                if (r != null) {
                    u.getRoles().add(r);
                    changed = true;
                }
            }
        }

        if (changed) {
            userRepo.saveAll(users);
        }
    }
}