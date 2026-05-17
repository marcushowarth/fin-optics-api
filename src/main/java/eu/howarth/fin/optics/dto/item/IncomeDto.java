package eu.howarth.fin.optics.dto.item;

import java.math.BigDecimal;

public record IncomeDto(
        String name,
        String description,
        String start,
        String end,
        BigDecimal monthlyAmount,
        BigDecimal annualGrowthRate
) implements FinancialItemDto {}
