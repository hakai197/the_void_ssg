package com.thevoid.ssg.repository;

import com.thevoid.ssg.model.entity.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SiteRepository extends JpaRepository<Site, String> {

    Optional<Site> findByName(String name);

    boolean existsByName(String name);

    @Query("SELECT s FROM Site s LEFT JOIN FETCH s.entries WHERE s.id = :id")
    Optional<Site> findByIdWithEntries(@Param("id") String id);
}
