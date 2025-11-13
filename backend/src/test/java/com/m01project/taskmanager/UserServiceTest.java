package com.m01project.taskmanager;

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
        User user = new User(1L, "test@example.com", "1234", null);
        when(userRepository.save(user)).thenReturn(user);

        User created = userService.createUser(user);

        assertNotNull(created);
        assertEquals("test@example.com", created.getEmail());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testCreateUserWithNullEmail() {
        User user = new User(1L, null, "1234", null);
        assertThrows(Exception.class, () -> userService.createUser(user));
    }

    @Test
    void testGetUserById() {
        User user = new User(1L, "user@example.com", "pass", null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> found = userService.getUserById(1L);

        assertTrue(found.isPresent());
        assertEquals("user@example.com", found.get().getEmail());
        verify(userRepository, times(1)).findById(1L);
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
        User existing = new User(1L, "old@example.com", "111", null);
        User updated = new User(1L, "new@example.com", "222", null);

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
