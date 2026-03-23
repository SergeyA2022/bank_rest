package com.example.bankcards.mapper;

import com.example.bankcards.dto.CardResponseDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.util.CardUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Интерфейс маппера для преобразования сущностей банковских карт в объекты передачи данных (DTO).
 * Использует библиотеку MapStruct для автоматической генерации реализации.
 * Интегрирован со Spring как компонент (componentModel = "spring").
 */
@Mapper(componentModel = "spring", uses = CardUtils.class)
public interface CardMapper {

    /**
     * Преобразует сущность {@link Card} в объект {@link CardResponseDTO}.
     * В процессе маппинга полный номер карты преобразуется в маскированный вид
     * с помощью методов класса {@link CardUtils}.
     *
     * @param card исходная сущность карты из базы данных
     * @return заполненный объект DTO для ответа API
     */
    @Mapping(target = "maskedNumber", source = "cardNumber")
    CardResponseDTO toDto(Card card);
}
