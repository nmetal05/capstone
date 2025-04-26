package com.allomed.app.service.impl;

import com.allomed.app.domain.DoctorProfile;
import com.allomed.app.repository.DoctorProfileRepository;
import com.allomed.app.repository.UserRepository;
import com.allomed.app.repository.search.DoctorProfileSearchRepository;
import com.allomed.app.service.DoctorProfileService;
import com.allomed.app.service.dto.DoctorProfileDTO;
import com.allomed.app.service.mapper.DoctorProfileMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.allomed.app.domain.DoctorProfile}.
 */
@Service
@Transactional
public class DoctorProfileServiceImpl implements DoctorProfileService {

    private static final Logger LOG = LoggerFactory.getLogger(DoctorProfileServiceImpl.class);

    private final DoctorProfileRepository doctorProfileRepository;

    private final DoctorProfileMapper doctorProfileMapper;

    private final DoctorProfileSearchRepository doctorProfileSearchRepository;

    private final UserRepository userRepository;

    public DoctorProfileServiceImpl(
        DoctorProfileRepository doctorProfileRepository,
        DoctorProfileMapper doctorProfileMapper,
        DoctorProfileSearchRepository doctorProfileSearchRepository,
        UserRepository userRepository
    ) {
        this.doctorProfileRepository = doctorProfileRepository;
        this.doctorProfileMapper = doctorProfileMapper;
        this.doctorProfileSearchRepository = doctorProfileSearchRepository;
        this.userRepository = userRepository;
    }

    @Override
    public DoctorProfileDTO save(DoctorProfileDTO doctorProfileDTO) {
        LOG.debug("Request to save DoctorProfile : {}", doctorProfileDTO);
        DoctorProfile doctorProfile = doctorProfileMapper.toEntity(doctorProfileDTO);
        String userId = doctorProfile.getInternalUser().getId();
        userRepository.findById(userId).ifPresent(doctorProfile::internalUser);
        doctorProfile = doctorProfileRepository.save(doctorProfile);
        doctorProfileSearchRepository.index(doctorProfile);
        return doctorProfileMapper.toDto(doctorProfile);
    }

    @Override
    public DoctorProfileDTO update(DoctorProfileDTO doctorProfileDTO) {
        LOG.debug("Request to update DoctorProfile : {}", doctorProfileDTO);
        DoctorProfile doctorProfile = doctorProfileMapper.toEntity(doctorProfileDTO);
        doctorProfile.setIsPersisted();
        doctorProfile = doctorProfileRepository.save(doctorProfile);
        doctorProfileSearchRepository.index(doctorProfile);
        return doctorProfileMapper.toDto(doctorProfile);
    }

    @Override
    public Optional<DoctorProfileDTO> partialUpdate(DoctorProfileDTO doctorProfileDTO) {
        LOG.debug("Request to partially update DoctorProfile : {}", doctorProfileDTO);

        return doctorProfileRepository
            .findById(doctorProfileDTO.getId())
            .map(existingDoctorProfile -> {
                doctorProfileMapper.partialUpdate(existingDoctorProfile, doctorProfileDTO);

                return existingDoctorProfile;
            })
            .map(doctorProfileRepository::save)
            .map(savedDoctorProfile -> {
                doctorProfileSearchRepository.index(savedDoctorProfile);
                return savedDoctorProfile;
            })
            .map(doctorProfileMapper::toDto);
    }

    public Page<DoctorProfileDTO> findAllWithEagerRelationships(Pageable pageable) {
        return doctorProfileRepository.findAllWithEagerRelationships(pageable).map(doctorProfileMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DoctorProfileDTO> findOne(String id) {
        LOG.debug("Request to get DoctorProfile : {}", id);
        return doctorProfileRepository.findOneWithEagerRelationships(id).map(doctorProfileMapper::toDto);
    }

    @Override
    public void delete(String id) {
        LOG.debug("Request to delete DoctorProfile : {}", id);
        doctorProfileRepository.deleteById(id);
        doctorProfileSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DoctorProfileDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of DoctorProfiles for query {}", query);
        return doctorProfileSearchRepository.search(query, pageable).map(doctorProfileMapper::toDto);
    }
}
