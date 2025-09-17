package com.example.socialmeet.repository;

import com.example.socialmeet.entity.IntimacyRelationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface IntimacyRelationshipRepository extends JpaRepository<IntimacyRelationship, Long> {
    @Query("SELECT ir FROM IntimacyRelationship ir WHERE ir.user1Id = :userId OR ir.user2Id = :userId ORDER BY ir.intimacyScore DESC")
    List<IntimacyRelationship> findByUserIdOrderByIntimacyScoreDesc(@Param("userId") Long userId);
    
    @Query("SELECT ir FROM IntimacyRelationship ir WHERE (ir.user1Id = :user1Id AND ir.user2Id = :user2Id) OR (ir.user1Id = :user2Id AND ir.user2Id = :user1Id)")
    Optional<IntimacyRelationship> findByUser1IdAndUser2Id(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);
    
    @Query("SELECT ir FROM IntimacyRelationship ir WHERE ir.level = :level ORDER BY ir.intimacyScore DESC")
    List<IntimacyRelationship> findByLevelOrderByIntimacyScoreDesc(@Param("level") String level);
}
