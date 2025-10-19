package rootmaker.rootmakerbackend.config;

import com.theokanning.openai.service.OpenAiService;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class OpenAiConfig {

    @Bean
    public OpenAiService openAiService() {
        Dotenv dotenv = Dotenv.load();
        String apiKey = dotenv.get("OPENAI_KEY");
        return new OpenAiService(apiKey, Duration.ofSeconds(60));
    }
}
