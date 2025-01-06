package ma.nabil.ITLens.dto.subject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.nabil.ITLens.dto.question.QuestionResponse;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubjectResponse {
    private Integer id;
    private String title;
    private Integer parentId;
    private List<SubjectResponse> children;
    private List<QuestionResponse> questions;
}
