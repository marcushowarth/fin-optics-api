package eu.howarth.fin.optics.dto;

import eu.howarth.fin.optics.dto.item.FinancialItemDto;

import java.util.List;

public record ProjectionRequest(
        String from,
        String to,
        String base,
        List<FinancialItemDto> items,
        List<ScenarioDefinition> scenarios
) {}
