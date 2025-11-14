// package com.m01project.taskmanager;

// import com.m01project.taskmanager.demo.entity.User;
// import com.m01project.taskmanager.demo.service.UserService;
// import com.m01project.taskmanager.demo.controller.UserController;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.test.web.servlet.MockMvc;

// import java.util.Optional;

// import static org.mockito.Mockito.when;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// @WebMvcTest(UserController.class)
// class UserControllerTest {

//     @Autowired
//     private MockMvc mockMvc;

//     @MockBean
//     private UserService userService;

//     @Test
//     void testGetUser() throws Exception {
//         User user = new User(1L, "adilet@gmail.com", "1234", null);
//         when(userService.getUserById(1L)).thenReturn(Optional.of(user));

//         mockMvc.perform(get("/api/users/1"))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.id").value(1))
//                 .andExpect(jsonPath("$.email").value("adilet@gmail.com"));
//     }

//     @Test
//     void testGetUserNotFound() throws Exception {
//         when(userService.getUserById(99L)).thenReturn(Optional.empty());

//         mockMvc.perform(get("/api/users/99"))
//                 .andExpect(status().isNotFound());
//     }
// }
