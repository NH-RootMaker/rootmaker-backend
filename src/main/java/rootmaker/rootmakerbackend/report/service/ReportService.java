package rootmaker.rootmakerbackend.report.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rootmaker.rootmakerbackend.domain.habit.HabitLog;
import rootmaker.rootmakerbackend.domain.repository.HabitLogRepository;
import rootmaker.rootmakerbackend.domain.repository.UserRepository;
import rootmaker.rootmakerbackend.domain.user.User;
import rootmaker.rootmakerbackend.report.dto.MonthlyReportResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final HabitLogRepository habitLogRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public MonthlyReportResponse generateMonthlyReport(Long userId, int year, int month) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = yearMonth.atEndOfMonth().atTime(23, 59, 59);

        List<HabitLog> logs = habitLogRepository.findByUserIdAndCreatedAtBetween(userId, startOfMonth, endOfMonth);

        long totalCount = logs.size();
        long successCount = logs.stream().filter(HabitLog::isSuccess).count();
        double successRate = (totalCount == 0) ? 0 : ((double) successCount / totalCount) * 100;

        BigDecimal totalSaved = logs.stream()
                .filter(HabitLog::isSuccess)
                .map(log -> log.getHabit().getSavingAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return MonthlyReportResponse.builder()
                .yearMonth(yearMonth.toString())
                .username(user.getUsername())
                .totalHabitCount(totalCount)
                .successHabitCount(successCount)
                .habitSuccessRate(successRate)
                .totalSavedAmount(totalSaved)
                .build();
    }
}
