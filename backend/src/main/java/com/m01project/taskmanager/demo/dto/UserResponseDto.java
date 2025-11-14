package com.m01project.taskmanager.demo.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class UserResponseDto {
    private Long id;
    private String email;
    private LocalDateTime createdAt;
}
