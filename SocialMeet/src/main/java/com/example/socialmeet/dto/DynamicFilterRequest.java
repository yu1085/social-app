package com.example.socialmeet.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 动态筛选请求DTO
 */
@Data
@Builder
public class DynamicFilterRequest {
    
    /**
     * 动态类型：latest, hot, nearby, following
     */
    private String type;
    
    /**
     * 页码
     */
    private Integer page;
    
    /**
     * 每页大小
     */
    private Integer size;
    
    /**
     * 位置筛选
     */
    private String location;
    
    /**
     * 性别筛选
     */
    private String gender;
    
    /**
     * 最小年龄
     */
    private Integer minAge;
    
    /**
     * 最大年龄
     */
    private Integer maxAge;
    
    /**
     * 关键词搜索
     */
    private String keyword;
    
    /**
     * 是否只显示有图片的动态
     */
    private Boolean hasImages;
    
    /**
     * 是否只显示有位置的动态
     */
    private Boolean hasLocation;
    
    /**
     * 最小点赞数
     */
    private Integer minLikes;
    
    /**
     * 最大点赞数
     */
    private Integer maxLikes;
    
    /**
     * 时间范围：today, week, month, year
     */
    private String timeRange;
    
    /**
     * 排序字段：publishTime, likeCount, commentCount, viewCount
     */
    private String sortBy;
    
    /**
     * 排序方向：ASC, DESC
     */
    private String sortDirection;
}
