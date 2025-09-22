package com.example.socialmeet.util;

import org.springframework.stereotype.Component;

/**
 * 距离计算工具类
 * 使用Haversine公式计算地球表面两点间的距离
 */
@Component
public class DistanceCalculator {
    
    // 地球半径（公里）
    private static final double EARTH_RADIUS = 6371.0;
    
    /**
     * 计算两点间的距离（公里）
     * @param lat1 第一个点的纬度
     * @param lon1 第一个点的经度
     * @param lat2 第二个点的纬度
     * @param lon2 第二个点的经度
     * @return 距离（公里）
     */
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // 将角度转换为弧度
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);
        
        // 计算纬度差和经度差
        double deltaLat = lat2Rad - lat1Rad;
        double deltaLon = lon2Rad - lon1Rad;
        
        // Haversine公式
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                   Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                   Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return EARTH_RADIUS * c;
    }
    
    /**
     * 计算距离并格式化显示
     * @param lat1 第一个点的纬度
     * @param lon1 第一个点的经度
     * @param lat2 第二个点的纬度
     * @param lon2 第二个点的经度
     * @return 格式化的距离字符串
     */
    public static String calculateDistanceString(double lat1, double lon1, double lat2, double lon2) {
        double distance = calculateDistance(lat1, lon1, lat2, lon2);
        return formatDistance(distance);
    }
    
    /**
     * 格式化距离显示
     * @param distanceKm 距离（公里）
     * @return 格式化的距离字符串
     */
    public static String formatDistance(double distanceKm) {
        if (distanceKm < 1.0) {
            // 小于1公里显示米
            int meters = (int) (distanceKm * 1000);
            return meters + "m";
        } else if (distanceKm < 10.0) {
            // 1-10公里显示一位小数
            return String.format("%.1fkm", distanceKm);
        } else {
            // 10公里以上显示整数
            return (int) distanceKm + "km";
        }
    }
    
    /**
     * 根据距离范围筛选
     * @param userLat 用户纬度
     * @param userLon 用户经度
     * @param targetLat 目标纬度
     * @param targetLon 目标经度
     * @param maxDistanceKm 最大距离（公里）
     * @return 是否在范围内
     */
    public static boolean isWithinDistance(double userLat, double userLon, 
                                         double targetLat, double targetLon, 
                                         double maxDistanceKm) {
        double distance = calculateDistance(userLat, userLon, targetLat, targetLon);
        return distance <= maxDistanceKm;
    }
    
    /**
     * 获取距离等级描述
     * @param distanceKm 距离（公里）
     * @return 距离等级描述
     */
    public static String getDistanceLevel(double distanceKm) {
        if (distanceKm < 1.0) {
            return "附近";
        } else if (distanceKm < 5.0) {
            return "很近";
        } else if (distanceKm < 20.0) {
            return "较近";
        } else if (distanceKm < 100.0) {
            return "较远";
        } else {
            return "很远";
        }
    }
}
