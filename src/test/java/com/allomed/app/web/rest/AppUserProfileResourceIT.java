package com.allomed.app.web.rest;

import static com.allomed.app.domain.AppUserProfileAsserts.*;
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
import com.allomed.app.domain.User;
import com.allomed.app.repository.AppUserProfileRepository;
import com.allomed.app.repository.UserRepository;
import com.allomed.app.repository.search.AppUserProfileSearchRepository;
import com.allomed.app.service.AppUserProfileService;
import com.allomed.app.service.dto.AppUserProfileDTO;
import com.allomed.app.service.mapper.AppUserProfileMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Streamable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link AppUserProfileResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class AppUserProfileResourceIT {

    private static final Double DEFAULT_LATITUDE = 1D;
    private static final Double UPDATED_LATITUDE = 2D;

    private static final Double DEFAULT_LONGITUDE = 1D;
    private static final Double UPDATED_LONGITUDE = 2D;

    private static final String DEFAULT_LAST_LOGIN_IP = "AAAAAAAAAA";
    private static final String UPDATED_LAST_LOGIN_IP = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_USER_AGENT = "AAAAAAAAAA";
    private static final String UPDATED_LAST_USER_AGENT = "BBBBBBBBBB";

    private static final Instant DEFAULT_LAST_LOGIN_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_LOGIN_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/app-user-profiles";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/app-user-profiles/_search";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AppUserProfileRepository appUserProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private AppUserProfileRepository appUserProfileRepositoryMock;

    @Autowired
    private AppUserProfileMapper appUserProfileMapper;

    @Mock
    private AppUserProfileService appUserProfileServiceMock;

    @Autowired
    private AppUserProfileSearchRepository appUserProfileSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAppUserProfileMockMvc;

    private AppUserProfile appUserProfile;

    private AppUserProfile insertedAppUserProfile;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AppUserProfile createEntity(EntityManager em) {
        AppUserProfile appUserProfile = new AppUserProfile()
            .latitude(DEFAULT_LATITUDE)
            .longitude(DEFAULT_LONGITUDE)
            .lastLoginIp(DEFAULT_LAST_LOGIN_IP)
            .lastUserAgent(DEFAULT_LAST_USER_AGENT)
            .lastLoginDate(DEFAULT_LAST_LOGIN_DATE);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        appUserProfile.setInternalUser(user);
        return appUserProfile;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AppUserProfile createUpdatedEntity(EntityManager em) {
        AppUserProfile updatedAppUserProfile = new AppUserProfile()
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE)
            .lastLoginIp(UPDATED_LAST_LOGIN_IP)
            .lastUserAgent(UPDATED_LAST_USER_AGENT)
            .lastLoginDate(UPDATED_LAST_LOGIN_DATE);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedAppUserProfile.setInternalUser(user);
        return updatedAppUserProfile;
    }

    @BeforeEach
    void initTest() {
        appUserProfile = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedAppUserProfile != null) {
            appUserProfileRepository.delete(insertedAppUserProfile);
            appUserProfileSearchRepository.delete(insertedAppUserProfile);
            insertedAppUserProfile = null;
        }
        userRepository.deleteAll();
    }

    @Test
    @Transactional
    void createAppUserProfile() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(appUserProfileSearchRepository.findAll());
        // Create the AppUserProfile
        AppUserProfileDTO appUserProfileDTO = appUserProfileMapper.toDto(appUserProfile);
        var returnedAppUserProfileDTO = om.readValue(
            restAppUserProfileMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(appUserProfileDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            AppUserProfileDTO.class
        );

        // Validate the AppUserProfile in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAppUserProfile = appUserProfileMapper.toEntity(returnedAppUserProfileDTO);
        assertAppUserProfileUpdatableFieldsEquals(returnedAppUserProfile, getPersistedAppUserProfile(returnedAppUserProfile));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(appUserProfileSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        assertAppUserProfileMapsIdRelationshipPersistedValue(appUserProfile, returnedAppUserProfile);

        insertedAppUserProfile = returnedAppUserProfile;
    }

    @Test
    @Transactional
    void createAppUserProfileWithExistingId() throws Exception {
        // Create the AppUserProfile with an existing ID
        appUserProfile.setId("existing_id");
        AppUserProfileDTO appUserProfileDTO = appUserProfileMapper.toDto(appUserProfile);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(appUserProfileSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restAppUserProfileMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(appUserProfileDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AppUserProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(appUserProfileSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void updateAppUserProfileMapsIdAssociationWithNewId() throws Exception {
        // Initialize the database
        insertedAppUserProfile = appUserProfileRepository.saveAndFlush(appUserProfile);
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(appUserProfileSearchRepository.findAll());
        // Add a new parent entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();

        // Load the appUserProfile
        AppUserProfile updatedAppUserProfile = appUserProfileRepository.findById(appUserProfile.getId()).orElseThrow();
        assertThat(updatedAppUserProfile).isNotNull();
        // Disconnect from session so that the updates on updatedAppUserProfile are not directly saved in db
        em.detach(updatedAppUserProfile);

        // Update the User with new association value
        updatedAppUserProfile.setInternalUser(user);
        AppUserProfileDTO updatedAppUserProfileDTO = appUserProfileMapper.toDto(updatedAppUserProfile);
        assertThat(updatedAppUserProfileDTO).isNotNull();

        // Update the entity
        restAppUserProfileMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedAppUserProfileDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedAppUserProfileDTO))
            )
            .andExpect(status().isOk());

        // Validate the AppUserProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);

        /**
         * Validate the id for MapsId, the ids must be same
         * Uncomment the following line for assertion. However, please note that there is a known issue and uncommenting will fail the test.
         * Please look at https://github.com/jhipster/generator-jhipster/issues/9100. You can modify this test as necessary.
         * assertThat(testAppUserProfile.getId()).isEqualTo(testAppUserProfile.getUser().getId());
         */
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(appUserProfileSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllAppUserProfiles() throws Exception {
        // Initialize the database
        insertedAppUserProfile = appUserProfileRepository.saveAndFlush(appUserProfile);

        // Get all the appUserProfileList
        restAppUserProfileMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(appUserProfile.getId())))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE)))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE)))
            .andExpect(jsonPath("$.[*].lastLoginIp").value(hasItem(DEFAULT_LAST_LOGIN_IP)))
            .andExpect(jsonPath("$.[*].lastUserAgent").value(hasItem(DEFAULT_LAST_USER_AGENT)))
            .andExpect(jsonPath("$.[*].lastLoginDate").value(hasItem(DEFAULT_LAST_LOGIN_DATE.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAppUserProfilesWithEagerRelationshipsIsEnabled() throws Exception {
        when(appUserProfileServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restAppUserProfileMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(appUserProfileServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAppUserProfilesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(appUserProfileServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restAppUserProfileMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(appUserProfileRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getAppUserProfile() throws Exception {
        // Initialize the database
        insertedAppUserProfile = appUserProfileRepository.saveAndFlush(appUserProfile);

        // Get the appUserProfile
        restAppUserProfileMockMvc
            .perform(get(ENTITY_API_URL_ID, appUserProfile.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(appUserProfile.getId()))
            .andExpect(jsonPath("$.latitude").value(DEFAULT_LATITUDE))
            .andExpect(jsonPath("$.longitude").value(DEFAULT_LONGITUDE))
            .andExpect(jsonPath("$.lastLoginIp").value(DEFAULT_LAST_LOGIN_IP))
            .andExpect(jsonPath("$.lastUserAgent").value(DEFAULT_LAST_USER_AGENT))
            .andExpect(jsonPath("$.lastLoginDate").value(DEFAULT_LAST_LOGIN_DATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingAppUserProfile() throws Exception {
        // Get the appUserProfile
        restAppUserProfileMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAppUserProfile() throws Exception {
        // Initialize the database
        insertedAppUserProfile = appUserProfileRepository.saveAndFlush(appUserProfile);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        appUserProfileSearchRepository.save(appUserProfile);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(appUserProfileSearchRepository.findAll());

        // Update the appUserProfile
        AppUserProfile updatedAppUserProfile = appUserProfileRepository.findById(appUserProfile.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAppUserProfile are not directly saved in db
        em.detach(updatedAppUserProfile);
        updatedAppUserProfile
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE)
            .lastLoginIp(UPDATED_LAST_LOGIN_IP)
            .lastUserAgent(UPDATED_LAST_USER_AGENT)
            .lastLoginDate(UPDATED_LAST_LOGIN_DATE);
        AppUserProfileDTO appUserProfileDTO = appUserProfileMapper.toDto(updatedAppUserProfile);

        restAppUserProfileMockMvc
            .perform(
                put(ENTITY_API_URL_ID, appUserProfileDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(appUserProfileDTO))
            )
            .andExpect(status().isOk());

        // Validate the AppUserProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAppUserProfileToMatchAllProperties(updatedAppUserProfile);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(appUserProfileSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<AppUserProfile> appUserProfileSearchList = Streamable.of(appUserProfileSearchRepository.findAll()).toList();
                AppUserProfile testAppUserProfileSearch = appUserProfileSearchList.get(searchDatabaseSizeAfter - 1);

                assertAppUserProfileAllPropertiesEquals(testAppUserProfileSearch, updatedAppUserProfile);
            });
    }

    @Test
    @Transactional
    void putNonExistingAppUserProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(appUserProfileSearchRepository.findAll());
        appUserProfile.setId(UUID.randomUUID().toString());

        // Create the AppUserProfile
        AppUserProfileDTO appUserProfileDTO = appUserProfileMapper.toDto(appUserProfile);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAppUserProfileMockMvc
            .perform(
                put(ENTITY_API_URL_ID, appUserProfileDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(appUserProfileDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AppUserProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(appUserProfileSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchAppUserProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(appUserProfileSearchRepository.findAll());
        appUserProfile.setId(UUID.randomUUID().toString());

        // Create the AppUserProfile
        AppUserProfileDTO appUserProfileDTO = appUserProfileMapper.toDto(appUserProfile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAppUserProfileMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(appUserProfileDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AppUserProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(appUserProfileSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAppUserProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(appUserProfileSearchRepository.findAll());
        appUserProfile.setId(UUID.randomUUID().toString());

        // Create the AppUserProfile
        AppUserProfileDTO appUserProfileDTO = appUserProfileMapper.toDto(appUserProfile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAppUserProfileMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(appUserProfileDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AppUserProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(appUserProfileSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateAppUserProfileWithPatch() throws Exception {
        // Initialize the database
        insertedAppUserProfile = appUserProfileRepository.saveAndFlush(appUserProfile);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the appUserProfile using partial update
        AppUserProfile partialUpdatedAppUserProfile = new AppUserProfile();
        partialUpdatedAppUserProfile.setId(appUserProfile.getId());

        partialUpdatedAppUserProfile
            .longitude(UPDATED_LONGITUDE)
            .lastUserAgent(UPDATED_LAST_USER_AGENT)
            .lastLoginDate(UPDATED_LAST_LOGIN_DATE);

        restAppUserProfileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAppUserProfile.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAppUserProfile))
            )
            .andExpect(status().isOk());

        // Validate the AppUserProfile in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAppUserProfileUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedAppUserProfile, appUserProfile),
            getPersistedAppUserProfile(appUserProfile)
        );
    }

    @Test
    @Transactional
    void fullUpdateAppUserProfileWithPatch() throws Exception {
        // Initialize the database
        insertedAppUserProfile = appUserProfileRepository.saveAndFlush(appUserProfile);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the appUserProfile using partial update
        AppUserProfile partialUpdatedAppUserProfile = new AppUserProfile();
        partialUpdatedAppUserProfile.setId(appUserProfile.getId());

        partialUpdatedAppUserProfile
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE)
            .lastLoginIp(UPDATED_LAST_LOGIN_IP)
            .lastUserAgent(UPDATED_LAST_USER_AGENT)
            .lastLoginDate(UPDATED_LAST_LOGIN_DATE);

        restAppUserProfileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAppUserProfile.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAppUserProfile))
            )
            .andExpect(status().isOk());

        // Validate the AppUserProfile in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAppUserProfileUpdatableFieldsEquals(partialUpdatedAppUserProfile, getPersistedAppUserProfile(partialUpdatedAppUserProfile));
    }

    @Test
    @Transactional
    void patchNonExistingAppUserProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(appUserProfileSearchRepository.findAll());
        appUserProfile.setId(UUID.randomUUID().toString());

        // Create the AppUserProfile
        AppUserProfileDTO appUserProfileDTO = appUserProfileMapper.toDto(appUserProfile);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAppUserProfileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, appUserProfileDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(appUserProfileDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AppUserProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(appUserProfileSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAppUserProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(appUserProfileSearchRepository.findAll());
        appUserProfile.setId(UUID.randomUUID().toString());

        // Create the AppUserProfile
        AppUserProfileDTO appUserProfileDTO = appUserProfileMapper.toDto(appUserProfile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAppUserProfileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(appUserProfileDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AppUserProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(appUserProfileSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAppUserProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(appUserProfileSearchRepository.findAll());
        appUserProfile.setId(UUID.randomUUID().toString());

        // Create the AppUserProfile
        AppUserProfileDTO appUserProfileDTO = appUserProfileMapper.toDto(appUserProfile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAppUserProfileMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(appUserProfileDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AppUserProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(appUserProfileSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteAppUserProfile() throws Exception {
        // Initialize the database
        insertedAppUserProfile = appUserProfileRepository.saveAndFlush(appUserProfile);
        appUserProfileRepository.save(appUserProfile);
        appUserProfileSearchRepository.save(appUserProfile);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(appUserProfileSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the appUserProfile
        restAppUserProfileMockMvc
            .perform(delete(ENTITY_API_URL_ID, appUserProfile.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(appUserProfileSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchAppUserProfile() throws Exception {
        // Initialize the database
        insertedAppUserProfile = appUserProfileRepository.saveAndFlush(appUserProfile);
        appUserProfileSearchRepository.save(appUserProfile);

        // Search the appUserProfile
        restAppUserProfileMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + appUserProfile.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(appUserProfile.getId())))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE)))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE)))
            .andExpect(jsonPath("$.[*].lastLoginIp").value(hasItem(DEFAULT_LAST_LOGIN_IP)))
            .andExpect(jsonPath("$.[*].lastUserAgent").value(hasItem(DEFAULT_LAST_USER_AGENT)))
            .andExpect(jsonPath("$.[*].lastLoginDate").value(hasItem(DEFAULT_LAST_LOGIN_DATE.toString())));
    }

    protected long getRepositoryCount() {
        return appUserProfileRepository.count();
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

    protected AppUserProfile getPersistedAppUserProfile(AppUserProfile appUserProfile) {
        return appUserProfileRepository.findById(appUserProfile.getId()).orElseThrow();
    }

    protected void assertPersistedAppUserProfileToMatchAllProperties(AppUserProfile expectedAppUserProfile) {
        assertAppUserProfileAllPropertiesEquals(expectedAppUserProfile, getPersistedAppUserProfile(expectedAppUserProfile));
    }

    protected void assertPersistedAppUserProfileToMatchUpdatableProperties(AppUserProfile expectedAppUserProfile) {
        assertAppUserProfileAllUpdatablePropertiesEquals(expectedAppUserProfile, getPersistedAppUserProfile(expectedAppUserProfile));
    }
}
