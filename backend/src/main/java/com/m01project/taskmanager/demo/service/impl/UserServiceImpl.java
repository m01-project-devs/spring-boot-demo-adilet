package com.m01project.taskmanager.demo.service.impl;

import com.m01project.taskmanager.demo.dto.UserRequestDto;
import com.m01project.taskmanager.demo.dto.UserResponseDto;
import com.m01project.taskmanager.demo.entity.User;
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
        throw new RuntimeException("Email already in use");
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
            createdUser.getCreatedAt()
    );
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
    public User updateUser(Long id, User user) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    existingUser.setEmail(user.getEmail());
                    existingUser.setPassword(user.getPassword());
                    existingUser.setFirstName(user.getFirstName());
                    existingUser.setLastName(user.getLastName());
                    existingUser.setPhoneNumber(user.getPhoneNumber());
                    return userRepository.save(existingUser);
                })
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
