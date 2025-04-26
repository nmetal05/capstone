package com.allomed.app.web.rest;

import com.allomed.app.repository.AppUserProfileRepository;
import com.allomed.app.service.AppUserProfileService;
import com.allomed.app.service.dto.AppUserProfileDTO;
import com.allomed.app.web.rest.errors.BadRequestAlertException;
import com.allomed.app.web.rest.errors.ElasticsearchExceptionMapper;
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
 * REST controller for managing {@link com.allomed.app.domain.AppUserProfile}.
 */
@RestController
@RequestMapping("/api/app-user-profiles")
public class AppUserProfileResource {

    private static final Logger LOG = LoggerFactory.getLogger(AppUserProfileResource.class);

    private static final String ENTITY_NAME = "appUserProfile";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AppUserProfileService appUserProfileService;

    private final AppUserProfileRepository appUserProfileRepository;

    public AppUserProfileResource(AppUserProfileService appUserProfileService, AppUserProfileRepository appUserProfileRepository) {
        this.appUserProfileService = appUserProfileService;
        this.appUserProfileRepository = appUserProfileRepository;
    }

    /**
     * {@code POST  /app-user-profiles} : Create a new appUserProfile.
     *
     * @param appUserProfileDTO the appUserProfileDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new appUserProfileDTO, or with status {@code 400 (Bad Request)} if the appUserProfile has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<AppUserProfileDTO> createAppUserProfile(@RequestBody AppUserProfileDTO appUserProfileDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save AppUserProfile : {}", appUserProfileDTO);
        if (appUserProfileDTO.getId() != null) {
            throw new BadRequestAlertException("A new appUserProfile cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if (Objects.isNull(appUserProfileDTO.getInternalUser())) {
            throw new BadRequestAlertException("Invalid association value provided", ENTITY_NAME, "null");
        }
        appUserProfileDTO = appUserProfileService.save(appUserProfileDTO);
        return ResponseEntity.created(new URI("/api/app-user-profiles/" + appUserProfileDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, appUserProfileDTO.getId()))
            .body(appUserProfileDTO);
    }

    /**
     * {@code PUT  /app-user-profiles/:id} : Updates an existing appUserProfile.
     *
     * @param id the id of the appUserProfileDTO to save.
     * @param appUserProfileDTO the appUserProfileDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated appUserProfileDTO,
     * or with status {@code 400 (Bad Request)} if the appUserProfileDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the appUserProfileDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AppUserProfileDTO> updateAppUserProfile(
        @PathVariable(value = "id", required = false) final String id,
        @RequestBody AppUserProfileDTO appUserProfileDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update AppUserProfile : {}, {}", id, appUserProfileDTO);
        if (appUserProfileDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, appUserProfileDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!appUserProfileRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        appUserProfileDTO = appUserProfileService.update(appUserProfileDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, appUserProfileDTO.getId()))
            .body(appUserProfileDTO);
    }

    /**
     * {@code PATCH  /app-user-profiles/:id} : Partial updates given fields of an existing appUserProfile, field will ignore if it is null
     *
     * @param id the id of the appUserProfileDTO to save.
     * @param appUserProfileDTO the appUserProfileDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated appUserProfileDTO,
     * or with status {@code 400 (Bad Request)} if the appUserProfileDTO is not valid,
     * or with status {@code 404 (Not Found)} if the appUserProfileDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the appUserProfileDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AppUserProfileDTO> partialUpdateAppUserProfile(
        @PathVariable(value = "id", required = false) final String id,
        @RequestBody AppUserProfileDTO appUserProfileDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update AppUserProfile partially : {}, {}", id, appUserProfileDTO);
        if (appUserProfileDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, appUserProfileDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!appUserProfileRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AppUserProfileDTO> result = appUserProfileService.partialUpdate(appUserProfileDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, appUserProfileDTO.getId())
        );
    }

    /**
     * {@code GET  /app-user-profiles} : get all the appUserProfiles.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of appUserProfiles in body.
     */
    @GetMapping("")
    public ResponseEntity<List<AppUserProfileDTO>> getAllAppUserProfiles(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of AppUserProfiles");
        Page<AppUserProfileDTO> page;
        if (eagerload) {
            page = appUserProfileService.findAllWithEagerRelationships(pageable);
        } else {
            page = appUserProfileService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /app-user-profiles/:id} : get the "id" appUserProfile.
     *
     * @param id the id of the appUserProfileDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the appUserProfileDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AppUserProfileDTO> getAppUserProfile(@PathVariable("id") String id) {
        LOG.debug("REST request to get AppUserProfile : {}", id);
        Optional<AppUserProfileDTO> appUserProfileDTO = appUserProfileService.findOne(id);
        return ResponseUtil.wrapOrNotFound(appUserProfileDTO);
    }

    /**
     * {@code DELETE  /app-user-profiles/:id} : delete the "id" appUserProfile.
     *
     * @param id the id of the appUserProfileDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppUserProfile(@PathVariable("id") String id) {
        LOG.debug("REST request to delete AppUserProfile : {}", id);
        appUserProfileService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build();
    }

    /**
     * {@code SEARCH  /app-user-profiles/_search?query=:query} : search for the appUserProfile corresponding
     * to the query.
     *
     * @param query the query of the appUserProfile search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<AppUserProfileDTO>> searchAppUserProfiles(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of AppUserProfiles for query {}", query);
        try {
            Page<AppUserProfileDTO> page = appUserProfileService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
