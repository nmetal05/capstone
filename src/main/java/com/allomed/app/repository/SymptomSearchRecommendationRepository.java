package com.allomed.app.repository;

import com.allomed.app.domain.SymptomSearchRecommendation;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SymptomSearchRecommendation entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SymptomSearchRecommendationRepository extends JpaRepository<SymptomSearchRecommendation, Long> {}
