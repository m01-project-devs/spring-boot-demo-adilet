package com.m01project.taskmanager.controller;

import com.m01project.taskmanager.demo.controller.UserController;
import com.m01project.taskmanager.demo.dto.UserRequestDto;
import com.m01project.taskmanager.demo.dto.UserResponseDto;
import com.m01project.taskmanager.demo.entity.User;
import com.m01project.taskmanager.demo.exception.EmailAlreadyExistsException;
import com.m01project.taskmanager.demo.exception.GlobalExceptionHandler;
import com.m01project.taskmanager.demo.exception.UserNotFoundException;
import com.m01project.taskmanager.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(GlobalExceptionHandler.class)
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

         /* ----------------------------------------FAIL TEST------------------------------------------ */

        @Test
        void testCreateUser_InvalidEmail_ShouldFail() throws Exception {
                UserRequestDto dto = new UserRequestDto("invalid-email", "pass123", "New", "User", "5552222");

                mockMvc.perform(post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void testCreateUser_BlankFields_ShouldFail() throws Exception {
                UserRequestDto dto = new UserRequestDto("", "", "", "", "");

                mockMvc.perform(post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void testCreateUser_InvalidPhone_ShouldFail() throws Exception {
                UserRequestDto dto = new UserRequestDto("test@gmail.com", "pass123", "New", "User", "abc");

                mockMvc.perform(post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void testUpdateUser_InvalidEmailParam_ShouldFail() throws Exception {
                UserRequestDto dto = new UserRequestDto("valid@gmail.com", "pass123", "New", "User", "5552222");

                mockMvc.perform(put("/api/users")
                                .param("email", "bad-email")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void testUpdateUser_InvalidBody_ShouldFail() throws Exception {
                UserRequestDto dto = new UserRequestDto("bad", "12", "", "", "notdigits");

                mockMvc.perform(put("/api/users")
                                .param("email", "old@gmail.com")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void testDeleteUser_InvalidEmailParam_ShouldFail() throws Exception {
                mockMvc.perform(delete("/api/users")
                                .param("email", "wrong-email"))
                                .andExpect(status().isBadRequest());
        }
        

        @Test
        void testCreateUser_EmailAlreadyExists() throws Exception {
                UserRequestDto dto = new UserRequestDto("existing@gmail.com", "pass123", "Exist", "User", "5552222");

                when(userService.createUser(dto)).thenThrow(new EmailAlreadyExistsException("Email already in use"));

                mockMvc.perform(post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                                .andExpect(status().isConflict())
                                .andExpect(jsonPath("$.message").value("Email already in use"));
        }

        @Test
        void testUpdateUser_NotFound() throws Exception {
                UserRequestDto dto = new UserRequestDto("notfound@gmail.com", "pass", "No", "User", "5551111");

                when(userService.updateUserByEmail("wrong@gmail.com", dto))
                                .thenThrow(new UserNotFoundException("User not found"));

                mockMvc.perform(put("/api/users")
                                .param("email", "wrong@gmail.com")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.message").value("User not found"));
        }

        @Test
        void testDeleteUser_NotFound() throws Exception {
                doThrow(new UserNotFoundException("User not found"))
                                .when(userService).deleteUserByEmail("wrong@gmail.com");

                mockMvc.perform(delete("/api/users")
                                .param("email", "wrong@gmail.com"))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.message").value("User not found"));
        }

        @Test
        void testGetUserByEmail() throws Exception {
                UserResponseDto responseDto = new UserResponseDto("get@gmail.com", "Get", "User", "5553333",
                                LocalDateTime.now());
                when(userService.getUserByEmail("get@gmail.com")).thenReturn(Optional.of(
                                new User(1L, responseDto.email(), "pass", responseDto.firstName(),
                                                responseDto.lastName(),
                                                responseDto.phoneNumber(), LocalDateTime.now())));

                mockMvc.perform(get("/api/users")
                                .param("email", "get@gmail.com"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.email").value("get@gmail.com"))
                                .andExpect(jsonPath("$.firstName").value("Get"));
        }

        @Test
        void testGetUserByEmail_NotFound() throws Exception {
                when(userService.getUserByEmail("notfound@gmail.com")).thenReturn(Optional.empty());

                mockMvc.perform(get("/api/users")
                                .param("email", "notfound@gmail.com"))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.message")
                                                .value("User not found with email: notfound@gmail.com"));
        }

        @Test
        void testGetAllUsers() throws Exception {
                when(userService.getAllUsers()).thenReturn(List.of(
                                new User(1L, "a@gmail.com", "pass", "A", "Alpha", "111", LocalDateTime.now()),
                                new User(2L, "b@gmail.com", "pass", "B", "Beta", "222", LocalDateTime.now())));

                mockMvc.perform(get("/api/users/all"))
                                .andExpect(status().isOk());
        }

}
