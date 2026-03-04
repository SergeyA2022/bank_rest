package com.example.bankcards.controller;

import com.example.bankcards.entity.User;
import com.example.bankcards.enums.Role;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void register_ShouldEncodePasswordAndSaveUser() throws Exception {
        User user = new User();
        user.setUsername("new_user");
        user.setPassword("raw_password");

        when(passwordEncoder.encode("raw_password")).thenReturn("encoded_password");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());

        verify(userRepository).save(argThat(u -> u.getPassword().equals("encoded_password")));
    }

    @Test
    void login_ShouldReturnToken_WhenCredentialsAreValid() throws Exception {
        User user = new User();
        user.setUsername("test_user");
        user.setPassword("encoded_password");
        user.setRole(Role.USER);

        Map<String, String> loginRequest = Map.of(
                "username", "test_user",
                "password", "raw_password"
        );

        when(userRepository.findByUsername("test_user")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("raw_password", "encoded_password")).thenReturn(true);
        when(jwtUtils.generateToken(anyString(), anyString())).thenReturn("test_jwt_token");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test_jwt_token"));
    }

    @Test
    void login_InvalidPassword_ShouldThrowException() throws Exception {
        User user = new User();
        user.setUsername("test_user");
        user.setPassword("encoded_password");

        Map<String, String> loginRequest = Map.of(
                "username", "test_user",
                "password", "_password"
        );

        when(userRepository.findByUsername("test_user")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), eq("encoded_password"))).thenReturn(false);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }
}
