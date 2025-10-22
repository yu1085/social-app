package com.socialmeet.backend.repository;

import com.socialmeet.backend.entity.IntimacyLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IntimacyLevelRepository extends JpaRepository<IntimacyLevel, Long> {

    /**
     * 根据等级查询
     */
    Optional<IntimacyLevel> findByLevel(Integer level);

    /**
     * 查询所有等级配置，按等级升序排列
     */
    List<IntimacyLevel> findAllByOrderByLevelAsc();

    /**
     * 根据所需温度查找对应等级
     * 查询温度值小于等于给定温度的最高等级
     */
    IntimacyLevel findTopByRequiredTemperatureLessThanEqualOrderByRequiredTemperatureDesc(Integer temperature);
}
