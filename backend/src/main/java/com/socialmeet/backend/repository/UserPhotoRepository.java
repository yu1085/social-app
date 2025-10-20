package com.socialmeet.backend.repository;

import com.socialmeet.backend.entity.UserPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户照片Repository
 */
@Repository
public interface UserPhotoRepository extends JpaRepository<UserPhoto, Long> {

    /**
     * 根据用户ID查询所有照片
     */
    List<UserPhoto> findByUserIdOrderByUploadTimeDesc(Long userId);

    /**
     * 根据用户ID和照片ID查询照片
     */
    Optional<UserPhoto> findByIdAndUserId(Long id, Long userId);

    /**
     * 查询用户的头像照片
     */
    Optional<UserPhoto> findByUserIdAndIsAvatarTrue(Long userId);

    /**
     * 统计用户照片数量
     */
    long countByUserId(Long userId);
}
