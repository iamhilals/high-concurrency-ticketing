package com.ticketing.controller;

import com.ticketing.dto.AuthResponse;
import com.ticketing.dto.RegisterRequest;
import com.ticketing.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/{userId}")
    public ResponseEntity<AuthResponse> updateProfile(@PathVariable Long userId, @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = userService.updateUserProfile(userId, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.ok(AuthResponse.builder().success(false).message(e.getMessage()).build());
        }
    }
}
