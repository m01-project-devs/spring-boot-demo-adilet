package com.m01project.taskmanager.demo.service;

import com.m01project.taskmanager.demo.dto.UserRequestDto;
import com.m01project.taskmanager.demo.dto.UserResponseDto;
import com.m01project.taskmanager.demo.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserResponseDto createUser(UserRequestDto userRequestDto);
    Optional<User> getUserByEmail(String email);
    List<User> getAllUsers();
    UserResponseDto updateUserByEmail(String email, UserRequestDto dto);
    void deleteUserByEmail(String email);
    Page<UserResponseDto> getUsersPaged(Pageable pageable);
}
