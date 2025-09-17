package com.example.socialmeet.repository;

import com.example.socialmeet.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByReceiverIdOrderByCreatedAtDesc(Long receiverId);
    List<Message> findBySenderIdAndReceiverIdOrReceiverIdAndSenderIdOrderByCreatedAtAsc(Long senderId1, Long receiverId1, Long senderId2, Long receiverId2);
    List<Message> findByReceiverIdAndIsReadFalse(Long receiverId);
}
