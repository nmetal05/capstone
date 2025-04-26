package com.allomed.app.service.impl;

import com.allomed.app.domain.DoctorViewHistory;
import com.allomed.app.repository.DoctorViewHistoryRepository;
import com.allomed.app.repository.search.DoctorViewHistorySearchRepository;
import com.allomed.app.service.DoctorViewHistoryService;
import com.allomed.app.service.dto.DoctorViewHistoryDTO;
import com.allomed.app.service.mapper.DoctorViewHistoryMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.allomed.app.domain.DoctorViewHistory}.
 */
@Service
@Transactional
public class DoctorViewHistoryServiceImpl implements DoctorViewHistoryService {

    private static final Logger LOG = LoggerFactory.getLogger(DoctorViewHistoryServiceImpl.class);

    private final DoctorViewHistoryRepository doctorViewHistoryRepository;

    private final DoctorViewHistoryMapper doctorViewHistoryMapper;

    private final DoctorViewHistorySearchRepository doctorViewHistorySearchRepository;

    public DoctorViewHistoryServiceImpl(
        DoctorViewHistoryRepository doctorViewHistoryRepository,
        DoctorViewHistoryMapper doctorViewHistoryMapper,
        DoctorViewHistorySearchRepository doctorViewHistorySearchRepository
    ) {
        this.doctorViewHistoryRepository = doctorViewHistoryRepository;
        this.doctorViewHistoryMapper = doctorViewHistoryMapper;
        this.doctorViewHistorySearchRepository = doctorViewHistorySearchRepository;
    }

    @Override
    public DoctorViewHistoryDTO save(DoctorViewHistoryDTO doctorViewHistoryDTO) {
        LOG.debug("Request to save DoctorViewHistory : {}", doctorViewHistoryDTO);
        DoctorViewHistory doctorViewHistory = doctorViewHistoryMapper.toEntity(doctorViewHistoryDTO);
        doctorViewHistory = doctorViewHistoryRepository.save(doctorViewHistory);
        doctorViewHistorySearchRepository.index(doctorViewHistory);
        return doctorViewHistoryMapper.toDto(doctorViewHistory);
    }

    @Override
    public DoctorViewHistoryDTO update(DoctorViewHistoryDTO doctorViewHistoryDTO) {
        LOG.debug("Request to update DoctorViewHistory : {}", doctorViewHistoryDTO);
        DoctorViewHistory doctorViewHistory = doctorViewHistoryMapper.toEntity(doctorViewHistoryDTO);
        doctorViewHistory = doctorViewHistoryRepository.save(doctorViewHistory);
        doctorViewHistorySearchRepository.index(doctorViewHistory);
        return doctorViewHistoryMapper.toDto(doctorViewHistory);
    }

    @Override
    public Optional<DoctorViewHistoryDTO> partialUpdate(DoctorViewHistoryDTO doctorViewHistoryDTO) {
        LOG.debug("Request to partially update DoctorViewHistory : {}", doctorViewHistoryDTO);

        return doctorViewHistoryRepository
            .findById(doctorViewHistoryDTO.getId())
            .map(existingDoctorViewHistory -> {
                doctorViewHistoryMapper.partialUpdate(existingDoctorViewHistory, doctorViewHistoryDTO);

                return existingDoctorViewHistory;
            })
            .map(doctorViewHistoryRepository::save)
            .map(savedDoctorViewHistory -> {
                doctorViewHistorySearchRepository.index(savedDoctorViewHistory);
                return savedDoctorViewHistory;
            })
            .map(doctorViewHistoryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DoctorViewHistoryDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all DoctorViewHistories");
        return doctorViewHistoryRepository.findAll(pageable).map(doctorViewHistoryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DoctorViewHistoryDTO> findOne(Long id) {
        LOG.debug("Request to get DoctorViewHistory : {}", id);
        return doctorViewHistoryRepository.findById(id).map(doctorViewHistoryMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete DoctorViewHistory : {}", id);
        doctorViewHistoryRepository.deleteById(id);
        doctorViewHistorySearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DoctorViewHistoryDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of DoctorViewHistories for query {}", query);
        return doctorViewHistorySearchRepository.search(query, pageable).map(doctorViewHistoryMapper::toDto);
    }
}
