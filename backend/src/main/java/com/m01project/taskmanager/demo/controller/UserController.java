package com.m01project.taskmanager.demo.controller;

import com.m01project.taskmanager.demo.dto.UserRequestDto;
import com.m01project.taskmanager.demo.dto.UserResponseDto;
import com.m01project.taskmanager.demo.entity.User;
import com.m01project.taskmanager.demo.service.UserService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
@Validated
public class UserController {

    private final UserService userService;

    // GET user by email using RequestParam
    @GetMapping
    public ResponseEntity<UserResponseDto> getUser(@RequestParam @NotBlank @Email String email) {
        return userService.getUserByEmail(email)
                .map(user -> ResponseEntity.ok(new UserResponseDto(
                        user.getEmail(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getPhoneNumber(),
                        user.getCreatedAt())))
                .orElseThrow(() -> new com.m01project.taskmanager.demo.exception.UserNotFoundException(
                        "User not found with email: " + email));
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
                        u.getCreatedAt()))
                .toList();
        return ResponseEntity.ok(users);
    }

    // POST create user
    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserRequestDto dto) {
        UserResponseDto created = userService.createUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // PUT update user by email
    @PutMapping
    public ResponseEntity<UserResponseDto> updateUser(@RequestParam @NotBlank @Email String email,
            @RequestBody @Valid UserRequestDto dto) {
        UserResponseDto updated = userService.updateUserByEmail(email, dto);
        return ResponseEntity.ok(updated);
    }

    // DELETE user by email
    @DeleteMapping
    public ResponseEntity<Void> deleteUser(@RequestParam @NotBlank @Email String email) {
        userService.deleteUserByEmail(email);
        return ResponseEntity.noContent().build();
    }

    // Pagination
    @GetMapping("/paged")
    public ResponseEntity<Page<UserResponseDto>> getUsersPaged(Pageable pageable) {
        Page<User> usersPage = userService.getAllUsers(pageable);

        Page<UserResponseDto> dtoPage = usersPage.map(u -> new UserResponseDto(
                u.getEmail(),
                u.getFirstName(),
                u.getLastName(),
                u.getPhoneNumber(),
                u.getCreatedAt()));

        return ResponseEntity.ok(dtoPage);
    }
}
