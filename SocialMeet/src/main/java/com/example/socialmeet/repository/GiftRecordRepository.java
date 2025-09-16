package com.example.socialmeet.repository;

import com.example.socialmeet.entity.GiftRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GiftRecordRepository extends JpaRepository<GiftRecord, Long> {
    Page<GiftRecord> findBySenderIdOrderByCreatedAtDesc(Long senderId, Pageable pageable);
    Page<GiftRecord> findByReceiverIdOrderByCreatedAtDesc(Long receiverId, Pageable pageable);
    
    @Query("SELECT gr FROM GiftRecord gr WHERE gr.senderId = :userId OR gr.receiverId = :userId ORDER BY gr.createdAt DESC")
    Page<GiftRecord> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId, Pageable pageable);
    
    @Query("SELECT SUM(gr.totalAmount) FROM GiftRecord gr WHERE gr.senderId = :userId")
    Double sumTotalAmountBySenderId(@Param("userId") Long userId);
    
    @Query("SELECT SUM(gr.totalAmount) FROM GiftRecord gr WHERE gr.receiverId = :userId")
    Double sumTotalAmountByReceiverId(@Param("userId") Long userId);
    
    @Query("SELECT gr FROM GiftRecord gr WHERE gr.createdAt BETWEEN :startDate AND :endDate ORDER BY gr.createdAt DESC")
    List<GiftRecord> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
