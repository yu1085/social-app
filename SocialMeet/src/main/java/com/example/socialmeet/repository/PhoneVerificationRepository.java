package com.example.socialmeet.repository;

import com.example.socialmeet.entity.PhoneVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhoneVerificationRepository extends JpaRepository<PhoneVerification, Long> {
    
    /**
     * 根据手机号和验证码查找验证记录
     */
    List<PhoneVerification> findByPhoneNumberAndVerificationCodeAndStatus(
            String phoneNumber, String verificationCode, String status);
    
    /**
     * 根据手机号和验证码查找验证记录（不限制状态）
     */
    List<PhoneVerification> findByPhoneNumberAndVerificationCode(
            String phoneNumber, String verificationCode);
    
    /**
     * 根据手机号和状态查找验证记录
     */
    List<PhoneVerification> findByPhoneNumberAndStatus(String phoneNumber, String status);
    
    /**
     * 根据手机号删除所有验证记录
     */
    @Modifying
    @Query("DELETE FROM PhoneVerification p WHERE p.phoneNumber = :phoneNumber")
    void deleteByPhoneNumber(String phoneNumber);
}
