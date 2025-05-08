package com.allomed.app.repository;

import com.allomed.app.domain.AppUserProfile;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the AppUserProfile entity.
 */
@Repository
public interface AppUserProfileRepository extends JpaRepository<AppUserProfile, String> {
    default Optional<AppUserProfile> findOneWithEagerRelationships(String id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<AppUserProfile> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<AppUserProfile> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select appUserProfile from AppUserProfile appUserProfile left join fetch appUserProfile.internalUser",
        countQuery = "select count(appUserProfile) from AppUserProfile appUserProfile"
    )
    Page<AppUserProfile> findAllWithToOneRelationships(Pageable pageable);

    @Query("select appUserProfile from AppUserProfile appUserProfile left join fetch appUserProfile.internalUser")
    List<AppUserProfile> findAllWithToOneRelationships();

    @Query(
        "select appUserProfile from AppUserProfile appUserProfile left join fetch appUserProfile.internalUser where appUserProfile.id =:id"
    )
    Optional<AppUserProfile> findOneWithToOneRelationships(@Param("id") String id);

    Optional<AppUserProfile> findOneByInternalUser_Id(String userId);

    boolean existsByInternalUser_Id(String userId);
}
