package com.allomed.app.service.impl;

import com.allomed.app.domain.DoctorAvailability;
import com.allomed.app.repository.DoctorAvailabilityRepository;
import com.allomed.app.service.DoctorAvailabilityService;
import com.allomed.app.service.dto.DoctorAvailabilityDTO;
import com.allomed.app.service.mapper.DoctorAvailabilityMapper;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link DoctorAvailability}.
 */
@Service
@Transactional
public class DoctorAvailabilityServiceImpl implements DoctorAvailabilityService {

    private final Logger log = LoggerFactory.getLogger(DoctorAvailabilityServiceImpl.class);

    private final DoctorAvailabilityRepository doctorAvailabilityRepository;

    private final DoctorAvailabilityMapper doctorAvailabilityMapper;

    public DoctorAvailabilityServiceImpl(
        DoctorAvailabilityRepository doctorAvailabilityRepository,
        DoctorAvailabilityMapper doctorAvailabilityMapper
    ) {
        this.doctorAvailabilityRepository = doctorAvailabilityRepository;
        this.doctorAvailabilityMapper = doctorAvailabilityMapper;
    }

    @Override
    public DoctorAvailabilityDTO save(DoctorAvailabilityDTO doctorAvailabilityDTO) {
        log.debug("Request to save DoctorAvailability : {}", doctorAvailabilityDTO);
        DoctorAvailability doctorAvailability = doctorAvailabilityMapper.toEntity(doctorAvailabilityDTO);
        doctorAvailability = doctorAvailabilityRepository.save(doctorAvailability);
        return doctorAvailabilityMapper.toDto(doctorAvailability);
    }

    @Override
    public DoctorAvailabilityDTO update(DoctorAvailabilityDTO doctorAvailabilityDTO) {
        log.debug("Request to update DoctorAvailability : {}", doctorAvailabilityDTO);
        DoctorAvailability doctorAvailability = doctorAvailabilityMapper.toEntity(doctorAvailabilityDTO);
        doctorAvailability = doctorAvailabilityRepository.save(doctorAvailability);
        return doctorAvailabilityMapper.toDto(doctorAvailability);
    }

    @Override
    public Optional<DoctorAvailabilityDTO> partialUpdate(DoctorAvailabilityDTO doctorAvailabilityDTO) {
        log.debug("Request to partially update DoctorAvailability : {}", doctorAvailabilityDTO);

        return doctorAvailabilityRepository
            .findById(doctorAvailabilityDTO.getId())
            .map(existingDoctorAvailability -> {
                doctorAvailabilityMapper.partialUpdate(existingDoctorAvailability, doctorAvailabilityDTO);

                return existingDoctorAvailability;
            })
            .map(doctorAvailabilityRepository::save)
            .map(doctorAvailabilityMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DoctorAvailabilityDTO> findAll(Pageable pageable) {
        log.debug("Request to get all DoctorAvailabilities");
        return doctorAvailabilityRepository.findAll(pageable).map(doctorAvailabilityMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DoctorAvailabilityDTO> findAllWithEagerRelationships(Pageable pageable) {
        return doctorAvailabilityRepository.findAll(pageable).map(doctorAvailabilityMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DoctorAvailabilityDTO> findOne(Long id) {
        log.debug("Request to get DoctorAvailability : {}", id);
        return doctorAvailabilityRepository.findById(id).map(doctorAvailabilityMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete DoctorAvailability : {}", id);
        doctorAvailabilityRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorAvailabilityDTO> findByDoctorId(String doctorId) {
        log.debug("Request to get all DoctorAvailabilities for doctor : {}", doctorId);
        return doctorAvailabilityRepository
            .findByDoctorId(doctorId)
            .stream()
            .map(doctorAvailabilityMapper::toDto)
            .collect(Collectors.toList());
    }
}
