package com.socialmeet.backend.repository;

import com.socialmeet.backend.entity.UserSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户设置Repository
 */
@Repository
public interface UserSettingsRepository extends JpaRepository<UserSettings, Long> {

    /**
     * 根据用户ID查找设置
     */
    Optional<UserSettings> findByUserId(Long userId);

    /**
     * 根据用户ID删除设置
     */
    void deleteByUserId(Long userId);

    /**
     * 根据用户ID列表批量查找设置
     */
    List<UserSettings> findByUserIdIn(List<Long> userIds);
}
