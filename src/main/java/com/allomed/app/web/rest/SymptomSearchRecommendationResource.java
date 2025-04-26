package com.allomed.app.web.rest;

import com.allomed.app.repository.SymptomSearchRecommendationRepository;
import com.allomed.app.service.SymptomSearchRecommendationService;
import com.allomed.app.service.dto.SymptomSearchRecommendationDTO;
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
 * REST controller for managing {@link com.allomed.app.domain.SymptomSearchRecommendation}.
 */
@RestController
@RequestMapping("/api/symptom-search-recommendations")
public class SymptomSearchRecommendationResource {

    private static final Logger LOG = LoggerFactory.getLogger(SymptomSearchRecommendationResource.class);

    private static final String ENTITY_NAME = "symptomSearchRecommendation";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SymptomSearchRecommendationService symptomSearchRecommendationService;

    private final SymptomSearchRecommendationRepository symptomSearchRecommendationRepository;

    public SymptomSearchRecommendationResource(
        SymptomSearchRecommendationService symptomSearchRecommendationService,
        SymptomSearchRecommendationRepository symptomSearchRecommendationRepository
    ) {
        this.symptomSearchRecommendationService = symptomSearchRecommendationService;
        this.symptomSearchRecommendationRepository = symptomSearchRecommendationRepository;
    }

    /**
     * {@code POST  /symptom-search-recommendations} : Create a new symptomSearchRecommendation.
     *
     * @param symptomSearchRecommendationDTO the symptomSearchRecommendationDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new symptomSearchRecommendationDTO, or with status {@code 400 (Bad Request)} if the symptomSearchRecommendation has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<SymptomSearchRecommendationDTO> createSymptomSearchRecommendation(
        @Valid @RequestBody SymptomSearchRecommendationDTO symptomSearchRecommendationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save SymptomSearchRecommendation : {}", symptomSearchRecommendationDTO);
        if (symptomSearchRecommendationDTO.getId() != null) {
            throw new BadRequestAlertException("A new symptomSearchRecommendation cannot already have an ID", ENTITY_NAME, "idexists");
        }
        symptomSearchRecommendationDTO = symptomSearchRecommendationService.save(symptomSearchRecommendationDTO);
        return ResponseEntity.created(new URI("/api/symptom-search-recommendations/" + symptomSearchRecommendationDTO.getId()))
            .headers(
                HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, symptomSearchRecommendationDTO.getId().toString())
            )
            .body(symptomSearchRecommendationDTO);
    }

    /**
     * {@code PUT  /symptom-search-recommendations/:id} : Updates an existing symptomSearchRecommendation.
     *
     * @param id the id of the symptomSearchRecommendationDTO to save.
     * @param symptomSearchRecommendationDTO the symptomSearchRecommendationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated symptomSearchRecommendationDTO,
     * or with status {@code 400 (Bad Request)} if the symptomSearchRecommendationDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the symptomSearchRecommendationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SymptomSearchRecommendationDTO> updateSymptomSearchRecommendation(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SymptomSearchRecommendationDTO symptomSearchRecommendationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update SymptomSearchRecommendation : {}, {}", id, symptomSearchRecommendationDTO);
        if (symptomSearchRecommendationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, symptomSearchRecommendationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!symptomSearchRecommendationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        symptomSearchRecommendationDTO = symptomSearchRecommendationService.update(symptomSearchRecommendationDTO);
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, symptomSearchRecommendationDTO.getId().toString())
            )
            .body(symptomSearchRecommendationDTO);
    }

    /**
     * {@code PATCH  /symptom-search-recommendations/:id} : Partial updates given fields of an existing symptomSearchRecommendation, field will ignore if it is null
     *
     * @param id the id of the symptomSearchRecommendationDTO to save.
     * @param symptomSearchRecommendationDTO the symptomSearchRecommendationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated symptomSearchRecommendationDTO,
     * or with status {@code 400 (Bad Request)} if the symptomSearchRecommendationDTO is not valid,
     * or with status {@code 404 (Not Found)} if the symptomSearchRecommendationDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the symptomSearchRecommendationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SymptomSearchRecommendationDTO> partialUpdateSymptomSearchRecommendation(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SymptomSearchRecommendationDTO symptomSearchRecommendationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update SymptomSearchRecommendation partially : {}, {}", id, symptomSearchRecommendationDTO);
        if (symptomSearchRecommendationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, symptomSearchRecommendationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!symptomSearchRecommendationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SymptomSearchRecommendationDTO> result = symptomSearchRecommendationService.partialUpdate(symptomSearchRecommendationDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, symptomSearchRecommendationDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /symptom-search-recommendations} : get all the symptomSearchRecommendations.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of symptomSearchRecommendations in body.
     */
    @GetMapping("")
    public ResponseEntity<List<SymptomSearchRecommendationDTO>> getAllSymptomSearchRecommendations(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of SymptomSearchRecommendations");
        Page<SymptomSearchRecommendationDTO> page = symptomSearchRecommendationService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /symptom-search-recommendations/:id} : get the "id" symptomSearchRecommendation.
     *
     * @param id the id of the symptomSearchRecommendationDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the symptomSearchRecommendationDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SymptomSearchRecommendationDTO> getSymptomSearchRecommendation(@PathVariable("id") Long id) {
        LOG.debug("REST request to get SymptomSearchRecommendation : {}", id);
        Optional<SymptomSearchRecommendationDTO> symptomSearchRecommendationDTO = symptomSearchRecommendationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(symptomSearchRecommendationDTO);
    }

    /**
     * {@code DELETE  /symptom-search-recommendations/:id} : delete the "id" symptomSearchRecommendation.
     *
     * @param id the id of the symptomSearchRecommendationDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSymptomSearchRecommendation(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete SymptomSearchRecommendation : {}", id);
        symptomSearchRecommendationService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /symptom-search-recommendations/_search?query=:query} : search for the symptomSearchRecommendation corresponding
     * to the query.
     *
     * @param query the query of the symptomSearchRecommendation search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<SymptomSearchRecommendationDTO>> searchSymptomSearchRecommendations(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of SymptomSearchRecommendations for query {}", query);
        try {
            Page<SymptomSearchRecommendationDTO> page = symptomSearchRecommendationService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
