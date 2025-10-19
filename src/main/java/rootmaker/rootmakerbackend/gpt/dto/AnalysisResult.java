package rootmaker.rootmakerbackend.gpt.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class AnalysisResult {

    private String algoVersion;
    private String formulaVersion;
    private Double percentile;
    private Integer consistencyScore;
    private Double predictedNextAmount;
    private Integer housingScore;
    private Double predictedSuccessProbability;
    private List<Recommendation> recommendations;
    private Map<String, Object> detail;

    @Data
    @NoArgsConstructor
    public static class Recommendation {
        private String kind;
        private String title;
        private String detail;
        private Cta cta;
    }

    @Data
    @NoArgsConstructor
    public static class Cta {
        private String type;
        private Payload payload;
    }

    @Data
    @NoArgsConstructor
    public static class Payload {
        private Double increment;
    }
}
