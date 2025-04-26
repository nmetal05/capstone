package com.allomed.app.repository;

import com.allomed.app.domain.GuestSession;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the GuestSession entity.
 */
@SuppressWarnings("unused")
@Repository
public interface GuestSessionRepository extends JpaRepository<GuestSession, Long> {}
