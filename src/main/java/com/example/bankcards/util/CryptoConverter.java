package com.example.bankcards.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.beans.factory.annotation.Value;

/**
 * Конвертер атрибутов JPA для прозрачного шифрования данных в базе данных.
 * Реализует интерфейс {@link AttributeConverter} для автоматического
 * шифрования строк при сохранении и расшифровке при чтении.
 *
 * Применяется для защиты чувствительных данных, таких как номера банковских карт,
 * в соответствии со стандартами безопасности.
 */
@Converter
public class CryptoConverter implements AttributeConverter<String, String> {

    private final TextEncryptor encryptor;

    public CryptoConverter(
            @Value("${app.crypto.key}") String secretKey,
            @Value("${app.crypto.salt}") String salt) {
        // Утилита Spring Security для выполнения симметричного шифрования текста
        this.encryptor = Encryptors.text(secretKey, salt);
    }

    /**
     * Преобразует значение атрибута сущности в зашифрованную строку для хранения в колонке БД.
     *
     * @param attribute исходная строка (например, номер карты)
     * @return зашифрованная строка или null, если входное значение null
     */
    @Override
    public String convertToDatabaseColumn(String attribute) {
        return (attribute == null) ? null : encryptor.encrypt(attribute);
    }

    /**
     * Преобразует зашифрованные данные из БД обратно в расшифрованный вид для использования в приложении.
     *
     * @param dbData зашифрованная строка из базы данных
     * @return расшифрованная исходная строка или null, если данные в БД отсутствуют
     */
    @Override
    public String convertToEntityAttribute(String dbData) {
        return (dbData == null) ? null : encryptor.decrypt(dbData);
    }
}
