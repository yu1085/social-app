package com.example.socialmeet.repository;

import com.example.socialmeet.entity.UserLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserLikeRepository extends JpaRepository<UserLike, Long> {
    List<UserLike> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<UserLike> findByLikedUserIdOrderByCreatedAtDesc(Long likedUserId);
    
    @Query("SELECT ul FROM UserLike ul WHERE ul.userId = :userId AND ul.likedUserId = :likedUserId")
    Optional<UserLike> findByUserIdAndLikedUserId(@Param("userId") Long userId, @Param("likedUserId") Long likedUserId);
    
    @Query("SELECT COUNT(ul) FROM UserLike ul WHERE ul.userId = :userId")
    Long countByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(ul) FROM UserLike ul WHERE ul.likedUserId = :likedUserId")
    Long countByLikedUserId(@Param("likedUserId") Long likedUserId);
    
    boolean existsByUserIdAndLikedUserId(Long userId, Long likedUserId);
}
