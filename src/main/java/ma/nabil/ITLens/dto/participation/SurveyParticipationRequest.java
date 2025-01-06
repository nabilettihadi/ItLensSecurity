package ma.nabil.ITLens.dto.participation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SurveyParticipationRequest {
    @NotEmpty(message = "At least one response is required")
    @Valid
    private List<QuestionResponse> responses;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class QuestionResponse {
        private Integer questionId;
        private List<Integer> answerIds;
    }
}
