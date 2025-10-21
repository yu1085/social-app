package com.socialmeet.backend.repository;

import com.socialmeet.backend.entity.UserRelationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRelationshipRepository extends JpaRepository<UserRelationship, Long> {

    /**
     * 查找用户的某种关系列表
     */
    @Query("SELECT r FROM UserRelationship r WHERE r.userId = :userId " +
           "AND r.relationshipType = :type ORDER BY r.updatedAt DESC")
    List<UserRelationship> findByUserIdAndType(@Param("userId") Long userId,
                                               @Param("type") UserRelationship.RelationshipType type);

    /**
     * 查找用户的所有关系
     */
    List<UserRelationship> findByUserId(Long userId);

    /**
     * 查找特定关系
     */
    Optional<UserRelationship> findByUserIdAndTargetUserIdAndRelationshipType(
            Long userId, Long targetUserId, UserRelationship.RelationshipType relationshipType);

    /**
     * 查找谁加我为好友
     */
    @Query("SELECT r FROM UserRelationship r WHERE r.targetUserId = :userId " +
           "AND r.relationshipType = 'FRIEND' ORDER BY r.createdAt DESC")
    List<UserRelationship> findWhoAddedMeAsFriend(@Param("userId") Long userId);

    /**
     * 查找谁喜欢我
     */
    @Query("SELECT r FROM UserRelationship r WHERE r.targetUserId = :userId " +
           "AND r.relationshipType = 'LIKE' ORDER BY r.createdAt DESC")
    List<UserRelationship> findWhoLikesMe(@Param("userId") Long userId);
}
