package com.allomed.app.service;

import com.allomed.app.service.dto.SymptomSearchRecommendationDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.allomed.app.domain.SymptomSearchRecommendation}.
 */
public interface SymptomSearchRecommendationService {
    /**
     * Save a symptomSearchRecommendation.
     *
     * @param symptomSearchRecommendationDTO the entity to save.
     * @return the persisted entity.
     */
    SymptomSearchRecommendationDTO save(SymptomSearchRecommendationDTO symptomSearchRecommendationDTO);

    /**
     * Updates a symptomSearchRecommendation.
     *
     * @param symptomSearchRecommendationDTO the entity to update.
     * @return the persisted entity.
     */
    SymptomSearchRecommendationDTO update(SymptomSearchRecommendationDTO symptomSearchRecommendationDTO);

    /**
     * Partially updates a symptomSearchRecommendation.
     *
     * @param symptomSearchRecommendationDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<SymptomSearchRecommendationDTO> partialUpdate(SymptomSearchRecommendationDTO symptomSearchRecommendationDTO);

    /**
     * Get all the symptomSearchRecommendations.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<SymptomSearchRecommendationDTO> findAll(Pageable pageable);

    /**
     * Get the "id" symptomSearchRecommendation.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<SymptomSearchRecommendationDTO> findOne(Long id);

    /**
     * Delete the "id" symptomSearchRecommendation.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the symptomSearchRecommendation corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<SymptomSearchRecommendationDTO> search(String query, Pageable pageable);
}
