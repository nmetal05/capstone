package com.allomed.app.service.impl;

import com.allomed.app.domain.Specialization;
import com.allomed.app.repository.SpecializationRepository;
import com.allomed.app.repository.search.SpecializationSearchRepository;
import com.allomed.app.service.SpecializationService;
import com.allomed.app.service.dto.SpecializationDTO;
import com.allomed.app.service.mapper.SpecializationMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.allomed.app.domain.Specialization}.
 */
@Service
@Transactional
public class SpecializationServiceImpl implements SpecializationService {

    private static final Logger LOG = LoggerFactory.getLogger(SpecializationServiceImpl.class);

    private final SpecializationRepository specializationRepository;

    private final SpecializationMapper specializationMapper;

    private final SpecializationSearchRepository specializationSearchRepository;

    public SpecializationServiceImpl(
        SpecializationRepository specializationRepository,
        SpecializationMapper specializationMapper,
        SpecializationSearchRepository specializationSearchRepository
    ) {
        this.specializationRepository = specializationRepository;
        this.specializationMapper = specializationMapper;
        this.specializationSearchRepository = specializationSearchRepository;
    }

    @Override
    public SpecializationDTO save(SpecializationDTO specializationDTO) {
        LOG.debug("Request to save Specialization : {}", specializationDTO);
        Specialization specialization = specializationMapper.toEntity(specializationDTO);
        specialization = specializationRepository.save(specialization);
        specializationSearchRepository.index(specialization);
        return specializationMapper.toDto(specialization);
    }

    @Override
    public SpecializationDTO update(SpecializationDTO specializationDTO) {
        LOG.debug("Request to update Specialization : {}", specializationDTO);
        Specialization specialization = specializationMapper.toEntity(specializationDTO);
        specialization = specializationRepository.save(specialization);
        specializationSearchRepository.index(specialization);
        return specializationMapper.toDto(specialization);
    }

    @Override
    public Optional<SpecializationDTO> partialUpdate(SpecializationDTO specializationDTO) {
        LOG.debug("Request to partially update Specialization : {}", specializationDTO);

        return specializationRepository
            .findById(specializationDTO.getId())
            .map(existingSpecialization -> {
                specializationMapper.partialUpdate(existingSpecialization, specializationDTO);

                return existingSpecialization;
            })
            .map(specializationRepository::save)
            .map(savedSpecialization -> {
                specializationSearchRepository.index(savedSpecialization);
                return savedSpecialization;
            })
            .map(specializationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SpecializationDTO> findOne(Long id) {
        LOG.debug("Request to get Specialization : {}", id);
        return specializationRepository.findById(id).map(specializationMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Specialization : {}", id);
        specializationRepository.deleteById(id);
        specializationSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SpecializationDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Specializations for query {}", query);
        return specializationSearchRepository.search(query, pageable).map(specializationMapper::toDto);
    }
}
