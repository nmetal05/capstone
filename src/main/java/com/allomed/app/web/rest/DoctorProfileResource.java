package com.allomed.app.web.rest;

import com.allomed.app.repository.DoctorProfileRepository;
import com.allomed.app.service.DoctorProfileQueryService;
import com.allomed.app.service.DoctorProfileService;
import com.allomed.app.service.criteria.DoctorProfileCriteria;
import com.allomed.app.service.dto.DoctorProfileDTO;
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
 * REST controller for managing {@link com.allomed.app.domain.DoctorProfile}.
 */
@RestController
@RequestMapping("/api/doctor-profiles")
public class DoctorProfileResource {

    private static final Logger LOG = LoggerFactory.getLogger(DoctorProfileResource.class);

    private static final String ENTITY_NAME = "doctorProfile";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DoctorProfileService doctorProfileService;

    private final DoctorProfileRepository doctorProfileRepository;

    private final DoctorProfileQueryService doctorProfileQueryService;

    public DoctorProfileResource(
        DoctorProfileService doctorProfileService,
        DoctorProfileRepository doctorProfileRepository,
        DoctorProfileQueryService doctorProfileQueryService
    ) {
        this.doctorProfileService = doctorProfileService;
        this.doctorProfileRepository = doctorProfileRepository;
        this.doctorProfileQueryService = doctorProfileQueryService;
    }

    /**
     * {@code POST  /doctor-profiles} : Create a new doctorProfile.
     *
     * @param doctorProfileDTO the doctorProfileDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new doctorProfileDTO, or with status {@code 400 (Bad Request)} if the doctorProfile has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<DoctorProfileDTO> createDoctorProfile(@Valid @RequestBody DoctorProfileDTO doctorProfileDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save DoctorProfile : {}", doctorProfileDTO);
        if (doctorProfileDTO.getId() != null) {
            throw new BadRequestAlertException("A new doctorProfile cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if (Objects.isNull(doctorProfileDTO.getInternalUser())) {
            throw new BadRequestAlertException("Invalid association value provided", ENTITY_NAME, "null");
        }
        doctorProfileDTO = doctorProfileService.save(doctorProfileDTO);
        return ResponseEntity.created(new URI("/api/doctor-profiles/" + doctorProfileDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, doctorProfileDTO.getId()))
            .body(doctorProfileDTO);
    }

    /**
     * {@code PUT  /doctor-profiles/:id} : Updates an existing doctorProfile.
     *
     * @param id the id of the doctorProfileDTO to save.
     * @param doctorProfileDTO the doctorProfileDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated doctorProfileDTO,
     * or with status {@code 400 (Bad Request)} if the doctorProfileDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the doctorProfileDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<DoctorProfileDTO> updateDoctorProfile(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody DoctorProfileDTO doctorProfileDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update DoctorProfile : {}, {}", id, doctorProfileDTO);
        if (doctorProfileDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, doctorProfileDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!doctorProfileRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        doctorProfileDTO = doctorProfileService.update(doctorProfileDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, doctorProfileDTO.getId()))
            .body(doctorProfileDTO);
    }

    /**
     * {@code PATCH  /doctor-profiles/:id} : Partial updates given fields of an existing doctorProfile, field will ignore if it is null
     *
     * @param id the id of the doctorProfileDTO to save.
     * @param doctorProfileDTO the doctorProfileDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated doctorProfileDTO,
     * or with status {@code 400 (Bad Request)} if the doctorProfileDTO is not valid,
     * or with status {@code 404 (Not Found)} if the doctorProfileDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the doctorProfileDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<DoctorProfileDTO> partialUpdateDoctorProfile(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody DoctorProfileDTO doctorProfileDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update DoctorProfile partially : {}, {}", id, doctorProfileDTO);
        if (doctorProfileDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, doctorProfileDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!doctorProfileRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<DoctorProfileDTO> result = doctorProfileService.partialUpdate(doctorProfileDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, doctorProfileDTO.getId())
        );
    }

    /**
     * {@code GET  /doctor-profiles} : get all the doctorProfiles.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of doctorProfiles in body.
     */
    @GetMapping("")
    public ResponseEntity<List<DoctorProfileDTO>> getAllDoctorProfiles(
        DoctorProfileCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get DoctorProfiles by criteria: {}", criteria);

        Page<DoctorProfileDTO> page = doctorProfileQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /doctor-profiles/count} : count all the doctorProfiles.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countDoctorProfiles(DoctorProfileCriteria criteria) {
        LOG.debug("REST request to count DoctorProfiles by criteria: {}", criteria);
        return ResponseEntity.ok().body(doctorProfileQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /doctor-profiles/:id} : get the "id" doctorProfile.
     *
     * @param id the id of the doctorProfileDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the doctorProfileDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DoctorProfileDTO> getDoctorProfile(@PathVariable("id") String id) {
        LOG.debug("REST request to get DoctorProfile : {}", id);
        Optional<DoctorProfileDTO> doctorProfileDTO = doctorProfileService.findOne(id);
        return ResponseUtil.wrapOrNotFound(doctorProfileDTO);
    }

    /**
     * {@code DELETE  /doctor-profiles/:id} : delete the "id" doctorProfile.
     *
     * @param id the id of the doctorProfileDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctorProfile(@PathVariable("id") String id) {
        LOG.debug("REST request to delete DoctorProfile : {}", id);
        doctorProfileService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build();
    }

    /**
     * {@code SEARCH  /doctor-profiles/_search?query=:query} : search for the doctorProfile corresponding
     * to the query.
     *
     * @param query the query of the doctorProfile search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<DoctorProfileDTO>> searchDoctorProfiles(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of DoctorProfiles for query {}", query);
        try {
            Page<DoctorProfileDTO> page = doctorProfileService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
