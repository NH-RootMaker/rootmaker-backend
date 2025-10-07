package rootmaker.rootmakerbackend.report.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class MonthlyReportResponse {
    private String yearMonth;
    private String username;
    private long totalHabitCount;
    private long successHabitCount;
    private double habitSuccessRate;
    private BigDecimal totalSavedAmount;
}
