package com.allomed.app.service;

import com.allomed.app.service.dto.DoctorDocumentDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.allomed.app.domain.DoctorDocument}.
 */
public interface DoctorDocumentService {
    /**
     * Save a doctorDocument.
     *
     * @param doctorDocumentDTO the entity to save.
     * @return the persisted entity.
     */
    DoctorDocumentDTO save(DoctorDocumentDTO doctorDocumentDTO);

    /**
     * Updates a doctorDocument.
     *
     * @param doctorDocumentDTO the entity to update.
     * @return the persisted entity.
     */
    DoctorDocumentDTO update(DoctorDocumentDTO doctorDocumentDTO);

    /**
     * Partially updates a doctorDocument.
     *
     * @param doctorDocumentDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<DoctorDocumentDTO> partialUpdate(DoctorDocumentDTO doctorDocumentDTO);

    /**
     * Get all the doctorDocuments.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<DoctorDocumentDTO> findAll(Pageable pageable);

    /**
     * Get the "id" doctorDocument.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<DoctorDocumentDTO> findOne(Long id);

    /**
     * Delete the "id" doctorDocument.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the doctorDocument corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<DoctorDocumentDTO> search(String query, Pageable pageable);
}
