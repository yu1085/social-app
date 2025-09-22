package com.example.socialmeet.repository;

import com.example.socialmeet.entity.CallRecordEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 通话记录数据访问层
 * 
 * @author SocialMeet Team
 * @version 1.0
 * @since 2024-01-01
 */
@Repository
public interface CallRecordRepository extends JpaRepository<CallRecordEntity, Long> {
    
    /**
     * 根据用户ID查询通话记录
     */
    @Query("SELECT c FROM CallRecordEntity c WHERE " +
           "(c.callerId = :userId OR c.receiverId = :userId) " +
           "AND (c.callerId = :userId AND c.isDeletedCaller = false OR " +
           "c.receiverId = :userId AND c.isDeletedReceiver = false) " +
           "ORDER BY c.startTime DESC")
    Page<CallRecordEntity> findByUserId(@Param("userId") Long userId, Pageable pageable);
    
    /**
     * 查询两个用户之间的通话记录
     */
    @Query("SELECT c FROM CallRecordEntity c WHERE " +
           "((c.callerId = :userId1 AND c.receiverId = :userId2) OR " +
           "(c.callerId = :userId2 AND c.receiverId = :userId1)) " +
           "AND (c.callerId = :userId1 AND c.isDeletedCaller = false OR " +
           "c.receiverId = :userId1 AND c.isDeletedReceiver = false) " +
           "ORDER BY c.startTime DESC")
    Page<CallRecordEntity> findCallRecordsBetweenUsers(@Param("userId1") Long userId1, 
                                                      @Param("userId2") Long userId2, 
                                                      Pageable pageable);
    
    /**
     * 根据通话类型查询
     */
    @Query("SELECT c FROM CallRecordEntity c WHERE " +
           "(c.callerId = :userId OR c.receiverId = :userId) " +
           "AND c.callType = :callType " +
           "AND (c.callerId = :userId AND c.isDeletedCaller = false OR " +
           "c.receiverId = :userId AND c.isDeletedReceiver = false) " +
           "ORDER BY c.startTime DESC")
    Page<CallRecordEntity> findByCallType(@Param("userId") Long userId, 
                                         @Param("callType") CallRecordEntity.CallType callType, 
                                         Pageable pageable);
    
    /**
     * 查询未接来电
     */
    @Query("SELECT c FROM CallRecordEntity c WHERE " +
           "c.receiverId = :userId AND c.isMissed = true " +
           "AND c.isDeletedReceiver = false " +
           "ORDER BY c.startTime DESC")
    List<CallRecordEntity> findMissedCallsByUserId(@Param("userId") Long userId);
    
    /**
     * 查询已接来电
     */
    @Query("SELECT c FROM CallRecordEntity c WHERE " +
           "(c.callerId = :userId OR c.receiverId = :userId) " +
           "AND c.isAnswered = true " +
           "AND (c.callerId = :userId AND c.isDeletedCaller = false OR " +
           "c.receiverId = :userId AND c.isDeletedReceiver = false) " +
           "ORDER BY c.startTime DESC")
    Page<CallRecordEntity> findAnsweredCallsByUserId(@Param("userId") Long userId, Pageable pageable);
    
    /**
     * 查询指定时间范围内的通话记录
     */
    @Query("SELECT c FROM CallRecordEntity c WHERE " +
           "(c.callerId = :userId OR c.receiverId = :userId) " +
           "AND c.startTime BETWEEN :startTime AND :endTime " +
           "AND (c.callerId = :userId AND c.isDeletedCaller = false OR " +
           "c.receiverId = :userId AND c.isDeletedReceiver = false) " +
           "ORDER BY c.startTime DESC")
    List<CallRecordEntity> findCallRecordsByTimeRange(@Param("userId") Long userId,
                                                      @Param("startTime") LocalDateTime startTime,
                                                      @Param("endTime") LocalDateTime endTime);
    
    /**
     * 查询通话统计信息
     */
    @Query("SELECT " +
           "COUNT(c) as totalCalls, " +
           "COUNT(CASE WHEN c.isAnswered = true THEN 1 END) as answeredCalls, " +
           "COUNT(CASE WHEN c.isMissed = true THEN 1 END) as missedCalls, " +
           "COUNT(CASE WHEN c.callType = 'VOICE' THEN 1 END) as voiceCalls, " +
           "COUNT(CASE WHEN c.callType = 'VIDEO' THEN 1 END) as videoCalls, " +
           "SUM(c.duration) as totalDuration, " +
           "SUM(c.totalCost) as totalCost " +
           "FROM CallRecordEntity c WHERE " +
           "(c.callerId = :userId OR c.receiverId = :userId) " +
           "AND (c.callerId = :userId AND c.isDeletedCaller = false OR " +
           "c.receiverId = :userId AND c.isDeletedReceiver = false)")
    Object[] getCallStatistics(@Param("userId") Long userId);
    
    /**
     * 查询最近的通话记录
     */
    @Query("SELECT c FROM CallRecordEntity c WHERE " +
           "(c.callerId = :userId OR c.receiverId = :userId) " +
           "AND (c.callerId = :userId AND c.isDeletedCaller = false OR " +
           "c.receiverId = :userId AND c.isDeletedReceiver = false) " +
           "ORDER BY c.startTime DESC")
    List<CallRecordEntity> findRecentCallRecords(@Param("userId") Long userId, Pageable pageable);
    
    /**
     * 查询通话时长最长的记录
     */
    @Query("SELECT c FROM CallRecordEntity c WHERE " +
           "(c.callerId = :userId OR c.receiverId = :userId) " +
           "AND c.isAnswered = true " +
           "AND (c.callerId = :userId AND c.isDeletedCaller = false OR " +
           "c.receiverId = :userId AND c.isDeletedReceiver = false) " +
           "ORDER BY c.duration DESC")
    List<CallRecordEntity> findLongestCallRecords(@Param("userId") Long userId, Pageable pageable);
    
    /**
     * 查询通话费用最高的记录
     */
    @Query("SELECT c FROM CallRecordEntity c WHERE " +
           "(c.callerId = :userId OR c.receiverId = :userId) " +
           "AND c.totalCost > 0 " +
           "AND (c.callerId = :userId AND c.isDeletedCaller = false OR " +
           "c.receiverId = :userId AND c.isDeletedReceiver = false) " +
           "ORDER BY c.totalCost DESC")
    List<CallRecordEntity> findMostExpensiveCallRecords(@Param("userId") Long userId, Pageable pageable);
    
    /**
     * 根据通话质量查询
     */
    @Query("SELECT c FROM CallRecordEntity c WHERE " +
           "(c.callerId = :userId OR c.receiverId = :userId) " +
           "AND c.qualityScore >= :minQuality " +
           "AND (c.callerId = :userId AND c.isDeletedCaller = false OR " +
           "c.receiverId = :userId AND c.isDeletedReceiver = false) " +
           "ORDER BY c.qualityScore DESC")
    List<CallRecordEntity> findByQualityScore(@Param("userId") Long userId, 
                                             @Param("minQuality") Double minQuality, 
                                             Pageable pageable);
}
