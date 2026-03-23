package com.example.bankcards.controller;

import com.example.bankcards.entity.User;
import com.example.bankcards.exception.InvalidPasswordException;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Контроллер для аутентификации и регистрации пользователей.
 * Обеспечивает механизмы входа в систему и создания новых учетных записей.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    /**
     * Регистрация нового пользователя в системе.
     * Пароль пользователя перед сохранением хешируется с использованием PasswordEncoder.
     *
     * @param user объект сущности пользователя, содержащий логин, пароль и роль
     */
    @PostMapping("/register")
    public void register(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    /**
     * Аутентификация пользователя и выдача JWT токена.
     * Выполняет поиск пользователя по имени и проверку соответствия пароля.
     *
     * @param request карта (Map), содержащая ключи "username" и "password"
     * @return карта с ключом "token" и сгенерированным JWT токеном
     * @throws InvalidPasswordException если пользователь не найден или пароль не совпадает
     */
    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new InvalidPasswordException("The user was not found"));

        if (passwordEncoder.matches(password, user.getPassword())) {
            String token = jwtUtils.generateToken(user.getUsername(), user.getRole().name());
            return Map.of("token", token);
        } else {
            throw new InvalidPasswordException("Invalid password");
        }
    }
}
