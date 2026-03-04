package com.example.bankcards.controller;

import com.example.bankcards.dto.CardResponseDTO;
import com.example.bankcards.enums.CardStatus;
import com.example.bankcards.enums.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final CardService cardService;
    private final UserRepository userRepository;

    @GetMapping("/cards")
    public Page<CardResponseDTO> getAllCards(
            @RequestParam(required = false) CardStatus status,
            @PageableDefault(size = 5, sort = "id") Pageable pageable) {
        return cardService.getAllCards(status, pageable);
    }

    @PatchMapping("/cards/{id}/block")
    public void blockCard(@PathVariable Long id) {
        cardService.updateCardStatus(id, CardStatus.BLOCKED);
    }

    @PatchMapping("/cards/{id}/activate")
    public void activateCard(@PathVariable Long id) {
        cardService.updateCardStatus(id, CardStatus.ACTIVE);
    }

    @DeleteMapping("/cards/{id}")
    public void deleteCard(@PathVariable Long id) {
        cardService.deleteCard(id);
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
    }

    @PatchMapping("/users/{id}/role")
    public void changeRole(@PathVariable Long id, @RequestParam Role newRole) {
        User user = userRepository.findById(id).orElseThrow();
        user.setRole(newRole);
        userRepository.save(user);
    }

    @PostMapping("/cards/{userId}")
    public CardResponseDTO createCard(@PathVariable Long userId) {
        return cardService.createCard(userId);
    }
}
