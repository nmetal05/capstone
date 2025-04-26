package com.allomed.app.service;

import com.allomed.app.service.dto.GuestSessionDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.allomed.app.domain.GuestSession}.
 */
public interface GuestSessionService {
    /**
     * Save a guestSession.
     *
     * @param guestSessionDTO the entity to save.
     * @return the persisted entity.
     */
    GuestSessionDTO save(GuestSessionDTO guestSessionDTO);

    /**
     * Updates a guestSession.
     *
     * @param guestSessionDTO the entity to update.
     * @return the persisted entity.
     */
    GuestSessionDTO update(GuestSessionDTO guestSessionDTO);

    /**
     * Partially updates a guestSession.
     *
     * @param guestSessionDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<GuestSessionDTO> partialUpdate(GuestSessionDTO guestSessionDTO);

    /**
     * Get all the guestSessions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<GuestSessionDTO> findAll(Pageable pageable);

    /**
     * Get the "id" guestSession.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<GuestSessionDTO> findOne(Long id);

    /**
     * Delete the "id" guestSession.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the guestSession corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<GuestSessionDTO> search(String query, Pageable pageable);
}
