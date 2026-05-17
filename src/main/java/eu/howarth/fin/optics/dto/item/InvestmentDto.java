package eu.howarth.fin.optics.dto.item;

import java.math.BigDecimal;

public record InvestmentDto(
        String name,
        String description,
        String start,
        BigDecimal startValue,
        BigDecimal annualGrowthRate,
        String drawdownStart,
        BigDecimal monthlyDrawdown
) implements FinancialItemDto {}
