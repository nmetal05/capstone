package com.allomed.app.web.rest;

import static com.allomed.app.domain.DoctorViewHistoryAsserts.*;
import static com.allomed.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.allomed.app.IntegrationTest;
import com.allomed.app.domain.AppUserProfile;
import com.allomed.app.domain.DoctorProfile;
import com.allomed.app.domain.DoctorViewHistory;
import com.allomed.app.repository.DoctorViewHistoryRepository;
import com.allomed.app.repository.search.DoctorViewHistorySearchRepository;
import com.allomed.app.service.dto.DoctorViewHistoryDTO;
import com.allomed.app.service.mapper.DoctorViewHistoryMapper;
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
 * Integration tests for the {@link DoctorViewHistoryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class DoctorViewHistoryResourceIT {

    private static final Instant DEFAULT_VIEW_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_VIEW_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/doctor-view-histories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/doctor-view-histories/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DoctorViewHistoryRepository doctorViewHistoryRepository;

    @Autowired
    private DoctorViewHistoryMapper doctorViewHistoryMapper;

    @Autowired
    private DoctorViewHistorySearchRepository doctorViewHistorySearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDoctorViewHistoryMockMvc;

    private DoctorViewHistory doctorViewHistory;

    private DoctorViewHistory insertedDoctorViewHistory;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DoctorViewHistory createEntity(EntityManager em) {
        DoctorViewHistory doctorViewHistory = new DoctorViewHistory().viewDate(DEFAULT_VIEW_DATE);
        // Add required entity
        AppUserProfile appUserProfile;
        if (TestUtil.findAll(em, AppUserProfile.class).isEmpty()) {
            appUserProfile = AppUserProfileResourceIT.createEntity(em);
            em.persist(appUserProfile);
            em.flush();
        } else {
            appUserProfile = TestUtil.findAll(em, AppUserProfile.class).get(0);
        }
        doctorViewHistory.setUser(appUserProfile);
        // Add required entity
        DoctorProfile doctorProfile;
        if (TestUtil.findAll(em, DoctorProfile.class).isEmpty()) {
            doctorProfile = DoctorProfileResourceIT.createEntity(em);
            em.persist(doctorProfile);
            em.flush();
        } else {
            doctorProfile = TestUtil.findAll(em, DoctorProfile.class).get(0);
        }
        doctorViewHistory.setDoctor(doctorProfile);
        return doctorViewHistory;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DoctorViewHistory createUpdatedEntity(EntityManager em) {
        DoctorViewHistory updatedDoctorViewHistory = new DoctorViewHistory().viewDate(UPDATED_VIEW_DATE);
        // Add required entity
        AppUserProfile appUserProfile;
        if (TestUtil.findAll(em, AppUserProfile.class).isEmpty()) {
            appUserProfile = AppUserProfileResourceIT.createUpdatedEntity(em);
            em.persist(appUserProfile);
            em.flush();
        } else {
            appUserProfile = TestUtil.findAll(em, AppUserProfile.class).get(0);
        }
        updatedDoctorViewHistory.setUser(appUserProfile);
        // Add required entity
        DoctorProfile doctorProfile;
        if (TestUtil.findAll(em, DoctorProfile.class).isEmpty()) {
            doctorProfile = DoctorProfileResourceIT.createUpdatedEntity(em);
            em.persist(doctorProfile);
            em.flush();
        } else {
            doctorProfile = TestUtil.findAll(em, DoctorProfile.class).get(0);
        }
        updatedDoctorViewHistory.setDoctor(doctorProfile);
        return updatedDoctorViewHistory;
    }

    @BeforeEach
    void initTest() {
        doctorViewHistory = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedDoctorViewHistory != null) {
            doctorViewHistoryRepository.delete(insertedDoctorViewHistory);
            doctorViewHistorySearchRepository.delete(insertedDoctorViewHistory);
            insertedDoctorViewHistory = null;
        }
    }

    @Test
    @Transactional
    void createDoctorViewHistory() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(doctorViewHistorySearchRepository.findAll());
        // Create the DoctorViewHistory
        DoctorViewHistoryDTO doctorViewHistoryDTO = doctorViewHistoryMapper.toDto(doctorViewHistory);
        var returnedDoctorViewHistoryDTO = om.readValue(
            restDoctorViewHistoryMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(doctorViewHistoryDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            DoctorViewHistoryDTO.class
        );

        // Validate the DoctorViewHistory in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedDoctorViewHistory = doctorViewHistoryMapper.toEntity(returnedDoctorViewHistoryDTO);
        assertDoctorViewHistoryUpdatableFieldsEquals(returnedDoctorViewHistory, getPersistedDoctorViewHistory(returnedDoctorViewHistory));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(doctorViewHistorySearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedDoctorViewHistory = returnedDoctorViewHistory;
    }

    @Test
    @Transactional
    void createDoctorViewHistoryWithExistingId() throws Exception {
        // Create the DoctorViewHistory with an existing ID
        doctorViewHistory.setId(1L);
        DoctorViewHistoryDTO doctorViewHistoryDTO = doctorViewHistoryMapper.toDto(doctorViewHistory);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(doctorViewHistorySearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restDoctorViewHistoryMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(doctorViewHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DoctorViewHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(doctorViewHistorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkViewDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(doctorViewHistorySearchRepository.findAll());
        // set the field null
        doctorViewHistory.setViewDate(null);

        // Create the DoctorViewHistory, which fails.
        DoctorViewHistoryDTO doctorViewHistoryDTO = doctorViewHistoryMapper.toDto(doctorViewHistory);

        restDoctorViewHistoryMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(doctorViewHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(doctorViewHistorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllDoctorViewHistories() throws Exception {
        // Initialize the database
        insertedDoctorViewHistory = doctorViewHistoryRepository.saveAndFlush(doctorViewHistory);

        // Get all the doctorViewHistoryList
        restDoctorViewHistoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(doctorViewHistory.getId().intValue())))
            .andExpect(jsonPath("$.[*].viewDate").value(hasItem(DEFAULT_VIEW_DATE.toString())));
    }

    @Test
    @Transactional
    void getDoctorViewHistory() throws Exception {
        // Initialize the database
        insertedDoctorViewHistory = doctorViewHistoryRepository.saveAndFlush(doctorViewHistory);

        // Get the doctorViewHistory
        restDoctorViewHistoryMockMvc
            .perform(get(ENTITY_API_URL_ID, doctorViewHistory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(doctorViewHistory.getId().intValue()))
            .andExpect(jsonPath("$.viewDate").value(DEFAULT_VIEW_DATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingDoctorViewHistory() throws Exception {
        // Get the doctorViewHistory
        restDoctorViewHistoryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDoctorViewHistory() throws Exception {
        // Initialize the database
        insertedDoctorViewHistory = doctorViewHistoryRepository.saveAndFlush(doctorViewHistory);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        doctorViewHistorySearchRepository.save(doctorViewHistory);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(doctorViewHistorySearchRepository.findAll());

        // Update the doctorViewHistory
        DoctorViewHistory updatedDoctorViewHistory = doctorViewHistoryRepository.findById(doctorViewHistory.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedDoctorViewHistory are not directly saved in db
        em.detach(updatedDoctorViewHistory);
        updatedDoctorViewHistory.viewDate(UPDATED_VIEW_DATE);
        DoctorViewHistoryDTO doctorViewHistoryDTO = doctorViewHistoryMapper.toDto(updatedDoctorViewHistory);

        restDoctorViewHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, doctorViewHistoryDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(doctorViewHistoryDTO))
            )
            .andExpect(status().isOk());

        // Validate the DoctorViewHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDoctorViewHistoryToMatchAllProperties(updatedDoctorViewHistory);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(doctorViewHistorySearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<DoctorViewHistory> doctorViewHistorySearchList = Streamable.of(doctorViewHistorySearchRepository.findAll()).toList();
                DoctorViewHistory testDoctorViewHistorySearch = doctorViewHistorySearchList.get(searchDatabaseSizeAfter - 1);

                assertDoctorViewHistoryAllPropertiesEquals(testDoctorViewHistorySearch, updatedDoctorViewHistory);
            });
    }

    @Test
    @Transactional
    void putNonExistingDoctorViewHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(doctorViewHistorySearchRepository.findAll());
        doctorViewHistory.setId(longCount.incrementAndGet());

        // Create the DoctorViewHistory
        DoctorViewHistoryDTO doctorViewHistoryDTO = doctorViewHistoryMapper.toDto(doctorViewHistory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDoctorViewHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, doctorViewHistoryDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(doctorViewHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DoctorViewHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(doctorViewHistorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchDoctorViewHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(doctorViewHistorySearchRepository.findAll());
        doctorViewHistory.setId(longCount.incrementAndGet());

        // Create the DoctorViewHistory
        DoctorViewHistoryDTO doctorViewHistoryDTO = doctorViewHistoryMapper.toDto(doctorViewHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDoctorViewHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(doctorViewHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DoctorViewHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(doctorViewHistorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDoctorViewHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(doctorViewHistorySearchRepository.findAll());
        doctorViewHistory.setId(longCount.incrementAndGet());

        // Create the DoctorViewHistory
        DoctorViewHistoryDTO doctorViewHistoryDTO = doctorViewHistoryMapper.toDto(doctorViewHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDoctorViewHistoryMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(doctorViewHistoryDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the DoctorViewHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(doctorViewHistorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateDoctorViewHistoryWithPatch() throws Exception {
        // Initialize the database
        insertedDoctorViewHistory = doctorViewHistoryRepository.saveAndFlush(doctorViewHistory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the doctorViewHistory using partial update
        DoctorViewHistory partialUpdatedDoctorViewHistory = new DoctorViewHistory();
        partialUpdatedDoctorViewHistory.setId(doctorViewHistory.getId());

        restDoctorViewHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDoctorViewHistory.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDoctorViewHistory))
            )
            .andExpect(status().isOk());

        // Validate the DoctorViewHistory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDoctorViewHistoryUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedDoctorViewHistory, doctorViewHistory),
            getPersistedDoctorViewHistory(doctorViewHistory)
        );
    }

    @Test
    @Transactional
    void fullUpdateDoctorViewHistoryWithPatch() throws Exception {
        // Initialize the database
        insertedDoctorViewHistory = doctorViewHistoryRepository.saveAndFlush(doctorViewHistory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the doctorViewHistory using partial update
        DoctorViewHistory partialUpdatedDoctorViewHistory = new DoctorViewHistory();
        partialUpdatedDoctorViewHistory.setId(doctorViewHistory.getId());

        partialUpdatedDoctorViewHistory.viewDate(UPDATED_VIEW_DATE);

        restDoctorViewHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDoctorViewHistory.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDoctorViewHistory))
            )
            .andExpect(status().isOk());

        // Validate the DoctorViewHistory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDoctorViewHistoryUpdatableFieldsEquals(
            partialUpdatedDoctorViewHistory,
            getPersistedDoctorViewHistory(partialUpdatedDoctorViewHistory)
        );
    }

    @Test
    @Transactional
    void patchNonExistingDoctorViewHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(doctorViewHistorySearchRepository.findAll());
        doctorViewHistory.setId(longCount.incrementAndGet());

        // Create the DoctorViewHistory
        DoctorViewHistoryDTO doctorViewHistoryDTO = doctorViewHistoryMapper.toDto(doctorViewHistory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDoctorViewHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, doctorViewHistoryDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(doctorViewHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DoctorViewHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(doctorViewHistorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDoctorViewHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(doctorViewHistorySearchRepository.findAll());
        doctorViewHistory.setId(longCount.incrementAndGet());

        // Create the DoctorViewHistory
        DoctorViewHistoryDTO doctorViewHistoryDTO = doctorViewHistoryMapper.toDto(doctorViewHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDoctorViewHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(doctorViewHistoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DoctorViewHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(doctorViewHistorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDoctorViewHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(doctorViewHistorySearchRepository.findAll());
        doctorViewHistory.setId(longCount.incrementAndGet());

        // Create the DoctorViewHistory
        DoctorViewHistoryDTO doctorViewHistoryDTO = doctorViewHistoryMapper.toDto(doctorViewHistory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDoctorViewHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(doctorViewHistoryDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the DoctorViewHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(doctorViewHistorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteDoctorViewHistory() throws Exception {
        // Initialize the database
        insertedDoctorViewHistory = doctorViewHistoryRepository.saveAndFlush(doctorViewHistory);
        doctorViewHistoryRepository.save(doctorViewHistory);
        doctorViewHistorySearchRepository.save(doctorViewHistory);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(doctorViewHistorySearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the doctorViewHistory
        restDoctorViewHistoryMockMvc
            .perform(delete(ENTITY_API_URL_ID, doctorViewHistory.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(doctorViewHistorySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchDoctorViewHistory() throws Exception {
        // Initialize the database
        insertedDoctorViewHistory = doctorViewHistoryRepository.saveAndFlush(doctorViewHistory);
        doctorViewHistorySearchRepository.save(doctorViewHistory);

        // Search the doctorViewHistory
        restDoctorViewHistoryMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + doctorViewHistory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(doctorViewHistory.getId().intValue())))
            .andExpect(jsonPath("$.[*].viewDate").value(hasItem(DEFAULT_VIEW_DATE.toString())));
    }

    protected long getRepositoryCount() {
        return doctorViewHistoryRepository.count();
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

    protected DoctorViewHistory getPersistedDoctorViewHistory(DoctorViewHistory doctorViewHistory) {
        return doctorViewHistoryRepository.findById(doctorViewHistory.getId()).orElseThrow();
    }

    protected void assertPersistedDoctorViewHistoryToMatchAllProperties(DoctorViewHistory expectedDoctorViewHistory) {
        assertDoctorViewHistoryAllPropertiesEquals(expectedDoctorViewHistory, getPersistedDoctorViewHistory(expectedDoctorViewHistory));
    }

    protected void assertPersistedDoctorViewHistoryToMatchUpdatableProperties(DoctorViewHistory expectedDoctorViewHistory) {
        assertDoctorViewHistoryAllUpdatablePropertiesEquals(
            expectedDoctorViewHistory,
            getPersistedDoctorViewHistory(expectedDoctorViewHistory)
        );
    }
}
