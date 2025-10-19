package rootmaker.rootmakerbackend.gpt.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import rootmaker.rootmakerbackend.gpt.dto.AnalysisResult;
import rootmaker.rootmakerbackend.gpt.dto.GptSuggestionResponse;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GptService {

    private final OpenAiService openAiService;
    private final ObjectMapper objectMapper;

    public GptSuggestionResponse getSuggestion(AnalysisResult analysisResult) {
        try {
            String analysisJson = objectMapper.writeValueAsString(analysisResult);
            String prompt = buildPrompt(analysisJson);

            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model("gpt-3.5-turbo")
                    .messages(List.of(new ChatMessage(ChatMessageRole.SYSTEM.value(), prompt)))
                    .temperature(0.7)
                    .maxTokens(1500)
                    .build();

            String responseContent = openAiService.createChatCompletion(request).getChoices().get(0).getMessage().getContent();

            return objectMapper.readValue(responseContent, GptSuggestionResponse.class);

        } catch (JsonProcessingException e) {
            log.error("Error processing JSON for GPT suggestion", e);
            throw new RuntimeException("Error processing JSON", e);
        }
    }

    private String buildPrompt(String analysisJson) {
        return "You are an expert financial advisor and motivational coach for a FinTech app. " +
                "Your task is to generate personalized recommendations and a mission roadmap for a user based on their financial analysis data.\n\n" +
                "Here is the user's financial analysis data:\n" +
                "'''json\n" + analysisJson + "\n'''\n\n" +
                "Based on this data, perform the following two tasks:\n\n" +
                "1. Generate a Personalized Encouragement Message: Create one short, friendly, and actionable message in Korean to encourage the user. " +
                                "Use the provided data to make it relevant. For example, if `consistencyScore` is high, praise them. If `predictedSuccessProbability` is high, motivate them for the next step.\n\n" +
                
                                "2. Generate a JSON array of exactly 9 sequential missions: Create a JSON array of 9 sequential missions designed to improve the user's financial literacy and saving habits. " +
                "The missions should be small, achievable, and varied. If the `housingScore` is low, you can include missions not related to housing subscriptions, like reading financial news.\n\n" +
                "Provide your final output as a single JSON object. The JSON object must have two keys: `suggestion` (for the message) and `roadmap` (for the array of missions). " +
                "The roadmap missions should have `title` and `description` fields.\n\n" +
                "Do not include any other text, explanations, or markdown formatting in your response. Your entire output must be only the JSON object.\n\n" +
                "Example of the final JSON structure:\n" +
                "'''json\n" +
                "{\n" +
                "  \"suggestion\": \"꾸준함 점수가 82점이나 되시네요! 다음 목표도 충분히 달성하실 수 있어요.\",\n" +
                "  \"roadmap\": [\n" +
                "    {\n" +
                "      \"title\": \"1단계: 2만원 저축하기\",\n" +
                "      \"description\": \"작은 금액부터 시작해서 성취감을 느껴보세요.\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"title\": \"2단계: 금융 뉴스 읽기\",\n" +
                "      \"description\": \"청약에 관심이 없으시다면, 다른 금융 상품에 대한 기사를 읽어보는 건 어때요?\"\n" +
                "    }\n" +
                "  ]\n" +
                "}\n" +
                "'''";
    }
}
