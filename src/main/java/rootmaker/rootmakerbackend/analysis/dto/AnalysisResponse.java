package rootmaker.rootmakerbackend.analysis.dto;

import java.util.List;
import java.util.Map;

public record AnalysisResponse(
    String algoVersion,
    String formulaVersion,
    Double percentile,
    Integer consistencyScore,
    Double predictedNextAmount,
    Integer housingScore,
    Double predictedSuccessProbability,
    List<Map<String, Object>> recommendations,
    Map<String, Object> detail
) {}
