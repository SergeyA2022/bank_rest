package com.example.bankcards.controller;


import com.example.bankcards.dto.CardResponseDTO;
import com.example.bankcards.dto.TransferDTO;
import com.example.bankcards.entity.User;
import com.example.bankcards.enums.CardStatus;
import com.example.bankcards.exception.CardOperationException;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final CardService cardService;
    private final UserRepository userRepository;

    @GetMapping("/cards")
    public Page<CardResponseDTO> getMyCards(
            @AuthenticationPrincipal String username,
            @RequestParam(required = false) CardStatus status,
            Pageable pageable) {

        return cardService.getMyCards(getUser(username).getId(), status, pageable);
    }

    @PatchMapping("/cards/{id}/block")
    @ResponseStatus(HttpStatus.OK)
    public void blockMyCard(@PathVariable Long id, @AuthenticationPrincipal String username) {
        cardService.blockMyCard(id, getUser(username).getId());
    }

    @PostMapping("/transfer")
    @ResponseStatus(HttpStatus.OK)
    public void transfer(@RequestBody TransferDTO transferDTO, @AuthenticationPrincipal String username) {

        cardService.transferBetweenOwnCards(
                transferDTO.getFromCardId(),
                transferDTO.getToCardId(),
                transferDTO.getAmount(),
                getUser(username)
        );
    }

    @GetMapping("/{cardId}/balance")
    public CardResponseDTO getBalance(
            @PathVariable Long cardId,
            @AuthenticationPrincipal String username) {

        return cardService.getCardBalance(cardId, getUser(username).getId());
    }

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new CardOperationException("User not found"));
    }
}
