package rootmaker.rootmakerbackend.gpt.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rootmaker.rootmakerbackend.gpt.dto.AnalysisResult;
import rootmaker.rootmakerbackend.gpt.dto.GptSuggestionResponse;
import rootmaker.rootmakerbackend.gpt.service.GptService;

@RestController
@RequestMapping("/api/gpt")
@RequiredArgsConstructor
public class GptController {

    private final GptService gptService;

    @PostMapping("/suggestions")
    public ResponseEntity<GptSuggestionResponse> getSuggestions(@RequestBody AnalysisResult analysisResult) {
        GptSuggestionResponse suggestion = gptService.getSuggestion(analysisResult);
        return ResponseEntity.ok(suggestion);
    }
}
