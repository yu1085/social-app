package com.example.socialmeet.repository;

import com.example.socialmeet.entity.ThirdPartyAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ThirdPartyAuthRepository extends JpaRepository<ThirdPartyAuth, Long> {
    
    /**
     * 根据认证ID查找
     */
    Optional<ThirdPartyAuth> findByAuthId(String authId);
    
    /**
     * 根据用户ID查找认证记录
     */
    List<ThirdPartyAuth> findByUserId(Long userId);
    
    /**
     * 根据用户ID和认证类型查找
     */
    List<ThirdPartyAuth> findByUserIdAndAuthType(Long userId, String authType);
    
    /**
     * 根据用户ID和状态查找
     */
    List<ThirdPartyAuth> findByUserIdAndStatus(Long userId, String status);
    
    /**
     * 根据第三方ID查找
     */
    Optional<ThirdPartyAuth> findByThirdPartyId(String thirdPartyId);
    
    /**
     * 查找过期的认证记录
     */
    @Query("SELECT t FROM ThirdPartyAuth t WHERE t.status = 'PENDING' AND t.expiresAt < :now")
    List<ThirdPartyAuth> findExpiredAuths(@Param("now") LocalDateTime now);
    
    /**
     * 根据用户ID查找最新的认证记录
     */
    @Query("SELECT t FROM ThirdPartyAuth t WHERE t.userId = :userId ORDER BY t.createdAt DESC")
    List<ThirdPartyAuth> findLatestByUserId(@Param("userId") Long userId);
    
    /**
     * 根据用户ID按创建时间倒序查找
     */
    List<ThirdPartyAuth> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    /**
     * 统计用户的认证次数
     */
    @Query("SELECT COUNT(t) FROM ThirdPartyAuth t WHERE t.userId = :userId AND t.authType = :authType")
    long countByUserIdAndAuthType(@Param("userId") Long userId, @Param("authType") String authType);
}
