package com.allomed.app.repository;

import com.allomed.app.domain.Specialization;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Specialization entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SpecializationRepository extends JpaRepository<Specialization, Long>, JpaSpecificationExecutor<Specialization> {}
