package eu.howarth.fin.optics.dto.item;

import java.math.BigDecimal;

public record AssetDto(
        String name,
        String description,
        String start,
        BigDecimal startValue,
        BigDecimal annualGrowthRate,
        String saleDate
) implements FinancialItemDto {}
