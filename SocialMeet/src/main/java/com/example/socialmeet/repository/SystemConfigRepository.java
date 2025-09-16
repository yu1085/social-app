package com.example.socialmeet.repository;

import com.example.socialmeet.entity.SystemConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SystemConfigRepository extends JpaRepository<SystemConfig, Long> {
    List<SystemConfig> findByIsActiveTrue();
    Optional<SystemConfig> findByConfigKey(String configKey);
    Optional<SystemConfig> findByConfigKeyAndIsActiveTrue(String configKey);
}
