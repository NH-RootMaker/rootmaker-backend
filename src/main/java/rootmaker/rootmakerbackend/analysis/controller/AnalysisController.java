package rootmaker.rootmakerbackend.analysis.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import reactor.core.publisher.Mono;
import rootmaker.rootmakerbackend.analysis.dto.AnalysisRequest;
import rootmaker.rootmakerbackend.analysis.dto.AnalysisResponse;
import rootmaker.rootmakerbackend.analysis.service.AnalysisService;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AnalysisController {

    private final AnalysisService analysisService;

    @PostMapping("/analysis/summary")
    public Mono<ResponseEntity<AnalysisResponse>> getAnalysisSummary(@RequestBody AnalysisRequest request) {
        return analysisService.getAnalysis(request)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/users/{userId}/ml-analysis")
    public Mono<ResponseEntity<AnalysisResponse>> getUserMlAnalysis(@PathVariable Long userId) {
        return analysisService.getUserMlAnalysis(userId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
