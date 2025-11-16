package com.m01project.taskmanager;

import com.m01project.taskmanager.demo.dto.UserRequestDto;
import com.m01project.taskmanager.demo.dto.UserResponseDto;
import com.m01project.taskmanager.demo.entity.User;
import com.m01project.taskmanager.demo.repository.UserRepository;
import com.m01project.taskmanager.demo.service.UserService;
import com.m01project.taskmanager.demo.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void testCreateUser() {
        UserRequestDto requestDto = new UserRequestDto("test@example.com", "1234");
        User savedUser = new User();
        savedUser.setEmail(requestDto.email());
        savedUser.setPassword(requestDto.password());

        when(userRepository.existsByEmail(requestDto.email())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponseDto response = userService.createUser(requestDto);

        assertNotNull(response);
        assertEquals("test@example.com", response.email());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testCreateUserWithExistingEmail() {
        UserRequestDto requestDto = new UserRequestDto("exist@example.com", "1234");
        when(userRepository.existsByEmail(requestDto.email())).thenReturn(true);

        assertThrows(RuntimeException.class, () -> userService.createUser(requestDto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testGetUserByEmail() {
        User user = new User();
        user.setEmail("user@example.com");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        Optional<User> found = userService.getUserByEmail("user@example.com");

        assertTrue(found.isPresent());
        assertEquals("user@example.com", found.get().getEmail());
        verify(userRepository, times(1)).findByEmail("user@example.com");
    }

    @Test
    void testGetAllUsers() {
        List<User> users = List.of(
                new User(1L, "a@example.com", "123", null),
                new User(2L, "b@example.com", "456", null)
        );
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertEquals(2, result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testUpdateUser() {
        User existing = new User();
        existing.setEmail("old@example.com");
        existing.setPassword("111");

        User updated = new User();
        updated.setEmail("new@example.com");
        updated.setPassword("222");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.save(existing)).thenReturn(updated);

        User result = userService.updateUser(1L, updated);

        assertEquals("new@example.com", result.getEmail());
        assertEquals("222", result.getPassword());
        verify(userRepository, times(1)).save(existing);
    }

    @Test
    void testDeleteUser() {
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }
}
