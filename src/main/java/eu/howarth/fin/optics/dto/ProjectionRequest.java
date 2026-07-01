package eu.howarth.fin.optics.dto;

import eu.howarth.fin.optics.dto.item.FinancialItemDto;

import java.math.BigDecimal;
import java.util.List;

public record ProjectionRequest(
        String from,
        String to,
        String base,
        BigDecimal startingCash,
        List<FinancialItemDto> items,
        List<ScenarioDefinition> scenarios
) {}
