package ma.nabil.ITLens.dto.participation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SurveyResultResponse {
    private String surveyTitle;
    private List<SubjectResult> subjects;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SubjectResult {
        private String title;
        private List<SubjectResult> children;
        private List<QuestionResult> questions;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class QuestionResult {
        private String question;
        private Map<String, Integer> answers;
        private Integer totalAnswers;
    }
}
