package com.example.bankcards.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;

@Converter
public class CryptoConverter implements AttributeConverter<String, String> {

    private static final String SECRET_KEY = "my-very-secret-key";
    private static final String SALT = "deadbeef";

    private final TextEncryptor encryptor = Encryptors.text(SECRET_KEY, SALT);

    @Override
    public String convertToDatabaseColumn(String attribute) {
        return (attribute == null) ? null : encryptor.encrypt(attribute);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        return (dbData == null) ? null : encryptor.decrypt(dbData);
    }
}
