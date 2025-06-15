package com.allomed.app.repository;

import com.allomed.app.domain.DoctorAvailability;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the DoctorAvailability entity.
 */
@Repository
public interface DoctorAvailabilityRepository
    extends JpaRepository<DoctorAvailability, Long>, JpaSpecificationExecutor<DoctorAvailability> {
    List<DoctorAvailability> findByDoctorId(String doctorId);
}
