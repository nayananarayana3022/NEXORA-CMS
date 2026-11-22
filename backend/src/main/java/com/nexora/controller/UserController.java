
package com.nexora.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nexora.dto.UserResponse;
import com.nexora.entity.User;
import com.nexora.service.UserService;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Register user
    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(
            @RequestBody User user) {

        User savedUser = userService.registerUser(user);

        UserResponse response = new UserResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getRole(),
                savedUser.getCreatedAt()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Login user and return JWT token
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(
            @RequestBody User user) {

        String token = userService.loginUser(
                user.getEmail(),
                user.getPassword()
        );

        Map<String, String> response = Map.of(
                "token", token,
                "tokenType", "Bearer"
        );

        return ResponseEntity.ok(response);
    }
    
    
}
