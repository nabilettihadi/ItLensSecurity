package ma.nabil.ITLens.service;

import lombok.RequiredArgsConstructor;
import ma.nabil.ITLens.dto.participation.SurveyParticipationRequest;
import ma.nabil.ITLens.dto.participation.SurveyResultResponse;
import ma.nabil.ITLens.entity.*;
import ma.nabil.ITLens.exception.ResourceNotFoundException;
import ma.nabil.ITLens.repository.AnswerRepository;
import ma.nabil.ITLens.repository.QuestionRepository;
import ma.nabil.ITLens.repository.SurveyEditionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SurveyParticipationService {
    private final SurveyEditionRepository surveyEditionRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    @Transactional
    public void recordParticipation(Integer surveyEditionId, SurveyParticipationRequest request) {
        var surveyEdition = surveyEditionRepository.findById(surveyEditionId)
                .orElseThrow(() -> new ResourceNotFoundException("SurveyEdition", surveyEditionId));

        for (var response : request.getResponses()) {
            var question = questionRepository.findById(response.getQuestionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Question", response.getQuestionId()));

            // Validate that the question belongs to this survey edition
            if (!question.getSubject().getSurveyEdition().getId().equals(surveyEditionId)) {
                throw new IllegalArgumentException("Question " + question.getId() + " does not belong to survey edition " + surveyEditionId);
            }

            // Validate answer count based on question type
            if (question.getType() == QuestionType.CHOIX_UNIQUE && response.getAnswerIds().size() != 1) {
                throw new IllegalArgumentException("Question " + question.getId() + " requires exactly one answer");
            }

            // Update answer counts
            question.setAnswerCount(question.getAnswerCount() + 1);
            questionRepository.save(question);

            for (Integer answerId : response.getAnswerIds()) {
                var answer = answerRepository.findById(answerId)
                        .orElseThrow(() -> new ResourceNotFoundException("Answer", answerId));

                // Validate that the answer belongs to this question
                if (!answer.getQuestion().getId().equals(question.getId())) {
                    throw new IllegalArgumentException("Answer " + answerId + " does not belong to question " + question.getId());
                }

                answer.setSelectionCount(answer.getSelectionCount() + 1);
                answer.setPercentage((double) answer.getSelectionCount() / question.getAnswerCount() * 100);
                answerRepository.save(answer);
            }
        }
    }

    public SurveyResultResponse getSurveyResults(Integer surveyEditionId) {
        var surveyEdition = surveyEditionRepository.findById(surveyEditionId)
                .orElseThrow(() -> new ResourceNotFoundException("SurveyEdition", surveyEditionId));

        return SurveyResultResponse.builder()
                .surveyTitle(surveyEdition.getSurvey().getTitle())
                .subjects(buildSubjectResults(surveyEdition.getSubjects()))
                .build();
    }

    private List<SurveyResultResponse.SubjectResult> buildSubjectResults(List<Subject> subjects) {
        return subjects.stream()
                .filter(subject -> subject.getParent() == null)
                .map(this::buildSubjectResult)
                .collect(Collectors.toList());
    }

    private SurveyResultResponse.SubjectResult buildSubjectResult(Subject subject) {
        return SurveyResultResponse.SubjectResult.builder()
                .title(subject.getTitle())
                .children(subject.getChildren().stream()
                        .map(this::buildSubjectResult)
                        .collect(Collectors.toList()))
                .questions(subject.getQuestions().stream()
                        .map(this::buildQuestionResult)
                        .collect(Collectors.toList()))
                .build();
    }

    private SurveyResultResponse.QuestionResult buildQuestionResult(Question question) {
        Map<String, Integer> answerCounts = question.getAnswers().stream()
                .collect(Collectors.toMap(
                        Answer::getText,
                        Answer::getSelectionCount
                ));

        return SurveyResultResponse.QuestionResult.builder()
                .question(question.getText())
                .answers(answerCounts)
                .totalAnswers(question.getAnswerCount())
                .build();
    }
}
