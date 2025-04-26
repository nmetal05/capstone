package com.allomed.app.web.rest;

import static com.allomed.app.domain.DoctorDocumentAsserts.*;
import static com.allomed.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.allomed.app.IntegrationTest;
import com.allomed.app.domain.DoctorDocument;
import com.allomed.app.domain.DoctorProfile;
import com.allomed.app.domain.enumeration.DocumentType;
import com.allomed.app.domain.enumeration.VerificationStatus;
import com.allomed.app.repository.DoctorDocumentRepository;
import com.allomed.app.repository.search.DoctorDocumentSearchRepository;
import com.allomed.app.service.dto.DoctorDocumentDTO;
import com.allomed.app.service.mapper.DoctorDocumentMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
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
 * Integration tests for the {@link DoctorDocumentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class DoctorDocumentResourceIT {

    private static final DocumentType DEFAULT_TYPE = DocumentType.DIPLOMA;
    private static final DocumentType UPDATED_TYPE = DocumentType.CV;

    private static final String DEFAULT_FILE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FILE_NAME = "BBBBBBBBBB";

    private static final byte[] DEFAULT_FILE_CONTENT = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_FILE_CONTENT = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_FILE_CONTENT_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_FILE_CONTENT_CONTENT_TYPE = "image/png";

    private static final Instant DEFAULT_UPLOAD_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPLOAD_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final VerificationStatus DEFAULT_VERIFICATION_STATUS = VerificationStatus.PENDING;
    private static final VerificationStatus UPDATED_VERIFICATION_STATUS = VerificationStatus.VERIFIED;

    private static final String ENTITY_API_URL = "/api/doctor-documents";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/doctor-documents/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DoctorDocumentRepository doctorDocumentRepository;

    @Autowired
    private DoctorDocumentMapper doctorDocumentMapper;

    @Autowired
    private DoctorDocumentSearchRepository doctorDocumentSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDoctorDocumentMockMvc;

    private DoctorDocument doctorDocument;

    private DoctorDocument insertedDoctorDocument;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DoctorDocument createEntity(EntityManager em) {
        DoctorDocument doctorDocument = new DoctorDocument()
            .type(DEFAULT_TYPE)
            .fileName(DEFAULT_FILE_NAME)
            .fileContent(DEFAULT_FILE_CONTENT)
            .fileContentContentType(DEFAULT_FILE_CONTENT_CONTENT_TYPE)
            .uploadDate(DEFAULT_UPLOAD_DATE)
            .verificationStatus(DEFAULT_VERIFICATION_STATUS);
        // Add required entity
        DoctorProfile doctorProfile;
        if (TestUtil.findAll(em, DoctorProfile.class).isEmpty()) {
            doctorProfile = DoctorProfileResourceIT.createEntity(em);
            em.persist(doctorProfile);
            em.flush();
        } else {
            doctorProfile = TestUtil.findAll(em, DoctorProfile.class).get(0);
        }
        doctorDocument.setDoctor(doctorProfile);
        return doctorDocument;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DoctorDocument createUpdatedEntity(EntityManager em) {
        DoctorDocument updatedDoctorDocument = new DoctorDocument()
            .type(UPDATED_TYPE)
            .fileName(UPDATED_FILE_NAME)
            .fileContent(UPDATED_FILE_CONTENT)
            .fileContentContentType(UPDATED_FILE_CONTENT_CONTENT_TYPE)
            .uploadDate(UPDATED_UPLOAD_DATE)
            .verificationStatus(UPDATED_VERIFICATION_STATUS);
        // Add required entity
        DoctorProfile doctorProfile;
        if (TestUtil.findAll(em, DoctorProfile.class).isEmpty()) {
            doctorProfile = DoctorProfileResourceIT.createUpdatedEntity(em);
            em.persist(doctorProfile);
            em.flush();
        } else {
            doctorProfile = TestUtil.findAll(em, DoctorProfile.class).get(0);
        }
        updatedDoctorDocument.setDoctor(doctorProfile);
        return updatedDoctorDocument;
    }

    @BeforeEach
    void initTest() {
        doctorDocument = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedDoctorDocument != null) {
            doctorDocumentRepository.delete(insertedDoctorDocument);
            doctorDocumentSearchRepository.delete(insertedDoctorDocument);
            insertedDoctorDocument = null;
        }
    }

    @Test
    @Transactional
    void createDoctorDocument() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(doctorDocumentSearchRepository.findAll());
        // Create the DoctorDocument
        DoctorDocumentDTO doctorDocumentDTO = doctorDocumentMapper.toDto(doctorDocument);
        var returnedDoctorDocumentDTO = om.readValue(
            restDoctorDocumentMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(doctorDocumentDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            DoctorDocumentDTO.class
        );

        // Validate the DoctorDocument in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedDoctorDocument = doctorDocumentMapper.toEntity(returnedDoctorDocumentDTO);
        assertDoctorDocumentUpdatableFieldsEquals(returnedDoctorDocument, getPersistedDoctorDocument(returnedDoctorDocument));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(doctorDocumentSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedDoctorDocument = returnedDoctorDocument;
    }

    @Test
    @Transactional
    void createDoctorDocumentWithExistingId() throws Exception {
        // Create the DoctorDocument with an existing ID
        doctorDocument.setId(1L);
        DoctorDocumentDTO doctorDocumentDTO = doctorDocumentMapper.toDto(doctorDocument);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(doctorDocumentSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restDoctorDocumentMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(doctorDocumentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DoctorDocument in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(doctorDocumentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(doctorDocumentSearchRepository.findAll());
        // set the field null
        doctorDocument.setType(null);

        // Create the DoctorDocument, which fails.
        DoctorDocumentDTO doctorDocumentDTO = doctorDocumentMapper.toDto(doctorDocument);

        restDoctorDocumentMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(doctorDocumentDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(doctorDocumentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkFileNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(doctorDocumentSearchRepository.findAll());
        // set the field null
        doctorDocument.setFileName(null);

        // Create the DoctorDocument, which fails.
        DoctorDocumentDTO doctorDocumentDTO = doctorDocumentMapper.toDto(doctorDocument);

        restDoctorDocumentMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(doctorDocumentDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(doctorDocumentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkUploadDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(doctorDocumentSearchRepository.findAll());
        // set the field null
        doctorDocument.setUploadDate(null);

        // Create the DoctorDocument, which fails.
        DoctorDocumentDTO doctorDocumentDTO = doctorDocumentMapper.toDto(doctorDocument);

        restDoctorDocumentMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(doctorDocumentDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(doctorDocumentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkVerificationStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(doctorDocumentSearchRepository.findAll());
        // set the field null
        doctorDocument.setVerificationStatus(null);

        // Create the DoctorDocument, which fails.
        DoctorDocumentDTO doctorDocumentDTO = doctorDocumentMapper.toDto(doctorDocument);

        restDoctorDocumentMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(doctorDocumentDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(doctorDocumentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllDoctorDocuments() throws Exception {
        // Initialize the database
        insertedDoctorDocument = doctorDocumentRepository.saveAndFlush(doctorDocument);

        // Get all the doctorDocumentList
        restDoctorDocumentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(doctorDocument.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].fileName").value(hasItem(DEFAULT_FILE_NAME)))
            .andExpect(jsonPath("$.[*].fileContentContentType").value(hasItem(DEFAULT_FILE_CONTENT_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].fileContent").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_FILE_CONTENT))))
            .andExpect(jsonPath("$.[*].uploadDate").value(hasItem(DEFAULT_UPLOAD_DATE.toString())))
            .andExpect(jsonPath("$.[*].verificationStatus").value(hasItem(DEFAULT_VERIFICATION_STATUS.toString())));
    }

    @Test
    @Transactional
    void getDoctorDocument() throws Exception {
        // Initialize the database
        insertedDoctorDocument = doctorDocumentRepository.saveAndFlush(doctorDocument);

        // Get the doctorDocument
        restDoctorDocumentMockMvc
            .perform(get(ENTITY_API_URL_ID, doctorDocument.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(doctorDocument.getId().intValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.fileName").value(DEFAULT_FILE_NAME))
            .andExpect(jsonPath("$.fileContentContentType").value(DEFAULT_FILE_CONTENT_CONTENT_TYPE))
            .andExpect(jsonPath("$.fileContent").value(Base64.getEncoder().encodeToString(DEFAULT_FILE_CONTENT)))
            .andExpect(jsonPath("$.uploadDate").value(DEFAULT_UPLOAD_DATE.toString()))
            .andExpect(jsonPath("$.verificationStatus").value(DEFAULT_VERIFICATION_STATUS.toString()));
    }

    @Test
    @Transactional
    void getNonExistingDoctorDocument() throws Exception {
        // Get the doctorDocument
        restDoctorDocumentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDoctorDocument() throws Exception {
        // Initialize the database
        insertedDoctorDocument = doctorDocumentRepository.saveAndFlush(doctorDocument);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        doctorDocumentSearchRepository.save(doctorDocument);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(doctorDocumentSearchRepository.findAll());

        // Update the doctorDocument
        DoctorDocument updatedDoctorDocument = doctorDocumentRepository.findById(doctorDocument.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedDoctorDocument are not directly saved in db
        em.detach(updatedDoctorDocument);
        updatedDoctorDocument
            .type(UPDATED_TYPE)
            .fileName(UPDATED_FILE_NAME)
            .fileContent(UPDATED_FILE_CONTENT)
            .fileContentContentType(UPDATED_FILE_CONTENT_CONTENT_TYPE)
            .uploadDate(UPDATED_UPLOAD_DATE)
            .verificationStatus(UPDATED_VERIFICATION_STATUS);
        DoctorDocumentDTO doctorDocumentDTO = doctorDocumentMapper.toDto(updatedDoctorDocument);

        restDoctorDocumentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, doctorDocumentDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(doctorDocumentDTO))
            )
            .andExpect(status().isOk());

        // Validate the DoctorDocument in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDoctorDocumentToMatchAllProperties(updatedDoctorDocument);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(doctorDocumentSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<DoctorDocument> doctorDocumentSearchList = Streamable.of(doctorDocumentSearchRepository.findAll()).toList();
                DoctorDocument testDoctorDocumentSearch = doctorDocumentSearchList.get(searchDatabaseSizeAfter - 1);

                assertDoctorDocumentAllPropertiesEquals(testDoctorDocumentSearch, updatedDoctorDocument);
            });
    }

    @Test
    @Transactional
    void putNonExistingDoctorDocument() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(doctorDocumentSearchRepository.findAll());
        doctorDocument.setId(longCount.incrementAndGet());

        // Create the DoctorDocument
        DoctorDocumentDTO doctorDocumentDTO = doctorDocumentMapper.toDto(doctorDocument);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDoctorDocumentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, doctorDocumentDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(doctorDocumentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DoctorDocument in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(doctorDocumentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchDoctorDocument() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(doctorDocumentSearchRepository.findAll());
        doctorDocument.setId(longCount.incrementAndGet());

        // Create the DoctorDocument
        DoctorDocumentDTO doctorDocumentDTO = doctorDocumentMapper.toDto(doctorDocument);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDoctorDocumentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(doctorDocumentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DoctorDocument in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(doctorDocumentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDoctorDocument() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(doctorDocumentSearchRepository.findAll());
        doctorDocument.setId(longCount.incrementAndGet());

        // Create the DoctorDocument
        DoctorDocumentDTO doctorDocumentDTO = doctorDocumentMapper.toDto(doctorDocument);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDoctorDocumentMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(doctorDocumentDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the DoctorDocument in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(doctorDocumentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateDoctorDocumentWithPatch() throws Exception {
        // Initialize the database
        insertedDoctorDocument = doctorDocumentRepository.saveAndFlush(doctorDocument);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the doctorDocument using partial update
        DoctorDocument partialUpdatedDoctorDocument = new DoctorDocument();
        partialUpdatedDoctorDocument.setId(doctorDocument.getId());

        partialUpdatedDoctorDocument
            .type(UPDATED_TYPE)
            .fileName(UPDATED_FILE_NAME)
            .fileContent(UPDATED_FILE_CONTENT)
            .fileContentContentType(UPDATED_FILE_CONTENT_CONTENT_TYPE)
            .verificationStatus(UPDATED_VERIFICATION_STATUS);

        restDoctorDocumentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDoctorDocument.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDoctorDocument))
            )
            .andExpect(status().isOk());

        // Validate the DoctorDocument in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDoctorDocumentUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedDoctorDocument, doctorDocument),
            getPersistedDoctorDocument(doctorDocument)
        );
    }

    @Test
    @Transactional
    void fullUpdateDoctorDocumentWithPatch() throws Exception {
        // Initialize the database
        insertedDoctorDocument = doctorDocumentRepository.saveAndFlush(doctorDocument);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the doctorDocument using partial update
        DoctorDocument partialUpdatedDoctorDocument = new DoctorDocument();
        partialUpdatedDoctorDocument.setId(doctorDocument.getId());

        partialUpdatedDoctorDocument
            .type(UPDATED_TYPE)
            .fileName(UPDATED_FILE_NAME)
            .fileContent(UPDATED_FILE_CONTENT)
            .fileContentContentType(UPDATED_FILE_CONTENT_CONTENT_TYPE)
            .uploadDate(UPDATED_UPLOAD_DATE)
            .verificationStatus(UPDATED_VERIFICATION_STATUS);

        restDoctorDocumentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDoctorDocument.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDoctorDocument))
            )
            .andExpect(status().isOk());

        // Validate the DoctorDocument in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDoctorDocumentUpdatableFieldsEquals(partialUpdatedDoctorDocument, getPersistedDoctorDocument(partialUpdatedDoctorDocument));
    }

    @Test
    @Transactional
    void patchNonExistingDoctorDocument() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(doctorDocumentSearchRepository.findAll());
        doctorDocument.setId(longCount.incrementAndGet());

        // Create the DoctorDocument
        DoctorDocumentDTO doctorDocumentDTO = doctorDocumentMapper.toDto(doctorDocument);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDoctorDocumentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, doctorDocumentDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(doctorDocumentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DoctorDocument in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(doctorDocumentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDoctorDocument() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(doctorDocumentSearchRepository.findAll());
        doctorDocument.setId(longCount.incrementAndGet());

        // Create the DoctorDocument
        DoctorDocumentDTO doctorDocumentDTO = doctorDocumentMapper.toDto(doctorDocument);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDoctorDocumentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(doctorDocumentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DoctorDocument in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(doctorDocumentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDoctorDocument() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(doctorDocumentSearchRepository.findAll());
        doctorDocument.setId(longCount.incrementAndGet());

        // Create the DoctorDocument
        DoctorDocumentDTO doctorDocumentDTO = doctorDocumentMapper.toDto(doctorDocument);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDoctorDocumentMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(doctorDocumentDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the DoctorDocument in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(doctorDocumentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteDoctorDocument() throws Exception {
        // Initialize the database
        insertedDoctorDocument = doctorDocumentRepository.saveAndFlush(doctorDocument);
        doctorDocumentRepository.save(doctorDocument);
        doctorDocumentSearchRepository.save(doctorDocument);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(doctorDocumentSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the doctorDocument
        restDoctorDocumentMockMvc
            .perform(delete(ENTITY_API_URL_ID, doctorDocument.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(doctorDocumentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchDoctorDocument() throws Exception {
        // Initialize the database
        insertedDoctorDocument = doctorDocumentRepository.saveAndFlush(doctorDocument);
        doctorDocumentSearchRepository.save(doctorDocument);

        // Search the doctorDocument
        restDoctorDocumentMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + doctorDocument.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(doctorDocument.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].fileName").value(hasItem(DEFAULT_FILE_NAME)))
            .andExpect(jsonPath("$.[*].fileContentContentType").value(hasItem(DEFAULT_FILE_CONTENT_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].fileContent").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_FILE_CONTENT))))
            .andExpect(jsonPath("$.[*].uploadDate").value(hasItem(DEFAULT_UPLOAD_DATE.toString())))
            .andExpect(jsonPath("$.[*].verificationStatus").value(hasItem(DEFAULT_VERIFICATION_STATUS.toString())));
    }

    protected long getRepositoryCount() {
        return doctorDocumentRepository.count();
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

    protected DoctorDocument getPersistedDoctorDocument(DoctorDocument doctorDocument) {
        return doctorDocumentRepository.findById(doctorDocument.getId()).orElseThrow();
    }

    protected void assertPersistedDoctorDocumentToMatchAllProperties(DoctorDocument expectedDoctorDocument) {
        assertDoctorDocumentAllPropertiesEquals(expectedDoctorDocument, getPersistedDoctorDocument(expectedDoctorDocument));
    }

    protected void assertPersistedDoctorDocumentToMatchUpdatableProperties(DoctorDocument expectedDoctorDocument) {
        assertDoctorDocumentAllUpdatablePropertiesEquals(expectedDoctorDocument, getPersistedDoctorDocument(expectedDoctorDocument));
    }
}
