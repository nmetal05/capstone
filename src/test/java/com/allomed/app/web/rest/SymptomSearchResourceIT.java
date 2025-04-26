package com.allomed.app.web.rest;

import static com.allomed.app.domain.SymptomSearchAsserts.*;
import static com.allomed.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.allomed.app.IntegrationTest;
import com.allomed.app.domain.SymptomSearch;
import com.allomed.app.repository.SymptomSearchRepository;
import com.allomed.app.repository.search.SymptomSearchSearchRepository;
import com.allomed.app.service.dto.SymptomSearchDTO;
import com.allomed.app.service.mapper.SymptomSearchMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link SymptomSearchResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SymptomSearchResourceIT {

    private static final Instant DEFAULT_SEARCH_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_SEARCH_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_SYMPTOMS = "AAAAAAAAAA";
    private static final String UPDATED_SYMPTOMS = "BBBBBBBBBB";

    private static final String DEFAULT_AI_RESPONSE_JSON = "AAAAAAAAAA";
    private static final String UPDATED_AI_RESPONSE_JSON = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/symptom-searches";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/symptom-searches/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SymptomSearchRepository symptomSearchRepository;

    @Autowired
    private SymptomSearchMapper symptomSearchMapper;

    @Autowired
    private SymptomSearchSearchRepository symptomSearchSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSymptomSearchMockMvc;

    private SymptomSearch symptomSearch;

    private SymptomSearch insertedSymptomSearch;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SymptomSearch createEntity() {
        return new SymptomSearch().searchDate(DEFAULT_SEARCH_DATE).symptoms(DEFAULT_SYMPTOMS).aiResponseJson(DEFAULT_AI_RESPONSE_JSON);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SymptomSearch createUpdatedEntity() {
        return new SymptomSearch().searchDate(UPDATED_SEARCH_DATE).symptoms(UPDATED_SYMPTOMS).aiResponseJson(UPDATED_AI_RESPONSE_JSON);
    }

    @BeforeEach
    void initTest() {
        symptomSearch = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedSymptomSearch != null) {
            symptomSearchRepository.delete(insertedSymptomSearch);
            symptomSearchSearchRepository.delete(insertedSymptomSearch);
            insertedSymptomSearch = null;
        }
    }

    @Test
    @Transactional
    void createSymptomSearch() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(symptomSearchSearchRepository.findAll());
        // Create the SymptomSearch
        SymptomSearchDTO symptomSearchDTO = symptomSearchMapper.toDto(symptomSearch);
        var returnedSymptomSearchDTO = om.readValue(
            restSymptomSearchMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(symptomSearchDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SymptomSearchDTO.class
        );

        // Validate the SymptomSearch in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSymptomSearch = symptomSearchMapper.toEntity(returnedSymptomSearchDTO);
        assertSymptomSearchUpdatableFieldsEquals(returnedSymptomSearch, getPersistedSymptomSearch(returnedSymptomSearch));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(symptomSearchSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedSymptomSearch = returnedSymptomSearch;
    }

    @Test
    @Transactional
    void createSymptomSearchWithExistingId() throws Exception {
        // Create the SymptomSearch with an existing ID
        symptomSearch.setId(1L);
        SymptomSearchDTO symptomSearchDTO = symptomSearchMapper.toDto(symptomSearch);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(symptomSearchSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restSymptomSearchMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(symptomSearchDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SymptomSearch in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(symptomSearchSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkSearchDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(symptomSearchSearchRepository.findAll());
        // set the field null
        symptomSearch.setSearchDate(null);

        // Create the SymptomSearch, which fails.
        SymptomSearchDTO symptomSearchDTO = symptomSearchMapper.toDto(symptomSearch);

        restSymptomSearchMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(symptomSearchDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(symptomSearchSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkSymptomsIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(symptomSearchSearchRepository.findAll());
        // set the field null
        symptomSearch.setSymptoms(null);

        // Create the SymptomSearch, which fails.
        SymptomSearchDTO symptomSearchDTO = symptomSearchMapper.toDto(symptomSearch);

        restSymptomSearchMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(symptomSearchDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(symptomSearchSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllSymptomSearches() throws Exception {
        // Initialize the database
        insertedSymptomSearch = symptomSearchRepository.saveAndFlush(symptomSearch);

        // Get all the symptomSearchList
        restSymptomSearchMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(symptomSearch.getId().intValue())))
            .andExpect(jsonPath("$.[*].searchDate").value(hasItem(DEFAULT_SEARCH_DATE.toString())))
            .andExpect(jsonPath("$.[*].symptoms").value(hasItem(DEFAULT_SYMPTOMS)))
            .andExpect(jsonPath("$.[*].aiResponseJson").value(hasItem(DEFAULT_AI_RESPONSE_JSON)));
    }

    @Test
    @Transactional
    void getSymptomSearch() throws Exception {
        // Initialize the database
        insertedSymptomSearch = symptomSearchRepository.saveAndFlush(symptomSearch);

        // Get the symptomSearch
        restSymptomSearchMockMvc
            .perform(get(ENTITY_API_URL_ID, symptomSearch.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(symptomSearch.getId().intValue()))
            .andExpect(jsonPath("$.searchDate").value(DEFAULT_SEARCH_DATE.toString()))
            .andExpect(jsonPath("$.symptoms").value(DEFAULT_SYMPTOMS))
            .andExpect(jsonPath("$.aiResponseJson").value(DEFAULT_AI_RESPONSE_JSON));
    }

    @Test
    @Transactional
    void getNonExistingSymptomSearch() throws Exception {
        // Get the symptomSearch
        restSymptomSearchMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSymptomSearch() throws Exception {
        // Initialize the database
        insertedSymptomSearch = symptomSearchRepository.saveAndFlush(symptomSearch);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        symptomSearchSearchRepository.save(symptomSearch);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(symptomSearchSearchRepository.findAll());

        // Update the symptomSearch
        SymptomSearch updatedSymptomSearch = symptomSearchRepository.findById(symptomSearch.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSymptomSearch are not directly saved in db
        em.detach(updatedSymptomSearch);
        updatedSymptomSearch.searchDate(UPDATED_SEARCH_DATE).symptoms(UPDATED_SYMPTOMS).aiResponseJson(UPDATED_AI_RESPONSE_JSON);
        SymptomSearchDTO symptomSearchDTO = symptomSearchMapper.toDto(updatedSymptomSearch);

        restSymptomSearchMockMvc
            .perform(
                put(ENTITY_API_URL_ID, symptomSearchDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(symptomSearchDTO))
            )
            .andExpect(status().isOk());

        // Validate the SymptomSearch in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSymptomSearchToMatchAllProperties(updatedSymptomSearch);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(symptomSearchSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<SymptomSearch> symptomSearchSearchList = Streamable.of(symptomSearchSearchRepository.findAll()).toList();
                SymptomSearch testSymptomSearchSearch = symptomSearchSearchList.get(searchDatabaseSizeAfter - 1);

                assertSymptomSearchAllPropertiesEquals(testSymptomSearchSearch, updatedSymptomSearch);
            });
    }

    @Test
    @Transactional
    void putNonExistingSymptomSearch() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(symptomSearchSearchRepository.findAll());
        symptomSearch.setId(longCount.incrementAndGet());

        // Create the SymptomSearch
        SymptomSearchDTO symptomSearchDTO = symptomSearchMapper.toDto(symptomSearch);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSymptomSearchMockMvc
            .perform(
                put(ENTITY_API_URL_ID, symptomSearchDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(symptomSearchDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SymptomSearch in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(symptomSearchSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchSymptomSearch() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(symptomSearchSearchRepository.findAll());
        symptomSearch.setId(longCount.incrementAndGet());

        // Create the SymptomSearch
        SymptomSearchDTO symptomSearchDTO = symptomSearchMapper.toDto(symptomSearch);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSymptomSearchMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(symptomSearchDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SymptomSearch in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(symptomSearchSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSymptomSearch() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(symptomSearchSearchRepository.findAll());
        symptomSearch.setId(longCount.incrementAndGet());

        // Create the SymptomSearch
        SymptomSearchDTO symptomSearchDTO = symptomSearchMapper.toDto(symptomSearch);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSymptomSearchMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(symptomSearchDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SymptomSearch in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(symptomSearchSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateSymptomSearchWithPatch() throws Exception {
        // Initialize the database
        insertedSymptomSearch = symptomSearchRepository.saveAndFlush(symptomSearch);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the symptomSearch using partial update
        SymptomSearch partialUpdatedSymptomSearch = new SymptomSearch();
        partialUpdatedSymptomSearch.setId(symptomSearch.getId());

        partialUpdatedSymptomSearch.searchDate(UPDATED_SEARCH_DATE);

        restSymptomSearchMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSymptomSearch.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSymptomSearch))
            )
            .andExpect(status().isOk());

        // Validate the SymptomSearch in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSymptomSearchUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedSymptomSearch, symptomSearch),
            getPersistedSymptomSearch(symptomSearch)
        );
    }

    @Test
    @Transactional
    void fullUpdateSymptomSearchWithPatch() throws Exception {
        // Initialize the database
        insertedSymptomSearch = symptomSearchRepository.saveAndFlush(symptomSearch);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the symptomSearch using partial update
        SymptomSearch partialUpdatedSymptomSearch = new SymptomSearch();
        partialUpdatedSymptomSearch.setId(symptomSearch.getId());

        partialUpdatedSymptomSearch.searchDate(UPDATED_SEARCH_DATE).symptoms(UPDATED_SYMPTOMS).aiResponseJson(UPDATED_AI_RESPONSE_JSON);

        restSymptomSearchMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSymptomSearch.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSymptomSearch))
            )
            .andExpect(status().isOk());

        // Validate the SymptomSearch in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSymptomSearchUpdatableFieldsEquals(partialUpdatedSymptomSearch, getPersistedSymptomSearch(partialUpdatedSymptomSearch));
    }

    @Test
    @Transactional
    void patchNonExistingSymptomSearch() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(symptomSearchSearchRepository.findAll());
        symptomSearch.setId(longCount.incrementAndGet());

        // Create the SymptomSearch
        SymptomSearchDTO symptomSearchDTO = symptomSearchMapper.toDto(symptomSearch);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSymptomSearchMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, symptomSearchDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(symptomSearchDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SymptomSearch in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(symptomSearchSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSymptomSearch() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(symptomSearchSearchRepository.findAll());
        symptomSearch.setId(longCount.incrementAndGet());

        // Create the SymptomSearch
        SymptomSearchDTO symptomSearchDTO = symptomSearchMapper.toDto(symptomSearch);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSymptomSearchMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(symptomSearchDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SymptomSearch in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(symptomSearchSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSymptomSearch() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(symptomSearchSearchRepository.findAll());
        symptomSearch.setId(longCount.incrementAndGet());

        // Create the SymptomSearch
        SymptomSearchDTO symptomSearchDTO = symptomSearchMapper.toDto(symptomSearch);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSymptomSearchMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(symptomSearchDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SymptomSearch in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(symptomSearchSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteSymptomSearch() throws Exception {
        // Initialize the database
        insertedSymptomSearch = symptomSearchRepository.saveAndFlush(symptomSearch);
        symptomSearchRepository.save(symptomSearch);
        symptomSearchSearchRepository.save(symptomSearch);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(symptomSearchSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the symptomSearch
        restSymptomSearchMockMvc
            .perform(delete(ENTITY_API_URL_ID, symptomSearch.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(symptomSearchSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchSymptomSearch() throws Exception {
        // Initialize the database
        insertedSymptomSearch = symptomSearchRepository.saveAndFlush(symptomSearch);
        symptomSearchSearchRepository.save(symptomSearch);

        // Search the symptomSearch
        restSymptomSearchMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + symptomSearch.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(symptomSearch.getId().intValue())))
            .andExpect(jsonPath("$.[*].searchDate").value(hasItem(DEFAULT_SEARCH_DATE.toString())))
            .andExpect(jsonPath("$.[*].symptoms").value(hasItem(DEFAULT_SYMPTOMS)))
            .andExpect(jsonPath("$.[*].aiResponseJson").value(hasItem(DEFAULT_AI_RESPONSE_JSON.toString())));
    }

    protected long getRepositoryCount() {
        return symptomSearchRepository.count();
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

    protected SymptomSearch getPersistedSymptomSearch(SymptomSearch symptomSearch) {
        return symptomSearchRepository.findById(symptomSearch.getId()).orElseThrow();
    }

    protected void assertPersistedSymptomSearchToMatchAllProperties(SymptomSearch expectedSymptomSearch) {
        assertSymptomSearchAllPropertiesEquals(expectedSymptomSearch, getPersistedSymptomSearch(expectedSymptomSearch));
    }

    protected void assertPersistedSymptomSearchToMatchUpdatableProperties(SymptomSearch expectedSymptomSearch) {
        assertSymptomSearchAllUpdatablePropertiesEquals(expectedSymptomSearch, getPersistedSymptomSearch(expectedSymptomSearch));
    }
}
