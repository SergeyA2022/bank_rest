package com.example.bankcards.controller;


import com.example.bankcards.dto.CardResponseDTO;
import com.example.bankcards.dto.TransferDTO;
import com.example.bankcards.enums.CardStatus;
import com.example.bankcards.entity.User;
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

    @GetMapping("/cards")
    public Page<CardResponseDTO> getMyCards(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) CardStatus status,
            Pageable pageable) {

        return cardService.getMyCards(user.getId(), status, pageable);
    }

    @PatchMapping("/cards/{id}/block")
    @ResponseStatus(HttpStatus.OK)
    public void blockMyCard(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        cardService.blockMyCard(id, currentUser.getId());
    }

    @PostMapping("/transfer")
    @ResponseStatus(HttpStatus.OK)
    public void transfer(@RequestBody TransferDTO transferDTO, @AuthenticationPrincipal User user) {
        cardService.transferBetweenOwnCards(
                transferDTO.getFromCardId(),
                transferDTO.getToCardId(),
                transferDTO.getAmount(),
                user
        );
    }

    @GetMapping("/{cardId}/balance")
    public CardResponseDTO getBalance(
            @PathVariable Long cardId,
            @AuthenticationPrincipal User user) {

        return cardService.getCardBalance(cardId, user.getId());
    }
}
