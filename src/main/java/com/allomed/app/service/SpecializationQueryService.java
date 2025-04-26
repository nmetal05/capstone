package com.allomed.app.service;

import com.allomed.app.domain.*; // for static metamodels
import com.allomed.app.domain.Specialization;
import com.allomed.app.repository.SpecializationRepository;
import com.allomed.app.repository.search.SpecializationSearchRepository;
import com.allomed.app.service.criteria.SpecializationCriteria;
import com.allomed.app.service.dto.SpecializationDTO;
import com.allomed.app.service.mapper.SpecializationMapper;
import jakarta.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Specialization} entities in the database.
 * The main input is a {@link SpecializationCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link SpecializationDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SpecializationQueryService extends QueryService<Specialization> {

    private static final Logger LOG = LoggerFactory.getLogger(SpecializationQueryService.class);

    private final SpecializationRepository specializationRepository;

    private final SpecializationMapper specializationMapper;

    private final SpecializationSearchRepository specializationSearchRepository;

    public SpecializationQueryService(
        SpecializationRepository specializationRepository,
        SpecializationMapper specializationMapper,
        SpecializationSearchRepository specializationSearchRepository
    ) {
        this.specializationRepository = specializationRepository;
        this.specializationMapper = specializationMapper;
        this.specializationSearchRepository = specializationSearchRepository;
    }

    /**
     * Return a {@link Page} of {@link SpecializationDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<SpecializationDTO> findByCriteria(SpecializationCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Specialization> specification = createSpecification(criteria);
        return specializationRepository.findAll(specification, page).map(specializationMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SpecializationCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Specialization> specification = createSpecification(criteria);
        return specializationRepository.count(specification);
    }

    /**
     * Function to convert {@link SpecializationCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Specialization> createSpecification(SpecializationCriteria criteria) {
        Specification<Specialization> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), Specialization_.id),
                buildStringSpecification(criteria.getName(), Specialization_.name),
                buildStringSpecification(criteria.getDescription(), Specialization_.description),
                buildSpecification(criteria.getDoctorProfilesId(), root ->
                    root.join(Specialization_.doctorProfiles, JoinType.LEFT).get(DoctorProfile_.id)
                )
            );
        }
        return specification;
    }
}
