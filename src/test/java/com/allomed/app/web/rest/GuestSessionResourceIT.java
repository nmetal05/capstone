package com.allomed.app.web.rest;

import static com.allomed.app.domain.GuestSessionAsserts.*;
import static com.allomed.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.allomed.app.IntegrationTest;
import com.allomed.app.domain.GuestSession;
import com.allomed.app.repository.GuestSessionRepository;
import com.allomed.app.repository.search.GuestSessionSearchRepository;
import com.allomed.app.service.dto.GuestSessionDTO;
import com.allomed.app.service.mapper.GuestSessionMapper;
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
 * Integration tests for the {@link GuestSessionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class GuestSessionResourceIT {

    private static final String DEFAULT_SESSION_ID = "AAAAAAAAAA";
    private static final String UPDATED_SESSION_ID = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_LAST_ACTIVE_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_ACTIVE_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_IP_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_IP_ADDRESS = "BBBBBBBBBB";

    private static final String DEFAULT_USER_AGENT = "AAAAAAAAAA";
    private static final String UPDATED_USER_AGENT = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/guest-sessions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/guest-sessions/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private GuestSessionRepository guestSessionRepository;

    @Autowired
    private GuestSessionMapper guestSessionMapper;

    @Autowired
    private GuestSessionSearchRepository guestSessionSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restGuestSessionMockMvc;

    private GuestSession guestSession;

    private GuestSession insertedGuestSession;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static GuestSession createEntity() {
        return new GuestSession()
            .sessionId(DEFAULT_SESSION_ID)
            .createdAt(DEFAULT_CREATED_AT)
            .lastActiveAt(DEFAULT_LAST_ACTIVE_AT)
            .ipAddress(DEFAULT_IP_ADDRESS)
            .userAgent(DEFAULT_USER_AGENT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static GuestSession createUpdatedEntity() {
        return new GuestSession()
            .sessionId(UPDATED_SESSION_ID)
            .createdAt(UPDATED_CREATED_AT)
            .lastActiveAt(UPDATED_LAST_ACTIVE_AT)
            .ipAddress(UPDATED_IP_ADDRESS)
            .userAgent(UPDATED_USER_AGENT);
    }

    @BeforeEach
    void initTest() {
        guestSession = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedGuestSession != null) {
            guestSessionRepository.delete(insertedGuestSession);
            guestSessionSearchRepository.delete(insertedGuestSession);
            insertedGuestSession = null;
        }
    }

    @Test
    @Transactional
    void createGuestSession() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(guestSessionSearchRepository.findAll());
        // Create the GuestSession
        GuestSessionDTO guestSessionDTO = guestSessionMapper.toDto(guestSession);
        var returnedGuestSessionDTO = om.readValue(
            restGuestSessionMockMvc
                .perform(
                    post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(guestSessionDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            GuestSessionDTO.class
        );

        // Validate the GuestSession in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedGuestSession = guestSessionMapper.toEntity(returnedGuestSessionDTO);
        assertGuestSessionUpdatableFieldsEquals(returnedGuestSession, getPersistedGuestSession(returnedGuestSession));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(guestSessionSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedGuestSession = returnedGuestSession;
    }

    @Test
    @Transactional
    void createGuestSessionWithExistingId() throws Exception {
        // Create the GuestSession with an existing ID
        guestSession.setId(1L);
        GuestSessionDTO guestSessionDTO = guestSessionMapper.toDto(guestSession);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(guestSessionSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restGuestSessionMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(guestSessionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the GuestSession in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(guestSessionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkSessionIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(guestSessionSearchRepository.findAll());
        // set the field null
        guestSession.setSessionId(null);

        // Create the GuestSession, which fails.
        GuestSessionDTO guestSessionDTO = guestSessionMapper.toDto(guestSession);

        restGuestSessionMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(guestSessionDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(guestSessionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(guestSessionSearchRepository.findAll());
        // set the field null
        guestSession.setCreatedAt(null);

        // Create the GuestSession, which fails.
        GuestSessionDTO guestSessionDTO = guestSessionMapper.toDto(guestSession);

        restGuestSessionMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(guestSessionDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(guestSessionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkLastActiveAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(guestSessionSearchRepository.findAll());
        // set the field null
        guestSession.setLastActiveAt(null);

        // Create the GuestSession, which fails.
        GuestSessionDTO guestSessionDTO = guestSessionMapper.toDto(guestSession);

        restGuestSessionMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(guestSessionDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(guestSessionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkIpAddressIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(guestSessionSearchRepository.findAll());
        // set the field null
        guestSession.setIpAddress(null);

        // Create the GuestSession, which fails.
        GuestSessionDTO guestSessionDTO = guestSessionMapper.toDto(guestSession);

        restGuestSessionMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(guestSessionDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(guestSessionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkUserAgentIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(guestSessionSearchRepository.findAll());
        // set the field null
        guestSession.setUserAgent(null);

        // Create the GuestSession, which fails.
        GuestSessionDTO guestSessionDTO = guestSessionMapper.toDto(guestSession);

        restGuestSessionMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(guestSessionDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(guestSessionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllGuestSessions() throws Exception {
        // Initialize the database
        insertedGuestSession = guestSessionRepository.saveAndFlush(guestSession);

        // Get all the guestSessionList
        restGuestSessionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(guestSession.getId().intValue())))
            .andExpect(jsonPath("$.[*].sessionId").value(hasItem(DEFAULT_SESSION_ID)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].lastActiveAt").value(hasItem(DEFAULT_LAST_ACTIVE_AT.toString())))
            .andExpect(jsonPath("$.[*].ipAddress").value(hasItem(DEFAULT_IP_ADDRESS)))
            .andExpect(jsonPath("$.[*].userAgent").value(hasItem(DEFAULT_USER_AGENT)));
    }

    @Test
    @Transactional
    void getGuestSession() throws Exception {
        // Initialize the database
        insertedGuestSession = guestSessionRepository.saveAndFlush(guestSession);

        // Get the guestSession
        restGuestSessionMockMvc
            .perform(get(ENTITY_API_URL_ID, guestSession.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(guestSession.getId().intValue()))
            .andExpect(jsonPath("$.sessionId").value(DEFAULT_SESSION_ID))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.lastActiveAt").value(DEFAULT_LAST_ACTIVE_AT.toString()))
            .andExpect(jsonPath("$.ipAddress").value(DEFAULT_IP_ADDRESS))
            .andExpect(jsonPath("$.userAgent").value(DEFAULT_USER_AGENT));
    }

    @Test
    @Transactional
    void getNonExistingGuestSession() throws Exception {
        // Get the guestSession
        restGuestSessionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingGuestSession() throws Exception {
        // Initialize the database
        insertedGuestSession = guestSessionRepository.saveAndFlush(guestSession);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        guestSessionSearchRepository.save(guestSession);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(guestSessionSearchRepository.findAll());

        // Update the guestSession
        GuestSession updatedGuestSession = guestSessionRepository.findById(guestSession.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedGuestSession are not directly saved in db
        em.detach(updatedGuestSession);
        updatedGuestSession
            .sessionId(UPDATED_SESSION_ID)
            .createdAt(UPDATED_CREATED_AT)
            .lastActiveAt(UPDATED_LAST_ACTIVE_AT)
            .ipAddress(UPDATED_IP_ADDRESS)
            .userAgent(UPDATED_USER_AGENT);
        GuestSessionDTO guestSessionDTO = guestSessionMapper.toDto(updatedGuestSession);

        restGuestSessionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, guestSessionDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(guestSessionDTO))
            )
            .andExpect(status().isOk());

        // Validate the GuestSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedGuestSessionToMatchAllProperties(updatedGuestSession);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(guestSessionSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<GuestSession> guestSessionSearchList = Streamable.of(guestSessionSearchRepository.findAll()).toList();
                GuestSession testGuestSessionSearch = guestSessionSearchList.get(searchDatabaseSizeAfter - 1);

                assertGuestSessionAllPropertiesEquals(testGuestSessionSearch, updatedGuestSession);
            });
    }

    @Test
    @Transactional
    void putNonExistingGuestSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(guestSessionSearchRepository.findAll());
        guestSession.setId(longCount.incrementAndGet());

        // Create the GuestSession
        GuestSessionDTO guestSessionDTO = guestSessionMapper.toDto(guestSession);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGuestSessionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, guestSessionDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(guestSessionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the GuestSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(guestSessionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchGuestSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(guestSessionSearchRepository.findAll());
        guestSession.setId(longCount.incrementAndGet());

        // Create the GuestSession
        GuestSessionDTO guestSessionDTO = guestSessionMapper.toDto(guestSession);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGuestSessionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(guestSessionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the GuestSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(guestSessionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamGuestSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(guestSessionSearchRepository.findAll());
        guestSession.setId(longCount.incrementAndGet());

        // Create the GuestSession
        GuestSessionDTO guestSessionDTO = guestSessionMapper.toDto(guestSession);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGuestSessionMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(guestSessionDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the GuestSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(guestSessionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateGuestSessionWithPatch() throws Exception {
        // Initialize the database
        insertedGuestSession = guestSessionRepository.saveAndFlush(guestSession);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the guestSession using partial update
        GuestSession partialUpdatedGuestSession = new GuestSession();
        partialUpdatedGuestSession.setId(guestSession.getId());

        partialUpdatedGuestSession.createdAt(UPDATED_CREATED_AT).userAgent(UPDATED_USER_AGENT);

        restGuestSessionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGuestSession.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedGuestSession))
            )
            .andExpect(status().isOk());

        // Validate the GuestSession in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertGuestSessionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedGuestSession, guestSession),
            getPersistedGuestSession(guestSession)
        );
    }

    @Test
    @Transactional
    void fullUpdateGuestSessionWithPatch() throws Exception {
        // Initialize the database
        insertedGuestSession = guestSessionRepository.saveAndFlush(guestSession);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the guestSession using partial update
        GuestSession partialUpdatedGuestSession = new GuestSession();
        partialUpdatedGuestSession.setId(guestSession.getId());

        partialUpdatedGuestSession
            .sessionId(UPDATED_SESSION_ID)
            .createdAt(UPDATED_CREATED_AT)
            .lastActiveAt(UPDATED_LAST_ACTIVE_AT)
            .ipAddress(UPDATED_IP_ADDRESS)
            .userAgent(UPDATED_USER_AGENT);

        restGuestSessionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGuestSession.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedGuestSession))
            )
            .andExpect(status().isOk());

        // Validate the GuestSession in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertGuestSessionUpdatableFieldsEquals(partialUpdatedGuestSession, getPersistedGuestSession(partialUpdatedGuestSession));
    }

    @Test
    @Transactional
    void patchNonExistingGuestSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(guestSessionSearchRepository.findAll());
        guestSession.setId(longCount.incrementAndGet());

        // Create the GuestSession
        GuestSessionDTO guestSessionDTO = guestSessionMapper.toDto(guestSession);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGuestSessionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, guestSessionDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(guestSessionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the GuestSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(guestSessionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchGuestSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(guestSessionSearchRepository.findAll());
        guestSession.setId(longCount.incrementAndGet());

        // Create the GuestSession
        GuestSessionDTO guestSessionDTO = guestSessionMapper.toDto(guestSession);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGuestSessionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(guestSessionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the GuestSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(guestSessionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamGuestSession() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(guestSessionSearchRepository.findAll());
        guestSession.setId(longCount.incrementAndGet());

        // Create the GuestSession
        GuestSessionDTO guestSessionDTO = guestSessionMapper.toDto(guestSession);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGuestSessionMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(guestSessionDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the GuestSession in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(guestSessionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteGuestSession() throws Exception {
        // Initialize the database
        insertedGuestSession = guestSessionRepository.saveAndFlush(guestSession);
        guestSessionRepository.save(guestSession);
        guestSessionSearchRepository.save(guestSession);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(guestSessionSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the guestSession
        restGuestSessionMockMvc
            .perform(delete(ENTITY_API_URL_ID, guestSession.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(guestSessionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchGuestSession() throws Exception {
        // Initialize the database
        insertedGuestSession = guestSessionRepository.saveAndFlush(guestSession);
        guestSessionSearchRepository.save(guestSession);

        // Search the guestSession
        restGuestSessionMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + guestSession.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(guestSession.getId().intValue())))
            .andExpect(jsonPath("$.[*].sessionId").value(hasItem(DEFAULT_SESSION_ID)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].lastActiveAt").value(hasItem(DEFAULT_LAST_ACTIVE_AT.toString())))
            .andExpect(jsonPath("$.[*].ipAddress").value(hasItem(DEFAULT_IP_ADDRESS)))
            .andExpect(jsonPath("$.[*].userAgent").value(hasItem(DEFAULT_USER_AGENT)));
    }

    protected long getRepositoryCount() {
        return guestSessionRepository.count();
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

    protected GuestSession getPersistedGuestSession(GuestSession guestSession) {
        return guestSessionRepository.findById(guestSession.getId()).orElseThrow();
    }

    protected void assertPersistedGuestSessionToMatchAllProperties(GuestSession expectedGuestSession) {
        assertGuestSessionAllPropertiesEquals(expectedGuestSession, getPersistedGuestSession(expectedGuestSession));
    }

    protected void assertPersistedGuestSessionToMatchUpdatableProperties(GuestSession expectedGuestSession) {
        assertGuestSessionAllUpdatablePropertiesEquals(expectedGuestSession, getPersistedGuestSession(expectedGuestSession));
    }
}
