package com.example.socialmeet.repository;

import com.example.socialmeet.entity.VipSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VipSubscriptionRepository extends JpaRepository<VipSubscription, Long> {
    List<VipSubscription> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    @Query("SELECT vs FROM VipSubscription vs WHERE vs.userId = :userId AND vs.status = 'ACTIVE' AND vs.endDate > :now")
    Optional<VipSubscription> findActiveSubscriptionByUserId(@Param("userId") Long userId, @Param("now") LocalDateTime now);
    
    @Query("SELECT vs FROM VipSubscription vs WHERE vs.status = 'ACTIVE' AND vs.endDate > :now")
    List<VipSubscription> findAllActiveSubscriptions(@Param("now") LocalDateTime now);
    
    @Query("SELECT vs FROM VipSubscription vs WHERE vs.status = 'ACTIVE' AND vs.endDate <= :now")
    List<VipSubscription> findExpiredSubscriptions(@Param("now") LocalDateTime now);
}
