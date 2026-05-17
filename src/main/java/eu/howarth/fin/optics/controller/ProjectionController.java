package eu.howarth.fin.optics.controller;

import eu.howarth.fin.optics.dto.ProjectionRequest;
import eu.howarth.fin.optics.dto.ProjectionResponse;
import eu.howarth.fin.optics.dto.ScenarioDefinition;
import eu.howarth.fin.optics.mapper.FinancialItemMapper;
import eu.howarth.fin.planning.FinancialItem;
import eu.howarth.fin.planning.FinancialModel;
import eu.howarth.fin.planning.ModelProjection;
import eu.howarth.fin.planning.RealTermsAdjuster;
import eu.howarth.fin.planning.RealTermsProjection;
import eu.howarth.fin.rpi.RpiDataset;
import eu.howarth.fin.rpi.RpiDatasetLoader;
import eu.howarth.fin.rpi.projection.ConstantInflationProjection;
import eu.howarth.fin.rpi.projection.RpiProjector;
import eu.howarth.fin.rpi.scenario.RpiScenario;
import eu.howarth.fin.rpi.scenario.RpiScenarioSet;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ProjectionController {

    private static final RpiDataset HISTORICAL = RpiDatasetLoader.bundled();

    @PostMapping("/projection")
    public ProjectionResponse project(@RequestBody ProjectionRequest request) {
        YearMonth from = YearMonth.parse(request.from());
        YearMonth to   = YearMonth.parse(request.to());
        YearMonth base = YearMonth.parse(request.base());

        List<FinancialItem> items = request.items().stream()
                .map(FinancialItemMapper::toModel)
                .toList();

        ModelProjection nominal = new FinancialModel(items).project(from, to);

        RealTermsProjection realTerms = null;
        if (request.scenarios() != null && !request.scenarios().isEmpty()) {
            realTerms = RealTermsAdjuster.adjust(nominal, buildScenarioSet(request.scenarios(), to), base);
        }

        return ProjectionResponse.from(nominal, realTerms);
    }

    private RpiScenarioSet buildScenarioSet(List<ScenarioDefinition> defs, YearMonth to) {
        List<RpiScenario> scenarios = defs.stream()
                .map(def -> {
                    RpiDataset projected = RpiProjector.project(
                            HISTORICAL, new ConstantInflationProjection(def.annualRate()), to);
                    return new RpiScenario(def.name(), projected);
                })
                .toList();
        return new RpiScenarioSet(scenarios);
    }
}
