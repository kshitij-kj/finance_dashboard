package com.finance.dashboard.controller;

import com.finance.dashboard.dto.request.RoleUpdateRequest;
import com.finance.dashboard.dto.response.UserResponse;
import com.finance.dashboard.entity.User;
import com.finance.dashboard.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // GET /api/users — ADMIN only (guard is in the service)
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // GET /api/users/me — any authenticated user can see their own profile
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyProfile(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(userService.getById(currentUser.getId()));
    }

    // PATCH /api/users/{id}/role — ADMIN only
    @PatchMapping("/{id}/role")
    public ResponseEntity<UserResponse> updateRole(
            @PathVariable UUID id,
            @Valid @RequestBody RoleUpdateRequest request) {
        return ResponseEntity.ok(userService.updateRole(id, request.role()));
    }

    // PATCH /api/users/{id}/status — ADMIN only (toggles active/inactive)
    @PatchMapping("/{id}/status")
    public ResponseEntity<UserResponse> toggleStatus(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.toggleStatus(id));
    }
}
