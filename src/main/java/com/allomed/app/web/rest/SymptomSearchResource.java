package com.allomed.app.web.rest;

import com.allomed.app.repository.SymptomSearchRepository;
import com.allomed.app.service.SymptomSearchService;
import com.allomed.app.service.dto.SymptomSearchDTO;
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
 * REST controller for managing {@link com.allomed.app.domain.SymptomSearch}.
 */
@RestController
@RequestMapping("/api/symptom-searches")
public class SymptomSearchResource {

    private static final Logger LOG = LoggerFactory.getLogger(SymptomSearchResource.class);

    private static final String ENTITY_NAME = "symptomSearch";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SymptomSearchService symptomSearchService;

    private final SymptomSearchRepository symptomSearchRepository;

    public SymptomSearchResource(SymptomSearchService symptomSearchService, SymptomSearchRepository symptomSearchRepository) {
        this.symptomSearchService = symptomSearchService;
        this.symptomSearchRepository = symptomSearchRepository;
    }

    /**
     * {@code POST  /symptom-searches} : Create a new symptomSearch.
     *
     * @param symptomSearchDTO the symptomSearchDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new symptomSearchDTO, or with status {@code 400 (Bad Request)} if the symptomSearch has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<SymptomSearchDTO> createSymptomSearch(@Valid @RequestBody SymptomSearchDTO symptomSearchDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save SymptomSearch : {}", symptomSearchDTO);
        if (symptomSearchDTO.getId() != null) {
            throw new BadRequestAlertException("A new symptomSearch cannot already have an ID", ENTITY_NAME, "idexists");
        }
        symptomSearchDTO = symptomSearchService.save(symptomSearchDTO);
        return ResponseEntity.created(new URI("/api/symptom-searches/" + symptomSearchDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, symptomSearchDTO.getId().toString()))
            .body(symptomSearchDTO);
    }

    /**
     * {@code PUT  /symptom-searches/:id} : Updates an existing symptomSearch.
     *
     * @param id the id of the symptomSearchDTO to save.
     * @param symptomSearchDTO the symptomSearchDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated symptomSearchDTO,
     * or with status {@code 400 (Bad Request)} if the symptomSearchDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the symptomSearchDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SymptomSearchDTO> updateSymptomSearch(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SymptomSearchDTO symptomSearchDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update SymptomSearch : {}, {}", id, symptomSearchDTO);
        if (symptomSearchDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, symptomSearchDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!symptomSearchRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        symptomSearchDTO = symptomSearchService.update(symptomSearchDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, symptomSearchDTO.getId().toString()))
            .body(symptomSearchDTO);
    }

    /**
     * {@code PATCH  /symptom-searches/:id} : Partial updates given fields of an existing symptomSearch, field will ignore if it is null
     *
     * @param id the id of the symptomSearchDTO to save.
     * @param symptomSearchDTO the symptomSearchDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated symptomSearchDTO,
     * or with status {@code 400 (Bad Request)} if the symptomSearchDTO is not valid,
     * or with status {@code 404 (Not Found)} if the symptomSearchDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the symptomSearchDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SymptomSearchDTO> partialUpdateSymptomSearch(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SymptomSearchDTO symptomSearchDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update SymptomSearch partially : {}, {}", id, symptomSearchDTO);
        if (symptomSearchDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, symptomSearchDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!symptomSearchRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SymptomSearchDTO> result = symptomSearchService.partialUpdate(symptomSearchDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, symptomSearchDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /symptom-searches} : get all the symptomSearches.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of symptomSearches in body.
     */
    @GetMapping("")
    public ResponseEntity<List<SymptomSearchDTO>> getAllSymptomSearches(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of SymptomSearches");
        Page<SymptomSearchDTO> page = symptomSearchService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /symptom-searches/:id} : get the "id" symptomSearch.
     *
     * @param id the id of the symptomSearchDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the symptomSearchDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SymptomSearchDTO> getSymptomSearch(@PathVariable("id") Long id) {
        LOG.debug("REST request to get SymptomSearch : {}", id);
        Optional<SymptomSearchDTO> symptomSearchDTO = symptomSearchService.findOne(id);
        return ResponseUtil.wrapOrNotFound(symptomSearchDTO);
    }

    /**
     * {@code DELETE  /symptom-searches/:id} : delete the "id" symptomSearch.
     *
     * @param id the id of the symptomSearchDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSymptomSearch(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete SymptomSearch : {}", id);
        symptomSearchService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /symptom-searches/_search?query=:query} : search for the symptomSearch corresponding
     * to the query.
     *
     * @param query the query of the symptomSearch search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<SymptomSearchDTO>> searchSymptomSearches(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of SymptomSearches for query {}", query);
        try {
            Page<SymptomSearchDTO> page = symptomSearchService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
