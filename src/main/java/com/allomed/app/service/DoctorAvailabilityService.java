package com.allomed.app.service;

import com.allomed.app.service.dto.DoctorAvailabilityDTO;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.allomed.app.domain.DoctorAvailability}.
 */
public interface DoctorAvailabilityService {
    /**
     * Save a doctorAvailability.
     *
     * @param doctorAvailabilityDTO the entity to save.
     * @return the persisted entity.
     */
    DoctorAvailabilityDTO save(DoctorAvailabilityDTO doctorAvailabilityDTO);

    /**
     * Updates a doctorAvailability.
     *
     * @param doctorAvailabilityDTO the entity to update.
     * @return the persisted entity.
     */
    DoctorAvailabilityDTO update(DoctorAvailabilityDTO doctorAvailabilityDTO);

    /**
     * Partially updates a doctorAvailability.
     *
     * @param doctorAvailabilityDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<DoctorAvailabilityDTO> partialUpdate(DoctorAvailabilityDTO doctorAvailabilityDTO);

    /**
     * Get all the doctorAvailabilities.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<DoctorAvailabilityDTO> findAll(Pageable pageable);

    /**
     * Get all the doctorAvailabilities with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<DoctorAvailabilityDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" doctorAvailability.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<DoctorAvailabilityDTO> findOne(Long id);

    /**
     * Delete the "id" doctorAvailability.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Get all availabilities for a specific doctor.
     *
     * @param doctorId the id of the doctor.
     * @return the list of availabilities.
     */
    List<DoctorAvailabilityDTO> findByDoctorId(String doctorId);
}
