package com.example.socialmeet.repository;

import com.example.socialmeet.entity.UserCertification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserCertificationRepository extends JpaRepository<UserCertification, Long> {
    Optional<UserCertification> findByUserId(Long userId);
    List<UserCertification> findByUserIdAndCertificationType(Long userId, String type);
    List<UserCertification> findByStatus(String status);
}
