package com.allomed.app.web.rest;

import com.allomed.app.repository.GuestSessionRepository;
import com.allomed.app.service.GuestSessionService;
import com.allomed.app.service.dto.GuestSessionDTO;
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
 * REST controller for managing {@link com.allomed.app.domain.GuestSession}.
 */
@RestController
@RequestMapping("/api/guest-sessions")
public class GuestSessionResource {

    private static final Logger LOG = LoggerFactory.getLogger(GuestSessionResource.class);

    private static final String ENTITY_NAME = "guestSession";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final GuestSessionService guestSessionService;

    private final GuestSessionRepository guestSessionRepository;

    public GuestSessionResource(GuestSessionService guestSessionService, GuestSessionRepository guestSessionRepository) {
        this.guestSessionService = guestSessionService;
        this.guestSessionRepository = guestSessionRepository;
    }

    /**
     * {@code POST  /guest-sessions} : Create a new guestSession.
     *
     * @param guestSessionDTO the guestSessionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new guestSessionDTO, or with status {@code 400 (Bad Request)} if the guestSession has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<GuestSessionDTO> createGuestSession(@Valid @RequestBody GuestSessionDTO guestSessionDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save GuestSession : {}", guestSessionDTO);
        if (guestSessionDTO.getId() != null) {
            throw new BadRequestAlertException("A new guestSession cannot already have an ID", ENTITY_NAME, "idexists");
        }
        guestSessionDTO = guestSessionService.save(guestSessionDTO);
        return ResponseEntity.created(new URI("/api/guest-sessions/" + guestSessionDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, guestSessionDTO.getId().toString()))
            .body(guestSessionDTO);
    }

    /**
     * {@code PUT  /guest-sessions/:id} : Updates an existing guestSession.
     *
     * @param id the id of the guestSessionDTO to save.
     * @param guestSessionDTO the guestSessionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated guestSessionDTO,
     * or with status {@code 400 (Bad Request)} if the guestSessionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the guestSessionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<GuestSessionDTO> updateGuestSession(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody GuestSessionDTO guestSessionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update GuestSession : {}, {}", id, guestSessionDTO);
        if (guestSessionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, guestSessionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!guestSessionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        guestSessionDTO = guestSessionService.update(guestSessionDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, guestSessionDTO.getId().toString()))
            .body(guestSessionDTO);
    }

    /**
     * {@code PATCH  /guest-sessions/:id} : Partial updates given fields of an existing guestSession, field will ignore if it is null
     *
     * @param id the id of the guestSessionDTO to save.
     * @param guestSessionDTO the guestSessionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated guestSessionDTO,
     * or with status {@code 400 (Bad Request)} if the guestSessionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the guestSessionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the guestSessionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<GuestSessionDTO> partialUpdateGuestSession(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody GuestSessionDTO guestSessionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update GuestSession partially : {}, {}", id, guestSessionDTO);
        if (guestSessionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, guestSessionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!guestSessionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<GuestSessionDTO> result = guestSessionService.partialUpdate(guestSessionDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, guestSessionDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /guest-sessions} : get all the guestSessions.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of guestSessions in body.
     */
    @GetMapping("")
    public ResponseEntity<List<GuestSessionDTO>> getAllGuestSessions(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of GuestSessions");
        Page<GuestSessionDTO> page = guestSessionService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /guest-sessions/:id} : get the "id" guestSession.
     *
     * @param id the id of the guestSessionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the guestSessionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<GuestSessionDTO> getGuestSession(@PathVariable("id") Long id) {
        LOG.debug("REST request to get GuestSession : {}", id);
        Optional<GuestSessionDTO> guestSessionDTO = guestSessionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(guestSessionDTO);
    }

    /**
     * {@code DELETE  /guest-sessions/:id} : delete the "id" guestSession.
     *
     * @param id the id of the guestSessionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGuestSession(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete GuestSession : {}", id);
        guestSessionService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /guest-sessions/_search?query=:query} : search for the guestSession corresponding
     * to the query.
     *
     * @param query the query of the guestSession search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<GuestSessionDTO>> searchGuestSessions(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of GuestSessions for query {}", query);
        try {
            Page<GuestSessionDTO> page = guestSessionService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
