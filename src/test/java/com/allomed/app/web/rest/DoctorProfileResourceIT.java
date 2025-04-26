package com.allomed.app.web.rest;

import static com.allomed.app.domain.DoctorProfileAsserts.*;
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
import com.allomed.app.domain.User;
import com.allomed.app.repository.DoctorProfileRepository;
import com.allomed.app.repository.UserRepository;
import com.allomed.app.repository.search.DoctorProfileSearchRepository;
import com.allomed.app.service.DoctorProfileService;
import com.allomed.app.service.dto.DoctorProfileDTO;
import com.allomed.app.service.mapper.DoctorProfileMapper;
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
 * Integration tests for the {@link DoctorProfileResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class DoctorProfileResourceIT {

    private static final String DEFAULT_PHONE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_PHONE_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_OFFICE_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_OFFICE_ADDRESS = "BBBBBBBBBB";

    private static final Double DEFAULT_LATITUDE = 1D;
    private static final Double UPDATED_LATITUDE = 2D;
    private static final Double SMALLER_LATITUDE = 1D - 1D;

    private static final Double DEFAULT_LONGITUDE = 1D;
    private static final Double UPDATED_LONGITUDE = 2D;
    private static final Double SMALLER_LONGITUDE = 1D - 1D;

    private static final String DEFAULT_INPE_CODE = "AAAAAAAAAA";
    private static final String UPDATED_INPE_CODE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_VERIFIED = false;
    private static final Boolean UPDATED_IS_VERIFIED = true;

    private static final String DEFAULT_LAST_LOGIN_IP = "AAAAAAAAAA";
    private static final String UPDATED_LAST_LOGIN_IP = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_USER_AGENT = "AAAAAAAAAA";
    private static final String UPDATED_LAST_USER_AGENT = "BBBBBBBBBB";

    private static final Instant DEFAULT_LAST_LOGIN_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_LOGIN_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/doctor-profiles";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/doctor-profiles/_search";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DoctorProfileRepository doctorProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private DoctorProfileRepository doctorProfileRepositoryMock;

    @Autowired
    private DoctorProfileMapper doctorProfileMapper;

    @Mock
    private DoctorProfileService doctorProfileServiceMock;

    @Autowired
    private DoctorProfileSearchRepository doctorProfileSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDoctorProfileMockMvc;

    private DoctorProfile doctorProfile;

    private DoctorProfile insertedDoctorProfile;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DoctorProfile createEntity(EntityManager em) {
        DoctorProfile doctorProfile = new DoctorProfile()
            .phoneNumber(DEFAULT_PHONE_NUMBER)
            .officeAddress(DEFAULT_OFFICE_ADDRESS)
            .latitude(DEFAULT_LATITUDE)
            .longitude(DEFAULT_LONGITUDE)
            .inpeCode(DEFAULT_INPE_CODE)
            .isVerified(DEFAULT_IS_VERIFIED)
            .lastLoginIp(DEFAULT_LAST_LOGIN_IP)
            .lastUserAgent(DEFAULT_LAST_USER_AGENT)
            .lastLoginDate(DEFAULT_LAST_LOGIN_DATE);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        doctorProfile.setInternalUser(user);
        return doctorProfile;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DoctorProfile createUpdatedEntity(EntityManager em) {
        DoctorProfile updatedDoctorProfile = new DoctorProfile()
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .officeAddress(UPDATED_OFFICE_ADDRESS)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE)
            .inpeCode(UPDATED_INPE_CODE)
            .isVerified(UPDATED_IS_VERIFIED)
            .lastLoginIp(UPDATED_LAST_LOGIN_IP)
            .lastUserAgent(UPDATED_LAST_USER_AGENT)
            .lastLoginDate(UPDATED_LAST_LOGIN_DATE);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedDoctorProfile.setInternalUser(user);
        return updatedDoctorProfile;
    }

    @BeforeEach
    void initTest() {
        doctorProfile = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedDoctorProfile != null) {
            doctorProfileRepository.delete(insertedDoctorProfile);
            doctorProfileSearchRepository.delete(insertedDoctorProfile);
            insertedDoctorProfile = null;
        }
        userRepository.deleteAll();
    }

    @Test
    @Transactional
    void createDoctorProfile() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(doctorProfileSearchRepository.findAll());
        // Create the DoctorProfile
        DoctorProfileDTO doctorProfileDTO = doctorProfileMapper.toDto(doctorProfile);
        var returnedDoctorProfileDTO = om.readValue(
            restDoctorProfileMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(doctorProfileDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            DoctorProfileDTO.class
        );

        // Validate the DoctorProfile in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedDoctorProfile = doctorProfileMapper.toEntity(returnedDoctorProfileDTO);
        assertDoctorProfileUpdatableFieldsEquals(returnedDoctorProfile, getPersistedDoctorProfile(returnedDoctorProfile));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(doctorProfileSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        assertDoctorProfileMapsIdRelationshipPersistedValue(doctorProfile, returnedDoctorProfile);

        insertedDoctorProfile = returnedDoctorProfile;
    }

    @Test
    @Transactional
    void createDoctorProfileWithExistingId() throws Exception {
        // Create the DoctorProfile with an existing ID
        doctorProfile.setId("existing_id");
        DoctorProfileDTO doctorProfileDTO = doctorProfileMapper.toDto(doctorProfile);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(doctorProfileSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restDoctorProfileMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(doctorProfileDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DoctorProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(doctorProfileSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void updateDoctorProfileMapsIdAssociationWithNewId() throws Exception {
        // Initialize the database
        insertedDoctorProfile = doctorProfileRepository.saveAndFlush(doctorProfile);
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(doctorProfileSearchRepository.findAll());
        // Add a new parent entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();

        // Load the doctorProfile
        DoctorProfile updatedDoctorProfile = doctorProfileRepository.findById(doctorProfile.getId()).orElseThrow();
        assertThat(updatedDoctorProfile).isNotNull();
        // Disconnect from session so that the updates on updatedDoctorProfile are not directly saved in db
        em.detach(updatedDoctorProfile);

        // Update the User with new association value
        updatedDoctorProfile.setUser(user);
        DoctorProfileDTO updatedDoctorProfileDTO = doctorProfileMapper.toDto(updatedDoctorProfile);
        assertThat(updatedDoctorProfileDTO).isNotNull();

        // Update the entity
        restDoctorProfileMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedDoctorProfileDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedDoctorProfileDTO))
            )
            .andExpect(status().isOk());

        // Validate the DoctorProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);

        /**
         * Validate the id for MapsId, the ids must be same
         * Uncomment the following line for assertion. However, please note that there is a known issue and uncommenting will fail the test.
         * Please look at https://github.com/jhipster/generator-jhipster/issues/9100. You can modify this test as necessary.
         * assertThat(testDoctorProfile.getId()).isEqualTo(testDoctorProfile.getUser().getId());
         */
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(doctorProfileSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkPhoneNumberIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(doctorProfileSearchRepository.findAll());
        // set the field null
        doctorProfile.setPhoneNumber(null);

        // Create the DoctorProfile, which fails.
        DoctorProfileDTO doctorProfileDTO = doctorProfileMapper.toDto(doctorProfile);

        restDoctorProfileMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(doctorProfileDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(doctorProfileSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkOfficeAddressIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(doctorProfileSearchRepository.findAll());
        // set the field null
        doctorProfile.setOfficeAddress(null);

        // Create the DoctorProfile, which fails.
        DoctorProfileDTO doctorProfileDTO = doctorProfileMapper.toDto(doctorProfile);

        restDoctorProfileMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(doctorProfileDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(doctorProfileSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkLatitudeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(doctorProfileSearchRepository.findAll());
        // set the field null
        doctorProfile.setLatitude(null);

        // Create the DoctorProfile, which fails.
        DoctorProfileDTO doctorProfileDTO = doctorProfileMapper.toDto(doctorProfile);

        restDoctorProfileMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(doctorProfileDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(doctorProfileSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkLongitudeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(doctorProfileSearchRepository.findAll());
        // set the field null
        doctorProfile.setLongitude(null);

        // Create the DoctorProfile, which fails.
        DoctorProfileDTO doctorProfileDTO = doctorProfileMapper.toDto(doctorProfile);

        restDoctorProfileMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(doctorProfileDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(doctorProfileSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkInpeCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(doctorProfileSearchRepository.findAll());
        // set the field null
        doctorProfile.setInpeCode(null);

        // Create the DoctorProfile, which fails.
        DoctorProfileDTO doctorProfileDTO = doctorProfileMapper.toDto(doctorProfile);

        restDoctorProfileMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(doctorProfileDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(doctorProfileSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkIsVerifiedIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(doctorProfileSearchRepository.findAll());
        // set the field null
        doctorProfile.setIsVerified(null);

        // Create the DoctorProfile, which fails.
        DoctorProfileDTO doctorProfileDTO = doctorProfileMapper.toDto(doctorProfile);

        restDoctorProfileMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(doctorProfileDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(doctorProfileSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllDoctorProfiles() throws Exception {
        // Initialize the database
        insertedDoctorProfile = doctorProfileRepository.saveAndFlush(doctorProfile);

        // Get all the doctorProfileList
        restDoctorProfileMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(doctorProfile.getId())))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)))
            .andExpect(jsonPath("$.[*].officeAddress").value(hasItem(DEFAULT_OFFICE_ADDRESS)))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE)))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE)))
            .andExpect(jsonPath("$.[*].inpeCode").value(hasItem(DEFAULT_INPE_CODE)))
            .andExpect(jsonPath("$.[*].isVerified").value(hasItem(DEFAULT_IS_VERIFIED)))
            .andExpect(jsonPath("$.[*].lastLoginIp").value(hasItem(DEFAULT_LAST_LOGIN_IP)))
            .andExpect(jsonPath("$.[*].lastUserAgent").value(hasItem(DEFAULT_LAST_USER_AGENT)))
            .andExpect(jsonPath("$.[*].lastLoginDate").value(hasItem(DEFAULT_LAST_LOGIN_DATE.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDoctorProfilesWithEagerRelationshipsIsEnabled() throws Exception {
        when(doctorProfileServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restDoctorProfileMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(doctorProfileServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDoctorProfilesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(doctorProfileServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restDoctorProfileMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(doctorProfileRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getDoctorProfile() throws Exception {
        // Initialize the database
        insertedDoctorProfile = doctorProfileRepository.saveAndFlush(doctorProfile);

        // Get the doctorProfile
        restDoctorProfileMockMvc
            .perform(get(ENTITY_API_URL_ID, doctorProfile.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(doctorProfile.getId()))
            .andExpect(jsonPath("$.phoneNumber").value(DEFAULT_PHONE_NUMBER))
            .andExpect(jsonPath("$.officeAddress").value(DEFAULT_OFFICE_ADDRESS))
            .andExpect(jsonPath("$.latitude").value(DEFAULT_LATITUDE))
            .andExpect(jsonPath("$.longitude").value(DEFAULT_LONGITUDE))
            .andExpect(jsonPath("$.inpeCode").value(DEFAULT_INPE_CODE))
            .andExpect(jsonPath("$.isVerified").value(DEFAULT_IS_VERIFIED))
            .andExpect(jsonPath("$.lastLoginIp").value(DEFAULT_LAST_LOGIN_IP))
            .andExpect(jsonPath("$.lastUserAgent").value(DEFAULT_LAST_USER_AGENT))
            .andExpect(jsonPath("$.lastLoginDate").value(DEFAULT_LAST_LOGIN_DATE.toString()));
    }

    @Test
    @Transactional
    void getDoctorProfilesByIdFiltering() throws Exception {
        // Initialize the database
        insertedDoctorProfile = doctorProfileRepository.saveAndFlush(doctorProfile);

        String id = doctorProfile.getId();

        defaultDoctorProfileFiltering("id.equals=" + id, "id.notEquals=" + id);
    }

    @Test
    @Transactional
    void getAllDoctorProfilesByPhoneNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDoctorProfile = doctorProfileRepository.saveAndFlush(doctorProfile);

        // Get all the doctorProfileList where phoneNumber equals to
        defaultDoctorProfileFiltering("phoneNumber.equals=" + DEFAULT_PHONE_NUMBER, "phoneNumber.equals=" + UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void getAllDoctorProfilesByPhoneNumberIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDoctorProfile = doctorProfileRepository.saveAndFlush(doctorProfile);

        // Get all the doctorProfileList where phoneNumber in
        defaultDoctorProfileFiltering(
            "phoneNumber.in=" + DEFAULT_PHONE_NUMBER + "," + UPDATED_PHONE_NUMBER,
            "phoneNumber.in=" + UPDATED_PHONE_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllDoctorProfilesByPhoneNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDoctorProfile = doctorProfileRepository.saveAndFlush(doctorProfile);

        // Get all the doctorProfileList where phoneNumber is not null
        defaultDoctorProfileFiltering("phoneNumber.specified=true", "phoneNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllDoctorProfilesByPhoneNumberContainsSomething() throws Exception {
        // Initialize the database
        insertedDoctorProfile = doctorProfileRepository.saveAndFlush(doctorProfile);

        // Get all the doctorProfileList where phoneNumber contains
        defaultDoctorProfileFiltering("phoneNumber.contains=" + DEFAULT_PHONE_NUMBER, "phoneNumber.contains=" + UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void getAllDoctorProfilesByPhoneNumberNotContainsSomething() throws Exception {
        // Initialize the database
        insertedDoctorProfile = doctorProfileRepository.saveAndFlush(doctorProfile);

        // Get all the doctorProfileList where phoneNumber does not contain
        defaultDoctorProfileFiltering(
            "phoneNumber.doesNotContain=" + UPDATED_PHONE_NUMBER,
            "phoneNumber.doesNotContain=" + DEFAULT_PHONE_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllDoctorProfilesByOfficeAddressIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDoctorProfile = doctorProfileRepository.saveAndFlush(doctorProfile);

        // Get all the doctorProfileList where officeAddress equals to
        defaultDoctorProfileFiltering("officeAddress.equals=" + DEFAULT_OFFICE_ADDRESS, "officeAddress.equals=" + UPDATED_OFFICE_ADDRESS);
    }

    @Test
    @Transactional
    void getAllDoctorProfilesByOfficeAddressIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDoctorProfile = doctorProfileRepository.saveAndFlush(doctorProfile);

        // Get all the doctorProfileList where officeAddress in
        defaultDoctorProfileFiltering(
            "officeAddress.in=" + DEFAULT_OFFICE_ADDRESS + "," + UPDATED_OFFICE_ADDRESS,
            "officeAddress.in=" + UPDATED_OFFICE_ADDRESS
        );
    }

    @Test
    @Transactional
    void getAllDoctorProfilesByOfficeAddressIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDoctorProfile = doctorProfileRepository.saveAndFlush(doctorProfile);

        // Get all the doctorProfileList where officeAddress is not null
        defaultDoctorProfileFiltering("officeAddress.specified=true", "officeAddress.specified=false");
    }

    @Test
    @Transactional
    void getAllDoctorProfilesByOfficeAddressContainsSomething() throws Exception {
        // Initialize the database
        insertedDoctorProfile = doctorProfileRepository.saveAndFlush(doctorProfile);

        // Get all the doctorProfileList where officeAddress contains
        defaultDoctorProfileFiltering(
            "officeAddress.contains=" + DEFAULT_OFFICE_ADDRESS,
            "officeAddress.contains=" + UPDATED_OFFICE_ADDRESS
        );
    }

    @Test
    @Transactional
    void getAllDoctorProfilesByOfficeAddressNotContainsSomething() throws Exception {
        // Initialize the database
        insertedDoctorProfile = doctorProfileRepository.saveAndFlush(doctorProfile);

        // Get all the doctorProfileList where officeAddress does not contain
        defaultDoctorProfileFiltering(
            "officeAddress.doesNotContain=" + UPDATED_OFFICE_ADDRESS,
            "officeAddress.doesNotContain=" + DEFAULT_OFFICE_ADDRESS
        );
    }

    @Test
    @Transactional
    void getAllDoctorProfilesByLatitudeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDoctorProfile = doctorProfileRepository.saveAndFlush(doctorProfile);

        // Get all the doctorProfileList where latitude equals to
        defaultDoctorProfileFiltering("latitude.equals=" + DEFAULT_LATITUDE, "latitude.equals=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllDoctorProfilesByLatitudeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDoctorProfile = doctorProfileRepository.saveAndFlush(doctorProfile);

        // Get all the doctorProfileList where latitude in
        defaultDoctorProfileFiltering("latitude.in=" + DEFAULT_LATITUDE + "," + UPDATED_LATITUDE, "latitude.in=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllDoctorProfilesByLatitudeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDoctorProfile = doctorProfileRepository.saveAndFlush(doctorProfile);

        // Get all the doctorProfileList where latitude is not null
        defaultDoctorProfileFiltering("latitude.specified=true", "latitude.specified=false");
    }

    @Test
    @Transactional
    void getAllDoctorProfilesByLatitudeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedDoctorProfile = doctorProfileRepository.saveAndFlush(doctorProfile);

        // Get all the doctorProfileList where latitude is greater than or equal to
        defaultDoctorProfileFiltering("latitude.greaterThanOrEqual=" + DEFAULT_LATITUDE, "latitude.greaterThanOrEqual=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllDoctorProfilesByLatitudeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedDoctorProfile = doctorProfileRepository.saveAndFlush(doctorProfile);

        // Get all the doctorProfileList where latitude is less than or equal to
        defaultDoctorProfileFiltering("latitude.lessThanOrEqual=" + DEFAULT_LATITUDE, "latitude.lessThanOrEqual=" + SMALLER_LATITUDE);
    }

    @Test
    @Transactional
    void getAllDoctorProfilesByLatitudeIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedDoctorProfile = doctorProfileRepository.saveAndFlush(doctorProfile);

        // Get all the doctorProfileList where latitude is less than
        defaultDoctorProfileFiltering("latitude.lessThan=" + UPDATED_LATITUDE, "latitude.lessThan=" + DEFAULT_LATITUDE);
    }

    @Test
    @Transactional
    void getAllDoctorProfilesByLatitudeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedDoctorProfile = doctorProfileRepository.saveAndFlush(doctorProfile);

        // Get all the doctorProfileList where latitude is greater than
        defaultDoctorProfileFiltering("latitude.greaterThan=" + SMALLER_LATITUDE, "latitude.greaterThan=" + DEFAULT_LATITUDE);
    }

    @Test
    @Transactional
    void getAllDoctorProfilesByLongitudeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDoctorProfile = doctorProfileRepository.saveAndFlush(doctorProfile);

        // Get all the doctorProfileList where longitude equals to
        defaultDoctorProfileFiltering("longitude.equals=" + DEFAULT_LONGITUDE, "longitude.equals=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllDoctorProfilesByLongitudeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDoctorProfile = doctorProfileRepository.saveAndFlush(doctorProfile);

        // Get all the doctorProfileList where longitude in
        defaultDoctorProfileFiltering("longitude.in=" + DEFAULT_LONGITUDE + "," + UPDATED_LONGITUDE, "longitude.in=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllDoctorProfilesByLongitudeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDoctorProfile = doctorProfileRepository.saveAndFlush(doctorProfile);

        // Get all the doctorProfileList where longitude is not null
        defaultDoctorProfileFiltering("longitude.specified=true", "longitude.specified=false");
    }

    @Test
    @Transactional
    void getAllDoctorProfilesByLongitudeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedDoctorProfile = doctorProfileRepository.saveAndFlush(doctorProfile);

        // Get all the doctorProfileList where longitude is greater than or equal to
        defaultDoctorProfileFiltering(
            "longitude.greaterThanOrEqual=" + DEFAULT_LONGITUDE,
            "longitude.greaterThanOrEqual=" + UPDATED_LONGITUDE
        );
    }

    @Test
    @Transactional
    void getAllDoctorProfilesByLongitudeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedDoctorProfile = doctorProfileRepository.saveAndFlush(doctorProfile);

        // Get all the doctorProfileList where longitude is less than or equal to
        defaultDoctorProfileFiltering("longitude.lessThanOrEqual=" + DEFAULT_LONGITUDE, "longitude.lessThanOrEqual=" + SMALLER_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllDoctorProfilesByLongitudeIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedDoctorProfile = doctorProfileRepository.saveAndFlush(doctorProfile);

        // Get all the doctorProfileList where longitude is less than
        defaultDoctorProfileFiltering("longitude.lessThan=" + UPDATED_LONGITUDE, "longitude.lessThan=" + DEFAULT_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllDoctorProfilesByLongitudeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedDoctorProfile = doctorProfileRepository.saveAndFlush(doctorProfile);

        // Get all the doctorProfileList where longitude is greater than
        defaultDoctorProfileFiltering("longitude.greaterThan=" + SMALLER_LONGITUDE, "longitude.greaterThan=" + DEFAULT_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllDoctorProfilesByInpeCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDoctorProfile = doctorProfileRepository.saveAndFlush(doctorProfile);

        // Get all the doctorProfileList where inpeCode equals to
        defaultDoctorProfileFiltering("inpeCode.equals=" + DEFAULT_INPE_CODE, "inpeCode.equals=" + UPDATED_INPE_CODE);
    }

    @Test
    @Transactional
    void getAllDoctorProfilesByInpeCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDoctorProfile = doctorProfileRepository.saveAndFlush(doctorProfile);

        // Get all the doctorProfileList where inpeCode in
        defaultDoctorProfileFiltering("inpeCode.in=" + DEFAULT_INPE_CODE + "," + UPDATED_INPE_CODE, "inpeCode.in=" + UPDATED_INPE_CODE);
    }

    @Test
    @Transactional
    void getAllDoctorProfilesByInpeCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDoctorProfile = doctorProfileRepository.saveAndFlush(doctorProfile);

        // Get all the doctorProfileList where inpeCode is not null
        defaultDoctorProfileFiltering("inpeCode.specified=true", "inpeCode.specified=false");
    }

    @Test
    @Transactional
    void getAllDoctorProfilesByInpeCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedDoctorProfile = doctorProfileRepository.saveAndFlush(doctorProfile);

        // Get all the doctorProfileList where inpeCode contains
        defaultDoctorProfileFiltering("inpeCode.contains=" + DEFAULT_INPE_CODE, "inpeCode.contains=" + UPDATED_INPE_CODE);
    }

    @Test
    @Transactional
    void getAllDoctorProfilesByInpeCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedDoctorProfile = doctorProfileRepository.saveAndFlush(doctorProfile);

        // Get all the doctorProfileList where inpeCode does not contain
        defaultDoctorProfileFiltering("inpeCode.doesNotContain=" + UPDATED_INPE_CODE, "inpeCode.doesNotContain=" + DEFAULT_INPE_CODE);
    }

    @Test
    @Transactional
    void getAllDoctorProfilesByIsVerifiedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDoctorProfile = doctorProfileRepository.saveAndFlush(doctorProfile);

        // Get all the doctorProfileList where isVerified equals to
        defaultDoctorProfileFiltering("isVerified.equals=" + DEFAULT_IS_VERIFIED, "isVerified.equals=" + UPDATED_IS_VERIFIED);
    }

    @Test
    @Transactional
    void getAllDoctorProfilesByIsVerifiedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDoctorProfile = doctorProfileRepository.saveAndFlush(doctorProfile);

        // Get all the doctorProfileList where isVerified in
        defaultDoctorProfileFiltering(
            "isVerified.in=" + DEFAULT_IS_VERIFIED + "," + UPDATED_IS_VERIFIED,
            "isVerified.in=" + UPDATED_IS_VERIFIED
        );
    }

    @Test
    @Transactional
    void getAllDoctorProfilesByIsVerifiedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDoctorProfile = doctorProfileRepository.saveAndFlush(doctorProfile);

        // Get all the doctorProfileList where isVerified is not null
        defaultDoctorProfileFiltering("isVerified.specified=true", "isVerified.specified=false");
    }

    @Test
    @Transactional
    void getAllDoctorProfilesByLastLoginIpIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDoctorProfile = doctorProfileRepository.saveAndFlush(doctorProfile);

        // Get all the doctorProfileList where lastLoginIp equals to
        defaultDoctorProfileFiltering("lastLoginIp.equals=" + DEFAULT_LAST_LOGIN_IP, "lastLoginIp.equals=" + UPDATED_LAST_LOGIN_IP);
    }

    @Test
    @Transactional
    void getAllDoctorProfilesByLastLoginIpIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDoctorProfile = doctorProfileRepository.saveAndFlush(doctorProfile);

        // Get all the doctorProfileList where lastLoginIp in
        defaultDoctorProfileFiltering(
            "lastLoginIp.in=" + DEFAULT_LAST_LOGIN_IP + "," + UPDATED_LAST_LOGIN_IP,
            "lastLoginIp.in=" + UPDATED_LAST_LOGIN_IP
        );
    }

    @Test
    @Transactional
    void getAllDoctorProfilesByLastLoginIpIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDoctorProfile = doctorProfileRepository.saveAndFlush(doctorProfile);

        // Get all the doctorProfileList where lastLoginIp is not null
        defaultDoctorProfileFiltering("lastLoginIp.specified=true", "lastLoginIp.specified=false");
    }

    @Test
    @Transactional
    void getAllDoctorProfilesByLastLoginIpContainsSomething() throws Exception {
        // Initialize the database
        insertedDoctorProfile = doctorProfileRepository.saveAndFlush(doctorProfile);

        // Get all the doctorProfileList where lastLoginIp contains
        defaultDoctorProfileFiltering("lastLoginIp.contains=" + DEFAULT_LAST_LOGIN_IP, "lastLoginIp.contains=" + UPDATED_LAST_LOGIN_IP);
    }

    @Test
    @Transactional
    void getAllDoctorProfilesByLastLoginIpNotContainsSomething() throws Exception {
        // Initialize the database
        insertedDoctorProfile = doctorProfileRepository.saveAndFlush(doctorProfile);

        // Get all the doctorProfileList where lastLoginIp does not contain
        defaultDoctorProfileFiltering(
            "lastLoginIp.doesNotContain=" + UPDATED_LAST_LOGIN_IP,
            "lastLoginIp.doesNotContain=" + DEFAULT_LAST_LOGIN_IP
        );
    }

    @Test
    @Transactional
    void getAllDoctorProfilesByLastUserAgentIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDoctorProfile = doctorProfileRepository.saveAndFlush(doctorProfile);

        // Get all the doctorProfileList where lastUserAgent equals to
        defaultDoctorProfileFiltering("lastUserAgent.equals=" + DEFAULT_LAST_USER_AGENT, "lastUserAgent.equals=" + UPDATED_LAST_USER_AGENT);
    }

    @Test
    @Transactional
    void getAllDoctorProfilesByLastUserAgentIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDoctorProfile = doctorProfileRepository.saveAndFlush(doctorProfile);

        // Get all the doctorProfileList where lastUserAgent in
        defaultDoctorProfileFiltering(
            "lastUserAgent.in=" + DEFAULT_LAST_USER_AGENT + "," + UPDATED_LAST_USER_AGENT,
            "lastUserAgent.in=" + UPDATED_LAST_USER_AGENT
        );
    }

    @Test
    @Transactional
    void getAllDoctorProfilesByLastUserAgentIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDoctorProfile = doctorProfileRepository.saveAndFlush(doctorProfile);

        // Get all the doctorProfileList where lastUserAgent is not null
        defaultDoctorProfileFiltering("lastUserAgent.specified=true", "lastUserAgent.specified=false");
    }

    @Test
    @Transactional
    void getAllDoctorProfilesByLastUserAgentContainsSomething() throws Exception {
        // Initialize the database
        insertedDoctorProfile = doctorProfileRepository.saveAndFlush(doctorProfile);

        // Get all the doctorProfileList where lastUserAgent contains
        defaultDoctorProfileFiltering(
            "lastUserAgent.contains=" + DEFAULT_LAST_USER_AGENT,
            "lastUserAgent.contains=" + UPDATED_LAST_USER_AGENT
        );
    }

    @Test
    @Transactional
    void getAllDoctorProfilesByLastUserAgentNotContainsSomething() throws Exception {
        // Initialize the database
        insertedDoctorProfile = doctorProfileRepository.saveAndFlush(doctorProfile);

        // Get all the doctorProfileList where lastUserAgent does not contain
        defaultDoctorProfileFiltering(
            "lastUserAgent.doesNotContain=" + UPDATED_LAST_USER_AGENT,
            "lastUserAgent.doesNotContain=" + DEFAULT_LAST_USER_AGENT
        );
    }

    @Test
    @Transactional
    void getAllDoctorProfilesByLastLoginDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDoctorProfile = doctorProfileRepository.saveAndFlush(doctorProfile);

        // Get all the doctorProfileList where lastLoginDate equals to
        defaultDoctorProfileFiltering("lastLoginDate.equals=" + DEFAULT_LAST_LOGIN_DATE, "lastLoginDate.equals=" + UPDATED_LAST_LOGIN_DATE);
    }

    @Test
    @Transactional
    void getAllDoctorProfilesByLastLoginDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDoctorProfile = doctorProfileRepository.saveAndFlush(doctorProfile);

        // Get all the doctorProfileList where lastLoginDate in
        defaultDoctorProfileFiltering(
            "lastLoginDate.in=" + DEFAULT_LAST_LOGIN_DATE + "," + UPDATED_LAST_LOGIN_DATE,
            "lastLoginDate.in=" + UPDATED_LAST_LOGIN_DATE
        );
    }

    @Test
    @Transactional
    void getAllDoctorProfilesByLastLoginDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDoctorProfile = doctorProfileRepository.saveAndFlush(doctorProfile);

        // Get all the doctorProfileList where lastLoginDate is not null
        defaultDoctorProfileFiltering("lastLoginDate.specified=true", "lastLoginDate.specified=false");
    }

    @Test
    @Transactional
    void getAllDoctorProfilesByInternalUserIsEqualToSomething() throws Exception {
        // Get already existing entity
        User internalUser = doctorProfile.getInternalUser();
        doctorProfileRepository.saveAndFlush(doctorProfile);
        String internalUserId = internalUser.getId();
        // Get all the doctorProfileList where internalUser equals to internalUserId
        defaultDoctorProfileShouldBeFound("internalUserId.equals=" + internalUserId);

        // Get all the doctorProfileList where internalUser equals to "invalid-id"
        defaultDoctorProfileShouldNotBeFound("internalUserId.equals=" + "invalid-id");
    }

    @Test
    @Transactional
    void getAllDoctorProfilesBySpecializationsIsEqualToSomething() throws Exception {
        Specialization specializations;
        if (TestUtil.findAll(em, Specialization.class).isEmpty()) {
            doctorProfileRepository.saveAndFlush(doctorProfile);
            specializations = SpecializationResourceIT.createEntity();
        } else {
            specializations = TestUtil.findAll(em, Specialization.class).get(0);
        }
        em.persist(specializations);
        em.flush();
        doctorProfile.addSpecializations(specializations);
        doctorProfileRepository.saveAndFlush(doctorProfile);
        Long specializationsId = specializations.getId();
        // Get all the doctorProfileList where specializations equals to specializationsId
        defaultDoctorProfileShouldBeFound("specializationsId.equals=" + specializationsId);

        // Get all the doctorProfileList where specializations equals to (specializationsId + 1)
        defaultDoctorProfileShouldNotBeFound("specializationsId.equals=" + (specializationsId + 1));
    }

    private void defaultDoctorProfileFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultDoctorProfileShouldBeFound(shouldBeFound);
        defaultDoctorProfileShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultDoctorProfileShouldBeFound(String filter) throws Exception {
        restDoctorProfileMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(doctorProfile.getId())))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)))
            .andExpect(jsonPath("$.[*].officeAddress").value(hasItem(DEFAULT_OFFICE_ADDRESS)))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE)))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE)))
            .andExpect(jsonPath("$.[*].inpeCode").value(hasItem(DEFAULT_INPE_CODE)))
            .andExpect(jsonPath("$.[*].isVerified").value(hasItem(DEFAULT_IS_VERIFIED)))
            .andExpect(jsonPath("$.[*].lastLoginIp").value(hasItem(DEFAULT_LAST_LOGIN_IP)))
            .andExpect(jsonPath("$.[*].lastUserAgent").value(hasItem(DEFAULT_LAST_USER_AGENT)))
            .andExpect(jsonPath("$.[*].lastLoginDate").value(hasItem(DEFAULT_LAST_LOGIN_DATE.toString())));

        // Check, that the count call also returns 1
        restDoctorProfileMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultDoctorProfileShouldNotBeFound(String filter) throws Exception {
        restDoctorProfileMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restDoctorProfileMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingDoctorProfile() throws Exception {
        // Get the doctorProfile
        restDoctorProfileMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDoctorProfile() throws Exception {
        // Initialize the database
        insertedDoctorProfile = doctorProfileRepository.saveAndFlush(doctorProfile);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        doctorProfileSearchRepository.save(doctorProfile);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(doctorProfileSearchRepository.findAll());

        // Update the doctorProfile
        DoctorProfile updatedDoctorProfile = doctorProfileRepository.findById(doctorProfile.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedDoctorProfile are not directly saved in db
        em.detach(updatedDoctorProfile);
        updatedDoctorProfile
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .officeAddress(UPDATED_OFFICE_ADDRESS)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE)
            .inpeCode(UPDATED_INPE_CODE)
            .isVerified(UPDATED_IS_VERIFIED)
            .lastLoginIp(UPDATED_LAST_LOGIN_IP)
            .lastUserAgent(UPDATED_LAST_USER_AGENT)
            .lastLoginDate(UPDATED_LAST_LOGIN_DATE);
        DoctorProfileDTO doctorProfileDTO = doctorProfileMapper.toDto(updatedDoctorProfile);

        restDoctorProfileMockMvc
            .perform(
                put(ENTITY_API_URL_ID, doctorProfileDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(doctorProfileDTO))
            )
            .andExpect(status().isOk());

        // Validate the DoctorProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDoctorProfileToMatchAllProperties(updatedDoctorProfile);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(doctorProfileSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<DoctorProfile> doctorProfileSearchList = Streamable.of(doctorProfileSearchRepository.findAll()).toList();
                DoctorProfile testDoctorProfileSearch = doctorProfileSearchList.get(searchDatabaseSizeAfter - 1);

                assertDoctorProfileAllPropertiesEquals(testDoctorProfileSearch, updatedDoctorProfile);
            });
    }

    @Test
    @Transactional
    void putNonExistingDoctorProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(doctorProfileSearchRepository.findAll());
        doctorProfile.setId(UUID.randomUUID().toString());

        // Create the DoctorProfile
        DoctorProfileDTO doctorProfileDTO = doctorProfileMapper.toDto(doctorProfile);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDoctorProfileMockMvc
            .perform(
                put(ENTITY_API_URL_ID, doctorProfileDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(doctorProfileDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DoctorProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(doctorProfileSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchDoctorProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(doctorProfileSearchRepository.findAll());
        doctorProfile.setId(UUID.randomUUID().toString());

        // Create the DoctorProfile
        DoctorProfileDTO doctorProfileDTO = doctorProfileMapper.toDto(doctorProfile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDoctorProfileMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(doctorProfileDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DoctorProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(doctorProfileSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDoctorProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(doctorProfileSearchRepository.findAll());
        doctorProfile.setId(UUID.randomUUID().toString());

        // Create the DoctorProfile
        DoctorProfileDTO doctorProfileDTO = doctorProfileMapper.toDto(doctorProfile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDoctorProfileMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(doctorProfileDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the DoctorProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(doctorProfileSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateDoctorProfileWithPatch() throws Exception {
        // Initialize the database
        insertedDoctorProfile = doctorProfileRepository.saveAndFlush(doctorProfile);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the doctorProfile using partial update
        DoctorProfile partialUpdatedDoctorProfile = new DoctorProfile();
        partialUpdatedDoctorProfile.setId(doctorProfile.getId());

        partialUpdatedDoctorProfile
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .officeAddress(UPDATED_OFFICE_ADDRESS)
            .latitude(UPDATED_LATITUDE)
            .inpeCode(UPDATED_INPE_CODE)
            .lastUserAgent(UPDATED_LAST_USER_AGENT)
            .lastLoginDate(UPDATED_LAST_LOGIN_DATE);

        restDoctorProfileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDoctorProfile.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDoctorProfile))
            )
            .andExpect(status().isOk());

        // Validate the DoctorProfile in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDoctorProfileUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedDoctorProfile, doctorProfile),
            getPersistedDoctorProfile(doctorProfile)
        );
    }

    @Test
    @Transactional
    void fullUpdateDoctorProfileWithPatch() throws Exception {
        // Initialize the database
        insertedDoctorProfile = doctorProfileRepository.saveAndFlush(doctorProfile);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the doctorProfile using partial update
        DoctorProfile partialUpdatedDoctorProfile = new DoctorProfile();
        partialUpdatedDoctorProfile.setId(doctorProfile.getId());

        partialUpdatedDoctorProfile
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .officeAddress(UPDATED_OFFICE_ADDRESS)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE)
            .inpeCode(UPDATED_INPE_CODE)
            .isVerified(UPDATED_IS_VERIFIED)
            .lastLoginIp(UPDATED_LAST_LOGIN_IP)
            .lastUserAgent(UPDATED_LAST_USER_AGENT)
            .lastLoginDate(UPDATED_LAST_LOGIN_DATE);

        restDoctorProfileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDoctorProfile.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDoctorProfile))
            )
            .andExpect(status().isOk());

        // Validate the DoctorProfile in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDoctorProfileUpdatableFieldsEquals(partialUpdatedDoctorProfile, getPersistedDoctorProfile(partialUpdatedDoctorProfile));
    }

    @Test
    @Transactional
    void patchNonExistingDoctorProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(doctorProfileSearchRepository.findAll());
        doctorProfile.setId(UUID.randomUUID().toString());

        // Create the DoctorProfile
        DoctorProfileDTO doctorProfileDTO = doctorProfileMapper.toDto(doctorProfile);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDoctorProfileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, doctorProfileDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(doctorProfileDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DoctorProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(doctorProfileSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDoctorProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(doctorProfileSearchRepository.findAll());
        doctorProfile.setId(UUID.randomUUID().toString());

        // Create the DoctorProfile
        DoctorProfileDTO doctorProfileDTO = doctorProfileMapper.toDto(doctorProfile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDoctorProfileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(doctorProfileDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DoctorProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(doctorProfileSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDoctorProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(doctorProfileSearchRepository.findAll());
        doctorProfile.setId(UUID.randomUUID().toString());

        // Create the DoctorProfile
        DoctorProfileDTO doctorProfileDTO = doctorProfileMapper.toDto(doctorProfile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDoctorProfileMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(doctorProfileDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the DoctorProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(doctorProfileSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteDoctorProfile() throws Exception {
        // Initialize the database
        insertedDoctorProfile = doctorProfileRepository.saveAndFlush(doctorProfile);
        doctorProfileRepository.save(doctorProfile);
        doctorProfileSearchRepository.save(doctorProfile);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(doctorProfileSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the doctorProfile
        restDoctorProfileMockMvc
            .perform(delete(ENTITY_API_URL_ID, doctorProfile.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(doctorProfileSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchDoctorProfile() throws Exception {
        // Initialize the database
        insertedDoctorProfile = doctorProfileRepository.saveAndFlush(doctorProfile);
        doctorProfileSearchRepository.save(doctorProfile);

        // Search the doctorProfile
        restDoctorProfileMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + doctorProfile.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(doctorProfile.getId())))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)))
            .andExpect(jsonPath("$.[*].officeAddress").value(hasItem(DEFAULT_OFFICE_ADDRESS)))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE)))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE)))
            .andExpect(jsonPath("$.[*].inpeCode").value(hasItem(DEFAULT_INPE_CODE)))
            .andExpect(jsonPath("$.[*].isVerified").value(hasItem(DEFAULT_IS_VERIFIED)))
            .andExpect(jsonPath("$.[*].lastLoginIp").value(hasItem(DEFAULT_LAST_LOGIN_IP)))
            .andExpect(jsonPath("$.[*].lastUserAgent").value(hasItem(DEFAULT_LAST_USER_AGENT)))
            .andExpect(jsonPath("$.[*].lastLoginDate").value(hasItem(DEFAULT_LAST_LOGIN_DATE.toString())));
    }

    protected long getRepositoryCount() {
        return doctorProfileRepository.count();
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

    protected DoctorProfile getPersistedDoctorProfile(DoctorProfile doctorProfile) {
        return doctorProfileRepository.findById(doctorProfile.getId()).orElseThrow();
    }

    protected void assertPersistedDoctorProfileToMatchAllProperties(DoctorProfile expectedDoctorProfile) {
        assertDoctorProfileAllPropertiesEquals(expectedDoctorProfile, getPersistedDoctorProfile(expectedDoctorProfile));
    }

    protected void assertPersistedDoctorProfileToMatchUpdatableProperties(DoctorProfile expectedDoctorProfile) {
        assertDoctorProfileAllUpdatablePropertiesEquals(expectedDoctorProfile, getPersistedDoctorProfile(expectedDoctorProfile));
    }
}
