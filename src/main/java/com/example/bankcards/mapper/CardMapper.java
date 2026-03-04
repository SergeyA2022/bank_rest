package com.example.bankcards.mapper;

import com.example.bankcards.dto.CardResponseDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.util.CardUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = CardUtils.class)
public interface CardMapper {

    @Mapping(target = "maskedNumber", source = "cardNumber")
    CardResponseDTO toDto(Card card);
}
