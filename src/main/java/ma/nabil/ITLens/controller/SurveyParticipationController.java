package ma.nabil.ITLens.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.nabil.ITLens.dto.participation.SurveyParticipationRequest;
import ma.nabil.ITLens.dto.participation.SurveyResultResponse;
import ma.nabil.ITLens.service.SurveyParticipationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/surveys")
@RequiredArgsConstructor
public class SurveyParticipationController {
    private final SurveyParticipationService participationService;

    @PostMapping("/{surveyEditionId}/participate")
    public ResponseEntity<Void> participateInSurvey(
            @PathVariable Integer surveyEditionId,
            @Valid @RequestBody SurveyParticipationRequest request) {
        participationService.recordParticipation(surveyEditionId, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{surveyEditionId}/results")
    public ResponseEntity<SurveyResultResponse> getSurveyResults(@PathVariable Integer surveyEditionId) {
        return ResponseEntity.ok(participationService.getSurveyResults(surveyEditionId));
    }
}
