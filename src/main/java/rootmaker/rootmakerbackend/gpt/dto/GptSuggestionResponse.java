package rootmaker.rootmakerbackend.gpt.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class GptSuggestionResponse {

    private String suggestion;
    private List<MissionDto> roadmap;

}
