package com.example.socialmeet.repository;

import com.example.socialmeet.entity.InviteRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InviteRecordRepository extends JpaRepository<InviteRecord, Long> {
    List<InviteRecord> findByInviterId(Long inviterId);
    List<InviteRecord> findByInviteeId(Long inviteeId);
    List<InviteRecord> findByInviteCode(String inviteCode);
    boolean existsByInviteeId(Long inviteeId);
}
