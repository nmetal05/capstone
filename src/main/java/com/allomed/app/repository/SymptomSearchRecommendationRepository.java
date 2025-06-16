package com.allomed.app.repository;

import com.allomed.app.domain.SymptomSearchRecommendation;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SymptomSearchRecommendation entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SymptomSearchRecommendationRepository extends JpaRepository<SymptomSearchRecommendation, Long> {
    /**
     * Find all recommendations for a specific symptom search with eager loading
     */
    @Query(
        "SELECT r FROM SymptomSearchRecommendation r " +
        "LEFT JOIN FETCH r.search " +
        "LEFT JOIN FETCH r.specialization " +
        "WHERE r.search.id = :searchId " +
        "ORDER BY r.rank ASC"
    )
    List<SymptomSearchRecommendation> findBySearchIdWithEagerRelationships(@Param("searchId") Long searchId);
}
