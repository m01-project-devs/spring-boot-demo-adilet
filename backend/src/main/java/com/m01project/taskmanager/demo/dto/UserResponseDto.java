package com.m01project.taskmanager.demo.dto;

import java.time.LocalDateTime;

public record UserResponseDto(String email, String firstName, String lastName, String phoneNumber, LocalDateTime createdAt) {
} 
