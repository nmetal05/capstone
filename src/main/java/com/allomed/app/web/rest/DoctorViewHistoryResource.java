package com.allomed.app.web.rest;

import com.allomed.app.repository.DoctorViewHistoryRepository;
import com.allomed.app.service.DoctorViewHistoryService;
import com.allomed.app.service.dto.DoctorViewHistoryDTO;
import com.allomed.app.web.rest.errors.BadRequestAlertException;
import com.allomed.app.web.rest.errors.ElasticsearchExceptionMapper;
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
 * REST controller for managing {@link com.allomed.app.domain.DoctorViewHistory}.
 */
@RestController
@RequestMapping("/api/doctor-view-histories")
public class DoctorViewHistoryResource {

    private static final Logger LOG = LoggerFactory.getLogger(DoctorViewHistoryResource.class);

    private static final String ENTITY_NAME = "doctorViewHistory";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DoctorViewHistoryService doctorViewHistoryService;

    private final DoctorViewHistoryRepository doctorViewHistoryRepository;

    public DoctorViewHistoryResource(
        DoctorViewHistoryService doctorViewHistoryService,
        DoctorViewHistoryRepository doctorViewHistoryRepository
    ) {
        this.doctorViewHistoryService = doctorViewHistoryService;
        this.doctorViewHistoryRepository = doctorViewHistoryRepository;
    }

    /**
     * {@code POST  /doctor-view-histories} : Create a new doctorViewHistory.
     *
     * @param doctorViewHistoryDTO the doctorViewHistoryDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new doctorViewHistoryDTO, or with status {@code 400 (Bad Request)} if the doctorViewHistory has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<DoctorViewHistoryDTO> createDoctorViewHistory(@Valid @RequestBody DoctorViewHistoryDTO doctorViewHistoryDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save DoctorViewHistory : {}", doctorViewHistoryDTO);
        if (doctorViewHistoryDTO.getId() != null) {
            throw new BadRequestAlertException("A new doctorViewHistory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        doctorViewHistoryDTO = doctorViewHistoryService.save(doctorViewHistoryDTO);
        return ResponseEntity.created(new URI("/api/doctor-view-histories/" + doctorViewHistoryDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, doctorViewHistoryDTO.getId().toString()))
            .body(doctorViewHistoryDTO);
    }

    /**
     * {@code PUT  /doctor-view-histories/:id} : Updates an existing doctorViewHistory.
     *
     * @param id the id of the doctorViewHistoryDTO to save.
     * @param doctorViewHistoryDTO the doctorViewHistoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated doctorViewHistoryDTO,
     * or with status {@code 400 (Bad Request)} if the doctorViewHistoryDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the doctorViewHistoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<DoctorViewHistoryDTO> updateDoctorViewHistory(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody DoctorViewHistoryDTO doctorViewHistoryDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update DoctorViewHistory : {}, {}", id, doctorViewHistoryDTO);
        if (doctorViewHistoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, doctorViewHistoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!doctorViewHistoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        doctorViewHistoryDTO = doctorViewHistoryService.update(doctorViewHistoryDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, doctorViewHistoryDTO.getId().toString()))
            .body(doctorViewHistoryDTO);
    }

    /**
     * {@code PATCH  /doctor-view-histories/:id} : Partial updates given fields of an existing doctorViewHistory, field will ignore if it is null
     *
     * @param id the id of the doctorViewHistoryDTO to save.
     * @param doctorViewHistoryDTO the doctorViewHistoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated doctorViewHistoryDTO,
     * or with status {@code 400 (Bad Request)} if the doctorViewHistoryDTO is not valid,
     * or with status {@code 404 (Not Found)} if the doctorViewHistoryDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the doctorViewHistoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<DoctorViewHistoryDTO> partialUpdateDoctorViewHistory(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody DoctorViewHistoryDTO doctorViewHistoryDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update DoctorViewHistory partially : {}, {}", id, doctorViewHistoryDTO);
        if (doctorViewHistoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, doctorViewHistoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!doctorViewHistoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<DoctorViewHistoryDTO> result = doctorViewHistoryService.partialUpdate(doctorViewHistoryDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, doctorViewHistoryDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /doctor-view-histories} : get all the doctorViewHistories.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of doctorViewHistories in body.
     */
    @GetMapping("")
    public ResponseEntity<List<DoctorViewHistoryDTO>> getAllDoctorViewHistories(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of DoctorViewHistories");
        Page<DoctorViewHistoryDTO> page = doctorViewHistoryService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /doctor-view-histories/:id} : get the "id" doctorViewHistory.
     *
     * @param id the id of the doctorViewHistoryDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the doctorViewHistoryDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DoctorViewHistoryDTO> getDoctorViewHistory(@PathVariable("id") Long id) {
        LOG.debug("REST request to get DoctorViewHistory : {}", id);
        Optional<DoctorViewHistoryDTO> doctorViewHistoryDTO = doctorViewHistoryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(doctorViewHistoryDTO);
    }

    /**
     * {@code DELETE  /doctor-view-histories/:id} : delete the "id" doctorViewHistory.
     *
     * @param id the id of the doctorViewHistoryDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctorViewHistory(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete DoctorViewHistory : {}", id);
        doctorViewHistoryService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /doctor-view-histories/_search?query=:query} : search for the doctorViewHistory corresponding
     * to the query.
     *
     * @param query the query of the doctorViewHistory search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<DoctorViewHistoryDTO>> searchDoctorViewHistories(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of DoctorViewHistories for query {}", query);
        try {
            Page<DoctorViewHistoryDTO> page = doctorViewHistoryService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
