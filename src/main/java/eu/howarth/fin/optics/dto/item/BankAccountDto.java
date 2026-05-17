package eu.howarth.fin.optics.dto.item;

import java.math.BigDecimal;

public record BankAccountDto(
        String name,
        String description,
        BigDecimal startBalance
) implements FinancialItemDto {}
