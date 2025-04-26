package com.allomed.app.service;

import com.allomed.app.service.dto.SpecializationDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.allomed.app.domain.Specialization}.
 */
public interface SpecializationService {
    /**
     * Save a specialization.
     *
     * @param specializationDTO the entity to save.
     * @return the persisted entity.
     */
    SpecializationDTO save(SpecializationDTO specializationDTO);

    /**
     * Updates a specialization.
     *
     * @param specializationDTO the entity to update.
     * @return the persisted entity.
     */
    SpecializationDTO update(SpecializationDTO specializationDTO);

    /**
     * Partially updates a specialization.
     *
     * @param specializationDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<SpecializationDTO> partialUpdate(SpecializationDTO specializationDTO);

    /**
     * Get the "id" specialization.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<SpecializationDTO> findOne(Long id);

    /**
     * Delete the "id" specialization.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the specialization corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<SpecializationDTO> search(String query, Pageable pageable);
}
