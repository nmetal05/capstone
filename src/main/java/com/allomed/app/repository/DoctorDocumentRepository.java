package com.allomed.app.repository;

import com.allomed.app.domain.DoctorDocument;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the DoctorDocument entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DoctorDocumentRepository extends JpaRepository<DoctorDocument, Long> {}
