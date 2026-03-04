package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.enums.CardStatus;
import com.example.bankcards.enums.Role;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
public class CardServiceIntegrationTest {

    @Autowired
    private CardService cardService;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private Card sender;
    private Card recipient;

    BigDecimal balance = new BigDecimal("3000.00");
    int threads = 200;
    BigDecimal transferAmount = new BigDecimal("7.00");

    @BeforeEach
    void setUp() {
        cardRepository.deleteAll();
        userRepository.deleteAll();

        testUser = userRepository.save(User.builder()
                .username("concurrency_user")
                .password("pass")
                .role(Role.USER)
                .build());

        sender = cardRepository.save(Card.builder()
                .cardNumber("1111222233334444")
                .balance(balance)
                .owner(testUser)
                .status(CardStatus.ACTIVE)
                .expiryDate(LocalDate.now().plusYears(1))
                .build());

        recipient = cardRepository.save(Card.builder()
                .cardNumber("5555666677778888")
                .balance(BigDecimal.ZERO)
                .owner(testUser)
                .status(CardStatus.ACTIVE)
                .expiryDate(LocalDate.now().plusYears(1))
                .build());


    }

    @Test
    void transfer_RaceCondition_ShouldHandleConcurrentTransfers() throws InterruptedException {
        Long fromId = sender.getId();
        Long toId = recipient.getId();

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threads);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);

        ExecutorService executor = Executors.newFixedThreadPool(threads);

        for (int i = 0; i < threads; i++) {
            executor.execute(() -> {
                try {
                    startLatch.await();
                    cardService.transferBetweenOwnCards(fromId, toId, transferAmount, testUser);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        doneLatch.await(10, TimeUnit.SECONDS);

        BigDecimal balanceSum = cardRepository.findById(toId).orElseThrow().getBalance()
                .add(cardRepository.findById(fromId).orElseThrow().getBalance());
        BigDecimal senderBalance = cardRepository.findById(fromId).orElseThrow().getBalance();
        BigDecimal recipientBalance = cardRepository.findById(toId).orElseThrow().getBalance();

        assertEquals(balance, balanceSum);
        assertEquals(balance.subtract(transferAmount.multiply(new BigDecimal(successCount.get()))), senderBalance);
        assertEquals(transferAmount.multiply(new BigDecimal(successCount.get())), recipientBalance);
        assertEquals(0, errorCount.get(), "The second transfer should drop (there won't be enough balance)");
    }
}
