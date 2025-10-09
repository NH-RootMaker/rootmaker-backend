package rootmaker.rootmakerbackend.analysis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

public record AnalysisRequest(
    Profile profile,
    String month,
    List<SeriesItem> series,
    Cohort cohort,
    Formula formula
) {
    public record Profile(
        String ageBand,
        String regionCode,
        String incomeBand,
        String typeCode,
        Integer payday
    ) {}

    public record SeriesItem(
        String month,
        Double amount,
        Integer count,
        Boolean auto
    ) {}

    public record Cohort(
        Double p10,
        Double p25,
        Double p50,
        Double p75,
        Double p90
    ) {}

    public record Formula(
        String version,
        @JsonProperty("none_home") List<Map<String, Object>> noneHome,
        List<Map<String, Object>> dependents,
        List<Map<String, Object>> subscription
    ) {}
}
