package eu.howarth.fin.optics.dto.item;

import java.math.BigDecimal;

public record FinancialEventDto(
        String name,
        String description,
        String date,
        BigDecimal amount
) implements FinancialItemDto {}
