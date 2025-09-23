package com.example.socialmeet.repository;

import com.example.socialmeet.entity.VipPrivilege;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * VIP特权Repository
 */
@Repository
public interface VipPrivilegeRepository extends JpaRepository<VipPrivilege, Long> {
    
    /**
     * 根据VIP等级ID查找特权
     */
    List<VipPrivilege> findByVipLevelIdAndIsActiveTrueOrderBySortOrderAsc(Long vipLevelId);
    
    /**
     * 根据特权类型查找
     */
    List<VipPrivilege> findByPrivilegeTypeAndIsActiveTrueOrderBySortOrderAsc(String privilegeType);
    
    /**
     * 根据VIP等级ID和特权类型查找
     */
    List<VipPrivilege> findByVipLevelIdAndPrivilegeTypeAndIsActiveTrueOrderBySortOrderAsc(Long vipLevelId, String privilegeType);
    
    /**
     * 查找所有活跃的特权
     */
    List<VipPrivilege> findByIsActiveTrueOrderByVipLevelIdAscSortOrderAsc();
}
