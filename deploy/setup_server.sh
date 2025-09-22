#!/bin/bash

# 云服务器初始化设置脚本
# 适用于 Ubuntu Server 22.04 LTS

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

print_message() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_header() {
    echo -e "${BLUE}================================${NC}"
    echo -e "${BLUE}  云服务器初始化设置${NC}"
    echo -e "${BLUE}================================${NC}"
}

# 检查是否为root用户
check_root() {
    if [ "$EUID" -eq 0 ]; then
        print_error "请不要使用root用户运行此脚本"
        print_message "请使用: sudo -u ubuntu $0"
        exit 1
    fi
}

# 更新系统
update_system() {
    print_message "更新系统包..."
    sudo apt update
    sudo apt upgrade -y
    print_message "系统更新完成"
}

# 安装基础工具
install_basic_tools() {
    print_message "安装基础工具..."
    sudo apt install -y \
        curl \
        wget \
        git \
        vim \
        unzip \
        htop \
        tree \
        net-tools \
        ufw \
        fail2ban
    print_message "基础工具安装完成"
}

# 安装Docker
install_docker() {
    print_message "安装Docker..."
    
    # 检查Docker是否已安装
    if command -v docker &> /dev/null; then
        print_warning "Docker已安装，跳过安装步骤"
        return
    fi
    
    # 安装Docker
    curl -fsSL https://get.docker.com -o get-docker.sh
    sudo sh get-docker.sh
    rm get-docker.sh
    
    # 将当前用户添加到docker组
    sudo usermod -aG docker $USER
    
    print_message "Docker安装完成"
}

# 安装Docker Compose
install_docker_compose() {
    print_message "安装Docker Compose..."
    
    # 检查Docker Compose是否已安装
    if command -v docker-compose &> /dev/null; then
        print_warning "Docker Compose已安装，跳过安装步骤"
        return
    fi
    
    # 获取最新版本号
    COMPOSE_VERSION=$(curl -s https://api.github.com/repos/docker/compose/releases/latest | grep 'tag_name' | cut -d\" -f4)
    
    # 下载并安装Docker Compose
    sudo curl -L "https://github.com/docker/compose/releases/download/${COMPOSE_VERSION}/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
    sudo chmod +x /usr/local/bin/docker-compose
    
    print_message "Docker Compose安装完成"
}

# 配置防火墙
configure_firewall() {
    print_message "配置防火墙..."
    
    # 重置防火墙规则
    sudo ufw --force reset
    
    # 设置默认策略
    sudo ufw default deny incoming
    sudo ufw default allow outgoing
    
    # 允许SSH
    sudo ufw allow 22/tcp
    
    # 允许HTTP和HTTPS
    sudo ufw allow 80/tcp
    sudo ufw allow 443/tcp
    
    # 允许后端服务端口（可选，用于调试）
    sudo ufw allow 8080/tcp
    
    # 启用防火墙
    sudo ufw --force enable
    
    print_message "防火墙配置完成"
}

# 配置SSH安全
configure_ssh() {
    print_message "配置SSH安全..."
    
    # 备份原始配置
    sudo cp /etc/ssh/sshd_config /etc/ssh/sshd_config.backup
    
    # 创建SSH配置
    sudo tee /etc/ssh/sshd_config.d/99-socialmeet.conf > /dev/null <<EOF
# SocialMeet SSH安全配置
Port 22
Protocol 2
PermitRootLogin no
PasswordAuthentication yes
PubkeyAuthentication yes
AuthorizedKeysFile .ssh/authorized_keys
MaxAuthTries 3
ClientAliveInterval 300
ClientAliveCountMax 2
X11Forwarding no
PrintMotd no
AcceptEnv LANG LC_*
Subsystem sftp /usr/lib/openssh/sftp-server
EOF
    
    # 重启SSH服务
    sudo systemctl restart ssh
    
    print_message "SSH安全配置完成"
}

# 配置时区
configure_timezone() {
    print_message "配置时区为Asia/Shanghai..."
    sudo timedatectl set-timezone Asia/Shanghai
    print_message "时区配置完成"
}

# 创建项目目录
create_project_directories() {
    print_message "创建项目目录..."
    
    mkdir -p ~/socialmeet
    mkdir -p ~/socialmeet/logs
    mkdir -p ~/socialmeet/backups
    mkdir -p ~/socialmeet/uploads
    mkdir -p ~/socialmeet/ssl
    
    print_message "项目目录创建完成"
}

# 配置系统监控
configure_monitoring() {
    print_message "配置系统监控..."
    
    # 安装htop
    sudo apt install -y htop iotop nethogs
    
    # 创建系统监控脚本
    cat > ~/monitor.sh << 'EOF'
#!/bin/bash
echo "=== 系统资源使用情况 ==="
echo "内存使用:"
free -h
echo ""
echo "磁盘使用:"
df -h
echo ""
echo "CPU使用:"
top -bn1 | grep "Cpu(s)"
echo ""
echo "Docker容器状态:"
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
echo ""
echo "网络连接:"
netstat -tlnp | grep -E ":(80|443|8080|3306|6379)"
EOF
    
    chmod +x ~/monitor.sh
    
    print_message "系统监控配置完成"
}

# 创建备份脚本
create_backup_script() {
    print_message "创建备份脚本..."
    
    cat > ~/backup.sh << 'EOF'
#!/bin/bash
BACKUP_DIR="~/socialmeet/backups/$(date +%Y%m%d_%H%M%S)"
mkdir -p $BACKUP_DIR

echo "开始备份..."

# 备份数据库
if docker ps | grep -q mysql; then
    echo "备份数据库..."
    docker-compose exec -T mysql mysqldump -u root -p${MYSQL_ROOT_PASSWORD:-SocialMeet2024!} socialmeet > $BACKUP_DIR/database.sql
fi

# 备份上传文件
if [ -d "uploads" ]; then
    echo "备份上传文件..."
    cp -r uploads $BACKUP_DIR/
fi

# 备份配置文件
echo "备份配置文件..."
cp docker-compose.yml $BACKUP_DIR/
cp .env $BACKUP_DIR/

echo "备份完成: $BACKUP_DIR"
EOF
    
    chmod +x ~/backup.sh
    
    print_message "备份脚本创建完成"
}

# 设置定时任务
setup_cron() {
    print_message "设置定时任务..."
    
    # 创建crontab文件
    cat > ~/crontab.tmp << 'EOF'
# 每天凌晨2点备份数据
0 2 * * * cd ~/socialmeet && ./backup.sh

# 每周日凌晨3点清理旧日志
0 3 * * 0 find ~/socialmeet/logs -name "*.log" -mtime +7 -delete

# 每天检查服务状态
0 */6 * * * cd ~/socialmeet && docker-compose ps | grep -q "Up" || echo "服务异常" | mail -s "SocialMeet服务异常" admin@example.com
EOF
    
    # 安装crontab
    crontab ~/crontab.tmp
    rm ~/crontab.tmp
    
    print_message "定时任务设置完成"
}

# 显示完成信息
show_completion_info() {
    print_header
    print_message "云服务器初始化完成！"
    echo ""
    print_message "下一步操作："
    echo "1. 重新登录以应用Docker组权限: exit && ssh ubuntu@119.45.174.10"
    echo "2. 上传项目文件到服务器"
    echo "3. 进入项目目录: cd ~/socialmeet"
    echo "4. 运行部署脚本: ./deploy.sh deploy prod"
    echo ""
    print_message "常用命令："
    echo "- 查看系统状态: ~/monitor.sh"
    echo "- 手动备份: ~/backup.sh"
    echo "- 查看定时任务: crontab -l"
    echo ""
    print_warning "请记住修改默认密码并配置SSH密钥！"
}

# 主函数
main() {
    print_header
    
    # 检查用户权限
    check_root
    
    # 执行初始化步骤
    update_system
    install_basic_tools
    install_docker
    install_docker_compose
    configure_firewall
    configure_ssh
    configure_timezone
    create_project_directories
    configure_monitoring
    create_backup_script
    setup_cron
    
    # 显示完成信息
    show_completion_info
}

# 执行主函数
main "$@"
