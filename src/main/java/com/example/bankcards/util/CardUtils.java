package com.example.bankcards.util;

/**
 * Утилитарный класс для работы с данными банковских карт.
 * Содержит вспомогательные методы для форматирования и безопасного отображения информации.
 */
public class CardUtils {

    /**
     * Преобразует полный номер карты в маскированный вид.
     * Скрывает все цифры, кроме последних четырех, заменяя их на звездочки.
     * Пример: "1234567812345678" -> "**** **** **** 5678".
     *
     * @param cardNumber полный номер карты (16 цифр)
     * @return маскированная строка для безопасного отображения в UI
     */
    public static String mask(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) return "****";
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }
}
