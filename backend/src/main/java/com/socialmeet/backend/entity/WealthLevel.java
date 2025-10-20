package com.socialmeet.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 财富等级实体
 */
@Entity
@Table(name = "wealth_levels")
@Data
@EqualsAndHashCode(callSuper = false)
@EntityListeners(AuditingEntityListener.class)
public class WealthLevel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "level_name", nullable = false, unique = true)
    private String levelName;
    
    @Column(name = "level_id", nullable = false, unique = true)
    private Integer levelId;
    
    @Column(name = "min_wealth_value", nullable = false)
    private Integer minWealthValue;
    
    @Column(name = "max_wealth_value")
    private Integer maxWealthValue;
    
    @Column(name = "level_description")
    private String levelDescription;
    
    @Column(name = "level_icon")
    private String levelIcon;
    
    @Column(name = "is_max_level", nullable = false)
    private Boolean isMaxLevel = false;
    
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}