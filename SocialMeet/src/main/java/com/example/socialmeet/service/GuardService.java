package com.example.socialmeet.service;

import com.example.socialmeet.dto.GuardRelationshipDTO;
import com.example.socialmeet.entity.GuardRelationship;
import com.example.socialmeet.entity.User;
import com.example.socialmeet.repository.GuardRelationshipRepository;
import com.example.socialmeet.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class GuardService {
    
    @Autowired
    private GuardRelationshipRepository guardRelationshipRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private WalletService walletService;
    
    public GuardRelationshipDTO becomeGuardian(Long guardianId, Long protectedId) {
        // 检查是否已经是守护者
        Optional<GuardRelationship> existing = guardRelationshipRepository
                .findByGuardianIdAndProtectedId(guardianId, protectedId);
        
        GuardRelationship relationship;
        
        if (existing.isPresent()) {
            relationship = existing.get();
            if (relationship.isActive()) {
                throw new RuntimeException("已经是该用户的守护者");
            }
            // 重新激活
            relationship.setStatus("ACTIVE");
            relationship.setStartDate(LocalDateTime.now());
            relationship.setEndDate(null);
            relationship = guardRelationshipRepository.save(relationship);
        } else {
            // 创建新的守护关系
            relationship = new GuardRelationship(guardianId, protectedId);
            relationship = guardRelationshipRepository.save(relationship);
        }
        
        return convertToDTO(relationship);
    }
    
    public boolean stopGuarding(Long guardianId, Long protectedId) {
        Optional<GuardRelationship> relationshipOpt = guardRelationshipRepository
                .findByGuardianIdAndProtectedId(guardianId, protectedId);
        
        if (relationshipOpt.isPresent()) {
            GuardRelationship relationship = relationshipOpt.get();
            relationship.setStatus("CANCELLED");
            relationship.setEndDate(LocalDateTime.now());
            guardRelationshipRepository.save(relationship);
            return true;
        }
        
        return false;
    }
    
    public List<GuardRelationshipDTO> getGuardiansByUserId(Long userId) {
        List<GuardRelationship> relationships = guardRelationshipRepository
                .findActiveGuardiansByProtectedId(userId, LocalDateTime.now());
        return relationships.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    public List<GuardRelationshipDTO> getProtectedUsersByGuardianId(Long guardianId) {
        List<GuardRelationship> relationships = guardRelationshipRepository
                .findByGuardianIdOrderByCreatedAtDesc(guardianId);
        return relationships.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    public List<GuardRelationshipDTO> getGuardIncomeRanking() {
        List<GuardRelationship> relationships = guardRelationshipRepository
                .findActiveGuardiansOrderByContribution(LocalDateTime.now());
        return relationships.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    public BigDecimal getGuardianIncome(Long guardianId) {
        List<GuardRelationship> relationships = guardRelationshipRepository
                .findByGuardianIdOrderByCreatedAtDesc(guardianId);
        
        return relationships.stream()
                .map(GuardRelationship::getTotalContribution)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public boolean addContribution(Long guardianId, Long protectedId, BigDecimal amount) {
        Optional<GuardRelationship> relationshipOpt = guardRelationshipRepository
                .findByGuardianIdAndProtectedId(guardianId, protectedId);
        
        if (relationshipOpt.isPresent()) {
            GuardRelationship relationship = relationshipOpt.get();
            if (relationship.isActive()) {
                relationship.addContribution(amount);
                guardRelationshipRepository.save(relationship);
                return true;
            }
        }
        
        return false;
    }
    
    public boolean isGuardian(Long guardianId, Long protectedId) {
        Optional<GuardRelationship> relationshipOpt = guardRelationshipRepository
                .findByGuardianIdAndProtectedId(guardianId, protectedId);
        
        return relationshipOpt.isPresent() && relationshipOpt.get().isActive();
    }
    
    private GuardRelationshipDTO convertToDTO(GuardRelationship relationship) {
        GuardRelationshipDTO dto = new GuardRelationshipDTO(relationship);
        
        // 获取守护者信息
        Optional<User> guardianOpt = userRepository.findById(relationship.getGuardianId());
        if (guardianOpt.isPresent()) {
            User guardian = guardianOpt.get();
            dto.setGuardianNickname(guardian.getNickname());
            dto.setGuardianAvatar(guardian.getAvatarUrl());
        }
        
        // 获取被守护者信息
        Optional<User> protectedOpt = userRepository.findById(relationship.getProtectedId());
        if (protectedOpt.isPresent()) {
            User protectedUser = protectedOpt.get();
            dto.setProtectedNickname(protectedUser.getNickname());
            dto.setProtectedAvatar(protectedUser.getAvatarUrl());
        }
        
        return dto;
    }
}
