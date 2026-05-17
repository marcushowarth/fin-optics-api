package eu.howarth.fin.optics.dto.item;

import java.math.BigDecimal;

public record LiabilityDto(
        String name,
        String description,
        String start,
        BigDecimal balance,
        BigDecimal annualInterestRate,
        BigDecimal monthlyRepayment
) implements FinancialItemDto {}
