package com.allomed.app.service.impl;

import com.allomed.app.domain.GuestSession;
import com.allomed.app.repository.GuestSessionRepository;
import com.allomed.app.repository.search.GuestSessionSearchRepository;
import com.allomed.app.service.GuestSessionService;
import com.allomed.app.service.dto.GuestSessionDTO;
import com.allomed.app.service.mapper.GuestSessionMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.allomed.app.domain.GuestSession}.
 */
@Service
@Transactional
public class GuestSessionServiceImpl implements GuestSessionService {

    private static final Logger LOG = LoggerFactory.getLogger(GuestSessionServiceImpl.class);

    private final GuestSessionRepository guestSessionRepository;

    private final GuestSessionMapper guestSessionMapper;

    private final GuestSessionSearchRepository guestSessionSearchRepository;

    public GuestSessionServiceImpl(
        GuestSessionRepository guestSessionRepository,
        GuestSessionMapper guestSessionMapper,
        GuestSessionSearchRepository guestSessionSearchRepository
    ) {
        this.guestSessionRepository = guestSessionRepository;
        this.guestSessionMapper = guestSessionMapper;
        this.guestSessionSearchRepository = guestSessionSearchRepository;
    }

    @Override
    public GuestSessionDTO save(GuestSessionDTO guestSessionDTO) {
        LOG.debug("Request to save GuestSession : {}", guestSessionDTO);
        GuestSession guestSession = guestSessionMapper.toEntity(guestSessionDTO);
        guestSession = guestSessionRepository.save(guestSession);
        guestSessionSearchRepository.index(guestSession);
        return guestSessionMapper.toDto(guestSession);
    }

    @Override
    public GuestSessionDTO update(GuestSessionDTO guestSessionDTO) {
        LOG.debug("Request to update GuestSession : {}", guestSessionDTO);
        GuestSession guestSession = guestSessionMapper.toEntity(guestSessionDTO);
        guestSession = guestSessionRepository.save(guestSession);
        guestSessionSearchRepository.index(guestSession);
        return guestSessionMapper.toDto(guestSession);
    }

    @Override
    public Optional<GuestSessionDTO> partialUpdate(GuestSessionDTO guestSessionDTO) {
        LOG.debug("Request to partially update GuestSession : {}", guestSessionDTO);

        return guestSessionRepository
            .findById(guestSessionDTO.getId())
            .map(existingGuestSession -> {
                guestSessionMapper.partialUpdate(existingGuestSession, guestSessionDTO);

                return existingGuestSession;
            })
            .map(guestSessionRepository::save)
            .map(savedGuestSession -> {
                guestSessionSearchRepository.index(savedGuestSession);
                return savedGuestSession;
            })
            .map(guestSessionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GuestSessionDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all GuestSessions");
        return guestSessionRepository.findAll(pageable).map(guestSessionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<GuestSessionDTO> findOne(Long id) {
        LOG.debug("Request to get GuestSession : {}", id);
        return guestSessionRepository.findById(id).map(guestSessionMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete GuestSession : {}", id);
        guestSessionRepository.deleteById(id);
        guestSessionSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GuestSessionDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of GuestSessions for query {}", query);
        return guestSessionSearchRepository.search(query, pageable).map(guestSessionMapper::toDto);
    }
}
