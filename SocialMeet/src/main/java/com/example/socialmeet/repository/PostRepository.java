package com.example.socialmeet.repository;

import com.example.socialmeet.entity.Post;
import com.example.socialmeet.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    
    Page<Post> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
    
    Page<Post> findByIsPublicTrueOrderByCreatedAtDesc(Pageable pageable);
    
    @Query("SELECT p FROM Post p WHERE p.user = :user AND p.isPublic = true ORDER BY p.createdAt DESC")
    Page<Post> findPublicPostsByUser(@Param("user") User user, Pageable pageable);
    
    @Query("SELECT p FROM Post p WHERE p.content LIKE %:keyword% AND p.isPublic = true ORDER BY p.createdAt DESC")
    Page<Post> findByContentContaining(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT p FROM Post p WHERE p.location LIKE %:location% AND p.isPublic = true ORDER BY p.createdAt DESC")
    Page<Post> findByLocationContaining(@Param("location") String location, Pageable pageable);
    
    List<Post> findByUserOrderByCreatedAtDesc(User user);
}
