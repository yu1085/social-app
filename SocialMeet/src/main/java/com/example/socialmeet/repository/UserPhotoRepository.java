package com.example.socialmeet.repository;

import com.example.socialmeet.entity.UserPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserPhotoRepository extends JpaRepository<UserPhoto, Long> {
    
    /**
     * 根据用户ID查找照片，按创建时间倒序
     */
    List<UserPhoto> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    /**
     * 根据用户ID和头像状态查找照片
     */
    List<UserPhoto> findByUserIdAndIsAvatarTrue(Long userId);
    
    /**
     * 根据用户ID删除所有照片
     */
    void deleteByUserId(Long userId);
}
