package rootmaker.rootmakerbackend.analysis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import rootmaker.rootmakerbackend.analysis.dto.AnalysisRequest;
import rootmaker.rootmakerbackend.analysis.dto.AnalysisResponse;
import rootmaker.rootmakerbackend.domain.repository.SubscriptionAccountRepository;
import rootmaker.rootmakerbackend.domain.repository.UserRepository;
import rootmaker.rootmakerbackend.domain.subscription.DepositHistory;
import rootmaker.rootmakerbackend.domain.subscription.SubscriptionAccount;
import rootmaker.rootmakerbackend.domain.user.User;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final WebClient webClient;
    private final SubscriptionAccountRepository subscriptionAccountRepository;

    @Value("${ml.server.url}")
    private String mlServerUrl;

    // This method is now for internal use or direct passthrough
    public Mono<AnalysisResponse> getAnalysis(AnalysisRequest request) {
        return webClient.post()
                .uri(mlServerUrl + "/analyze/summary")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(AnalysisResponse.class);
    }

    private User findUserByNameAndAccountNumber(String name, String accountNumber) {
        SubscriptionAccount account = subscriptionAccountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        if (!Objects.equals(account.getUser().getUsername(), name)) {
            throw new IllegalArgumentException("User name does not match account owner");
        }
        return account.getUser();
    }

    public Mono<AnalysisResponse> getUserMlAnalysis(String name, String accountNumber) {
        User user = findUserByNameAndAccountNumber(name, accountNumber);
        SubscriptionAccount account = subscriptionAccountRepository.findByUser(user).get(); // Already fetched in findUser

        // 1. profile 데이터 구성 (상세 정보 포함)
        List<AnalysisRequest.Dependent> dependentDtos = user.getDependents().stream()
                .map(d -> new AnalysisRequest.Dependent(d.getRelationship(), d.getBirthDate(), d.isCohabiting()))
                .collect(Collectors.toList());

        String ageBand = calculateAgeBand(user.getBirthDate());

        AnalysisRequest.Profile profile = new AnalysisRequest.Profile(
                ageBand,
                user.getRegionCode(),
                user.getIncomeBand(),
                user.getTypeCode(),
                user.getPayday(),
                user.getBirthDate(),
                user.getMaritalStatus(),
                user.getMarriageDate(),
                user.getHomelessStartDate(),
                account.getSubscriptionStartDate(),
                dependentDtos
        );

        // 2. series 데이터 구성 (DB의 DepositHistory 사용)
        List<AnalysisRequest.SeriesItem> series = account.getDepositHistories().stream()
                .map(h -> new AnalysisRequest.SeriesItem(h.getMonth(), h.getAmount(), h.getCount(), h.getAuto()))
                .collect(Collectors.toList());

        // 3. cohort, formula 데이터 구성
        // cohort는 데모용 임시 데이터, formula는 실제 청약홈 기준 적용
        AnalysisRequest.Cohort cohort = new AnalysisRequest.Cohort(100000.0, 150000.0, 200000.0, 250000.0, 300000.0);
        AnalysisRequest.Formula formula = new AnalysisRequest.Formula(
                "2024-05-13", // 기준일
                // 무주택기간 (최대 32점)
                List.of(
                        Map.of("year_min", 0, "year_max", 1, "score", 2),
                        Map.of("year_min", 1, "year_max", 2, "score", 4),
                        Map.of("year_min", 2, "year_max", 3, "score", 6),
                        Map.of("year_min", 3, "year_max", 4, "score", 8),
                        Map.of("year_min", 4, "year_max", 5, "score", 10),
                        Map.of("year_min", 5, "year_max", 6, "score", 12),
                        Map.of("year_min", 6, "year_max", 7, "score", 14),
                        Map.of("year_min", 7, "year_max", 8, "score", 16),
                        Map.of("year_min", 8, "year_max", 9, "score", 18),
                        Map.of("year_min", 9, "year_max", 10, "score", 20),
                        Map.of("year_min", 10, "year_max", 11, "score", 22),
                        Map.of("year_min", 11, "year_max", 12, "score", 24),
                        Map.of("year_min", 12, "year_max", 13, "score", 26),
                        Map.of("year_min", 13, "year_max", 14, "score", 28),
                        Map.of("year_min", 14, "year_max", 15, "score", 30),
                        Map.of("year_min", 15, "year_max", 99, "score", 32) // 15년 이상
                ),
                // 부양가족수 (최대 35점)
                List.of(
                        Map.of("count", 0, "score", 5),
                        Map.of("count", 1, "score", 10),
                        Map.of("count", 2, "score", 15),
                        Map.of("count", 3, "score", 20),
                        Map.of("count", 4, "score", 25),
                        Map.of("count", 5, "score", 30),
                        Map.of("count", 6, "score", 35) // 6명 이상
                ),
                // 청약통장 가입기간 (최대 17점)
                List.of(
                        Map.of("year_min", 0, "year_max", 0.5, "score", 1), // 6개월 미만
                        Map.of("year_min", 0.5, "year_max", 1, "score", 2),
                        Map.of("year_min", 1, "year_max", 2, "score", 3),
                        Map.of("year_min", 2, "year_max", 3, "score", 4),
                        Map.of("year_min", 3, "year_max", 4, "score", 5),
                        Map.of("year_min", 4, "year_max", 5, "score", 6),
                        Map.of("year_min", 5, "year_max", 6, "score", 7),
                        Map.of("year_min", 6, "year_max", 7, "score", 8),
                        Map.of("year_min", 7, "year_max", 8, "score", 9),
                        Map.of("year_min", 8, "year_max", 9, "score", 10),
                        Map.of("year_min", 9, "year_max", 10, "score", 11),
                        Map.of("year_min", 10, "year_max", 11, "score", 12),
                        Map.of("year_min", 11, "year_max", 12, "score", 13),
                        Map.of("year_min", 12, "year_max", 13, "score", 14),
                        Map.of("year_min", 13, "year_max", 14, "score", 15),
                        Map.of("year_min", 14, "year_max", 15, "score", 16),
                        Map.of("year_min", 15, "year_max", 99, "score", 17) // 15년 이상
                )
        );

        // 4. ML 서버에 보낼 요청 객체 생성
        AnalysisRequest request = new AnalysisRequest(
                profile,
                YearMonth.now().format(DateTimeFormatter.ofPattern("yyyy-MM")),
                series,
                cohort,
                formula
        );

        // 5. ML 서버에 분석 요청
        return getAnalysis(request);
    }

    private String calculateAgeBand(String birthDate) {
        if (birthDate == null || birthDate.length() < 4) {
            return "unknown"; // 또는 다른 기본값
        }
        int birthYear = Integer.parseInt(birthDate.substring(0, 4));
        int currentYear = YearMonth.now().getYear();
        int age = currentYear - birthYear;

        if (age < 20) return "10s";
        if (age < 30) return "20s";
        if (age < 40) return "30s";
        if (age < 50) return "40s";
        if (age < 60) return "50s";
        return "60s+";
    }
}
