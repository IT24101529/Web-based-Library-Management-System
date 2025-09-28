package com.library.app.controller;

import com.library.app.entity.User;
import com.library.app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    // Main admin dashboard
    @GetMapping
    public String adminDashboard() {
        return "admin/dashboard"; // We will create this new dashboard page
    }

    // == USER MANAGEMENT ==

    // Display list of users
    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.findAllUsers());
        return "admin/users-list";
    }

    // Show form to add a new user
    @GetMapping("/users/add")
    public String showAddUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("pageTitle", "Add New User");
        return "admin/user-form";
    }

    // Show form to edit an existing user
    @GetMapping("/users/edit/{id}")
    public String showEditUserForm(@PathVariable("id") int id, Model model, RedirectAttributes ra) {
        return userService.findUserById(id)
                .map(user -> {
                    model.addAttribute("user", user);
                    model.addAttribute("pageTitle", "Edit User (ID: " + id + ")");
                    return "admin/user-form";
                })
                .orElseGet(() -> {
                    ra.addFlashAttribute("errorMessage", "User not found with ID: " + id);
                    return "redirect:/admin/users";
                });
    }

    // Process the add/edit user form
    @PostMapping("/users/save")
    public String saveUser(User user, RedirectAttributes ra) {
        userService.saveUserByAdmin(user);
        ra.addFlashAttribute("successMessage", "User saved successfully!");
        return "redirect:/admin/users";
    }

    // Delete a user permanently
    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable("id") int id, RedirectAttributes ra) {
        userService.deleteUser(id);
        ra.addFlashAttribute("successMessage", "User deleted permanently!");
        return "redirect:/admin/users";
    }

    // Method for viewing user details
    @GetMapping("/users/view/{id}")
    public String viewUser(@PathVariable("id") int id, Model model, RedirectAttributes ra) {
        return userService.findUserById(id)
                .map(user -> {
                    model.addAttribute("user", user);
                    return "admin/user-details"; // Path to the new details HTML page
                })
                .orElseGet(() -> {
                    ra.addFlashAttribute("errorMessage", "User not found with ID: " + id);
                    return "redirect:/admin/users";
                });
    }
}