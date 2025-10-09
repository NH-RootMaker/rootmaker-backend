package rootmaker.rootmakerbackend.report.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rootmaker.rootmakerbackend.domain.habit.HabitLog;
import rootmaker.rootmakerbackend.domain.repository.HabitLogRepository;
import rootmaker.rootmakerbackend.domain.repository.SubscriptionAccountRepository;
import rootmaker.rootmakerbackend.domain.subscription.SubscriptionAccount;
import rootmaker.rootmakerbackend.domain.user.User;
import rootmaker.rootmakerbackend.report.dto.MonthlyReportResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final HabitLogRepository habitLogRepository;
    private final SubscriptionAccountRepository subscriptionAccountRepository;

    private User findUserByNameAndAccountNumber(String name, String accountNumber) {
        SubscriptionAccount account = subscriptionAccountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        if (!Objects.equals(account.getUser().getUsername(), name)) {
            throw new IllegalArgumentException("User name does not match account owner");
        }
        return account.getUser();
    }

    @Transactional(readOnly = true)
    public MonthlyReportResponse generateMonthlyReport(String name, String accountNumber, int year, int month) {
        User user = findUserByNameAndAccountNumber(name, accountNumber);
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = yearMonth.atEndOfMonth().atTime(23, 59, 59);

        List<HabitLog> logs = habitLogRepository.findByUserIdAndCreatedAtBetween(user.getId(), startOfMonth, endOfMonth);

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
