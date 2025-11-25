package com.m01project.taskmanager.service;

import com.m01project.taskmanager.demo.dto.UserRequestDto;
import com.m01project.taskmanager.demo.dto.UserResponseDto;
import com.m01project.taskmanager.demo.entity.User;
import com.m01project.taskmanager.demo.exception.EmailAlreadyExistsException;
import com.m01project.taskmanager.demo.exception.UserNotFoundException;
import com.m01project.taskmanager.demo.repository.UserRepository;
import com.m01project.taskmanager.demo.service.UserService;
import com.m01project.taskmanager.demo.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        UserRequestDto dto = new UserRequestDto("aiba@gmail.com", "1234", "Aibek", "Shermatov", "5551234");
        User user = new User(1L, dto.email(), dto.password(), dto.firstName(), dto.lastName(), dto.phoneNumber(),
                LocalDateTime.now());

        when(userRepository.existsByEmail(dto.email())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        var created = userService.createUser(dto);

        assertNotNull(created);
        assertEquals("aiba@gmail.com", created.email());
        assertEquals("Aibek", created.firstName());
        assertEquals("Shermatov", created.lastName());
        assertEquals("5551234", created.phoneNumber());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testCreateUserWithExistingEmail() {
        UserRequestDto dto = new UserRequestDto("aiba@gmail.com", "1234", "Aibek", "Shermatov", "5551234");
        when(userRepository.existsByEmail(dto.email())).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> userService.createUser(dto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testGetUserByEmail() {
        User user = new User(1L, "user@gmail.com", "pass", "Tima", "Varol", "5555678", LocalDateTime.now());
        when(userRepository.findByEmail("user@gmail.com")).thenReturn(Optional.of(user));

        Optional<User> found = userService.getUserByEmail("user@gmail.com");

        assertTrue(found.isPresent());
        assertEquals("user@gmail.com", found.get().getEmail());
        assertEquals("Tima", found.get().getFirstName());
        verify(userRepository, times(1)).findByEmail("user@gmail.com");
    }

    @Test
    void testGetAllUsers() {
        List<User> users = List.of(
                new User(1L, "a@gmail.com", "123", "A", "Alpha", "111", LocalDateTime.now()),
                new User(2L, "b@gmail.com", "456", "B", "Beta", "222", LocalDateTime.now()));
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertEquals(2, result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testUpdateUserByEmail() {
        UserRequestDto dto = new UserRequestDto("new@gmail.com", "pass222", "New", "User", "999999");
        User existing = new User(1L, "old@gmail.com", "pass111", "Old", "User", "555555", LocalDateTime.now());
        User updated = new User(1L, dto.email(), dto.password(), dto.firstName(), dto.lastName(), dto.phoneNumber(),
                existing.getCreatedAt());

        when(userRepository.findByEmail("old@gmail.com")).thenReturn(Optional.of(existing));
        when(userRepository.save(existing)).thenReturn(updated);

        UserResponseDto result = userService.updateUserByEmail("old@gmail.com", dto);

        assertEquals("new@gmail.com", result.email());
        assertEquals("New", result.firstName());
        assertEquals("User", result.lastName());
        assertEquals("999999", result.phoneNumber());
        verify(userRepository, times(1)).save(existing);
    }

    @Test
    void testDeleteUserByEmail() {
        User existing = new User(1L, "delete@gmail.com", "pass", "Del", "User", "555", LocalDateTime.now());
        when(userRepository.findByEmail("delete@gmail.com")).thenReturn(Optional.of(existing));
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUserByEmail("delete@gmail.com");

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void testGetUserByEmailNotFound() {
        when(userRepository.findByEmail("notfound@gmail.com")).thenReturn(Optional.empty());

        Optional<User> found = userService.getUserByEmail("notfound@gmail.com");
        assertTrue(found.isEmpty());
        verify(userRepository, times(1)).findByEmail("notfound@gmail.com");
    }

    @Test
    void testUpdateUserByEmailNotFound() {
        UserRequestDto dto = new UserRequestDto(
                "new@gmail.com", "pass222", "New", "User", "999999");
        when(userRepository.findByEmail("notfound@gmail.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUserByEmail("notfound@gmail.com", dto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testDeleteUserByEmailNotFound() {
        when(userRepository.findByEmail("notfound@gmail.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.deleteUserByEmail("notfound@gmail.com"));
        verify(userRepository, never()).deleteById(anyLong());
    }
}
