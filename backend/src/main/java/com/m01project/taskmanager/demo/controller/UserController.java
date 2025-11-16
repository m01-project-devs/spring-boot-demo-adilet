package com.m01project.taskmanager.demo.controller;

import com.m01project.taskmanager.demo.dto.UserResponseDto;
import com.m01project.taskmanager.demo.service.UserService;
import com.m01project.taskmanager.demo.dto.UserRequestDto;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/{email}")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable String email) {
        return userService.getUserByEmail(email)
                .map(user -> ResponseEntity.ok(new UserResponseDto(user.getEmail(), user.getCreatedAt())))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@RequestBody UserRequestDto userRequestDto) {
        return ResponseEntity.ok(userService.createUser(userRequestDto));
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
}