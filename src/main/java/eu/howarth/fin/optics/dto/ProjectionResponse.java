package eu.howarth.fin.optics.dto;

import eu.howarth.fin.planning.ModelProjection;
import eu.howarth.fin.planning.RealTermsProjection;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.stream.Collectors;

public record ProjectionResponse(NominalDto nominal, RealTermsDto realTerms) {

    public static ProjectionResponse from(ModelProjection nominal, RealTermsProjection realTerms) {
        return new ProjectionResponse(
                NominalDto.from(nominal),
                realTerms == null ? null : RealTermsDto.from(realTerms));
    }

    public record NominalDto(
            Map<String, BigDecimal> netWorth,
            Map<String, BigDecimal> cashPosition,
            Map<String, Map<String, BigDecimal>> itemPositions,
            List<SolvencyWarningDto> warnings) {

        static NominalDto from(ModelProjection p) {
            return new NominalDto(
                    toStringKeys(p.netWorth()),
                    toStringKeys(p.cashPosition()),
                    p.itemPositions().entrySet().stream()
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey,
                                    e -> toStringKeys(e.getValue()))),
                    p.warnings().stream()
                            .map(w -> new SolvencyWarningDto(w.month().toString(), w.cashPosition()))
                            .toList());
        }
    }

    public record RealTermsDto(
            String base,
            Map<String, Map<String, BigDecimal>> netWorth,
            Map<String, Map<String, BigDecimal>> cashPosition,
            Map<String, Map<String, Map<String, BigDecimal>>> itemPositions) {

        static RealTermsDto from(RealTermsProjection p) {
            return new RealTermsDto(
                    p.base().toString(),
                    toScenarioStringKeys(p.netWorth()),
                    toScenarioStringKeys(p.cashPosition()),
                    p.itemPositions().entrySet().stream()
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey,
                                    e -> e.getValue().entrySet().stream()
                                            .collect(Collectors.toMap(
                                                    Map.Entry::getKey,
                                                    ie -> toStringKeys(ie.getValue()))))));
        }
    }

    public record SolvencyWarningDto(String month, BigDecimal cashPosition) {}

    private static Map<String, BigDecimal> toStringKeys(NavigableMap<YearMonth, BigDecimal> map) {
        return map.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey().toString(),
                        Map.Entry::getValue,
                        (a, b) -> b,
                        LinkedHashMap::new));
    }

    private static Map<String, Map<String, BigDecimal>> toScenarioStringKeys(
            Map<String, NavigableMap<YearMonth, BigDecimal>> map) {
        return map.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> toStringKeys(e.getValue())));
    }
}
