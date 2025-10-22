package com.socialmeet.backend.repository;

import com.socialmeet.backend.entity.IntimacyReward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IntimacyRewardRepository extends JpaRepository<IntimacyReward, Long> {

    /**
     * 查询用户的所有未领取奖励
     */
    List<IntimacyReward> findByUserIdAndIsClaimedFalse(Long userId);

    /**
     * 查询用户在指定等级的奖励
     */
    Optional<IntimacyReward> findByUserIdAndTargetUserIdAndLevel(Long userId, Long targetUserId, Integer level);

    /**
     * 查询用户与目标用户的所有奖励记录
     */
    List<IntimacyReward> findByUserIdAndTargetUserIdOrderByLevelAsc(Long userId, Long targetUserId);

    /**
     * 检查是否已存在该等级的奖励记录
     */
    boolean existsByUserIdAndTargetUserIdAndLevel(Long userId, Long targetUserId, Integer level);
}
