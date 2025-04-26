package com.allomed.app.service.impl;

import com.allomed.app.domain.DoctorDocument;
import com.allomed.app.repository.DoctorDocumentRepository;
import com.allomed.app.repository.search.DoctorDocumentSearchRepository;
import com.allomed.app.service.DoctorDocumentService;
import com.allomed.app.service.dto.DoctorDocumentDTO;
import com.allomed.app.service.mapper.DoctorDocumentMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.allomed.app.domain.DoctorDocument}.
 */
@Service
@Transactional
public class DoctorDocumentServiceImpl implements DoctorDocumentService {

    private static final Logger LOG = LoggerFactory.getLogger(DoctorDocumentServiceImpl.class);

    private final DoctorDocumentRepository doctorDocumentRepository;

    private final DoctorDocumentMapper doctorDocumentMapper;

    private final DoctorDocumentSearchRepository doctorDocumentSearchRepository;

    public DoctorDocumentServiceImpl(
        DoctorDocumentRepository doctorDocumentRepository,
        DoctorDocumentMapper doctorDocumentMapper,
        DoctorDocumentSearchRepository doctorDocumentSearchRepository
    ) {
        this.doctorDocumentRepository = doctorDocumentRepository;
        this.doctorDocumentMapper = doctorDocumentMapper;
        this.doctorDocumentSearchRepository = doctorDocumentSearchRepository;
    }

    @Override
    public DoctorDocumentDTO save(DoctorDocumentDTO doctorDocumentDTO) {
        LOG.debug("Request to save DoctorDocument : {}", doctorDocumentDTO);
        DoctorDocument doctorDocument = doctorDocumentMapper.toEntity(doctorDocumentDTO);
        doctorDocument = doctorDocumentRepository.save(doctorDocument);
        doctorDocumentSearchRepository.index(doctorDocument);
        return doctorDocumentMapper.toDto(doctorDocument);
    }

    @Override
    public DoctorDocumentDTO update(DoctorDocumentDTO doctorDocumentDTO) {
        LOG.debug("Request to update DoctorDocument : {}", doctorDocumentDTO);
        DoctorDocument doctorDocument = doctorDocumentMapper.toEntity(doctorDocumentDTO);
        doctorDocument = doctorDocumentRepository.save(doctorDocument);
        doctorDocumentSearchRepository.index(doctorDocument);
        return doctorDocumentMapper.toDto(doctorDocument);
    }

    @Override
    public Optional<DoctorDocumentDTO> partialUpdate(DoctorDocumentDTO doctorDocumentDTO) {
        LOG.debug("Request to partially update DoctorDocument : {}", doctorDocumentDTO);

        return doctorDocumentRepository
            .findById(doctorDocumentDTO.getId())
            .map(existingDoctorDocument -> {
                doctorDocumentMapper.partialUpdate(existingDoctorDocument, doctorDocumentDTO);

                return existingDoctorDocument;
            })
            .map(doctorDocumentRepository::save)
            .map(savedDoctorDocument -> {
                doctorDocumentSearchRepository.index(savedDoctorDocument);
                return savedDoctorDocument;
            })
            .map(doctorDocumentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DoctorDocumentDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all DoctorDocuments");
        return doctorDocumentRepository.findAll(pageable).map(doctorDocumentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DoctorDocumentDTO> findOne(Long id) {
        LOG.debug("Request to get DoctorDocument : {}", id);
        return doctorDocumentRepository.findById(id).map(doctorDocumentMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete DoctorDocument : {}", id);
        doctorDocumentRepository.deleteById(id);
        doctorDocumentSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DoctorDocumentDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of DoctorDocuments for query {}", query);
        return doctorDocumentSearchRepository.search(query, pageable).map(doctorDocumentMapper::toDto);
    }
}
