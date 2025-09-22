package com.example.socialmeet.service;

import com.example.socialmeet.entity.Dynamic;
import com.example.socialmeet.entity.User;
import com.example.socialmeet.repository.DynamicRepository;
import com.example.socialmeet.repository.UserRepository;
import com.example.socialmeet.util.DistanceCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 动态位置服务
 * 处理动态位置相关业务逻辑
 */
@Service
public class DynamicLocationService {
    
    @Autowired
    private DynamicRepository dynamicRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private LocationService locationService;
    
    /**
     * 为动态添加位置信息
     * @param dynamic 动态对象
     * @param location 位置描述
     * @param latitude 纬度（可选）
     * @param longitude 经度（可选）
     */
    public void addLocationToDynamic(Dynamic dynamic, String location, Double latitude, Double longitude) {
        dynamic.setLocation(location);
        
        // 如果有精确坐标，可以存储到用户位置信息中
        if (latitude != null && longitude != null) {
            User user = userRepository.findById(dynamic.getUserId()).orElse(null);
            if (user != null) {
                // 更新用户位置信息
                locationService.updateUserLocation(dynamic.getUserId(), latitude, longitude, location);
            }
        }
    }
    
    /**
     * 根据位置筛选动态
     * @param userLat 用户纬度
     * @param userLon 用户经度
     * @param maxDistanceKm 最大距离（公里）
     * @return 附近的动态列表
     */
    public List<Dynamic> getNearbyDynamics(Double userLat, Double userLon, double maxDistanceKm) {
        // 这里应该实现基于地理位置的查询
        // 由于MySQL的空间查询比较复杂，这里先返回所有动态
        // 实际项目中可以使用PostGIS或Elasticsearch等支持地理查询的数据库
        
        List<Dynamic> allDynamics = dynamicRepository.findByIsDeletedFalseOrderByPublishTimeDesc(
            org.springframework.data.domain.PageRequest.of(0, 100)
        ).getContent();
        
        // 过滤附近的动态
        return allDynamics.stream()
            .filter(dynamic -> {
                User user = userRepository.findById(dynamic.getUserId()).orElse(null);
                if (user == null) return false;
                
                // 如果有精确坐标，计算距离
                if (user.getLatitude() != null && user.getLongitude() != null) {
                    return DistanceCalculator.isWithinDistance(
                        userLat, userLon,
                        user.getLatitude(), user.getLongitude(),
                        maxDistanceKm
                    );
                }
                
                // 使用城市坐标计算
                LocationService.CityCoordinate userCoord = locationService.getCityCoordinate(user.getLocation());
                if (userCoord != null) {
                    return DistanceCalculator.isWithinDistance(
                        userLat, userLon,
                        userCoord.latitude, userCoord.longitude,
                        maxDistanceKm
                    );
                }
                
                return false;
            })
            .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * 获取动态的位置信息
     * @param dynamicId 动态ID
     * @return 位置信息
     */
    public String getDynamicLocation(Long dynamicId) {
        Optional<Dynamic> dynamicOpt = dynamicRepository.findById(dynamicId);
        if (dynamicOpt.isPresent()) {
            Dynamic dynamic = dynamicOpt.get();
            return dynamic.getLocation() != null ? dynamic.getLocation() : "未知位置";
        }
        return "未知位置";
    }
    
    /**
     * 计算动态与用户的距离
     * @param dynamicId 动态ID
     * @param userId 用户ID
     * @return 距离（公里），如果无法计算返回-1
     */
    public double calculateDynamicDistance(Long dynamicId, Long userId) {
        Optional<Dynamic> dynamicOpt = dynamicRepository.findById(dynamicId);
        if (!dynamicOpt.isPresent()) {
            return -1;
        }
        
        Dynamic dynamic = dynamicOpt.get();
        return locationService.calculateUserDistance(userId, dynamic.getUserId());
    }
    
    /**
     * 获取动态的距离字符串
     * @param dynamicId 动态ID
     * @param userId 用户ID
     * @return 格式化的距离字符串
     */
    public String getDynamicDistanceString(Long dynamicId, Long userId) {
        double distance = calculateDynamicDistance(dynamicId, userId);
        if (distance < 0) {
            return "未知";
        }
        return DistanceCalculator.formatDistance(distance);
    }
    
    /**
     * 根据城市筛选动态
     * @param cityName 城市名称
     * @return 该城市的动态列表
     */
    public List<Dynamic> getDynamicsByCity(String cityName) {
        return dynamicRepository.findByIsDeletedFalseOrderByPublishTimeDesc(
            org.springframework.data.domain.PageRequest.of(0, 100)
        ).getContent().stream()
            .filter(dynamic -> {
                User user = userRepository.findById(dynamic.getUserId()).orElse(null);
                if (user == null) return false;
                
                // 检查用户位置是否包含该城市
                String userLocation = user.getLocation();
                return userLocation != null && userLocation.contains(cityName);
            })
            .collect(java.util.stream.Collectors.toList());
    }
}
