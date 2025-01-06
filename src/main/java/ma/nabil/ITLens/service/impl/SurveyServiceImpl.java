package ma.nabil.ITLens.service.impl;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import ma.nabil.ITLens.dto.SurveyDTO;
import ma.nabil.ITLens.dto.survey.SurveyCreateRequest;
import ma.nabil.ITLens.entity.Owner;
import ma.nabil.ITLens.entity.Role;
import ma.nabil.ITLens.entity.Survey;
import ma.nabil.ITLens.entity.User;
import ma.nabil.ITLens.exception.ResourceNotFoundException;
import ma.nabil.ITLens.mapper.SurveyMapper;
import ma.nabil.ITLens.repository.OwnerRepository;
import ma.nabil.ITLens.repository.SurveyRepository;
import ma.nabil.ITLens.repository.UserRepository;
import ma.nabil.ITLens.service.SurveyService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class SurveyServiceImpl extends GenericServiceImpl<Survey, SurveyDTO, Integer> implements SurveyService {
    private final OwnerRepository ownerRepository;
    private final SurveyRepository surveyRepository;
    private final UserRepository userRepository;

    public SurveyServiceImpl(
            SurveyRepository repository,
            SurveyMapper mapper,
            OwnerRepository ownerRepository,
            UserRepository userRepository) {
        super(repository, mapper);
        this.ownerRepository = ownerRepository;
        this.surveyRepository = repository;
        this.userRepository = userRepository;
    }

    @Override
    public Page<SurveyDTO> getAll(Pageable pageable) {
        User currentUser = getCurrentUser();
        if (currentUser.getRole() == Role.ADMIN) {
            return surveyRepository.findAllWithEditions(pageable)
                    .map(mapper::toDto);
        } else if (currentUser.getRole() == Role.OWNER) {
            Owner owner = ownerRepository.findByUserId(currentUser.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Owner not found for user " + currentUser.getId()));
            return getSurveysByOwnerId(owner.getId(), pageable);
        }
        return surveyRepository.findPublicSurveys(pageable)
                .map(mapper::toDto);
    }

    @Override
    public Page<SurveyDTO> getSurveysByOwnerId(Integer ownerId, Pageable pageable) {
        User currentUser = getCurrentUser();
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Owner", ownerId));

        if (currentUser.getRole() != Role.ADMIN && 
            (currentUser.getRole() != Role.OWNER || !owner.getUser().getId().equals(currentUser.getId()))) {
            throw new AccessDeniedException("Not authorized to view these surveys");
        }

        return surveyRepository.findByOwnerId(ownerId, pageable)
                .map(mapper::toDto);
    }

    @Override
    public SurveyDTO save(SurveyDTO dto) {
        User currentUser = getCurrentUser();
        if (currentUser.getRole() != Role.OWNER && currentUser.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Only OWNER and ADMIN can create surveys");
        }

        Owner owner;
        if (currentUser.getRole() == Role.OWNER) {
            owner = ownerRepository.findByUserId(currentUser.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Owner not found for user " + currentUser.getId()));
        } else {
            // Admin creating survey for specific owner
            owner = ownerRepository.findById(dto.getOwner().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Owner", dto.getOwner().getId()));
        }

        Survey survey = mapper.toEntity(dto);
        survey.setOwner(owner);
        return mapper.toDto(surveyRepository.save(survey));
    }

    @Override
    public void deleteById(Integer id) {
        User currentUser = getCurrentUser();
        Survey survey = surveyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(getEntityName(), id));

        if (currentUser.getRole() != Role.ADMIN && 
            (currentUser.getRole() != Role.OWNER || !survey.getOwner().getUser().getId().equals(currentUser.getId()))) {
            throw new AccessDeniedException("Not authorized to delete this survey");
        }

        surveyRepository.deleteById(id);
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AccessDeniedException("User not found"));
    }

    @Override
    protected String getEntityName() {
        return "Survey";
    }
}