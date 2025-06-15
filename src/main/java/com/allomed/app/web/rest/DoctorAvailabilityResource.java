package com.allomed.app.web.rest;

import com.allomed.app.repository.DoctorAvailabilityRepository;
import com.allomed.app.service.DoctorAvailabilityService;
import com.allomed.app.service.dto.DoctorAvailabilityDTO;
import com.allomed.app.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.allomed.app.domain.DoctorAvailability}.
 */
@RestController
@RequestMapping("/api")
public class DoctorAvailabilityResource {

    private final Logger log = LoggerFactory.getLogger(DoctorAvailabilityResource.class);

    private static final String ENTITY_NAME = "doctorAvailability";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DoctorAvailabilityService doctorAvailabilityService;

    private final DoctorAvailabilityRepository doctorAvailabilityRepository;

    public DoctorAvailabilityResource(
        DoctorAvailabilityService doctorAvailabilityService,
        DoctorAvailabilityRepository doctorAvailabilityRepository
    ) {
        this.doctorAvailabilityService = doctorAvailabilityService;
        this.doctorAvailabilityRepository = doctorAvailabilityRepository;
    }

    /**
     * {@code POST  /doctor-availabilities} : Create a new doctorAvailability.
     *
     * @param doctorAvailabilityDTO the doctorAvailabilityDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new doctorAvailabilityDTO, or with status {@code 400 (Bad Request)} if the doctorAvailability has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/doctor-availabilities")
    public ResponseEntity<DoctorAvailabilityDTO> createDoctorAvailability(@Valid @RequestBody DoctorAvailabilityDTO doctorAvailabilityDTO)
        throws URISyntaxException {
        log.debug("REST request to save DoctorAvailability : {}", doctorAvailabilityDTO);
        if (doctorAvailabilityDTO.getId() != null) {
            throw new BadRequestAlertException("A new doctorAvailability cannot already have an ID", ENTITY_NAME, "idexists");
        }
        DoctorAvailabilityDTO result = doctorAvailabilityService.save(doctorAvailabilityDTO);
        return ResponseEntity.created(new URI("/api/doctor-availabilities/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /doctor-availabilities/:id} : Updates an existing doctorAvailability.
     *
     * @param id the id of the doctorAvailabilityDTO to save.
     * @param doctorAvailabilityDTO the doctorAvailabilityDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated doctorAvailabilityDTO,
     * or with status {@code 400 (Bad Request)} if the doctorAvailabilityDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the doctorAvailabilityDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/doctor-availabilities/{id}")
    public ResponseEntity<DoctorAvailabilityDTO> updateDoctorAvailability(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody DoctorAvailabilityDTO doctorAvailabilityDTO
    ) throws URISyntaxException {
        log.debug("REST request to update DoctorAvailability : {}, {}", id, doctorAvailabilityDTO);
        if (doctorAvailabilityDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, doctorAvailabilityDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!doctorAvailabilityRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        DoctorAvailabilityDTO result = doctorAvailabilityService.update(doctorAvailabilityDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, doctorAvailabilityDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /doctor-availabilities/:id} : Partial updates given fields of an existing doctorAvailability, field will ignore if it is null
     *
     * @param id the id of the doctorAvailabilityDTO to save.
     * @param doctorAvailabilityDTO the doctorAvailabilityDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated doctorAvailabilityDTO,
     * or with status {@code 400 (Bad Request)} if the doctorAvailabilityDTO is not valid,
     * or with status {@code 404 (Not Found)} if the doctorAvailabilityDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the doctorAvailabilityDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/doctor-availabilities/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<DoctorAvailabilityDTO> partialUpdateDoctorAvailability(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody DoctorAvailabilityDTO doctorAvailabilityDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update DoctorAvailability partially : {}, {}", id, doctorAvailabilityDTO);
        if (doctorAvailabilityDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, doctorAvailabilityDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!doctorAvailabilityRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<DoctorAvailabilityDTO> result = doctorAvailabilityService.partialUpdate(doctorAvailabilityDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, doctorAvailabilityDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /doctor-availabilities} : get all the doctorAvailabilities.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of doctorAvailabilities in body.
     */
    @GetMapping("/doctor-availabilities")
    public ResponseEntity<List<DoctorAvailabilityDTO>> getAllDoctorAvailabilities(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get a page of DoctorAvailabilities");
        Page<DoctorAvailabilityDTO> page = doctorAvailabilityService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /doctor-availabilities/:id} : get the "id" doctorAvailability.
     *
     * @param id the id of the doctorAvailabilityDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the doctorAvailabilityDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/doctor-availabilities/{id}")
    public ResponseEntity<DoctorAvailabilityDTO> getDoctorAvailability(@PathVariable Long id) {
        log.debug("REST request to get DoctorAvailability : {}", id);
        Optional<DoctorAvailabilityDTO> doctorAvailabilityDTO = doctorAvailabilityService.findOne(id);
        return ResponseUtil.wrapOrNotFound(doctorAvailabilityDTO);
    }

    /**
     * {@code DELETE  /doctor-availabilities/:id} : delete the "id" doctorAvailability.
     *
     * @param id the id of the doctorAvailabilityDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/doctor-availabilities/{id}")
    public ResponseEntity<Void> deleteDoctorAvailability(@PathVariable Long id) {
        log.debug("REST request to delete DoctorAvailability : {}", id);
        doctorAvailabilityService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code GET  /doctor-availabilities/doctor/:doctorId} : get all availabilities for a specific doctor.
     *
     * @param doctorId the id of the doctor.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of availabilities in body.
     */
    @GetMapping("/doctor-availabilities/doctor/{doctorId}")
    public ResponseEntity<List<DoctorAvailabilityDTO>> getDoctorAvailabilities(@PathVariable String doctorId) {
        log.debug("REST request to get DoctorAvailabilities for doctor : {}", doctorId);
        List<DoctorAvailabilityDTO> availabilities = doctorAvailabilityService.findByDoctorId(doctorId);
        return ResponseEntity.ok().body(availabilities);
    }
}
