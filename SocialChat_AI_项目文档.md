# SocialChat AI 项目文档

## 项目概述

**项目名称**：SocialChat AI  
**项目类型**：社交交友应用  
**技术栈**：Spring Boot + MySQL + JPush + Swagger  
**部署状态**：已部署并运行  

## 服务器信息

### 基础信息
- **服务器IP**：119.45.174.10
- **服务器端口**：8080
- **操作系统**：Ubuntu 22.04.5 LTS
- **Java版本**：Java 21.0.8
- **Spring Boot版本**：3.2.5

### 应用状态
- **运行状态**：✅ 正常运行
- **进程ID**：255334
- **内存使用**：343MB
- **启动时间**：2025-09-21 10:40:19

## 域名信息

### 域名基本信息
- **主域名**：socialchatai.cloud
- **注册商**：阿里云
- **注册时间**：2025-09-21
- **到期时间**：2026-09-21
- **年费**：¥10.00
- **域名状态**：已注册，待实名认证

### DNS解析配置
#### 主域名解析
- **记录类型**：A记录
- **主机记录**：@ (socialchatai.cloud)
- **解析值**：119.45.174.10
- **TTL**：600秒（10分钟）
- **状态**：已配置

#### www子域名解析
- **记录类型**：A记录
- **主机记录**：www (www.socialchatai.cloud)
- **解析值**：119.45.174.10
- **TTL**：600秒（10分钟）
- **状态**：已配置

### 域名管理信息
- **阿里云控制台**：https://dc.console.aliyun.com/
- **云解析DNS**：https://dnsnext.console.aliyun.com/
- **域名管理账号**：aliyun0536223728
- **实名认证状态**：⏳ 待完成
- **备案状态**：未备案

### 域名访问状态
- **解析状态**：⏳ 传播中（全球DNS服务器尚未完全生效）
- **预计生效时间**：24-48小时
- **当前可访问**：通过IP地址 119.45.174.10

## 应用访问地址

### API接口
- **健康检查**：http://119.45.174.10:8080/api/health
- **Swagger UI**：http://119.45.174.10:8080/swagger-ui.html
- **API文档**：http://119.45.174.10:8080/api-docs

### 域名访问（解析生效后）
- **主站**：http://socialchatai.cloud
- **www站**：http://www.socialchatai.cloud
- **API接口**：http://socialchatai.cloud:8080
- **Swagger文档**：http://socialchatai.cloud:8080/swagger-ui.html

## 数据库配置

### MySQL信息
- **数据库名**：socialmeet
- **用户名**：socialmeet
- **密码**：SocialMeet2025
- **端口**：3306
- **状态**：✅ 正常运行

### 连接信息
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/socialmeet
spring.datasource.username=socialmeet
spring.datasource.password=SocialMeet2025
```

## 应用功能模块

### 核心功能
1. **用户认证系统**
   - 手机验证码登录
   - JWT令牌认证
   - 用户注册和认证

2. **社交功能**
   - 用户资料管理
   - 头像和照片上传
   - 用户搜索和匹配
   - 关注/取消关注功能

3. **消息系统**
   - 实时聊天
   - 消息历史
   - 未读消息统计

4. **经济系统**
   - 钱包管理
   - 充值功能
   - 支付订单
   - 交易记录

5. **通话功能**
   - 语音/视频通话
   - 通话计费
   - 通话历史

6. **其他功能**
   - 礼物系统
   - VIP订阅
   - 优惠券系统
   - 守护关系
   - 邀请系统

## API接口文档

### 主要控制器
- **auth-controller**：认证相关接口
- **user-controller**：用户管理接口
- **message-controller**：消息系统接口
- **call-controller**：通话功能接口
- **payment-controller**：支付相关接口
- **gift-controller**：礼物系统接口
- **vip-controller**：VIP功能接口
- **wallet-controller**：钱包管理接口

### 接口访问
- **Swagger UI**：http://119.45.174.10:8080/swagger-ui.html
- **OpenAPI文档**：http://119.45.174.10:8080/api-docs

## 部署架构

### 技术栈
- **后端框架**：Spring Boot 3.2.5
- **数据库**：MySQL 8.0
- **安全框架**：Spring Security
- **API文档**：Swagger/OpenAPI 3.0
- **推送服务**：JPush
- **构建工具**：Gradle

### 服务器配置
- **CPU**：2核
- **内存**：4GB
- **存储**：50GB SSD
- **带宽**：5Mbps
- **操作系统**：Ubuntu 22.04.5 LTS

## 安全配置

### Spring Security
- **CORS**：已配置，允许跨域访问
- **CSRF**：已禁用
- **认证**：JWT令牌认证
- **授权**：基于角色的访问控制

### 数据库安全
- **连接加密**：SSL连接
- **密码策略**：强密码要求
- **访问控制**：用户权限管理

## 监控和日志

### 应用监控
- **健康检查**：/api/health
- **应用状态**：实时监控
- **性能指标**：内存、CPU使用率

### 日志管理
- **日志级别**：INFO
- **日志文件**：app.log
- **日志轮转**：按大小和时间

## 下一步计划

### 短期目标（1-2周）
1. **完成域名实名认证**
2. **等待DNS解析生效**
3. **搭建官方网站**
4. **开发移动端应用**

### 中期目标（1-2个月）
1. **完善应用功能**
2. **优化用户体验**
3. **增加用户量**
4. **完善支付系统**

### 长期目标（3-6个月）
1. **扩大用户规模**
2. **增加AI功能**
3. **国际化部署**
4. **商业化运营**

## 腾讯云信息

### 腾讯云账号信息
- **账号ID**：待补充
- **登录邮箱**：待补充
- **手机号码**：待补充
- **注册时间**：待补充
- **账号状态**：待确认

### 腾讯云控制台
- **主控制台**：https://console.cloud.tencent.com/
- **域名注册**：https://console.cloud.tencent.com/domain
- **云解析DNS**：https://console.cloud.tencent.com/cns
- **云服务器CVM**：https://console.cloud.tencent.com/cvm
- **轻量应用服务器**：https://console.cloud.tencent.com/lighthouse

### 腾讯云服务
- **域名服务**：域名注册、解析、管理
- **云服务器**：CVM、轻量应用服务器
- **云解析DNS**：域名解析服务
- **云安全**：DDoS防护、WAF等
- **云监控**：资源监控和告警

### 腾讯云优势
- **价格优势**：部分服务价格更便宜
- **微信生态**：与微信小程序集成更好
- **技术支持**：腾讯技术团队支持
- **国内访问**：国内用户访问速度快

## 阿里云信息

### 阿里云账号信息
- **主账号**：aliyun0536223728
- **控制台**：https://ecs.console.aliyun.com/
- **域名管理**：https://dc.console.aliyun.com/
- **云解析DNS**：https://dnsnext.console.aliyun.com/
- **轻量应用服务器**：119.45.174.10

### 当前使用服务
- **域名注册**：socialchatai.cloud
- **云解析DNS**：域名解析服务
- **轻量应用服务器**：应用部署服务器

### 服务器登录信息
- **服务器IP**：119.45.174.10
- **用户名**：ubuntu
- **SSH端口**：22
- **登录方式**：SSH密钥或密码

## 联系信息

### 技术支持
- **服务器管理**：通过SSH连接
- **数据库管理**：MySQL命令行
- **应用管理**：Spring Boot应用

### 域名管理
- **注册商**：阿里云
- **管理控制台**：https://dc.console.aliyun.com/
- **DNS管理**：https://dnsnext.console.aliyun.com/

### 云服务商信息

#### 阿里云
- **控制台**：https://ecs.console.aliyun.com/
- **域名控制台**：https://dc.console.aliyun.com/
- **云解析DNS**：https://dnsnext.console.aliyun.com/
- **轻量应用服务器**：119.45.174.10

#### 腾讯云
- **控制台**：https://console.cloud.tencent.com/
- **域名注册**：https://console.cloud.tencent.com/domain
- **云解析DNS**：https://console.cloud.tencent.com/cns
- **账号信息**：待补充

### 应用管理
- **应用目录**：/home/ubuntu/SocialMeet
- **启动脚本**：java -jar build/libs/socialmeet-0.0.1-SNAPSHOT.jar
- **日志文件**：app.log

## 故障排除

### 常见问题
1. **域名无法访问**
   - 检查DNS解析状态
   - 确认实名认证完成
   - 等待DNS传播完成

2. **应用无法启动**
   - 检查Java环境
   - 确认数据库连接
   - 查看应用日志

3. **数据库连接失败**
   - 检查MySQL服务状态
   - 确认用户名密码
   - 检查网络连接

### 应急处理
1. **应用重启**：sudo pkill -f socialmeet && nohup java -jar build/libs/socialmeet-0.0.1-SNAPSHOT.jar > app.log 2>&1 &
2. **数据库重启**：sudo systemctl restart mysql
3. **服务器重启**：sudo reboot

---

**文档更新时间**：2025-09-21  
**文档版本**：v1.0  
**维护人员**：项目团队
