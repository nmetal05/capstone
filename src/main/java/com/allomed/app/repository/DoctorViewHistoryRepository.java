package com.allomed.app.repository;

import com.allomed.app.domain.DoctorViewHistory;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the DoctorViewHistory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DoctorViewHistoryRepository extends JpaRepository<DoctorViewHistory, Long> {}
