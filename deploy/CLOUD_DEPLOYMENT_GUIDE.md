# SocialMeet 云服务器部署指南

## 服务器信息
- **服务器IP**: 119.45.174.10
- **内网IP**: 10.206.0.5
- **操作系统**: Ubuntu Server 22.04 LTS 64位
- **用户名**: ubuntu
- **初始密码**: Q4!zVBTL5^*p)tkb
- **配置**: 2核/4GB/1Mbps
- **可用区**: 南京一区

## 部署步骤

### 1. 连接服务器

```bash
# 使用SSH连接服务器
ssh ubuntu@119.45.174.10

# 输入密码: Q4!zVBTL5^*p)tkb
```

### 2. 更新系统并安装必要软件

```bash
# 更新系统包
sudo apt update && sudo apt upgrade -y

# 安装必要工具
sudo apt install -y curl wget git vim unzip

# 安装Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
sudo usermod -aG docker ubuntu

# 安装Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# 重新登录以应用Docker组权限
exit
ssh ubuntu@119.45.174.10
```

### 3. 上传项目文件

#### 方法一：使用Git（推荐）
```bash
# 克隆项目（如果项目在Git仓库中）
git clone <your-repo-url> socialmeet
cd socialmeet
```

#### 方法二：使用SCP上传
```bash
# 在本地执行，上传项目文件
scp -r F:\MyApplication\deploy ubuntu@119.45.174.10:~/
scp -r F:\MyApplication\SocialMeet ubuntu@119.45.174.10:~/
```

### 4. 配置环境变量

```bash
# 进入部署目录
cd ~/deploy

# 复制环境配置文件
cp env.example .env

# 编辑环境配置
vim .env
```

**重要配置项**：
```bash
# 数据库配置
MYSQL_ROOT_PASSWORD=SocialMeet2024!@#
MYSQL_PASSWORD=SocialMeet2024!@#

# JWT配置（请生成强密码）
JWT_SECRET=your-super-secret-jwt-key-change-this-in-production-$(openssl rand -base64 32)

# 极光推送配置
JPUSH_APP_KEY=ff90a2867fcf541a3f3e8ed4
JPUSH_MASTER_SECRET=your-jpush-master-secret-here

# 服务器配置
SERVER_PORT=8080
SERVER_HOST=0.0.0.0

# 安全配置
CORS_ALLOWED_ORIGINS=https://119.45.174.10,http://119.45.174.10
```

### 5. 生成SSL证书

```bash
# 给部署脚本执行权限
chmod +x deploy.sh

# 生成自签名SSL证书
./deploy.sh ssl
```

### 6. 部署应用

```bash
# 完整部署（构建+部署）
./deploy.sh deploy prod

# 或者分步执行
./deploy.sh build prod
./deploy.sh deploy prod
```

### 7. 验证部署

```bash
# 检查服务状态
./deploy.sh status

# 查看服务日志
./deploy.sh logs

# 测试API接口
curl http://119.45.174.10/api/health
curl https://119.45.174.10/api/health
```

## 服务访问地址

- **API接口**: https://119.45.174.10/api/
- **健康检查**: https://119.45.174.10/api/health
- **文件上传**: https://119.45.174.10/uploads/

## 防火墙配置

```bash
# 开放必要端口
sudo ufw allow 22    # SSH
sudo ufw allow 80    # HTTP
sudo ufw allow 443   # HTTPS
sudo ufw allow 8080  # 后端服务（可选，用于调试）

# 启用防火墙
sudo ufw enable
```

## 常用管理命令

```bash
# 查看服务状态
./deploy.sh status

# 重启服务
./deploy.sh restart

# 停止服务
./deploy.sh stop

# 查看日志
./deploy.sh logs
./deploy.sh logs backend
./deploy.sh logs mysql
./deploy.sh logs nginx

# 备份数据
./deploy.sh backup

# 进入容器调试
docker-compose exec backend bash
docker-compose exec mysql mysql -u root -p
```

## 监控和维护

### 1. 系统监控
```bash
# 查看系统资源使用
htop
df -h
free -h

# 查看Docker容器状态
docker ps
docker stats
```

### 2. 日志管理
```bash
# 查看应用日志
tail -f logs/backend/application.log
tail -f logs/nginx/access.log
tail -f logs/nginx/error.log
```

### 3. 数据库维护
```bash
# 连接数据库
docker-compose exec mysql mysql -u root -p

# 备份数据库
./deploy.sh backup

# 恢复数据库
docker-compose exec mysql mysql -u root -p socialmeet < backups/20240101_120000/database.sql
```

## 安全建议

1. **修改默认密码**：立即修改服务器登录密码
2. **配置SSH密钥**：使用SSH密钥替代密码登录
3. **定期更新**：保持系统和软件包更新
4. **监控日志**：定期检查访问日志和错误日志
5. **备份数据**：定期备份数据库和重要文件
6. **SSL证书**：生产环境使用正式的SSL证书

## 故障排除

### 1. 服务无法启动
```bash
# 查看详细错误信息
docker-compose logs backend
docker-compose logs mysql
docker-compose logs nginx

# 检查端口占用
sudo netstat -tlnp | grep :8080
sudo netstat -tlnp | grep :3306
```

### 2. 数据库连接失败
```bash
# 检查数据库状态
docker-compose exec mysql mysqladmin ping -h localhost

# 检查数据库日志
docker-compose logs mysql
```

### 3. Nginx配置错误
```bash
# 测试Nginx配置
docker-compose exec nginx nginx -t

# 重新加载配置
docker-compose exec nginx nginx -s reload
```

## Android应用配置

更新Android应用中的API地址：

```kotlin
// ApiConfig.kt
object ApiConfig {
    private const val PROD_BASE_URL = "https://119.45.174.10/api"
    private const val DEV_BASE_URL = "http://10.0.2.2:8080/api"
    
    fun getBaseUrl(): String {
        return if (BuildConfig.DEBUG) DEV_BASE_URL else PROD_BASE_URL
    }
}
```

## 性能优化

1. **数据库优化**：配置MySQL参数，添加索引
2. **缓存策略**：使用Redis缓存热点数据
3. **CDN加速**：静态资源使用CDN
4. **负载均衡**：多实例部署时使用负载均衡
5. **监控告警**：配置系统监控和告警

现在您的SocialMeet应用已经成功部署到云服务器上了！🎉
