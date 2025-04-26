package com.allomed.app.service.impl;

import com.allomed.app.domain.AppUserProfile;
import com.allomed.app.repository.AppUserProfileRepository;
import com.allomed.app.repository.UserRepository;
import com.allomed.app.repository.search.AppUserProfileSearchRepository;
import com.allomed.app.service.AppUserProfileService;
import com.allomed.app.service.dto.AppUserProfileDTO;
import com.allomed.app.service.mapper.AppUserProfileMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.allomed.app.domain.AppUserProfile}.
 */
@Service
@Transactional
public class AppUserProfileServiceImpl implements AppUserProfileService {

    private static final Logger LOG = LoggerFactory.getLogger(AppUserProfileServiceImpl.class);

    private final AppUserProfileRepository appUserProfileRepository;

    private final AppUserProfileMapper appUserProfileMapper;

    private final AppUserProfileSearchRepository appUserProfileSearchRepository;

    private final UserRepository userRepository;

    public AppUserProfileServiceImpl(
        AppUserProfileRepository appUserProfileRepository,
        AppUserProfileMapper appUserProfileMapper,
        AppUserProfileSearchRepository appUserProfileSearchRepository,
        UserRepository userRepository
    ) {
        this.appUserProfileRepository = appUserProfileRepository;
        this.appUserProfileMapper = appUserProfileMapper;
        this.appUserProfileSearchRepository = appUserProfileSearchRepository;
        this.userRepository = userRepository;
    }

    @Override
    public AppUserProfileDTO save(AppUserProfileDTO appUserProfileDTO) {
        LOG.debug("Request to save AppUserProfile : {}", appUserProfileDTO);
        AppUserProfile appUserProfile = appUserProfileMapper.toEntity(appUserProfileDTO);
        String userId = appUserProfile.getInternalUser().getId();
        userRepository.findById(userId).ifPresent(appUserProfile::internalUser);
        appUserProfile = appUserProfileRepository.save(appUserProfile);
        appUserProfileSearchRepository.index(appUserProfile);
        return appUserProfileMapper.toDto(appUserProfile);
    }

    @Override
    public AppUserProfileDTO update(AppUserProfileDTO appUserProfileDTO) {
        LOG.debug("Request to update AppUserProfile : {}", appUserProfileDTO);
        AppUserProfile appUserProfile = appUserProfileMapper.toEntity(appUserProfileDTO);
        appUserProfile.setIsPersisted();
        appUserProfile = appUserProfileRepository.save(appUserProfile);
        appUserProfileSearchRepository.index(appUserProfile);
        return appUserProfileMapper.toDto(appUserProfile);
    }

    @Override
    public Optional<AppUserProfileDTO> partialUpdate(AppUserProfileDTO appUserProfileDTO) {
        LOG.debug("Request to partially update AppUserProfile : {}", appUserProfileDTO);

        return appUserProfileRepository
            .findById(appUserProfileDTO.getId())
            .map(existingAppUserProfile -> {
                appUserProfileMapper.partialUpdate(existingAppUserProfile, appUserProfileDTO);

                return existingAppUserProfile;
            })
            .map(appUserProfileRepository::save)
            .map(savedAppUserProfile -> {
                appUserProfileSearchRepository.index(savedAppUserProfile);
                return savedAppUserProfile;
            })
            .map(appUserProfileMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AppUserProfileDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all AppUserProfiles");
        return appUserProfileRepository.findAll(pageable).map(appUserProfileMapper::toDto);
    }

    public Page<AppUserProfileDTO> findAllWithEagerRelationships(Pageable pageable) {
        return appUserProfileRepository.findAllWithEagerRelationships(pageable).map(appUserProfileMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AppUserProfileDTO> findOne(String id) {
        LOG.debug("Request to get AppUserProfile : {}", id);
        return appUserProfileRepository.findOneWithEagerRelationships(id).map(appUserProfileMapper::toDto);
    }

    @Override
    public void delete(String id) {
        LOG.debug("Request to delete AppUserProfile : {}", id);
        appUserProfileRepository.deleteById(id);
        appUserProfileSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AppUserProfileDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of AppUserProfiles for query {}", query);
        return appUserProfileSearchRepository.search(query, pageable).map(appUserProfileMapper::toDto);
    }
}
