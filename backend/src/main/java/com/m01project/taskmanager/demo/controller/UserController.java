package com.m01project.taskmanager.demo.controller;

import com.m01project.taskmanager.demo.dto.UserRequestDto;
import com.m01project.taskmanager.demo.dto.UserResponseDto;
import com.m01project.taskmanager.demo.entity.User;
import com.m01project.taskmanager.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;


    // GET user by email using RequestParam
    @GetMapping
    public ResponseEntity<UserResponseDto> getUser(@RequestParam String email) {
        return userService.getUserByEmail(email)
                .map(user -> ResponseEntity.ok(new UserResponseDto(
                        user.getEmail(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getPhoneNumber(),
                        user.getCreatedAt()
                )))
                .orElse(ResponseEntity.notFound().build());
    }

    // GET all users
    @GetMapping("/all")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<UserResponseDto> users = userService.getAllUsers()
                .stream()
                .map(u -> new UserResponseDto(
                        u.getEmail(),
                        u.getFirstName(),
                        u.getLastName(),
                        u.getPhoneNumber(),
                        u.getCreatedAt()
                ))
                .toList();
        return ResponseEntity.ok(users);
    }

    // POST create user
    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@RequestBody UserRequestDto dto) {
        UserResponseDto created = userService.createUser(dto);
        return ResponseEntity.status(201).body(created);
    }

    // PUT update user by email
    @PutMapping
    public ResponseEntity<UserResponseDto> updateUser(@RequestParam String email, @RequestBody UserRequestDto dto) {
        User updatedUser = userService.getUserByEmail(email)
                .map(existing -> userService.updateUser(existing.getId(), new User(
                        existing.getId(),
                        dto.email(),
                        dto.password(),
                        dto.firstName(),
                        dto.lastName(),
                        dto.phoneNumber(),
                        existing.getCreatedAt()
                )))
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserResponseDto response = new UserResponseDto(
                updatedUser.getEmail(),
                updatedUser.getFirstName(),
                updatedUser.getLastName(),
                updatedUser.getPhoneNumber(),
                updatedUser.getCreatedAt()
        );

        return ResponseEntity.ok(response);
    }

    // DELETE user by email
    @DeleteMapping
    public ResponseEntity<Void> deleteUser(@RequestParam String email) {
        User user = userService.getUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userService.deleteUser(user.getId());
        return ResponseEntity.noContent().build();
    }
}
