package ma.nabil.ITLens.dto.question;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class QuestionCreateRequest {
    @NotBlank(message = "Question text is mandatory")
    @Size(min = 10, max = 500, message = "Question text must be between 10 and 500 characters")
    private String text;

    @NotNull(message = "Question type is mandatory")
    private QuestionType type;

    @Size(min = 2, message = "At least 2 answers are required")
    private List<String> answers;
}
