package com.example.socialmeet.service;

import com.example.socialmeet.dto.WealthLevelDTO;
import com.example.socialmeet.entity.WealthLevel;
import com.example.socialmeet.repository.WealthLevelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class WealthService {
    
    @Autowired
    private WealthLevelRepository wealthLevelRepository;
    
    public List<WealthLevelDTO> getAllWealthLevels() {
        List<WealthLevel> levels = wealthLevelRepository.findAll();
        return levels.stream().map(WealthLevelDTO::new).collect(Collectors.toList());
    }
    
    public WealthLevelDTO getWealthLevelById(Long id) {
        Optional<WealthLevel> levelOpt = wealthLevelRepository.findById(id);
        if (levelOpt.isPresent()) {
            return new WealthLevelDTO(levelOpt.get());
        }
        return null;
    }
    
    public WealthLevelDTO getWealthLevelByContribution(BigDecimal contribution) {
        // 根据财富值查找匹配的等级
        List<WealthLevel> levels = wealthLevelRepository.findByWealthValueGreaterThanEqual(contribution.intValue());
        if (!levels.isEmpty()) {
            return new WealthLevelDTO(levels.get(0));
        }
        return null;
    }
    
    public List<WealthLevelDTO> getHigherWealthLevels(Integer currentLevel) {
        // 查找财富值大于当前等级的用户
        List<WealthLevel> levels = wealthLevelRepository.findByWealthValueGreaterThanEqual(currentLevel);
        return levels.stream().map(WealthLevelDTO::new).collect(Collectors.toList());
    }
    
    public Integer getUserWealthLevel(BigDecimal totalContribution) {
        // 根据财富值获取等级
        List<WealthLevel> levels = wealthLevelRepository.findByWealthValueGreaterThanEqual(totalContribution.intValue());
        if (!levels.isEmpty()) {
            return levels.get(0).getWealthValue();
        }
        return 1; // 默认青铜等级
    }
    
    public String getUserWealthLevelName(BigDecimal totalContribution) {
        // 根据财富值获取等级名称
        List<WealthLevel> levels = wealthLevelRepository.findByWealthValueGreaterThanEqual(totalContribution.intValue());
        if (!levels.isEmpty()) {
            return levels.get(0).getLevelName();
        }
        return "青铜"; // 默认等级名称
    }
}
