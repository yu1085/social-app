package com.socialmeet.backend.config;

import cn.jiguang.sdk.api.PushApi;
import feign.Logger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * JPush 配置类
 * 使用极光推送最新SDK 5.2.9
 */
@Slf4j
@Configuration
public class JPushConfig {

    @Value("${jpush.app-key:ff90a2867fcf541a3f3e8ed4}")
    private String appKey;

    @Value("${jpush.master-secret:112ee5a04324ae703d2d6b3d}")
    private String masterSecret;

    @Bean
    public PushApi pushApi() {
        log.info("初始化JPush PushApi - AppKey: {}", appKey);

        return new PushApi.Builder()
                .setAppKey(appKey)
                .setMasterSecret(masterSecret)
                .setLoggerLevel(Logger.Level.FULL) // 设置日志级别为FULL，便于调试
                .build();
    }
}
