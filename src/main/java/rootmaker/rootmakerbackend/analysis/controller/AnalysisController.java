package rootmaker.rootmakerbackend.analysis.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;
import rootmaker.rootmakerbackend.analysis.dto.AnalysisRequest;
import rootmaker.rootmakerbackend.analysis.dto.AnalysisResponse;
import rootmaker.rootmakerbackend.analysis.service.AnalysisService;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AnalysisController {

    private final AnalysisService analysisService;

    @PostMapping("/analysis/summary") // This is a passthrough, not for direct frontend use
    public Mono<ResponseEntity<AnalysisResponse>> getAnalysisSummary(@RequestBody AnalysisRequest request) {
        return analysisService.getAnalysis(request)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/ml-analysis")
    public Mono<ResponseEntity<AnalysisResponse>> getUserMlAnalysis(@RequestParam String name, @RequestParam String accountNumber) {
        return analysisService.getUserMlAnalysis(name, accountNumber)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
