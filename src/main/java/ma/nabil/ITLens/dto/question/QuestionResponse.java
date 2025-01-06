package ma.nabil.ITLens.dto.question;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.nabil.ITLens.entity.QuestionType;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuestionResponse {
    private Integer id;
    private String text;
    private QuestionType type;
    private Integer answerCount;
    private List<AnswerResponse> answers;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AnswerResponse {
        private Integer id;
        private String text;
        private Integer selectionCount;
        private Double percentage;
    }
}
