package com.library.app.service;

import com.library.app.dto.RegisterRequest;
import com.library.app.entity.Role;
import com.library.app.entity.User;
import com.library.app.repository.RoleRepository;
import com.library.app.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User registerEndUser(RegisterRequest req) {
        if (userRepo.existsByUsernameIgnoreCase(req.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepo.existsByEmailIgnoreCase(req.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        if (!req.getPassword().equals(req.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        Role endUserRole = roleRepo.findByRoleName("END_USER")
                .orElseThrow(() -> new IllegalStateException("END_USER role not present"));

        User u = new User();
        u.setUsername(req.getUsername().trim());
        u.setFullName(req.getFullName().trim());
        u.setEmail(req.getEmail().trim());
        u.setPhone(req.getPhone().trim());
        u.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        u.setRole("END_USER"); // keep mirrored column up to date
        u.getRoles().add(endUserRole);
        u.setStatus("ACTIVE");

        return userRepo.save(u);
    }
}