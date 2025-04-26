package com.allomed.app.web.rest;

import static com.allomed.app.domain.SpecializationAsserts.*;
import static com.allomed.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.allomed.app.IntegrationTest;
import com.allomed.app.domain.DoctorProfile;
import com.allomed.app.domain.Specialization;
import com.allomed.app.repository.SpecializationRepository;
import com.allomed.app.repository.search.SpecializationSearchRepository;
import com.allomed.app.service.dto.SpecializationDTO;
import com.allomed.app.service.mapper.SpecializationMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.util.Streamable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link SpecializationResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SpecializationResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/specializations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/specializations/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SpecializationRepository specializationRepository;

    @Autowired
    private SpecializationMapper specializationMapper;

    @Autowired
    private SpecializationSearchRepository specializationSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSpecializationMockMvc;

    private Specialization specialization;

    private Specialization insertedSpecialization;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Specialization createEntity() {
        return new Specialization().name(DEFAULT_NAME).description(DEFAULT_DESCRIPTION);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Specialization createUpdatedEntity() {
        return new Specialization().name(UPDATED_NAME).description(UPDATED_DESCRIPTION);
    }

    @BeforeEach
    void initTest() {
        specialization = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedSpecialization != null) {
            specializationRepository.delete(insertedSpecialization);
            specializationSearchRepository.delete(insertedSpecialization);
            insertedSpecialization = null;
        }
    }

    @Test
    @Transactional
    void createSpecialization() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(specializationSearchRepository.findAll());
        // Create the Specialization
        SpecializationDTO specializationDTO = specializationMapper.toDto(specialization);
        var returnedSpecializationDTO = om.readValue(
            restSpecializationMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(specializationDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SpecializationDTO.class
        );

        // Validate the Specialization in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSpecialization = specializationMapper.toEntity(returnedSpecializationDTO);
        assertSpecializationUpdatableFieldsEquals(returnedSpecialization, getPersistedSpecialization(returnedSpecialization));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(specializationSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedSpecialization = returnedSpecialization;
    }

    @Test
    @Transactional
    void createSpecializationWithExistingId() throws Exception {
        // Create the Specialization with an existing ID
        specialization.setId(1L);
        SpecializationDTO specializationDTO = specializationMapper.toDto(specialization);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(specializationSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restSpecializationMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(specializationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Specialization in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(specializationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(specializationSearchRepository.findAll());
        // set the field null
        specialization.setName(null);

        // Create the Specialization, which fails.
        SpecializationDTO specializationDTO = specializationMapper.toDto(specialization);

        restSpecializationMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(specializationDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(specializationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllSpecializations() throws Exception {
        // Initialize the database
        insertedSpecialization = specializationRepository.saveAndFlush(specialization);

        // Get all the specializationList
        restSpecializationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(specialization.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getSpecialization() throws Exception {
        // Initialize the database
        insertedSpecialization = specializationRepository.saveAndFlush(specialization);

        // Get the specialization
        restSpecializationMockMvc
            .perform(get(ENTITY_API_URL_ID, specialization.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(specialization.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getSpecializationsByIdFiltering() throws Exception {
        // Initialize the database
        insertedSpecialization = specializationRepository.saveAndFlush(specialization);

        Long id = specialization.getId();

        defaultSpecializationFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultSpecializationFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultSpecializationFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllSpecializationsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSpecialization = specializationRepository.saveAndFlush(specialization);

        // Get all the specializationList where name equals to
        defaultSpecializationFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllSpecializationsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSpecialization = specializationRepository.saveAndFlush(specialization);

        // Get all the specializationList where name in
        defaultSpecializationFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllSpecializationsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSpecialization = specializationRepository.saveAndFlush(specialization);

        // Get all the specializationList where name is not null
        defaultSpecializationFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllSpecializationsByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedSpecialization = specializationRepository.saveAndFlush(specialization);

        // Get all the specializationList where name contains
        defaultSpecializationFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllSpecializationsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSpecialization = specializationRepository.saveAndFlush(specialization);

        // Get all the specializationList where name does not contain
        defaultSpecializationFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllSpecializationsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSpecialization = specializationRepository.saveAndFlush(specialization);

        // Get all the specializationList where description equals to
        defaultSpecializationFiltering("description.equals=" + DEFAULT_DESCRIPTION, "description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllSpecializationsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSpecialization = specializationRepository.saveAndFlush(specialization);

        // Get all the specializationList where description in
        defaultSpecializationFiltering(
            "description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION,
            "description.in=" + UPDATED_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllSpecializationsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSpecialization = specializationRepository.saveAndFlush(specialization);

        // Get all the specializationList where description is not null
        defaultSpecializationFiltering("description.specified=true", "description.specified=false");
    }

    @Test
    @Transactional
    void getAllSpecializationsByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        insertedSpecialization = specializationRepository.saveAndFlush(specialization);

        // Get all the specializationList where description contains
        defaultSpecializationFiltering("description.contains=" + DEFAULT_DESCRIPTION, "description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllSpecializationsByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSpecialization = specializationRepository.saveAndFlush(specialization);

        // Get all the specializationList where description does not contain
        defaultSpecializationFiltering(
            "description.doesNotContain=" + UPDATED_DESCRIPTION,
            "description.doesNotContain=" + DEFAULT_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllSpecializationsByDoctorProfilesIsEqualToSomething() throws Exception {
        DoctorProfile doctorProfiles;
        if (TestUtil.findAll(em, DoctorProfile.class).isEmpty()) {
            specializationRepository.saveAndFlush(specialization);
            doctorProfiles = DoctorProfileResourceIT.createEntity(em);
        } else {
            doctorProfiles = TestUtil.findAll(em, DoctorProfile.class).get(0);
        }
        em.persist(doctorProfiles);
        em.flush();
        specialization.addDoctorProfiles(doctorProfiles);
        specializationRepository.saveAndFlush(specialization);
        String doctorProfilesId = doctorProfiles.getId();
        // Get all the specializationList where doctorProfiles equals to doctorProfilesId
        defaultSpecializationShouldBeFound("doctorProfilesId.equals=" + doctorProfilesId);

        // Get all the specializationList where doctorProfiles equals to "invalid-id"
        defaultSpecializationShouldNotBeFound("doctorProfilesId.equals=" + "invalid-id");
    }

    private void defaultSpecializationFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultSpecializationShouldBeFound(shouldBeFound);
        defaultSpecializationShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSpecializationShouldBeFound(String filter) throws Exception {
        restSpecializationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(specialization.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));

        // Check, that the count call also returns 1
        restSpecializationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSpecializationShouldNotBeFound(String filter) throws Exception {
        restSpecializationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSpecializationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingSpecialization() throws Exception {
        // Get the specialization
        restSpecializationMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSpecialization() throws Exception {
        // Initialize the database
        insertedSpecialization = specializationRepository.saveAndFlush(specialization);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        specializationSearchRepository.save(specialization);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(specializationSearchRepository.findAll());

        // Update the specialization
        Specialization updatedSpecialization = specializationRepository.findById(specialization.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSpecialization are not directly saved in db
        em.detach(updatedSpecialization);
        updatedSpecialization.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);
        SpecializationDTO specializationDTO = specializationMapper.toDto(updatedSpecialization);

        restSpecializationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, specializationDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(specializationDTO))
            )
            .andExpect(status().isOk());

        // Validate the Specialization in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSpecializationToMatchAllProperties(updatedSpecialization);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(specializationSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Specialization> specializationSearchList = Streamable.of(specializationSearchRepository.findAll()).toList();
                Specialization testSpecializationSearch = specializationSearchList.get(searchDatabaseSizeAfter - 1);

                assertSpecializationAllPropertiesEquals(testSpecializationSearch, updatedSpecialization);
            });
    }

    @Test
    @Transactional
    void putNonExistingSpecialization() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(specializationSearchRepository.findAll());
        specialization.setId(longCount.incrementAndGet());

        // Create the Specialization
        SpecializationDTO specializationDTO = specializationMapper.toDto(specialization);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSpecializationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, specializationDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(specializationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Specialization in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(specializationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchSpecialization() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(specializationSearchRepository.findAll());
        specialization.setId(longCount.incrementAndGet());

        // Create the Specialization
        SpecializationDTO specializationDTO = specializationMapper.toDto(specialization);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSpecializationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(specializationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Specialization in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(specializationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSpecialization() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(specializationSearchRepository.findAll());
        specialization.setId(longCount.incrementAndGet());

        // Create the Specialization
        SpecializationDTO specializationDTO = specializationMapper.toDto(specialization);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSpecializationMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(specializationDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Specialization in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(specializationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateSpecializationWithPatch() throws Exception {
        // Initialize the database
        insertedSpecialization = specializationRepository.saveAndFlush(specialization);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the specialization using partial update
        Specialization partialUpdatedSpecialization = new Specialization();
        partialUpdatedSpecialization.setId(specialization.getId());

        partialUpdatedSpecialization.name(UPDATED_NAME);

        restSpecializationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSpecialization.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSpecialization))
            )
            .andExpect(status().isOk());

        // Validate the Specialization in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSpecializationUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedSpecialization, specialization),
            getPersistedSpecialization(specialization)
        );
    }

    @Test
    @Transactional
    void fullUpdateSpecializationWithPatch() throws Exception {
        // Initialize the database
        insertedSpecialization = specializationRepository.saveAndFlush(specialization);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the specialization using partial update
        Specialization partialUpdatedSpecialization = new Specialization();
        partialUpdatedSpecialization.setId(specialization.getId());

        partialUpdatedSpecialization.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        restSpecializationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSpecialization.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSpecialization))
            )
            .andExpect(status().isOk());

        // Validate the Specialization in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSpecializationUpdatableFieldsEquals(partialUpdatedSpecialization, getPersistedSpecialization(partialUpdatedSpecialization));
    }

    @Test
    @Transactional
    void patchNonExistingSpecialization() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(specializationSearchRepository.findAll());
        specialization.setId(longCount.incrementAndGet());

        // Create the Specialization
        SpecializationDTO specializationDTO = specializationMapper.toDto(specialization);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSpecializationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, specializationDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(specializationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Specialization in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(specializationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSpecialization() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(specializationSearchRepository.findAll());
        specialization.setId(longCount.incrementAndGet());

        // Create the Specialization
        SpecializationDTO specializationDTO = specializationMapper.toDto(specialization);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSpecializationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(specializationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Specialization in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(specializationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSpecialization() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(specializationSearchRepository.findAll());
        specialization.setId(longCount.incrementAndGet());

        // Create the Specialization
        SpecializationDTO specializationDTO = specializationMapper.toDto(specialization);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSpecializationMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(specializationDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Specialization in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(specializationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteSpecialization() throws Exception {
        // Initialize the database
        insertedSpecialization = specializationRepository.saveAndFlush(specialization);
        specializationRepository.save(specialization);
        specializationSearchRepository.save(specialization);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(specializationSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the specialization
        restSpecializationMockMvc
            .perform(delete(ENTITY_API_URL_ID, specialization.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(specializationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchSpecialization() throws Exception {
        // Initialize the database
        insertedSpecialization = specializationRepository.saveAndFlush(specialization);
        specializationSearchRepository.save(specialization);

        // Search the specialization
        restSpecializationMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + specialization.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(specialization.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    protected long getRepositoryCount() {
        return specializationRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Specialization getPersistedSpecialization(Specialization specialization) {
        return specializationRepository.findById(specialization.getId()).orElseThrow();
    }

    protected void assertPersistedSpecializationToMatchAllProperties(Specialization expectedSpecialization) {
        assertSpecializationAllPropertiesEquals(expectedSpecialization, getPersistedSpecialization(expectedSpecialization));
    }

    protected void assertPersistedSpecializationToMatchUpdatableProperties(Specialization expectedSpecialization) {
        assertSpecializationAllUpdatablePropertiesEquals(expectedSpecialization, getPersistedSpecialization(expectedSpecialization));
    }
}
