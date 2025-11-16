package com.m01project.taskmanager;

import com.m01project.taskmanager.demo.dto.UserResponseDto;
import com.m01project.taskmanager.demo.service.UserService;
import com.m01project.taskmanager.demo.controller.UserController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void testGetUser() throws Exception {
        UserResponseDto responseDto = new UserResponseDto("adilet@gmail.com", LocalDateTime.now());
        when(userService.getUserByEmail("adilet@gmail.com")).thenReturn(Optional.of(new com.m01project.taskmanager.demo.entity.User(null, "adilet@gmail.com", null, responseDto.createdAt())));

        mockMvc.perform(get("/api/users/adilet@gmail.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("adilet@gmail.com"));
    }

    @Test
    void testGetUserNotFound() throws Exception {
        when(userService.getUserByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/nonexistent@example.com"))
                .andExpect(status().isNotFound());
    }
}
