package com.allomed.app.service;

import com.allomed.app.service.dto.SymptomSearchDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.allomed.app.domain.SymptomSearch}.
 */
public interface SymptomSearchService {
    /**
     * Save a symptomSearch.
     *
     * @param symptomSearchDTO the entity to save.
     * @return the persisted entity.
     */
    SymptomSearchDTO save(SymptomSearchDTO symptomSearchDTO);

    /**
     * Updates a symptomSearch.
     *
     * @param symptomSearchDTO the entity to update.
     * @return the persisted entity.
     */
    SymptomSearchDTO update(SymptomSearchDTO symptomSearchDTO);

    /**
     * Partially updates a symptomSearch.
     *
     * @param symptomSearchDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<SymptomSearchDTO> partialUpdate(SymptomSearchDTO symptomSearchDTO);

    /**
     * Get all the symptomSearches.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<SymptomSearchDTO> findAll(Pageable pageable);

    /**
     * Get the "id" symptomSearch.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<SymptomSearchDTO> findOne(Long id);

    /**
     * Delete the "id" symptomSearch.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the symptomSearch corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<SymptomSearchDTO> search(String query, Pageable pageable);
}
