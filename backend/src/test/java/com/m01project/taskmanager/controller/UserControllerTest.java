// UserControllerTest.java
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
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

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
        User user = new User(1L, dto.email(), dto.password(), dto.firstName(), dto.lastName(), dto.phoneNumber(), LocalDateTime.now());

        when(userService.createUser(dto)).thenReturn(
                new com.m01project.taskmanager.demo.dto.UserResponseDto(
                        user.getEmail(), user.getFirstName(), user.getLastName(), user.getPhoneNumber(),
                        user.getCreatedAt()));

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("new@gmail.com"));
    }

    @Test
    void testUpdateUser() throws Exception {
        UserRequestDto dto = new UserRequestDto("updated@gmail.com", "newpass", "Updated", "User", "5559999");
        User updated = new User(1L, dto.email(), dto.password(), dto.firstName(), dto.lastName(), dto.phoneNumber(), LocalDateTime.now());

        when(userService.getUserByEmail("old@gmail.com")).thenReturn(Optional.of(updated));
        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(updated);

        mockMvc.perform(put("/api/users")
                .param("email", "old@gmail.com")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updated@gmail.com"));
    }

    @Test
    void testDeleteUser() throws Exception {
        User user = new User(1L, "delete@gmail.com", "123", "Del", "User", "555", LocalDateTime.now());
        when(userService.getUserByEmail("delete@gmail.com")).thenReturn(Optional.of(user));
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users")
                .param("email", "delete@gmail.com"))
                .andExpect(status().isNoContent());
    }
}
