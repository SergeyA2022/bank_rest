package com.example.bankcards.controller;

import com.example.bankcards.dto.CardResponseDTO;
import com.example.bankcards.dto.TransferDTO;
import com.example.bankcards.entity.User;
import com.example.bankcards.enums.Role;
import com.example.bankcards.security.JwtUtils;
import com.example.bankcards.service.CardServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CardServiceImpl cardService;

    @MockBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserDetailsService userDetailsService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("test_user");
        testUser.setRole(Role.USER);

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(new ObjectMapper()
                .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule()));

        mockMvc = MockMvcBuilders.standaloneSetup(new UserController(cardService))
                .setCustomArgumentResolvers(
                        new HandlerMethodArgumentResolver() {
                            @Override
                            public boolean supportsParameter(MethodParameter p) {
                                return p.hasParameterAnnotation(AuthenticationPrincipal.class);
                            }

                            @Override
                            public Object resolveArgument(MethodParameter p, ModelAndViewContainer m,
                                                          NativeWebRequest w, WebDataBinderFactory f) {
                                return testUser;
                            }
                        },
                        new PageableHandlerMethodArgumentResolver()
                )
                .setMessageConverters(converter)
                .build();
    }

    @Test
    void getMyCards_ShouldReturnPage() throws Exception {
        CardResponseDTO dto = new CardResponseDTO();
        dto.setMaskedNumber("**** 1234");
        List<CardResponseDTO> content = new ArrayList<>();
        content.add(dto);
        Page<CardResponseDTO> page = new PageImpl<>(content, PageRequest.of(0, 5), 1);

        when(cardService.getMyCards(eq(testUser.getId()), any(), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/user/cards")
                        .with(authentication(new UsernamePasswordAuthenticationToken(testUser, null)))
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].maskedNumber").value("**** 1234"));
    }

    @Test
    void blockMyCard_ShouldReturnSuccessMessage() throws Exception {
        Long cardId = 1L;
        doNothing().when(cardService).blockMyCard(eq(cardId), eq(testUser.getId()));

        mockMvc.perform(patch("/api/user/cards/{id}/block", cardId))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
        verify(cardService, times(1)).blockMyCard(eq(cardId), eq(testUser.getId()));
    }

    @Test
    void transfer_ShouldReturnSuccess() throws Exception {
        TransferDTO dto = new TransferDTO();
        dto.setFromCardId(1L);
        dto.setToCardId(2L);
        dto.setAmount(new BigDecimal("100.00"));
        doNothing().when(cardService).transferBetweenOwnCards(
                eq(1L), eq(2L), any(BigDecimal.class), any(User.class)
        );

        mockMvc.perform(post("/api/user/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(cardService, times(1)).transferBetweenOwnCards(
                eq(1L), eq(2L), any(BigDecimal.class), any(User.class)
        );
    }

    @Test
    void getBalance_ShouldReturnCardDto() throws Exception {
        CardResponseDTO response = new CardResponseDTO();
        response.setBalance(new BigDecimal("500.0"));
        when(cardService.getCardBalance(anyLong(), any())).thenReturn(response);

        mockMvc.perform(get("/api/user/1/balance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(500.0));
    }
}
