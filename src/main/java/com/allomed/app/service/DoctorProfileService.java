package com.allomed.app.service;

import com.allomed.app.service.dto.DoctorProfileDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.allomed.app.domain.DoctorProfile}.
 */
public interface DoctorProfileService {
    /**
     * Save a doctorProfile.
     *
     * @param doctorProfileDTO the entity to save.
     * @return the persisted entity.
     */
    DoctorProfileDTO save(DoctorProfileDTO doctorProfileDTO);

    /**
     * Updates a doctorProfile.
     *
     * @param doctorProfileDTO the entity to update.
     * @return the persisted entity.
     */
    DoctorProfileDTO update(DoctorProfileDTO doctorProfileDTO);

    /**
     * Partially updates a doctorProfile.
     *
     * @param doctorProfileDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<DoctorProfileDTO> partialUpdate(DoctorProfileDTO doctorProfileDTO);

    /**
     * Get all the doctorProfiles with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<DoctorProfileDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" doctorProfile.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<DoctorProfileDTO> findOne(String id);

    /**
     * Delete the "id" doctorProfile.
     *
     * @param id the id of the entity.
     */
    void delete(String id);

    /**
     * Search for the doctorProfile corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<DoctorProfileDTO> search(String query, Pageable pageable);
}
