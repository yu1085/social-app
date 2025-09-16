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
        List<WealthLevel> levels = wealthLevelRepository.findByIsActiveTrueOrderByLevelAsc();
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
        Optional<WealthLevel> levelOpt = wealthLevelRepository.findMatchingWealthLevel(contribution);
        if (levelOpt.isPresent()) {
            return new WealthLevelDTO(levelOpt.get());
        }
        return null;
    }
    
    public List<WealthLevelDTO> getHigherWealthLevels(Integer currentLevel) {
        List<WealthLevel> levels = wealthLevelRepository.findHigherWealthLevels(currentLevel);
        return levels.stream().map(WealthLevelDTO::new).collect(Collectors.toList());
    }
    
    public Integer getUserWealthLevel(BigDecimal totalContribution) {
        Optional<WealthLevel> levelOpt = wealthLevelRepository.findMatchingWealthLevel(totalContribution);
        if (levelOpt.isPresent()) {
            return levelOpt.get().getLevel();
        }
        return 1; // 默认青铜等级
    }
    
    public String getUserWealthLevelName(BigDecimal totalContribution) {
        Optional<WealthLevel> levelOpt = wealthLevelRepository.findMatchingWealthLevel(totalContribution);
        if (levelOpt.isPresent()) {
            return levelOpt.get().getName();
        }
        return "青铜"; // 默认等级名称
    }
}
