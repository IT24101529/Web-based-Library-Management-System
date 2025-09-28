package com.library.app.controller;

import com.library.app.entity.User;
import com.library.app.service.UserService;
import com.library.app.service.ReportService;
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
    private final ReportService reportService;

    // Main admin dashboard
    @GetMapping
    public String adminDashboard() {
        return "admin/dashboard"; // We will create this new dashboard page
    }

    @GetMapping("/reports")
    public String showReports(Model model) {
        model.addAttribute("reportData", reportService.generateAdminDashboardReport());
        return "admin/reports";
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
        try {
            userService.saveUserByAdmin(user);
            ra.addFlashAttribute("successMessage", "User saved successfully!");
            return "redirect:/admin/users";
        } catch (IllegalArgumentException e) {
            // If an error is thrown (like a duplicate username), handle it here
            ra.addFlashAttribute("errorMessage", e.getMessage());
            ra.addFlashAttribute("user", user); // Send the user's data back to the form

            // Redirect back to the correct form (add or edit)
            if (user.getUserId() == null || user.getUserId() == 0) {
                return "redirect:/admin/users/add";
            } else {
                return "redirect:/admin/users/edit/" + user.getUserId();
            }
        }
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