package com.allomed.app.service.impl;

import com.allomed.app.domain.SymptomSearch;
import com.allomed.app.repository.SymptomSearchRepository;
import com.allomed.app.repository.search.SymptomSearchSearchRepository;
import com.allomed.app.service.SymptomSearchService;
import com.allomed.app.service.dto.SymptomSearchDTO;
import com.allomed.app.service.mapper.SymptomSearchMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.allomed.app.domain.SymptomSearch}.
 */
@Service
@Transactional
public class SymptomSearchServiceImpl implements SymptomSearchService {

    private static final Logger LOG = LoggerFactory.getLogger(SymptomSearchServiceImpl.class);

    private final SymptomSearchRepository symptomSearchRepository;

    private final SymptomSearchMapper symptomSearchMapper;

    private final SymptomSearchSearchRepository symptomSearchSearchRepository;

    public SymptomSearchServiceImpl(
        SymptomSearchRepository symptomSearchRepository,
        SymptomSearchMapper symptomSearchMapper,
        SymptomSearchSearchRepository symptomSearchSearchRepository
    ) {
        this.symptomSearchRepository = symptomSearchRepository;
        this.symptomSearchMapper = symptomSearchMapper;
        this.symptomSearchSearchRepository = symptomSearchSearchRepository;
    }

    @Override
    public SymptomSearchDTO save(SymptomSearchDTO symptomSearchDTO) {
        LOG.debug("Request to save SymptomSearch : {}", symptomSearchDTO);
        SymptomSearch symptomSearch = symptomSearchMapper.toEntity(symptomSearchDTO);
        symptomSearch = symptomSearchRepository.save(symptomSearch);
        symptomSearchSearchRepository.index(symptomSearch);
        return symptomSearchMapper.toDto(symptomSearch);
    }

    @Override
    public SymptomSearchDTO update(SymptomSearchDTO symptomSearchDTO) {
        LOG.debug("Request to update SymptomSearch : {}", symptomSearchDTO);
        SymptomSearch symptomSearch = symptomSearchMapper.toEntity(symptomSearchDTO);
        symptomSearch = symptomSearchRepository.save(symptomSearch);
        symptomSearchSearchRepository.index(symptomSearch);
        return symptomSearchMapper.toDto(symptomSearch);
    }

    @Override
    public Optional<SymptomSearchDTO> partialUpdate(SymptomSearchDTO symptomSearchDTO) {
        LOG.debug("Request to partially update SymptomSearch : {}", symptomSearchDTO);

        return symptomSearchRepository
            .findById(symptomSearchDTO.getId())
            .map(existingSymptomSearch -> {
                symptomSearchMapper.partialUpdate(existingSymptomSearch, symptomSearchDTO);

                return existingSymptomSearch;
            })
            .map(symptomSearchRepository::save)
            .map(savedSymptomSearch -> {
                symptomSearchSearchRepository.index(savedSymptomSearch);
                return savedSymptomSearch;
            })
            .map(symptomSearchMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SymptomSearchDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all SymptomSearches");
        return symptomSearchRepository.findAll(pageable).map(symptomSearchMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SymptomSearchDTO> findOne(Long id) {
        LOG.debug("Request to get SymptomSearch : {}", id);
        return symptomSearchRepository.findById(id).map(symptomSearchMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete SymptomSearch : {}", id);
        symptomSearchRepository.deleteById(id);
        symptomSearchSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SymptomSearchDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of SymptomSearches for query {}", query);
        return symptomSearchSearchRepository.search(query, pageable).map(symptomSearchMapper::toDto);
    }
}
