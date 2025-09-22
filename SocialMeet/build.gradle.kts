plugins {
    java
    id("org.springframework.boot") version "3.3.5"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
description = "Social Meet Backend"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

// 仓库配置已移至 settings.gradle.kts

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    
    // 监控和指标
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-registry-prometheus")
    
    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    
    // JWT
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")
    
    // 极光推送 SDK
    implementation("cn.jiguang.sdk:jpush:5.6.0")
    
    // HTTP 客户端用于推送
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    
    // 阿里云SMS SDK
    implementation("com.aliyun:alibabacloud-dysmsapi20170525:2.0.24")
    implementation("com.aliyun:tea-openapi:0.2.8")
    implementation("com.aliyun:tea-util:0.2.21")
    
    // FastJSON
    implementation("com.alibaba:fastjson:2.0.43")
    
    // 阿里云号码认证SDK (暂时使用模拟实现)
    // implementation("com.aliyun:dypnsapi20170525:2.0.0")
    // implementation("com.aliyun:tea-openapi:0.2.8")
    // implementation("com.aliyun:tea-util:0.2.21")
    
    // 运营商一键登录SDK (暂时注释，使用模拟实现)
    // implementation("com.mobile.auth:mobile-auth-sdk:3.0.0")
    // implementation("com.mobile.auth:mobile-auth-sdk-phone:3.0.0")
    
    // Swagger/OpenAPI 文档
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")
    
    // Servlet API (用于 PerformanceMonitor)
    implementation("javax.servlet:javax.servlet-api:4.0.1")
    
    // 支付宝SDK
    implementation("com.alipay.sdk:alipay-sdk-java:4.38.10.ALL")
    
    // JSON处理
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.core:jackson-core")
    implementation("com.fasterxml.jackson.core:jackson-annotations")
    
    runtimeOnly("com.mysql:mysql-connector-j")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

// 构建优化配置
tasks.withType<JavaCompile> {
    options.isIncremental = true
    options.isFork = true
    options.forkOptions.jvmArgs = listOf("-Xmx1024m")
}

// 跳过测试以加速构建
tasks.named("test") {
    enabled = false
}

// 优化JAR打包
tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    archiveFileName.set("${project.name}-${project.version}.jar")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    mainClass.set("com.example.socialmeet.SocialMeetApplication")
}

// 清理任务优化 - 使用默认clean任务
