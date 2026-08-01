package eu.howarth.fin.optics.dto.item;

import java.math.BigDecimal;

public record InvestmentDto(
        String name,
        String description,
        String start,
        BigDecimal startValue,
        BigDecimal annualGrowthRate,
        String contributionStart,
        BigDecimal monthlyContribution,
        BigDecimal contributionGrowthRate,
        String contributionEnd,
        String drawdownStart,
        BigDecimal monthlyDrawdown,
        BigDecimal drawdownGrowthRate
) implements FinancialItemDto {}
