package com.allomed.app.repository;

import com.allomed.app.domain.SymptomSearch;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SymptomSearch entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SymptomSearchRepository extends JpaRepository<SymptomSearch, Long> {}
