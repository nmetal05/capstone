package com.allomed.app.web.rest;

import com.allomed.app.repository.DoctorDocumentRepository;
import com.allomed.app.service.DoctorDocumentService;
import com.allomed.app.service.dto.DoctorDocumentDTO;
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
 * REST controller for managing {@link com.allomed.app.domain.DoctorDocument}.
 */
@RestController
@RequestMapping("/api/doctor-documents")
public class DoctorDocumentResource {

    private static final Logger LOG = LoggerFactory.getLogger(DoctorDocumentResource.class);

    private static final String ENTITY_NAME = "doctorDocument";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DoctorDocumentService doctorDocumentService;

    private final DoctorDocumentRepository doctorDocumentRepository;

    public DoctorDocumentResource(DoctorDocumentService doctorDocumentService, DoctorDocumentRepository doctorDocumentRepository) {
        this.doctorDocumentService = doctorDocumentService;
        this.doctorDocumentRepository = doctorDocumentRepository;
    }

    /**
     * {@code POST  /doctor-documents} : Create a new doctorDocument.
     *
     * @param doctorDocumentDTO the doctorDocumentDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new doctorDocumentDTO, or with status {@code 400 (Bad Request)} if the doctorDocument has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<DoctorDocumentDTO> createDoctorDocument(@Valid @RequestBody DoctorDocumentDTO doctorDocumentDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save DoctorDocument : {}", doctorDocumentDTO);
        if (doctorDocumentDTO.getId() != null) {
            throw new BadRequestAlertException("A new doctorDocument cannot already have an ID", ENTITY_NAME, "idexists");
        }
        doctorDocumentDTO = doctorDocumentService.save(doctorDocumentDTO);
        return ResponseEntity.created(new URI("/api/doctor-documents/" + doctorDocumentDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, doctorDocumentDTO.getId().toString()))
            .body(doctorDocumentDTO);
    }

    /**
     * {@code PUT  /doctor-documents/:id} : Updates an existing doctorDocument.
     *
     * @param id the id of the doctorDocumentDTO to save.
     * @param doctorDocumentDTO the doctorDocumentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated doctorDocumentDTO,
     * or with status {@code 400 (Bad Request)} if the doctorDocumentDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the doctorDocumentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<DoctorDocumentDTO> updateDoctorDocument(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody DoctorDocumentDTO doctorDocumentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update DoctorDocument : {}, {}", id, doctorDocumentDTO);
        if (doctorDocumentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, doctorDocumentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!doctorDocumentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        doctorDocumentDTO = doctorDocumentService.update(doctorDocumentDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, doctorDocumentDTO.getId().toString()))
            .body(doctorDocumentDTO);
    }

    /**
     * {@code PATCH  /doctor-documents/:id} : Partial updates given fields of an existing doctorDocument, field will ignore if it is null
     *
     * @param id the id of the doctorDocumentDTO to save.
     * @param doctorDocumentDTO the doctorDocumentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated doctorDocumentDTO,
     * or with status {@code 400 (Bad Request)} if the doctorDocumentDTO is not valid,
     * or with status {@code 404 (Not Found)} if the doctorDocumentDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the doctorDocumentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<DoctorDocumentDTO> partialUpdateDoctorDocument(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody DoctorDocumentDTO doctorDocumentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update DoctorDocument partially : {}, {}", id, doctorDocumentDTO);
        if (doctorDocumentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, doctorDocumentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!doctorDocumentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<DoctorDocumentDTO> result = doctorDocumentService.partialUpdate(doctorDocumentDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, doctorDocumentDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /doctor-documents} : get all the doctorDocuments.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of doctorDocuments in body.
     */
    @GetMapping("")
    public ResponseEntity<List<DoctorDocumentDTO>> getAllDoctorDocuments(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of DoctorDocuments");
        Page<DoctorDocumentDTO> page = doctorDocumentService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /doctor-documents/:id} : get the "id" doctorDocument.
     *
     * @param id the id of the doctorDocumentDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the doctorDocumentDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DoctorDocumentDTO> getDoctorDocument(@PathVariable("id") Long id) {
        LOG.debug("REST request to get DoctorDocument : {}", id);
        Optional<DoctorDocumentDTO> doctorDocumentDTO = doctorDocumentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(doctorDocumentDTO);
    }

    /**
     * {@code DELETE  /doctor-documents/:id} : delete the "id" doctorDocument.
     *
     * @param id the id of the doctorDocumentDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctorDocument(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete DoctorDocument : {}", id);
        doctorDocumentService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /doctor-documents/_search?query=:query} : search for the doctorDocument corresponding
     * to the query.
     *
     * @param query the query of the doctorDocument search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<DoctorDocumentDTO>> searchDoctorDocuments(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of DoctorDocuments for query {}", query);
        try {
            Page<DoctorDocumentDTO> page = doctorDocumentService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
