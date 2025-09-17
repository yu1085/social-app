package com.example.socialmeet.repository;

import com.example.socialmeet.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByIsActiveTrueOrderByCreatedAtDesc(Pageable pageable);
    List<Post> findByIsActiveTrueOrderByCreatedAtDesc();
    List<Post> findByUserIdAndIsActiveTrueOrderByCreatedAtDesc(Long userId);
}