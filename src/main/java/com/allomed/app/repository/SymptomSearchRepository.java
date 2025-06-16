package com.allomed.app.repository;

import com.allomed.app.domain.SymptomSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SymptomSearch entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SymptomSearchRepository extends JpaRepository<SymptomSearch, Long> {
    /**
     * Find all symptom searches by user ID ordered by search date descending
     */
    @Query("SELECT s FROM SymptomSearch s WHERE s.user.id = :userId ORDER BY s.searchDate DESC")
    Page<SymptomSearch> findByUserIdOrderBySearchDateDesc(@Param("userId") String userId, Pageable pageable);

    /**
     * Find all symptom searches by user ID with eager loading of relationships
     */
    @Query(
        "SELECT s FROM SymptomSearch s LEFT JOIN FETCH s.user LEFT JOIN FETCH s.guestSession WHERE s.user.id = :userId ORDER BY s.searchDate DESC"
    )
    Page<SymptomSearch> findByUserIdWithEagerRelationships(@Param("userId") String userId, Pageable pageable);

    /**
     * Find all symptom searches by guest session ID ordered by search date descending
     */
    @Query("SELECT s FROM SymptomSearch s WHERE s.guestSession.id = :guestSessionId ORDER BY s.searchDate DESC")
    Page<SymptomSearch> findByGuestSessionIdOrderBySearchDateDesc(@Param("guestSessionId") Long guestSessionId, Pageable pageable);
}
