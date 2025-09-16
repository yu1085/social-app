package com.example.socialmeet.repository;

import com.example.socialmeet.entity.VipLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VipLevelRepository extends JpaRepository<VipLevel, Long> {
    List<VipLevel> findByIsActiveTrueOrderByLevelAsc();
    Optional<VipLevel> findByLevel(Integer level);
}
