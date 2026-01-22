package com.travelbuddy.controller;

import com.travelbuddy.dto.UserDTO;
import com.travelbuddy.model.User;
import com.travelbuddy.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ---------------- GET ALL USERS ----------------
    @GetMapping
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    // ---------------- GET USER BY ID ----------------
    @GetMapping("/{id}")
    public UserDTO getUserById(@PathVariable Long id) {
        return toDTO(userService.getUserById(id));
    }

    // ---------------- CREATE USER (SIGNUP) ----------------
    @PostMapping
    public UserDTO createUser(@RequestBody User user) {
        return toDTO(userService.createUser(user));
    }

    // ---------------- UPDATE USER ----------------
    @PutMapping("/{id}")
    public UserDTO updateUser(@PathVariable Long id,
                              @RequestBody User updatedUser) {
        return toDTO(userService.updateUser(id, updatedUser));
    }

    // ---------------- DELETE USER ----------------
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
    }

    // ---------------- RESET PASSWORD ----------------
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> body) {

        String email = body.get("email");
        String newPassword = body.get("password");

        userService.resetPassword(email, newPassword);

        return ResponseEntity.ok(
                Map.of("message", "Password reset successful")
        );
    }

    // ---------------- DTO MAPPER ----------------
    private UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setProfilePicUrl(user.getProfilePicUrl());
        return dto;
    }
}
