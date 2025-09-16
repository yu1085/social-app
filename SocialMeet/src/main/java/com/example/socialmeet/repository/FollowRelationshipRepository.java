package com.example.socialmeet.repository;

import com.example.socialmeet.entity.FollowRelationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRelationshipRepository extends JpaRepository<FollowRelationship, Long> {
    List<FollowRelationship> findByFollowerIdOrderByCreatedAtDesc(Long followerId);
    List<FollowRelationship> findByFollowingIdOrderByCreatedAtDesc(Long followingId);
    
    @Query("SELECT fr FROM FollowRelationship fr WHERE fr.followerId = :followerId AND fr.followingId = :followingId")
    Optional<FollowRelationship> findByFollowerIdAndFollowingId(@Param("followerId") Long followerId, @Param("followingId") Long followingId);
    
    @Query("SELECT COUNT(fr) FROM FollowRelationship fr WHERE fr.followerId = :userId")
    Long countByFollowerId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(fr) FROM FollowRelationship fr WHERE fr.followingId = :userId")
    Long countByFollowingId(@Param("userId") Long userId);
    
    boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);
}
