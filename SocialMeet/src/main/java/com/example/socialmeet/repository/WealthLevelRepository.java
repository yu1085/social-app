package com.example.socialmeet.repository;

import com.example.socialmeet.entity.WealthLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface WealthLevelRepository extends JpaRepository<WealthLevel, Long> {
    List<WealthLevel> findByIsActiveTrueOrderByLevelAsc();
    
    @Query("SELECT wl FROM WealthLevel wl WHERE wl.isActive = true AND wl.minContribution <= :contribution AND (wl.maxContribution IS NULL OR wl.maxContribution > :contribution) ORDER BY wl.level DESC")
    Optional<WealthLevel> findMatchingWealthLevel(@Param("contribution") BigDecimal contribution);
    
    @Query("SELECT wl FROM WealthLevel wl WHERE wl.isActive = true AND wl.level > :level ORDER BY wl.level ASC")
    List<WealthLevel> findHigherWealthLevels(@Param("level") Integer level);
}
