package com.thevoid.ssg.repository;

import com.thevoid.ssg.model.entity.Entry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EntryRepository extends JpaRepository<Entry, String> {

    List<Entry> findBySiteId(String siteId);

    Optional<Entry> findBySiteIdAndSlug(String siteId, String slug);

    @Query("SELECT e FROM Entry e WHERE e.site.id = :siteId AND e.corruptionLevel >= :threshold")
    List<Entry> findCorruptedEntries(@Param("siteId") String siteId, @Param("threshold") int threshold);

    @Modifying
    @Query("UPDATE Entry e SET e.viewCount = e.viewCount + 1 WHERE e.id = :id")
    void incrementViewCount(@Param("id") String id);

    @Query("SELECT e FROM Entry e WHERE e.entityInfluence IS NOT NULL")
    List<Entry> findEntityInfluencedEntries();

    @Query("SELECT COUNT(e) FROM Entry e WHERE e.site.id = :siteId")
    int countBySiteId(@Param("siteId") String siteId);

    Page<Entry> findBySiteId(String siteId, Pageable pageable);
}