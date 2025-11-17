package com.m01project.taskmanager.controller;

import com.m01project.taskmanager.demo.controller.UserController;
import com.m01project.taskmanager.demo.dto.UserResponseDto;
import com.m01project.taskmanager.demo.entity.User;
import com.m01project.taskmanager.demo.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void testGetUser() throws Exception {
        LocalDateTime createdAt = LocalDateTime.now();
        User user = new User(null, "adilet@gmail.com", "1234", createdAt);
        when(userService.getUserByEmail("adilet@gmail.com")).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/users").param("email", "adilet@gmail.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("adilet@gmail.com"))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void testGetUserNotFound() throws Exception {
        when(userService.getUserByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users").param("email", "nonexistent@example.com"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllUsers() throws Exception {
        List<User> users = List.of(
                new User(1L, "a@example.com", "1234", LocalDateTime.now()),
                new User(2L, "b@example.com", "5678", LocalDateTime.now())
        );
        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/users/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
}
