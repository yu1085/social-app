package com.example.socialmeet.repository;

import com.example.socialmeet.entity.IdCardVerify;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface IdCardVerifyRepository extends JpaRepository<IdCardVerify, Long> {
    
    /**
     * 根据认证ID查找
     */
    Optional<IdCardVerify> findByVerifyId(String verifyId);
    
    /**
     * 根据用户ID查找认证记录
     */
    List<IdCardVerify> findByUserId(Long userId);
    
    /**
     * 根据用户ID和状态查找
     */
    List<IdCardVerify> findByUserIdAndStatus(Long userId, String status);
    
    /**
     * 根据支付宝认证单据号查找
     */
    Optional<IdCardVerify> findByCertifyId(String certifyId);
    
    /**
     * 查找过期的认证记录
     */
    @Query("SELECT v FROM IdCardVerify v WHERE v.status = 'PENDING' AND v.expiresAt < :now")
    List<IdCardVerify> findExpiredVerifies(@Param("now") LocalDateTime now);
    
    /**
     * 根据用户ID按创建时间倒序查找
     */
    List<IdCardVerify> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    /**
     * 统计用户的认证次数
     */
    @Query("SELECT COUNT(v) FROM IdCardVerify v WHERE v.userId = :userId")
    long countByUserId(@Param("userId") Long userId);
}
