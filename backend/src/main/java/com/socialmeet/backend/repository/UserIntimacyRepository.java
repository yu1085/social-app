package com.socialmeet.backend.repository;

import com.socialmeet.backend.entity.UserIntimacy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserIntimacyRepository extends JpaRepository<UserIntimacy, Long> {

    /**
     * 查询两个用户之间的亲密度记录
     */
    Optional<UserIntimacy> findByUserIdAndTargetUserId(Long userId, Long targetUserId);

    /**
     * 查询用户的所有亲密度记录，按温度降序
     */
    List<UserIntimacy> findByUserIdOrderByCurrentTemperatureDesc(Long userId);

    /**
     * 分页查询用户的亲密度记录，按温度降序
     */
    Page<UserIntimacy> findByUserIdOrderByCurrentTemperatureDesc(Long userId, Pageable pageable);

    /**
     * 查询用户指定等级的亲密度记录
     */
    List<UserIntimacy> findByUserIdAndCurrentLevel(Long userId, Integer level);

    /**
     * 查询用户温度大于指定值的亲密度记录数量
     */
    Long countByUserIdAndCurrentTemperatureGreaterThanEqual(Long userId, Integer temperature);

    /**
     * 查询用户的亲密度排行榜（前N名）
     */
    @Query("SELECT ui FROM UserIntimacy ui WHERE ui.userId = :userId ORDER BY ui.currentTemperature DESC")
    List<UserIntimacy> findTopIntimacyUsers(@Param("userId") Long userId, Pageable pageable);
}
