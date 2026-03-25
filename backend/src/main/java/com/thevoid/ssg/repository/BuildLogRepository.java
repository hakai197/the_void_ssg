package com.thevoid.ssg.repository;

import com.thevoid.ssg.model.entity.BuildLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BuildLogRepository extends JpaRepository<BuildLog, String> {

    List<BuildLog> findBySiteIdOrderByTimestampDesc(String siteId);

    @Query("SELECT AVG(b.buildDurationMs) FROM BuildLog b WHERE b.site.id = :siteId")
    Long getAverageBuildDuration(@Param("siteId") String siteId);

    @Query("SELECT b FROM BuildLog b WHERE b.site.id = :siteId AND b.timestamp >= :since")
    List<BuildLog> findRecentBuilds(@Param("siteId") String siteId, @Param("since") LocalDateTime since);

    long countBySiteIdAndBuildSuccessful(String siteId, boolean successful);
}