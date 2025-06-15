package com.allomed.app.repository;

import com.allomed.app.domain.DoctorDocument;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the DoctorDocument entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DoctorDocumentRepository extends JpaRepository<DoctorDocument, Long> {
    List<DoctorDocument> findByDoctorId(String doctorId);

    @Query(
        "select doctorDocument from DoctorDocument doctorDocument left join fetch doctorDocument.doctor doctor left join fetch doctor.internalUser user left join fetch user.authorities left join fetch doctor.specializations where doctorDocument.id =:id"
    )
    Optional<DoctorDocument> findOneWithEagerRelationships(@Param("id") Long id);
}
