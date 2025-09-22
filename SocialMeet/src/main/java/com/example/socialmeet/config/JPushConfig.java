package com.example.socialmeet.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 极光推送配置类
 */
@Configuration
@ConfigurationProperties(prefix = "jpush")
public class JPushConfig {
    
    private String appKey;
    private String masterSecret;
    private boolean apnsProduction = false;
    private int timeToLive = 60;
    private boolean statisticsEnabled = true;
    private int retryTimes = 3;
    private int retryInterval = 1000;
    
    public String getAppKey() {
        return appKey;
    }
    
    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }
    
    public String getMasterSecret() {
        return masterSecret;
    }
    
    public void setMasterSecret(String masterSecret) {
        this.masterSecret = masterSecret;
    }
    
    public boolean isApnsProduction() {
        return apnsProduction;
    }
    
    public void setApnsProduction(boolean apnsProduction) {
        this.apnsProduction = apnsProduction;
    }
    
    public int getTimeToLive() {
        return timeToLive;
    }
    
    public void setTimeToLive(int timeToLive) {
        this.timeToLive = timeToLive;
    }
    
    public boolean isStatisticsEnabled() {
        return statisticsEnabled;
    }
    
    public void setStatisticsEnabled(boolean statisticsEnabled) {
        this.statisticsEnabled = statisticsEnabled;
    }
    
    public int getRetryTimes() {
        return retryTimes;
    }
    
    public void setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
    }
    
    public int getRetryInterval() {
        return retryInterval;
    }
    
    public void setRetryInterval(int retryInterval) {
        this.retryInterval = retryInterval;
    }
}
