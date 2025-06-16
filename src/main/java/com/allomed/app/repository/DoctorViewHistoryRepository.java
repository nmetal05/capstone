package com.allomed.app.repository;

import com.allomed.app.domain.DoctorViewHistory;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the DoctorViewHistory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DoctorViewHistoryRepository extends JpaRepository<DoctorViewHistory, Long> {
    /**
     * Find all doctor view history by user ID ordered by view date descending
     */
    @Query("SELECT d FROM DoctorViewHistory d WHERE d.user.id = :userId ORDER BY d.viewDate DESC")
    Page<DoctorViewHistory> findByUserIdOrderByViewDateDesc(@Param("userId") String userId, Pageable pageable);

    /**
     * Find all doctor view history by user ID with eager loading of relationships
     * Note: Removed specializations fetch to avoid pagination issues with collection fetch joins
     */
    @Query(
        "SELECT d FROM DoctorViewHistory d " +
        "LEFT JOIN FETCH d.user " +
        "LEFT JOIN FETCH d.doctor dp " +
        "LEFT JOIN FETCH dp.internalUser " +
        "WHERE d.user.id = :userId ORDER BY d.viewDate DESC"
    )
    Page<DoctorViewHistory> findByUserIdWithEagerRelationships(@Param("userId") String userId, Pageable pageable);

    /**
     * Find doctor view history by IDs with specializations (for post-processing)
     */
    @Query(
        "SELECT DISTINCT d FROM DoctorViewHistory d " +
        "LEFT JOIN FETCH d.user " +
        "LEFT JOIN FETCH d.doctor dp " +
        "LEFT JOIN FETCH dp.internalUser " +
        "LEFT JOIN FETCH dp.specializations " +
        "WHERE d.id IN :ids ORDER BY d.viewDate DESC"
    )
    List<DoctorViewHistory> findByIdsWithSpecializations(@Param("ids") List<Long> ids);

    /**
     * Find all doctor view history by doctor ID ordered by view date descending
     */
    @Query("SELECT d FROM DoctorViewHistory d WHERE d.doctor.id = :doctorId ORDER BY d.viewDate DESC")
    Page<DoctorViewHistory> findByDoctorIdOrderByViewDateDesc(@Param("doctorId") String doctorId, Pageable pageable);
}
