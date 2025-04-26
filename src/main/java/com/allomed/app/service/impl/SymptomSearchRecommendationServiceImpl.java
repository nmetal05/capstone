package com.allomed.app.service.impl;

import com.allomed.app.domain.SymptomSearchRecommendation;
import com.allomed.app.repository.SymptomSearchRecommendationRepository;
import com.allomed.app.repository.search.SymptomSearchRecommendationSearchRepository;
import com.allomed.app.service.SymptomSearchRecommendationService;
import com.allomed.app.service.dto.SymptomSearchRecommendationDTO;
import com.allomed.app.service.mapper.SymptomSearchRecommendationMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.allomed.app.domain.SymptomSearchRecommendation}.
 */
@Service
@Transactional
public class SymptomSearchRecommendationServiceImpl implements SymptomSearchRecommendationService {

    private static final Logger LOG = LoggerFactory.getLogger(SymptomSearchRecommendationServiceImpl.class);

    private final SymptomSearchRecommendationRepository symptomSearchRecommendationRepository;

    private final SymptomSearchRecommendationMapper symptomSearchRecommendationMapper;

    private final SymptomSearchRecommendationSearchRepository symptomSearchRecommendationSearchRepository;

    public SymptomSearchRecommendationServiceImpl(
        SymptomSearchRecommendationRepository symptomSearchRecommendationRepository,
        SymptomSearchRecommendationMapper symptomSearchRecommendationMapper,
        SymptomSearchRecommendationSearchRepository symptomSearchRecommendationSearchRepository
    ) {
        this.symptomSearchRecommendationRepository = symptomSearchRecommendationRepository;
        this.symptomSearchRecommendationMapper = symptomSearchRecommendationMapper;
        this.symptomSearchRecommendationSearchRepository = symptomSearchRecommendationSearchRepository;
    }

    @Override
    public SymptomSearchRecommendationDTO save(SymptomSearchRecommendationDTO symptomSearchRecommendationDTO) {
        LOG.debug("Request to save SymptomSearchRecommendation : {}", symptomSearchRecommendationDTO);
        SymptomSearchRecommendation symptomSearchRecommendation = symptomSearchRecommendationMapper.toEntity(
            symptomSearchRecommendationDTO
        );
        symptomSearchRecommendation = symptomSearchRecommendationRepository.save(symptomSearchRecommendation);
        symptomSearchRecommendationSearchRepository.index(symptomSearchRecommendation);
        return symptomSearchRecommendationMapper.toDto(symptomSearchRecommendation);
    }

    @Override
    public SymptomSearchRecommendationDTO update(SymptomSearchRecommendationDTO symptomSearchRecommendationDTO) {
        LOG.debug("Request to update SymptomSearchRecommendation : {}", symptomSearchRecommendationDTO);
        SymptomSearchRecommendation symptomSearchRecommendation = symptomSearchRecommendationMapper.toEntity(
            symptomSearchRecommendationDTO
        );
        symptomSearchRecommendation = symptomSearchRecommendationRepository.save(symptomSearchRecommendation);
        symptomSearchRecommendationSearchRepository.index(symptomSearchRecommendation);
        return symptomSearchRecommendationMapper.toDto(symptomSearchRecommendation);
    }

    @Override
    public Optional<SymptomSearchRecommendationDTO> partialUpdate(SymptomSearchRecommendationDTO symptomSearchRecommendationDTO) {
        LOG.debug("Request to partially update SymptomSearchRecommendation : {}", symptomSearchRecommendationDTO);

        return symptomSearchRecommendationRepository
            .findById(symptomSearchRecommendationDTO.getId())
            .map(existingSymptomSearchRecommendation -> {
                symptomSearchRecommendationMapper.partialUpdate(existingSymptomSearchRecommendation, symptomSearchRecommendationDTO);

                return existingSymptomSearchRecommendation;
            })
            .map(symptomSearchRecommendationRepository::save)
            .map(savedSymptomSearchRecommendation -> {
                symptomSearchRecommendationSearchRepository.index(savedSymptomSearchRecommendation);
                return savedSymptomSearchRecommendation;
            })
            .map(symptomSearchRecommendationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SymptomSearchRecommendationDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all SymptomSearchRecommendations");
        return symptomSearchRecommendationRepository.findAll(pageable).map(symptomSearchRecommendationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SymptomSearchRecommendationDTO> findOne(Long id) {
        LOG.debug("Request to get SymptomSearchRecommendation : {}", id);
        return symptomSearchRecommendationRepository.findById(id).map(symptomSearchRecommendationMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete SymptomSearchRecommendation : {}", id);
        symptomSearchRecommendationRepository.deleteById(id);
        symptomSearchRecommendationSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SymptomSearchRecommendationDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of SymptomSearchRecommendations for query {}", query);
        return symptomSearchRecommendationSearchRepository.search(query, pageable).map(symptomSearchRecommendationMapper::toDto);
    }
}
