package com.socialmeet.backend.service;

import com.socialmeet.backend.dto.UserDTO;
import com.socialmeet.backend.entity.User;
import com.socialmeet.backend.entity.UserRelationship;
import com.socialmeet.backend.entity.UserRelationship.RelationshipType;
import com.socialmeet.backend.repository.UserRelationshipRepository;
import com.socialmeet.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 用户关系服务
 * 处理知友、喜欢、亲密关系的业务逻辑
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserRelationshipService {

    private final UserRelationshipRepository relationshipRepository;
    private final UserRepository userRepository;

    /**
     * 添加知友
     */
    @Transactional
    public boolean addFriend(Long userId, Long targetUserId) {
        if (userId.equals(targetUserId)) {
            log.warn("不能添加自己为知友 - userId: {}", userId);
            return false;
        }

        // 检查是否已经是知友
        Optional<UserRelationship> existing = relationshipRepository
                .findByUserIdAndTargetUserIdAndRelationshipType(userId, targetUserId, RelationshipType.FRIEND);

        if (existing.isPresent()) {
            log.info("已经是知友了 - userId: {}, targetUserId: {}", userId, targetUserId);
            return true;
        }

        // 创建知友关系
        UserRelationship relationship = new UserRelationship();
        relationship.setUserId(userId);
        relationship.setTargetUserId(targetUserId);
        relationship.setRelationshipType(RelationshipType.FRIEND);
        relationship.setIntimacyScore(0);

        relationshipRepository.save(relationship);
        log.info("添加知友成功 - userId: {}, targetUserId: {}", userId, targetUserId);

        return true;
    }

    /**
     * 删除知友
     */
    @Transactional
    public boolean removeFriend(Long userId, Long targetUserId) {
        Optional<UserRelationship> relationship = relationshipRepository
                .findByUserIdAndTargetUserIdAndRelationshipType(userId, targetUserId, RelationshipType.FRIEND);

        if (relationship.isPresent()) {
            relationshipRepository.delete(relationship.get());
            log.info("删除知友成功 - userId: {}, targetUserId: {}", userId, targetUserId);
            return true;
        }

        return false;
    }

    /**
     * 添加喜欢
     */
    @Transactional
    public boolean addLike(Long userId, Long targetUserId) {
        if (userId.equals(targetUserId)) {
            log.warn("不能喜欢自己 - userId: {}", userId);
            return false;
        }

        // 检查是否已经喜欢
        Optional<UserRelationship> existing = relationshipRepository
                .findByUserIdAndTargetUserIdAndRelationshipType(userId, targetUserId, RelationshipType.LIKE);

        if (existing.isPresent()) {
            log.info("已经喜欢了 - userId: {}, targetUserId: {}", userId, targetUserId);
            return true;
        }

        // 创建喜欢关系
        UserRelationship relationship = new UserRelationship();
        relationship.setUserId(userId);
        relationship.setTargetUserId(targetUserId);
        relationship.setRelationshipType(RelationshipType.LIKE);
        relationship.setIntimacyScore(0);

        relationshipRepository.save(relationship);
        log.info("添加喜欢成功 - userId: {}, targetUserId: {}", userId, targetUserId);

        return true;
    }

    /**
     * 取消喜欢
     */
    @Transactional
    public boolean removeLike(Long userId, Long targetUserId) {
        Optional<UserRelationship> relationship = relationshipRepository
                .findByUserIdAndTargetUserIdAndRelationshipType(userId, targetUserId, RelationshipType.LIKE);

        if (relationship.isPresent()) {
            relationshipRepository.delete(relationship.get());
            log.info("取消喜欢成功 - userId: {}, targetUserId: {}", userId, targetUserId);
            return true;
        }

        return false;
    }

    /**
     * 检查是否已喜欢
     */
    public boolean isLiked(Long userId, Long targetUserId) {
        return relationshipRepository
                .findByUserIdAndTargetUserIdAndRelationshipType(userId, targetUserId, RelationshipType.LIKE)
                .isPresent();
    }

    /**
     * 检查是否是知友
     */
    public boolean isFriend(Long userId, Long targetUserId) {
        return relationshipRepository
                .findByUserIdAndTargetUserIdAndRelationshipType(userId, targetUserId, RelationshipType.FRIEND)
                .isPresent();
    }

    /**
     * 获取知友列表
     */
    public List<UserDTO> getFriendsList(Long userId) {
        List<UserRelationship> relationships = relationshipRepository
                .findByUserIdAndType(userId, RelationshipType.FRIEND);

        return relationships.stream()
                .map(r -> userRepository.findById(r.getTargetUserId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 获取喜欢列表
     */
    public List<UserDTO> getLikesList(Long userId) {
        List<UserRelationship> relationships = relationshipRepository
                .findByUserIdAndType(userId, RelationshipType.LIKE);

        return relationships.stream()
                .map(r -> userRepository.findById(r.getTargetUserId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 获取亲密列表（根据聊天频率统计）
     * TODO: 实际应该基于聊天记录统计
     */
    public List<UserDTO> getIntimateList(Long userId) {
        List<UserRelationship> relationships = relationshipRepository
                .findByUserIdAndType(userId, RelationshipType.INTIMATE);

        return relationships.stream()
                .map(r -> userRepository.findById(r.getTargetUserId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 获取谁加我为好友
     */
    public List<UserDTO> getWhoAddedMeAsFriend(Long userId) {
        List<UserRelationship> relationships = relationshipRepository.findWhoAddedMeAsFriend(userId);

        return relationships.stream()
                .map(r -> userRepository.findById(r.getUserId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 获取谁喜欢我
     */
    public List<UserDTO> getWhoLikesMe(Long userId) {
        List<UserRelationship> relationships = relationshipRepository.findWhoLikesMe(userId);

        return relationships.stream()
                .map(r -> userRepository.findById(r.getUserId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 更新亲密度分数
     */
    @Transactional
    public void updateIntimacyScore(Long userId, Long targetUserId, int scoreDelta) {
        // 查找或创建亲密关系
        Optional<UserRelationship> existing = relationshipRepository
                .findByUserIdAndTargetUserIdAndRelationshipType(userId, targetUserId, RelationshipType.INTIMATE);

        UserRelationship relationship;
        if (existing.isPresent()) {
            relationship = existing.get();
            relationship.setIntimacyScore(relationship.getIntimacyScore() + scoreDelta);
        } else {
            relationship = new UserRelationship();
            relationship.setUserId(userId);
            relationship.setTargetUserId(targetUserId);
            relationship.setRelationshipType(RelationshipType.INTIMATE);
            relationship.setIntimacyScore(scoreDelta);
        }

        relationshipRepository.save(relationship);
        log.info("更新亲密度分数 - userId: {}, targetUserId: {}, 新分数: {}",
                userId, targetUserId, relationship.getIntimacyScore());
    }

    // ========== 订阅功能 ==========

    /**
     * 订阅用户状态通知
     */
    @Transactional
    public boolean subscribeUser(Long userId, Long targetUserId) {
        if (userId.equals(targetUserId)) {
            log.warn("不能订阅自己 - userId: {}", userId);
            return false;
        }

        // 查找或创建SUBSCRIBE关系
        Optional<UserRelationship> existing = relationshipRepository
                .findByUserIdAndTargetUserIdAndRelationshipType(userId, targetUserId, RelationshipType.SUBSCRIBE);

        if (existing.isPresent()) {
            UserRelationship relationship = existing.get();
            relationship.setIsSubscribed(true);
            relationshipRepository.save(relationship);
            log.info("更新订阅状态 - userId: {}, targetUserId: {}", userId, targetUserId);
        } else {
            UserRelationship relationship = new UserRelationship();
            relationship.setUserId(userId);
            relationship.setTargetUserId(targetUserId);
            relationship.setRelationshipType(RelationshipType.SUBSCRIBE);
            relationship.setIsSubscribed(true);
            relationship.setIntimacyScore(0);
            relationshipRepository.save(relationship);
            log.info("创建订阅关系 - userId: {}, targetUserId: {}", userId, targetUserId);
        }

        return true;
    }

    /**
     * 取消订阅用户状态通知
     */
    @Transactional
    public boolean unsubscribeUser(Long userId, Long targetUserId) {
        Optional<UserRelationship> relationship = relationshipRepository
                .findByUserIdAndTargetUserIdAndRelationshipType(userId, targetUserId, RelationshipType.SUBSCRIBE);

        if (relationship.isPresent()) {
            relationshipRepository.delete(relationship.get());
            log.info("取消订阅成功 - userId: {}, targetUserId: {}", userId, targetUserId);
            return true;
        }

        return false;
    }

    /**
     * 检查是否已订阅
     */
    public boolean isSubscribed(Long userId, Long targetUserId) {
        Optional<UserRelationship> relationship = relationshipRepository
                .findByUserIdAndTargetUserIdAndRelationshipType(userId, targetUserId, RelationshipType.SUBSCRIBE);

        return relationship.isPresent() && Boolean.TRUE.equals(relationship.get().getIsSubscribed());
    }

    // ========== 备注功能 ==========

    /**
     * 设置用户备注
     */
    @Transactional
    public boolean setUserRemark(Long userId, Long targetUserId, String remark) {
        // 查找FRIEND或LIKE关系
        Optional<UserRelationship> friendRelation = relationshipRepository
                .findByUserIdAndTargetUserIdAndRelationshipType(userId, targetUserId, RelationshipType.FRIEND);

        Optional<UserRelationship> likeRelation = relationshipRepository
                .findByUserIdAndTargetUserIdAndRelationshipType(userId, targetUserId, RelationshipType.LIKE);

        UserRelationship relationship = null;

        if (friendRelation.isPresent()) {
            relationship = friendRelation.get();
        } else if (likeRelation.isPresent()) {
            relationship = likeRelation.get();
        } else {
            // 如果没有任何关系,创建一个FRIEND关系
            relationship = new UserRelationship();
            relationship.setUserId(userId);
            relationship.setTargetUserId(targetUserId);
            relationship.setRelationshipType(RelationshipType.FRIEND);
            relationship.setIntimacyScore(0);
        }

        relationship.setRemark(remark);
        relationshipRepository.save(relationship);
        log.info("设置备注成功 - userId: {}, targetUserId: {}, remark: {}", userId, targetUserId, remark);

        return true;
    }

    /**
     * 获取用户备注
     */
    public String getUserRemark(Long userId, Long targetUserId) {
        // 依次查找FRIEND、LIKE关系中的备注
        Optional<UserRelationship> friendRelation = relationshipRepository
                .findByUserIdAndTargetUserIdAndRelationshipType(userId, targetUserId, RelationshipType.FRIEND);

        if (friendRelation.isPresent() && friendRelation.get().getRemark() != null) {
            return friendRelation.get().getRemark();
        }

        Optional<UserRelationship> likeRelation = relationshipRepository
                .findByUserIdAndTargetUserIdAndRelationshipType(userId, targetUserId, RelationshipType.LIKE);

        if (likeRelation.isPresent() && likeRelation.get().getRemark() != null) {
            return likeRelation.get().getRemark();
        }

        return null;
    }

    // ========== 黑名单功能 ==========

    /**
     * 加入黑名单
     */
    @Transactional
    public boolean addToBlacklist(Long userId, Long targetUserId) {
        if (userId.equals(targetUserId)) {
            log.warn("不能将自己加入黑名单 - userId: {}", userId);
            return false;
        }

        // 检查是否已在黑名单
        Optional<UserRelationship> existing = relationshipRepository
                .findByUserIdAndTargetUserIdAndRelationshipType(userId, targetUserId, RelationshipType.BLACKLIST);

        if (existing.isPresent()) {
            log.info("已在黑名单中 - userId: {}, targetUserId: {}", userId, targetUserId);
            return true;
        }

        // 创建黑名单关系
        UserRelationship relationship = new UserRelationship();
        relationship.setUserId(userId);
        relationship.setTargetUserId(targetUserId);
        relationship.setRelationshipType(RelationshipType.BLACKLIST);
        relationship.setIntimacyScore(0);

        relationshipRepository.save(relationship);
        log.info("加入黑名单成功 - userId: {}, targetUserId: {}", userId, targetUserId);

        return true;
    }

    /**
     * 移出黑名单
     */
    @Transactional
    public boolean removeFromBlacklist(Long userId, Long targetUserId) {
        Optional<UserRelationship> relationship = relationshipRepository
                .findByUserIdAndTargetUserIdAndRelationshipType(userId, targetUserId, RelationshipType.BLACKLIST);

        if (relationship.isPresent()) {
            relationshipRepository.delete(relationship.get());
            log.info("移出黑名单成功 - userId: {}, targetUserId: {}", userId, targetUserId);
            return true;
        }

        return false;
    }

    /**
     * 检查是否在黑名单
     */
    public boolean isBlacklisted(Long userId, Long targetUserId) {
        return relationshipRepository
                .findByUserIdAndTargetUserIdAndRelationshipType(userId, targetUserId, RelationshipType.BLACKLIST)
                .isPresent();
    }

    /**
     * 获取黑名单列表
     */
    public List<UserDTO> getBlacklistUsers(Long userId) {
        List<UserRelationship> relationships = relationshipRepository
                .findByUserIdAndType(userId, RelationshipType.BLACKLIST);

        return relationships.stream()
                .map(r -> userRepository.findById(r.getTargetUserId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 获取当前用户的所有黑名单用户ID
     */
    public List<Long> getBlacklistedUserIds(Long userId) {
        List<UserRelationship> relationships = relationshipRepository
                .findByUserIdAndType(userId, RelationshipType.BLACKLIST);

        return relationships.stream()
                .map(UserRelationship::getTargetUserId)
                .collect(Collectors.toList());
    }

    /**
     * 批量移出黑名单
     */
    @Transactional
    public int batchRemoveFromBlacklist(Long userId, List<Long> targetUserIds) {
        int successCount = 0;
        for (Long targetUserId : targetUserIds) {
            if (removeFromBlacklist(userId, targetUserId)) {
                successCount++;
            }
        }
        log.info("批量移出黑名单 - userId: {}, 成功移出: {}/{}", userId, successCount, targetUserIds.size());
        return successCount;
    }

    /**
     * 将User实体转换为DTO
     */
    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setNickname(user.getNickname());
        dto.setPhone(user.getPhone());
        dto.setGender(user.getGender() != null ? user.getGender().name() : null);
        dto.setAge(user.getAge());
        dto.setLocation(user.getLocation());
        dto.setSignature(user.getSignature());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setIsOnline(user.getIsOnline());
        dto.setStatus(user.getStatus() != null ? user.getStatus().name() : null);
        dto.setIsVip(user.getIsVip());
        dto.setWealthLevel(user.getWealthLevel());
        dto.setBalance(user.getBalance() != null ? user.getBalance().doubleValue() : 0.0);
        return dto;
    }
}
