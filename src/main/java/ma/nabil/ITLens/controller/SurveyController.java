package ma.nabil.ITLens.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.nabil.ITLens.dto.SurveyDTO;
import ma.nabil.ITLens.dto.survey.SurveyCreateRequest;
import ma.nabil.ITLens.service.SurveyService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/v1/surveys")
@RequiredArgsConstructor
public class SurveyController {
    private final SurveyService surveyService;

    @GetMapping
    public ResponseEntity<Page<SurveyDTO>> getAllSurveys(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(surveyService.getAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SurveyDTO> getSurveyById(@PathVariable Integer id) {
        return ResponseEntity.ok(surveyService.getById(id));
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<Page<SurveyDTO>> getSurveysByOwnerId(
            @PathVariable Integer ownerId,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(surveyService.getSurveysByOwnerId(ownerId, pageable));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<SurveyDTO> createSurvey(@RequestBody @Valid SurveyDTO request) {
        return new ResponseEntity<>(surveyService.save(request), CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<SurveyDTO> updateSurvey(
            @PathVariable Integer id,
            @RequestBody @Valid SurveyDTO request) {
        request.setId(id);
        return ResponseEntity.ok(surveyService.save(request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<Void> deleteSurvey(@PathVariable Integer id) {
        surveyService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}