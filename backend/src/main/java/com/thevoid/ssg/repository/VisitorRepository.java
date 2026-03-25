package com.thevoid.ssg.repository;

import com.thevoid.ssg.model.entity.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface VisitorRepository extends JpaRepository<Visitor, String> {

    Optional<Visitor> findBySiteIdAndUserHash(String siteId, String userHash);

    @Modifying
    @Query("UPDATE Visitor v SET v.visitCount = v.visitCount + 1, v.lastSeen = :now WHERE v.id = :id")
    void updateVisitCount(@Param("id") String id, @Param("now") LocalDateTime now);

    long countBySiteId(String siteId);
}