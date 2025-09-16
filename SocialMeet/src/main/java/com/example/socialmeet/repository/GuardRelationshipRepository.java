package com.example.socialmeet.repository;

import com.example.socialmeet.entity.GuardRelationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface GuardRelationshipRepository extends JpaRepository<GuardRelationship, Long> {
    List<GuardRelationship> findByGuardianIdOrderByCreatedAtDesc(Long guardianId);
    List<GuardRelationship> findByProtectedIdOrderByCreatedAtDesc(Long protectedId);
    
    @Query("SELECT gr FROM GuardRelationship gr WHERE gr.guardianId = :userId OR gr.protectedId = :userId ORDER BY gr.createdAt DESC")
    List<GuardRelationship> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);
    
    @Query("SELECT gr FROM GuardRelationship gr WHERE gr.guardianId = :guardianId AND gr.protectedId = :protectedId")
    Optional<GuardRelationship> findByGuardianIdAndProtectedId(@Param("guardianId") Long guardianId, @Param("protectedId") Long protectedId);
    
    @Query("SELECT gr FROM GuardRelationship gr WHERE gr.status = 'ACTIVE' AND (gr.endDate IS NULL OR gr.endDate > :now) ORDER BY gr.totalContribution DESC")
    List<GuardRelationship> findActiveGuardiansOrderByContribution(@Param("now") LocalDateTime now);
    
    @Query("SELECT gr FROM GuardRelationship gr WHERE gr.protectedId = :protectedId AND gr.status = 'ACTIVE' AND (gr.endDate IS NULL OR gr.endDate > :now) ORDER BY gr.totalContribution DESC")
    List<GuardRelationship> findActiveGuardiansByProtectedId(@Param("protectedId") Long protectedId, @Param("now") LocalDateTime now);
}
