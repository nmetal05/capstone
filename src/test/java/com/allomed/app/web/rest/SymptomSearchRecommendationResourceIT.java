package com.allomed.app.web.rest;

import static com.allomed.app.domain.SymptomSearchRecommendationAsserts.*;
import static com.allomed.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.allomed.app.IntegrationTest;
import com.allomed.app.domain.Specialization;
import com.allomed.app.domain.SymptomSearch;
import com.allomed.app.domain.SymptomSearchRecommendation;
import com.allomed.app.repository.SymptomSearchRecommendationRepository;
import com.allomed.app.repository.search.SymptomSearchRecommendationSearchRepository;
import com.allomed.app.service.dto.SymptomSearchRecommendationDTO;
import com.allomed.app.service.mapper.SymptomSearchRecommendationMapper;
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
 * Integration tests for the {@link SymptomSearchRecommendationResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SymptomSearchRecommendationResourceIT {

    private static final Double DEFAULT_CONFIDENCE_SCORE = 1D;
    private static final Double UPDATED_CONFIDENCE_SCORE = 2D;

    private static final Integer DEFAULT_RANK = 1;
    private static final Integer UPDATED_RANK = 2;

    private static final String DEFAULT_REASONING = "AAAAAAAAAA";
    private static final String UPDATED_REASONING = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/symptom-search-recommendations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/symptom-search-recommendations/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SymptomSearchRecommendationRepository symptomSearchRecommendationRepository;

    @Autowired
    private SymptomSearchRecommendationMapper symptomSearchRecommendationMapper;

    @Autowired
    private SymptomSearchRecommendationSearchRepository symptomSearchRecommendationSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSymptomSearchRecommendationMockMvc;

    private SymptomSearchRecommendation symptomSearchRecommendation;

    private SymptomSearchRecommendation insertedSymptomSearchRecommendation;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SymptomSearchRecommendation createEntity(EntityManager em) {
        SymptomSearchRecommendation symptomSearchRecommendation = new SymptomSearchRecommendation()
            .confidenceScore(DEFAULT_CONFIDENCE_SCORE)
            .rank(DEFAULT_RANK)
            .reasoning(DEFAULT_REASONING);
        // Add required entity
        SymptomSearch symptomSearch;
        if (TestUtil.findAll(em, SymptomSearch.class).isEmpty()) {
            symptomSearch = SymptomSearchResourceIT.createEntity();
            em.persist(symptomSearch);
            em.flush();
        } else {
            symptomSearch = TestUtil.findAll(em, SymptomSearch.class).get(0);
        }
        symptomSearchRecommendation.setSearch(symptomSearch);
        // Add required entity
        Specialization specialization;
        if (TestUtil.findAll(em, Specialization.class).isEmpty()) {
            specialization = SpecializationResourceIT.createEntity();
            em.persist(specialization);
            em.flush();
        } else {
            specialization = TestUtil.findAll(em, Specialization.class).get(0);
        }
        symptomSearchRecommendation.setSpecialization(specialization);
        return symptomSearchRecommendation;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SymptomSearchRecommendation createUpdatedEntity(EntityManager em) {
        SymptomSearchRecommendation updatedSymptomSearchRecommendation = new SymptomSearchRecommendation()
            .confidenceScore(UPDATED_CONFIDENCE_SCORE)
            .rank(UPDATED_RANK)
            .reasoning(UPDATED_REASONING);
        // Add required entity
        SymptomSearch symptomSearch;
        if (TestUtil.findAll(em, SymptomSearch.class).isEmpty()) {
            symptomSearch = SymptomSearchResourceIT.createUpdatedEntity();
            em.persist(symptomSearch);
            em.flush();
        } else {
            symptomSearch = TestUtil.findAll(em, SymptomSearch.class).get(0);
        }
        updatedSymptomSearchRecommendation.setSearch(symptomSearch);
        // Add required entity
        Specialization specialization;
        if (TestUtil.findAll(em, Specialization.class).isEmpty()) {
            specialization = SpecializationResourceIT.createUpdatedEntity();
            em.persist(specialization);
            em.flush();
        } else {
            specialization = TestUtil.findAll(em, Specialization.class).get(0);
        }
        updatedSymptomSearchRecommendation.setSpecialization(specialization);
        return updatedSymptomSearchRecommendation;
    }

    @BeforeEach
    void initTest() {
        symptomSearchRecommendation = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedSymptomSearchRecommendation != null) {
            symptomSearchRecommendationRepository.delete(insertedSymptomSearchRecommendation);
            symptomSearchRecommendationSearchRepository.delete(insertedSymptomSearchRecommendation);
            insertedSymptomSearchRecommendation = null;
        }
    }

    @Test
    @Transactional
    void createSymptomSearchRecommendation() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(symptomSearchRecommendationSearchRepository.findAll());
        // Create the SymptomSearchRecommendation
        SymptomSearchRecommendationDTO symptomSearchRecommendationDTO = symptomSearchRecommendationMapper.toDto(
            symptomSearchRecommendation
        );
        var returnedSymptomSearchRecommendationDTO = om.readValue(
            restSymptomSearchRecommendationMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(symptomSearchRecommendationDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SymptomSearchRecommendationDTO.class
        );

        // Validate the SymptomSearchRecommendation in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSymptomSearchRecommendation = symptomSearchRecommendationMapper.toEntity(returnedSymptomSearchRecommendationDTO);
        assertSymptomSearchRecommendationUpdatableFieldsEquals(
            returnedSymptomSearchRecommendation,
            getPersistedSymptomSearchRecommendation(returnedSymptomSearchRecommendation)
        );

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(symptomSearchRecommendationSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedSymptomSearchRecommendation = returnedSymptomSearchRecommendation;
    }

    @Test
    @Transactional
    void createSymptomSearchRecommendationWithExistingId() throws Exception {
        // Create the SymptomSearchRecommendation with an existing ID
        symptomSearchRecommendation.setId(1L);
        SymptomSearchRecommendationDTO symptomSearchRecommendationDTO = symptomSearchRecommendationMapper.toDto(
            symptomSearchRecommendation
        );

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(symptomSearchRecommendationSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restSymptomSearchRecommendationMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(symptomSearchRecommendationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SymptomSearchRecommendation in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(symptomSearchRecommendationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkConfidenceScoreIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(symptomSearchRecommendationSearchRepository.findAll());
        // set the field null
        symptomSearchRecommendation.setConfidenceScore(null);

        // Create the SymptomSearchRecommendation, which fails.
        SymptomSearchRecommendationDTO symptomSearchRecommendationDTO = symptomSearchRecommendationMapper.toDto(
            symptomSearchRecommendation
        );

        restSymptomSearchRecommendationMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(symptomSearchRecommendationDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(symptomSearchRecommendationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkRankIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(symptomSearchRecommendationSearchRepository.findAll());
        // set the field null
        symptomSearchRecommendation.setRank(null);

        // Create the SymptomSearchRecommendation, which fails.
        SymptomSearchRecommendationDTO symptomSearchRecommendationDTO = symptomSearchRecommendationMapper.toDto(
            symptomSearchRecommendation
        );

        restSymptomSearchRecommendationMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(symptomSearchRecommendationDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(symptomSearchRecommendationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkReasoningIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(symptomSearchRecommendationSearchRepository.findAll());
        // set the field null
        symptomSearchRecommendation.setReasoning(null);

        // Create the SymptomSearchRecommendation, which fails.
        SymptomSearchRecommendationDTO symptomSearchRecommendationDTO = symptomSearchRecommendationMapper.toDto(
            symptomSearchRecommendation
        );

        restSymptomSearchRecommendationMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(symptomSearchRecommendationDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(symptomSearchRecommendationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllSymptomSearchRecommendations() throws Exception {
        // Initialize the database
        insertedSymptomSearchRecommendation = symptomSearchRecommendationRepository.saveAndFlush(symptomSearchRecommendation);

        // Get all the symptomSearchRecommendationList
        restSymptomSearchRecommendationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(symptomSearchRecommendation.getId().intValue())))
            .andExpect(jsonPath("$.[*].confidenceScore").value(hasItem(DEFAULT_CONFIDENCE_SCORE)))
            .andExpect(jsonPath("$.[*].rank").value(hasItem(DEFAULT_RANK)))
            .andExpect(jsonPath("$.[*].reasoning").value(hasItem(DEFAULT_REASONING)));
    }

    @Test
    @Transactional
    void getSymptomSearchRecommendation() throws Exception {
        // Initialize the database
        insertedSymptomSearchRecommendation = symptomSearchRecommendationRepository.saveAndFlush(symptomSearchRecommendation);

        // Get the symptomSearchRecommendation
        restSymptomSearchRecommendationMockMvc
            .perform(get(ENTITY_API_URL_ID, symptomSearchRecommendation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(symptomSearchRecommendation.getId().intValue()))
            .andExpect(jsonPath("$.confidenceScore").value(DEFAULT_CONFIDENCE_SCORE))
            .andExpect(jsonPath("$.rank").value(DEFAULT_RANK))
            .andExpect(jsonPath("$.reasoning").value(DEFAULT_REASONING));
    }

    @Test
    @Transactional
    void getNonExistingSymptomSearchRecommendation() throws Exception {
        // Get the symptomSearchRecommendation
        restSymptomSearchRecommendationMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSymptomSearchRecommendation() throws Exception {
        // Initialize the database
        insertedSymptomSearchRecommendation = symptomSearchRecommendationRepository.saveAndFlush(symptomSearchRecommendation);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        symptomSearchRecommendationSearchRepository.save(symptomSearchRecommendation);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(symptomSearchRecommendationSearchRepository.findAll());

        // Update the symptomSearchRecommendation
        SymptomSearchRecommendation updatedSymptomSearchRecommendation = symptomSearchRecommendationRepository
            .findById(symptomSearchRecommendation.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedSymptomSearchRecommendation are not directly saved in db
        em.detach(updatedSymptomSearchRecommendation);
        updatedSymptomSearchRecommendation.confidenceScore(UPDATED_CONFIDENCE_SCORE).rank(UPDATED_RANK).reasoning(UPDATED_REASONING);
        SymptomSearchRecommendationDTO symptomSearchRecommendationDTO = symptomSearchRecommendationMapper.toDto(
            updatedSymptomSearchRecommendation
        );

        restSymptomSearchRecommendationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, symptomSearchRecommendationDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(symptomSearchRecommendationDTO))
            )
            .andExpect(status().isOk());

        // Validate the SymptomSearchRecommendation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSymptomSearchRecommendationToMatchAllProperties(updatedSymptomSearchRecommendation);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(symptomSearchRecommendationSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<SymptomSearchRecommendation> symptomSearchRecommendationSearchList = Streamable.of(
                    symptomSearchRecommendationSearchRepository.findAll()
                ).toList();
                SymptomSearchRecommendation testSymptomSearchRecommendationSearch = symptomSearchRecommendationSearchList.get(
                    searchDatabaseSizeAfter - 1
                );

                assertSymptomSearchRecommendationAllPropertiesEquals(
                    testSymptomSearchRecommendationSearch,
                    updatedSymptomSearchRecommendation
                );
            });
    }

    @Test
    @Transactional
    void putNonExistingSymptomSearchRecommendation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(symptomSearchRecommendationSearchRepository.findAll());
        symptomSearchRecommendation.setId(longCount.incrementAndGet());

        // Create the SymptomSearchRecommendation
        SymptomSearchRecommendationDTO symptomSearchRecommendationDTO = symptomSearchRecommendationMapper.toDto(
            symptomSearchRecommendation
        );

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSymptomSearchRecommendationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, symptomSearchRecommendationDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(symptomSearchRecommendationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SymptomSearchRecommendation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(symptomSearchRecommendationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchSymptomSearchRecommendation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(symptomSearchRecommendationSearchRepository.findAll());
        symptomSearchRecommendation.setId(longCount.incrementAndGet());

        // Create the SymptomSearchRecommendation
        SymptomSearchRecommendationDTO symptomSearchRecommendationDTO = symptomSearchRecommendationMapper.toDto(
            symptomSearchRecommendation
        );

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSymptomSearchRecommendationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(symptomSearchRecommendationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SymptomSearchRecommendation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(symptomSearchRecommendationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSymptomSearchRecommendation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(symptomSearchRecommendationSearchRepository.findAll());
        symptomSearchRecommendation.setId(longCount.incrementAndGet());

        // Create the SymptomSearchRecommendation
        SymptomSearchRecommendationDTO symptomSearchRecommendationDTO = symptomSearchRecommendationMapper.toDto(
            symptomSearchRecommendation
        );

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSymptomSearchRecommendationMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(symptomSearchRecommendationDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SymptomSearchRecommendation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(symptomSearchRecommendationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateSymptomSearchRecommendationWithPatch() throws Exception {
        // Initialize the database
        insertedSymptomSearchRecommendation = symptomSearchRecommendationRepository.saveAndFlush(symptomSearchRecommendation);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the symptomSearchRecommendation using partial update
        SymptomSearchRecommendation partialUpdatedSymptomSearchRecommendation = new SymptomSearchRecommendation();
        partialUpdatedSymptomSearchRecommendation.setId(symptomSearchRecommendation.getId());

        partialUpdatedSymptomSearchRecommendation.rank(UPDATED_RANK);

        restSymptomSearchRecommendationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSymptomSearchRecommendation.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSymptomSearchRecommendation))
            )
            .andExpect(status().isOk());

        // Validate the SymptomSearchRecommendation in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSymptomSearchRecommendationUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedSymptomSearchRecommendation, symptomSearchRecommendation),
            getPersistedSymptomSearchRecommendation(symptomSearchRecommendation)
        );
    }

    @Test
    @Transactional
    void fullUpdateSymptomSearchRecommendationWithPatch() throws Exception {
        // Initialize the database
        insertedSymptomSearchRecommendation = symptomSearchRecommendationRepository.saveAndFlush(symptomSearchRecommendation);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the symptomSearchRecommendation using partial update
        SymptomSearchRecommendation partialUpdatedSymptomSearchRecommendation = new SymptomSearchRecommendation();
        partialUpdatedSymptomSearchRecommendation.setId(symptomSearchRecommendation.getId());

        partialUpdatedSymptomSearchRecommendation.confidenceScore(UPDATED_CONFIDENCE_SCORE).rank(UPDATED_RANK).reasoning(UPDATED_REASONING);

        restSymptomSearchRecommendationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSymptomSearchRecommendation.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSymptomSearchRecommendation))
            )
            .andExpect(status().isOk());

        // Validate the SymptomSearchRecommendation in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSymptomSearchRecommendationUpdatableFieldsEquals(
            partialUpdatedSymptomSearchRecommendation,
            getPersistedSymptomSearchRecommendation(partialUpdatedSymptomSearchRecommendation)
        );
    }

    @Test
    @Transactional
    void patchNonExistingSymptomSearchRecommendation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(symptomSearchRecommendationSearchRepository.findAll());
        symptomSearchRecommendation.setId(longCount.incrementAndGet());

        // Create the SymptomSearchRecommendation
        SymptomSearchRecommendationDTO symptomSearchRecommendationDTO = symptomSearchRecommendationMapper.toDto(
            symptomSearchRecommendation
        );

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSymptomSearchRecommendationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, symptomSearchRecommendationDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(symptomSearchRecommendationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SymptomSearchRecommendation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(symptomSearchRecommendationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSymptomSearchRecommendation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(symptomSearchRecommendationSearchRepository.findAll());
        symptomSearchRecommendation.setId(longCount.incrementAndGet());

        // Create the SymptomSearchRecommendation
        SymptomSearchRecommendationDTO symptomSearchRecommendationDTO = symptomSearchRecommendationMapper.toDto(
            symptomSearchRecommendation
        );

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSymptomSearchRecommendationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(symptomSearchRecommendationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SymptomSearchRecommendation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(symptomSearchRecommendationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSymptomSearchRecommendation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(symptomSearchRecommendationSearchRepository.findAll());
        symptomSearchRecommendation.setId(longCount.incrementAndGet());

        // Create the SymptomSearchRecommendation
        SymptomSearchRecommendationDTO symptomSearchRecommendationDTO = symptomSearchRecommendationMapper.toDto(
            symptomSearchRecommendation
        );

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSymptomSearchRecommendationMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(symptomSearchRecommendationDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SymptomSearchRecommendation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(symptomSearchRecommendationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteSymptomSearchRecommendation() throws Exception {
        // Initialize the database
        insertedSymptomSearchRecommendation = symptomSearchRecommendationRepository.saveAndFlush(symptomSearchRecommendation);
        symptomSearchRecommendationRepository.save(symptomSearchRecommendation);
        symptomSearchRecommendationSearchRepository.save(symptomSearchRecommendation);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(symptomSearchRecommendationSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the symptomSearchRecommendation
        restSymptomSearchRecommendationMockMvc
            .perform(delete(ENTITY_API_URL_ID, symptomSearchRecommendation.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(symptomSearchRecommendationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchSymptomSearchRecommendation() throws Exception {
        // Initialize the database
        insertedSymptomSearchRecommendation = symptomSearchRecommendationRepository.saveAndFlush(symptomSearchRecommendation);
        symptomSearchRecommendationSearchRepository.save(symptomSearchRecommendation);

        // Search the symptomSearchRecommendation
        restSymptomSearchRecommendationMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + symptomSearchRecommendation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(symptomSearchRecommendation.getId().intValue())))
            .andExpect(jsonPath("$.[*].confidenceScore").value(hasItem(DEFAULT_CONFIDENCE_SCORE)))
            .andExpect(jsonPath("$.[*].rank").value(hasItem(DEFAULT_RANK)))
            .andExpect(jsonPath("$.[*].reasoning").value(hasItem(DEFAULT_REASONING)));
    }

    protected long getRepositoryCount() {
        return symptomSearchRecommendationRepository.count();
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

    protected SymptomSearchRecommendation getPersistedSymptomSearchRecommendation(SymptomSearchRecommendation symptomSearchRecommendation) {
        return symptomSearchRecommendationRepository.findById(symptomSearchRecommendation.getId()).orElseThrow();
    }

    protected void assertPersistedSymptomSearchRecommendationToMatchAllProperties(
        SymptomSearchRecommendation expectedSymptomSearchRecommendation
    ) {
        assertSymptomSearchRecommendationAllPropertiesEquals(
            expectedSymptomSearchRecommendation,
            getPersistedSymptomSearchRecommendation(expectedSymptomSearchRecommendation)
        );
    }

    protected void assertPersistedSymptomSearchRecommendationToMatchUpdatableProperties(
        SymptomSearchRecommendation expectedSymptomSearchRecommendation
    ) {
        assertSymptomSearchRecommendationAllUpdatablePropertiesEquals(
            expectedSymptomSearchRecommendation,
            getPersistedSymptomSearchRecommendation(expectedSymptomSearchRecommendation)
        );
    }
}
