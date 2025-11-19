package com.m01project.taskmanager.demo.dto;

import java.time.LocalDateTime;

public record UserResponseDto(String email, LocalDateTime createdAt) {
} 
