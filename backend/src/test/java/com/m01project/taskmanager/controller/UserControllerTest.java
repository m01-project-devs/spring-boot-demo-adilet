package com.m01project.taskmanager.controller;

import com.m01project.taskmanager.demo.controller.UserController;
import com.m01project.taskmanager.demo.dto.UserRequestDto;
import com.m01project.taskmanager.demo.dto.UserResponseDto;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    void testCreateUser() throws Exception {
        UserRequestDto dto = new UserRequestDto("new@gmail.com", "pass123", "New", "User", "5552222");
        UserResponseDto responseDto = new UserResponseDto(
                dto.email(), dto.firstName(), dto.lastName(), dto.phoneNumber(), LocalDateTime.now());

        when(userService.createUser(dto)).thenReturn(responseDto);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("new@gmail.com"))
                .andExpect(jsonPath("$.firstName").value("New"))
                .andExpect(jsonPath("$.lastName").value("User"))
                .andExpect(jsonPath("$.phoneNumber").value("5552222"));
    }

    @Test
    void testUpdateUser() throws Exception {
        UserRequestDto dto = new UserRequestDto("updated@gmail.com", "newpass", "Updated", "User", "5559999");
        UserResponseDto responseDto = new UserResponseDto(
                dto.email(), dto.firstName(), dto.lastName(), dto.phoneNumber(), LocalDateTime.now());

        when(userService.updateUserByEmail("old@gmail.com", dto)).thenReturn(responseDto);

        mockMvc.perform(put("/api/users")
                .param("email", "old@gmail.com")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updated@gmail.com"))
                .andExpect(jsonPath("$.firstName").value("Updated"))
                .andExpect(jsonPath("$.lastName").value("User"))
                .andExpect(jsonPath("$.phoneNumber").value("5559999"));
    }

    @Test
    void testDeleteUser() throws Exception {
        doNothing().when(userService).deleteUserByEmail("delete@gmail.com");

        mockMvc.perform(delete("/api/users")
                .param("email", "delete@gmail.com"))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUserByEmail("delete@gmail.com");
    }
}
