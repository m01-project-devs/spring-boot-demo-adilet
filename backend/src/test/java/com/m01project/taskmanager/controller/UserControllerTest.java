package com.m01project.taskmanager.controller;

import com.m01project.taskmanager.demo.controller.UserController;
import com.m01project.taskmanager.demo.dto.UserRequestDto;
import com.m01project.taskmanager.demo.entity.User;
import com.m01project.taskmanager.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void testGetUser() throws Exception {
        User user = new User(1L, "adilet@gmail.com", "1234", "Adilet", "Dzhuraev", "5551111", LocalDateTime.now());
        when(userService.getUserByEmail("adilet@gmail.com")).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/users")
                .param("email", "adilet@gmail.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("adilet@gmail.com"))
                .andExpect(jsonPath("$.firstName").value("Adilet"))
                .andExpect(jsonPath("$.lastName").value("Dzhuraev"))
                .andExpect(jsonPath("$.phoneNumber").value("5551111"));
    }

    @Test
    void testGetUserNotFound() throws Exception {
        when(userService.getUserByEmail("noone@gmail.com")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users")
                .param("email", "noone@gmail.com"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateUser() throws Exception {
        UserRequestDto dto = new UserRequestDto("new@gmail.com", "pass123", "New", "User", "5552222");
        User user = new User(1L, dto.email(), dto.password(), dto.firstName(), dto.lastName(), dto.phoneNumber(),
                LocalDateTime.now());

        when(userService.createUser(dto)).thenReturn(
                new com.m01project.taskmanager.demo.dto.UserResponseDto(
                        user.getEmail(), user.getFirstName(), user.getLastName(), user.getPhoneNumber(),
                        user.getCreatedAt()));

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("new@gmail.com"))
                .andExpect(jsonPath("$.firstName").value("New"))
                .andExpect(jsonPath("$.lastName").value("User"))
                .andExpect(jsonPath("$.phoneNumber").value("5552222"));

    }
}
