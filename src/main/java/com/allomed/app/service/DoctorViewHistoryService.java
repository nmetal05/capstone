package com.allomed.app.service;

import com.allomed.app.service.dto.DoctorViewHistoryDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.allomed.app.domain.DoctorViewHistory}.
 */
public interface DoctorViewHistoryService {
    /**
     * Save a doctorViewHistory.
     *
     * @param doctorViewHistoryDTO the entity to save.
     * @return the persisted entity.
     */
    DoctorViewHistoryDTO save(DoctorViewHistoryDTO doctorViewHistoryDTO);

    /**
     * Updates a doctorViewHistory.
     *
     * @param doctorViewHistoryDTO the entity to update.
     * @return the persisted entity.
     */
    DoctorViewHistoryDTO update(DoctorViewHistoryDTO doctorViewHistoryDTO);

    /**
     * Partially updates a doctorViewHistory.
     *
     * @param doctorViewHistoryDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<DoctorViewHistoryDTO> partialUpdate(DoctorViewHistoryDTO doctorViewHistoryDTO);

    /**
     * Get all the doctorViewHistories.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<DoctorViewHistoryDTO> findAll(Pageable pageable);

    /**
     * Get the "id" doctorViewHistory.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<DoctorViewHistoryDTO> findOne(Long id);

    /**
     * Delete the "id" doctorViewHistory.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the doctorViewHistory corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<DoctorViewHistoryDTO> search(String query, Pageable pageable);
}
