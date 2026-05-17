package eu.howarth.fin.optics.dto.item;

import java.math.BigDecimal;

public record ExpenditureDto(
        String name,
        String description,
        String start,
        String end,
        BigDecimal monthlyAmount
) implements FinancialItemDto {}
