package com.example.socialmeet.repository;

import com.example.socialmeet.entity.InviteCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InviteCodeRepository extends JpaRepository<InviteCode, Long> {
    Optional<InviteCode> findByUserId(Long userId);
    Optional<InviteCode> findByInviteCode(String inviteCode);
    List<InviteCode> findByUserIdAndIsActiveTrue(Long userId);
}
