package com.example.bankcards.controller;

import com.example.bankcards.dto.CardResponseDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.enums.CardStatus;
import com.example.bankcards.enums.Role;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.CardServiceImpl;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CardServiceImpl cardService;

    @MockBean
    private UserRepository userRepository;

    private final CardMapper cardMapper = Mappers.getMapper(CardMapper.class);

    @Test
    @WithMockUser(roles = "USER")
    void access_Denied_For_User_Role() throws Exception {
        mockMvc.perform(get("/api/admin/cards"))
                .andExpect(status().isForbidden());
    }

    @Test
    void access_Denied_For_Anonymous() throws Exception {
        mockMvc.perform(get("/api/admin/cards"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllCards_ShouldReturnPage() throws Exception {
        CardResponseDTO dto = new CardResponseDTO();
        dto.setMaskedNumber("**** 4444");
        Page<CardResponseDTO> page = new PageImpl<>(List.of(dto));
        when(cardService.getAllCards(any(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/admin/cards")
                        .param("status", "ACTIVE")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].maskedNumber").value("**** 4444"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void blockCard_ShouldReturnSuccessMessage() throws Exception {
        Long cardId = 1L;

        mockMvc.perform(patch("/api/admin/cards/{id}/block", cardId))
                .andExpect(status().isOk());

        verify(cardService).updateCardStatus(cardId, CardStatus.BLOCKED);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteCard_ShouldCallService() throws Exception {
        Long cardId = 1L;

        mockMvc.perform(delete("/api/admin/cards/{id}", cardId))
                .andExpect(status().isOk());
        verify(cardService).deleteCard(cardId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void changeRole_ShouldUpdateUserInDb() throws Exception {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        mockMvc.perform(patch("/api/admin/users/{id}/role", userId)
                        .param("newRole", "ADMIN"))
                .andExpect(status().isOk());

        verify(userRepository).save(argThat(u -> u.getRole() == Role.ADMIN));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCard_ShouldReturnCardResponse() throws Exception {
        Long userId = 1L;
        CardResponseDTO response = new CardResponseDTO();
        response.setMaskedNumber("**** **** **** 4444");

        when(cardService.createCard(userId)).thenReturn(response);

        mockMvc.perform(post("/api/admin/cards/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.maskedNumber").value("**** **** **** 4444"));
    }

    @Test
    void cardResponse_Masking_Correctly() {
        Card card = new Card();
        card.setCardNumber("1234567812345678");
        card.setStatus(CardStatus.ACTIVE);
        card.setBalance(BigDecimal.ZERO);
        card.setExpiryDate(LocalDate.now().plusYears(1));

        CardResponseDTO response = cardMapper.toDto(card);
        assertEquals("**** **** **** 5678", response.getMaskedNumber());
    }
}
