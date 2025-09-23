package com.example.socialmeet.service;

import com.example.socialmeet.entity.VipPrivilege;
import com.example.socialmeet.repository.VipPrivilegeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * VIP特权服务
 */
@Service
@Slf4j
@Transactional
public class VipPrivilegeService {
    
    @Autowired
    private VipPrivilegeRepository vipPrivilegeRepository;
    
    /**
     * 获取VIP等级的所有特权
     */
    public List<VipPrivilege> getVipPrivileges(Long vipLevelId) {
        return vipPrivilegeRepository.findByVipLevelIdAndIsActiveTrueOrderBySortOrderAsc(vipLevelId);
    }
    
    /**
     * 获取所有特权类型
     */
    public List<VipPrivilege> getAllPrivileges() {
        return vipPrivilegeRepository.findByIsActiveTrueOrderByVipLevelIdAscSortOrderAsc();
    }
    
    /**
     * 获取特权配置
     */
    public Map<String, Object> getPrivilegeConfig(Long vipLevelId) {
        List<VipPrivilege> privileges = getVipPrivileges(vipLevelId);
        
        return Map.of(
            "vipLevelId", vipLevelId,
            "privileges", privileges.stream().map(privilege -> Map.of(
                "id", privilege.getId(),
                "type", privilege.getPrivilegeType(),
                "name", privilege.getPrivilegeName(),
                "description", privilege.getPrivilegeDescription(),
                "value", privilege.getPrivilegeValue(),
                "sortOrder", privilege.getSortOrder()
            )).collect(Collectors.toList())
        );
    }
    
    /**
     * 检查用户是否有特定特权
     */
    public boolean hasPrivilege(Long vipLevelId, String privilegeType) {
        return !vipPrivilegeRepository.findByVipLevelIdAndPrivilegeTypeAndIsActiveTrueOrderBySortOrderAsc(vipLevelId, privilegeType).isEmpty();
    }
    
    /**
     * 获取特权值
     */
    public String getPrivilegeValue(Long vipLevelId, String privilegeType) {
        List<VipPrivilege> privileges = vipPrivilegeRepository.findByVipLevelIdAndPrivilegeTypeAndIsActiveTrueOrderBySortOrderAsc(vipLevelId, privilegeType);
        if (!privileges.isEmpty()) {
            return privileges.get(0).getPrivilegeValue();
        }
        return null;
    }
}
