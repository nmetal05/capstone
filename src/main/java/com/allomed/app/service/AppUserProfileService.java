package com.allomed.app.service;

import com.allomed.app.service.dto.AppUserProfileDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.allomed.app.domain.AppUserProfile}.
 */
public interface AppUserProfileService {
    /**
     * Save a appUserProfile.
     *
     * @param appUserProfileDTO the entity to save.
     * @return the persisted entity.
     */
    AppUserProfileDTO save(AppUserProfileDTO appUserProfileDTO);

    /**
     * Updates a appUserProfile.
     *
     * @param appUserProfileDTO the entity to update.
     * @return the persisted entity.
     */
    AppUserProfileDTO update(AppUserProfileDTO appUserProfileDTO);

    /**
     * Partially updates a appUserProfile.
     *
     * @param appUserProfileDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<AppUserProfileDTO> partialUpdate(AppUserProfileDTO appUserProfileDTO);

    /**
     * Get all the appUserProfiles.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<AppUserProfileDTO> findAll(Pageable pageable);

    /**
     * Get all the appUserProfiles with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<AppUserProfileDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" appUserProfile.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<AppUserProfileDTO> findOne(String id);

    /**
     * Delete the "id" appUserProfile.
     *
     * @param id the id of the entity.
     */
    void delete(String id);

    /**
     * Search for the appUserProfile corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<AppUserProfileDTO> search(String query, Pageable pageable);
}
