package com.m01project.taskmanager.demo.service.impl;

import com.m01project.taskmanager.demo.dto.UserRequestDto;
import com.m01project.taskmanager.demo.dto.UserResponseDto;
import com.m01project.taskmanager.demo.entity.User;
import com.m01project.taskmanager.demo.exception.EmailAlreadyExistsException;
import com.m01project.taskmanager.demo.exception.UserNotFoundException;
import com.m01project.taskmanager.demo.repository.UserRepository;
import com.m01project.taskmanager.demo.service.UserService;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserResponseDto createUser(UserRequestDto dto) {
        if (userRepository.existsByEmail(dto.email())) {
            throw new EmailAlreadyExistsException("Email already in use " + dto.email());
        }

        User user = User.builder()
                .email(dto.email())
                .password(dto.password())
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .phoneNumber(dto.phoneNumber())
                .createdAt(LocalDateTime.now())
                .build();

        User createdUser = userRepository.save(user);

        return new UserResponseDto(
                createdUser.getEmail(),
                createdUser.getFirstName(),
                createdUser.getLastName(),
                createdUser.getPhoneNumber(),
                createdUser.getCreatedAt());
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public UserResponseDto updateUserByEmail(String email, UserRequestDto dto) {
        User existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        existingUser.setEmail(dto.email());
        existingUser.setPassword(dto.password());
        existingUser.setFirstName(dto.firstName());
        existingUser.setLastName(dto.lastName());
        existingUser.setPhoneNumber(dto.phoneNumber());

        User updatedUser = userRepository.save(existingUser);

        return new UserResponseDto(
                updatedUser.getEmail(),
                updatedUser.getFirstName(),
                updatedUser.getLastName(),
                updatedUser.getPhoneNumber(),
                updatedUser.getCreatedAt());
    }

    @Override
    public void deleteUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
        userRepository.deleteById(user.getId());
    }
}
