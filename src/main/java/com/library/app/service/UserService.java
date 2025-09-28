package com.library.app.service;

import com.library.app.dto.RegisterRequest;
import com.library.app.entity.Role;
import com.library.app.entity.User;
import com.library.app.repository.RoleRepository;
import com.library.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
        u.setRole("END_USER");
        u.setStatus("ACTIVE");

        User savedUser = userRepo.save(u);
        userRepo.linkUserToRole(savedUser.getUserId(), endUserRole.getRoleId());

        return savedUser;
    }

    // === ADMIN CRUD METHODS ===

    public List<User> findAllUsers() {
        return userRepo.findAll();
    }

    public Optional<User> findUserById(int id) {
        return userRepo.findById(id);
    }

    @Transactional
    public void saveUserByAdmin(User user) {
        boolean isNewUser = user.getUserId() == null || user.getUserId() == 0;

        if (isNewUser) {
            user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
            User savedUser = userRepo.save(user);
            Role role = roleRepo.findByRoleName(user.getRole()).orElseThrow();
            userRepo.linkUserToRole(savedUser.getUserId(), role.getRoleId());
        } else {
            User existingUser = userRepo.findById(user.getUserId()).orElseThrow();
            if (user.getPasswordHash() != null && !user.getPasswordHash().isEmpty()) {
                existingUser.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
            }
            existingUser.setUsername(user.getUsername());
            existingUser.setFullName(user.getFullName());
            existingUser.setEmail(user.getEmail());
            existingUser.setPhone(user.getPhone());
            existingUser.setRole(user.getRole());
            existingUser.setStatus(user.getStatus());

            userRepo.update(existingUser);

            userRepo.clearUserRoles(existingUser.getUserId());
            Role role = roleRepo.findByRoleName(user.getRole()).orElseThrow();
            userRepo.linkUserToRole(existingUser.getUserId(), role.getRoleId());
        }
    }

    @Transactional
    public void deactivateUser(int id) {
        User user = userRepo.findById(id).orElseThrow();
        user.setStatus("INACTIVE");
        userRepo.update(user);
    }
}