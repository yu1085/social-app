package com.example.socialmeet.service;

import com.example.socialmeet.entity.User;
import com.example.socialmeet.repository.UserRepository;
import com.example.socialmeet.util.DistanceCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 位置服务
 * 处理用户位置相关业务逻辑
 */
@Service
public class LocationService {
    
    @Autowired
    private UserRepository userRepository;
    
    // 城市坐标缓存
    private static final Map<String, CityCoordinate> CITY_COORDINATES = new HashMap<>();
    
    static {
        // 初始化主要城市坐标
        CITY_COORDINATES.put("北京市", new CityCoordinate(39.9042, 116.4074));
        CITY_COORDINATES.put("上海市", new CityCoordinate(31.2304, 121.4737));
        CITY_COORDINATES.put("广州市", new CityCoordinate(23.1291, 113.2644));
        CITY_COORDINATES.put("深圳市", new CityCoordinate(22.5431, 114.0579));
        CITY_COORDINATES.put("杭州市", new CityCoordinate(30.2741, 120.1551));
        CITY_COORDINATES.put("南京市", new CityCoordinate(32.0603, 118.7969));
        CITY_COORDINATES.put("武汉市", new CityCoordinate(30.5928, 114.3055));
        CITY_COORDINATES.put("成都市", new CityCoordinate(30.5728, 104.0668));
        CITY_COORDINATES.put("西安市", new CityCoordinate(34.3416, 108.9398));
        CITY_COORDINATES.put("重庆市", new CityCoordinate(29.4316, 106.9123));
        CITY_COORDINATES.put("天津市", new CityCoordinate(39.3434, 117.3616));
        CITY_COORDINATES.put("苏州市", new CityCoordinate(31.2989, 120.5853));
        CITY_COORDINATES.put("安徽省", new CityCoordinate(31.8612, 117.2838));
        CITY_COORDINATES.put("江苏省", new CityCoordinate(32.0612, 118.7639));
        CITY_COORDINATES.put("浙江省", new CityCoordinate(30.2741, 120.1551));
        CITY_COORDINATES.put("广东省", new CityCoordinate(23.1291, 113.2644));
        CITY_COORDINATES.put("四川省", new CityCoordinate(30.5728, 104.0668));
        CITY_COORDINATES.put("湖北省", new CityCoordinate(30.5928, 114.3055));
        CITY_COORDINATES.put("陕西省", new CityCoordinate(34.3416, 108.9398));
        CITY_COORDINATES.put("山东省", new CityCoordinate(36.6758, 117.0009));
        CITY_COORDINATES.put("河南省", new CityCoordinate(34.7579, 113.6496));
        CITY_COORDINATES.put("河北省", new CityCoordinate(38.0428, 114.5149));
        CITY_COORDINATES.put("山西省", new CityCoordinate(37.8706, 112.5489));
        CITY_COORDINATES.put("辽宁省", new CityCoordinate(41.8057, 123.4315));
        CITY_COORDINATES.put("吉林省", new CityCoordinate(43.8171, 125.3235));
        CITY_COORDINATES.put("黑龙江省", new CityCoordinate(45.7732, 126.6617));
        CITY_COORDINATES.put("福建省", new CityCoordinate(26.0745, 119.2965));
        CITY_COORDINATES.put("江西省", new CityCoordinate(28.6820, 115.8999));
        CITY_COORDINATES.put("湖南省", new CityCoordinate(28.2278, 112.9388));
        CITY_COORDINATES.put("广西壮族自治区", new CityCoordinate(22.8170, 108.3669));
        CITY_COORDINATES.put("海南省", new CityCoordinate(20.0311, 110.3312));
        CITY_COORDINATES.put("云南省", new CityCoordinate(25.0389, 102.7183));
        CITY_COORDINATES.put("贵州省", new CityCoordinate(26.5783, 106.7135));
        CITY_COORDINATES.put("甘肃省", new CityCoordinate(36.0611, 103.8343));
        CITY_COORDINATES.put("青海省", new CityCoordinate(36.6232, 101.7782));
        CITY_COORDINATES.put("宁夏回族自治区", new CityCoordinate(38.4872, 106.2782));
        CITY_COORDINATES.put("新疆维吾尔自治区", new CityCoordinate(43.7928, 87.6177));
        CITY_COORDINATES.put("西藏自治区", new CityCoordinate(29.6465, 91.1172));
        CITY_COORDINATES.put("内蒙古自治区", new CityCoordinate(40.8175, 111.7656));
    }
    
    /**
     * 根据城市名称获取坐标
     * @param cityName 城市名称
     * @return 城市坐标，如果未找到返回null
     */
    public CityCoordinate getCityCoordinate(String cityName) {
        return CITY_COORDINATES.get(cityName);
    }
    
    /**
     * 计算用户与目标用户的距离
     * @param currentUserId 当前用户ID
     * @param targetUserId 目标用户ID
     * @return 距离（公里），如果无法计算返回-1
     */
    public double calculateUserDistance(Long currentUserId, Long targetUserId) {
        User currentUser = userRepository.findById(currentUserId).orElse(null);
        User targetUser = userRepository.findById(targetUserId).orElse(null);
        
        if (currentUser == null || targetUser == null) {
            return -1;
        }
        
        // 优先使用精确坐标
        if (currentUser.getLatitude() != null && currentUser.getLongitude() != null &&
            targetUser.getLatitude() != null && targetUser.getLongitude() != null) {
            return DistanceCalculator.calculateDistance(
                currentUser.getLatitude(), currentUser.getLongitude(),
                targetUser.getLatitude(), targetUser.getLongitude()
            );
        }
        
        // 使用城市坐标计算
        CityCoordinate currentCoord = getCityCoordinate(currentUser.getLocation());
        CityCoordinate targetCoord = getCityCoordinate(targetUser.getLocation());
        
        if (currentCoord != null && targetCoord != null) {
            return DistanceCalculator.calculateDistance(
                currentCoord.latitude, currentCoord.longitude,
                targetCoord.latitude, targetCoord.longitude
            );
        }
        
        return -1; // 无法计算距离
    }
    
    /**
     * 计算用户与目标用户的距离字符串
     * @param currentUserId 当前用户ID
     * @param targetUserId 目标用户ID
     * @return 格式化的距离字符串
     */
    public String calculateUserDistanceString(Long currentUserId, Long targetUserId) {
        double distance = calculateUserDistance(currentUserId, targetUserId);
        if (distance < 0) {
            return "未知";
        }
        return DistanceCalculator.formatDistance(distance);
    }
    
    /**
     * 更新用户位置
     * @param userId 用户ID
     * @param latitude 纬度
     * @param longitude 经度
     * @param location 位置描述
     * @return 是否更新成功
     */
    public boolean updateUserLocation(Long userId, Double latitude, Double longitude, String location) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return false;
        }
        
        user.setLatitude(latitude);
        user.setLongitude(longitude);
        user.setLocation(location);
        
        userRepository.save(user);
        return true;
    }
    
    /**
     * 根据城市名称更新用户位置
     * @param userId 用户ID
     * @param cityName 城市名称
     * @return 是否更新成功
     */
    public boolean updateUserLocationByCity(Long userId, String cityName) {
        CityCoordinate coord = getCityCoordinate(cityName);
        if (coord == null) {
            return false;
        }
        
        return updateUserLocation(userId, coord.latitude, coord.longitude, cityName);
    }
    
    /**
     * 城市坐标内部类
     */
    public static class CityCoordinate {
        public final double latitude;
        public final double longitude;
        
        public CityCoordinate(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }
}
