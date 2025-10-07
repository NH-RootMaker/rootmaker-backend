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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final WebClient webClient;
    private final UserRepository userRepository;
    private final SubscriptionAccountRepository subscriptionAccountRepository;

    @Value("${ml.server.url}")
    private String mlServerUrl;

    public Mono<AnalysisResponse> getAnalysis(AnalysisRequest request) {
        return webClient.post()
                .uri(mlServerUrl + "/analyze/summary")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(AnalysisResponse.class);
    }

    public Mono<AnalysisResponse> getUserMlAnalysis(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        SubscriptionAccount account = subscriptionAccountRepository.findByUserId(userId).orElseThrow(() -> new IllegalArgumentException("Subscription account not found"));

        // 1. profile 데이터 구성
        AnalysisRequest.Profile profile = new AnalysisRequest.Profile(
                user.getAgeBand(), user.getRegionCode(), user.getIncomeBand(), user.getTypeCode(), user.getPayday()
        );

        // 2. series 데이터 구성 (DB의 DepositHistory 사용)
        List<AnalysisRequest.SeriesItem> series = account.getDepositHistories().stream()
                .map(h -> new AnalysisRequest.SeriesItem(h.getMonth(), h.getAmount(), h.getCount(), h.getAuto()))
                .collect(Collectors.toList());

        // 3. cohort, formula 데이터 (데모용 임시 데이터 생성)
        AnalysisRequest.Cohort cohort = new AnalysisRequest.Cohort(100000.0, 150000.0, 200000.0, 250000.0, 300000.0);
        AnalysisRequest.Formula formula = new AnalysisRequest.Formula(
                "2025-09",
                List.of(Map.of("year_min", 0, "year_max", 1, "score", 2)),
                List.of(Map.of("count", 0, "score", 5)),
                List.of(Map.of("year_min", 0, "year_max", 1, "score", 1))
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
}
