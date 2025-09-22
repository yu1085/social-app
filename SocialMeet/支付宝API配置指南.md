# 支付宝API配置指南

## 1. 获取支付宝应用信息

### 1.1 登录支付宝开放平台
访问：https://open.alipay.com/

### 1.2 创建应用
1. 进入"控制台" → "应用管理"
2. 点击"创建应用"
3. 填写应用信息：
   - 应用名称：SocialMeet实名认证
   - 应用类型：移动应用
   - 应用平台：Android
   - 应用签名：C1:78:E6:2D:6F:75:75:A7:8A:4E:CE:C0:FC:CE:A3:85:9E:31:E7:CA
   - 应用包名：com.example.myapplication

### 1.3 获取应用信息
- **APPID**: 2021005195696348
- **应用私钥**: 从开放平台下载
- **支付宝公钥**: 从开放平台获取

## 2. 申请API权限

### 2.1 申请身份证二要素核验接口
1. 进入应用详情页面
2. 点击"可调用产品"
3. 搜索"身份证二要素核验"
4. 申请 `datadigital.fincloud.generalsaas.twometa.check` 接口权限

### 2.2 配置接口加签方式
1. 进入"开发设置"
2. 配置"接口加签方式"为RSA2
3. 上传应用私钥和支付宝公钥

## 3. 配置应用信息

### 3.1 更新配置文件
编辑 `src/main/resources/application-alipay.yml`：

```yaml
app:
  alipay:
    # 替换为您的真实APPID
    app-id: 2021005195696348
    # 替换为您的应用私钥
    private-key: |
      MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC...
    # 替换为支付宝公钥
    public-key: |
      MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA...
```

### 3.2 私钥格式要求
- 私钥必须是PKCS8格式
- 包含完整的BEGIN/END标记
- 每行64个字符

## 4. 测试API连接

### 4.1 启动应用
```bash
./gradlew bootRun --args='--spring.profiles.active=alipay'
```

### 4.2 测试连接
```bash
curl http://localhost:8080/api/test/alipay/connection
```

### 4.3 测试身份证核验
```bash
curl -X POST "http://localhost:8080/api/test/alipay/verify?certName=张三&certNo=110101199001011234"
```

## 5. 常见问题

### 5.1 签名错误
- 检查私钥格式是否正确
- 确认使用的是PKCS8格式
- 检查私钥是否与开放平台配置一致

### 5.2 权限不足
- 确认已申请身份证二要素核验接口权限
- 检查应用状态是否为"已上线"
- 确认接口加签方式配置正确

### 5.3 网络连接问题
- 检查服务器网络是否正常
- 确认防火墙允许访问支付宝网关
- 检查SSL证书是否有效

## 6. 生产环境配置

### 6.1 安全配置
- 将私钥存储在环境变量中
- 使用配置中心管理敏感信息
- 定期轮换密钥

### 6.2 监控配置
- 配置API调用监控
- 设置错误告警
- 记录API调用日志

### 6.3 性能优化
- 配置连接池
- 设置合理的超时时间
- 实现重试机制

## 7. 费用说明

### 7.1 计费方式
- 按调用次数计费
- 每次调用约0.01元
- 支持包年包月套餐

### 7.2 免费额度
- 新用户通常有免费调用额度
- 具体额度以开放平台为准

## 8. 技术支持

### 8.1 官方文档
- 接口文档：https://opendocs.alipay.com/open/0bk0tu
- SDK文档：https://opendocs.alipay.com/open/54/104506

### 8.2 技术支持
- 钉钉群：107040000958
- 客服电话：400-758-5858
- 开放社区：https://openclub.alipay.com/
