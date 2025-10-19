package rootmaker.rootmakerbackend.config;

import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class OpenAiConfig {

    @Bean
    public OpenAiService openAiService(@Value("${OPENAI_KEY}") String apiKey) {
        return new OpenAiService(apiKey, Duration.ofSeconds(60));
    }
}
