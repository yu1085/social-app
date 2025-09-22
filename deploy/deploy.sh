#!/bin/bash

# SocialMeet 云服务器部署脚本
# 使用方法: ./deploy.sh [环境] [操作]
# 环境: dev|prod
# 操作: build|deploy|restart|stop|logs|status

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 配置变量
PROJECT_NAME="socialmeet"
DOCKER_COMPOSE_FILE="docker-compose.yml"
ENV_FILE=".env"

# 打印带颜色的消息
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
    echo -e "${BLUE}  SocialMeet 部署脚本${NC}"
    echo -e "${BLUE}================================${NC}"
}

# 检查Docker是否安装
check_docker() {
    if ! command -v docker &> /dev/null; then
        print_error "Docker 未安装，请先安装 Docker"
        exit 1
    fi
    
    if ! command -v docker-compose &> /dev/null; then
        print_error "Docker Compose 未安装，请先安装 Docker Compose"
        exit 1
    fi
    
    print_message "Docker 环境检查通过"
}

# 检查环境文件
check_env_file() {
    if [ ! -f "$ENV_FILE" ]; then
        print_warning "环境文件 $ENV_FILE 不存在，从示例文件创建"
        if [ -f "env.example" ]; then
            cp env.example $ENV_FILE
            print_message "请编辑 $ENV_FILE 文件，配置正确的环境变量"
            print_warning "特别是数据库密码和JWT密钥！"
            exit 1
        else
            print_error "示例环境文件 env.example 也不存在"
            exit 1
        fi
    fi
    print_message "环境文件检查通过"
}

# 构建Spring Boot应用
build_backend() {
    print_message "开始构建 Spring Boot 后端应用..."
    
    cd ../SocialMeet
    
    # 检查Gradle Wrapper
    if [ ! -f "./gradlew" ]; then
        print_error "Gradle Wrapper 不存在，请确保在正确的项目目录中"
        exit 1
    fi
    
    # 给gradlew执行权限
    chmod +x ./gradlew
    
    # 构建应用
    print_message "执行 Gradle 构建..."
    ./gradlew clean build -x test
    
    # 检查构建结果
    if [ ! -f "build/libs/*.jar" ]; then
        print_error "构建失败，未找到生成的jar文件"
        exit 1
    fi
    
    print_message "Spring Boot 应用构建完成"
    cd ../deploy
}

# 构建Docker镜像
build_docker() {
    print_message "开始构建 Docker 镜像..."
    
    # 构建后端镜像
    docker-compose -f $DOCKER_COMPOSE_FILE build backend
    
    print_message "Docker 镜像构建完成"
}

# 部署服务
deploy_services() {
    print_message "开始部署服务..."
    
    # 停止现有服务
    docker-compose -f $DOCKER_COMPOSE_FILE down
    
    # 启动服务
    docker-compose -f $DOCKER_COMPOSE_FILE up -d
    
    # 等待服务启动
    print_message "等待服务启动..."
    sleep 30
    
    # 检查服务状态
    check_services_health
}

# 检查服务健康状态
check_services_health() {
    print_message "检查服务健康状态..."
    
    # 检查MySQL
    if docker-compose -f $DOCKER_COMPOSE_FILE exec mysql mysqladmin ping -h localhost --silent; then
        print_message "MySQL 服务正常"
    else
        print_error "MySQL 服务异常"
    fi
    
    # 检查Redis
    if docker-compose -f $DOCKER_COMPOSE_FILE exec redis redis-cli ping | grep -q PONG; then
        print_message "Redis 服务正常"
    else
        print_error "Redis 服务异常"
    fi
    
    # 检查后端服务
    if curl -f http://localhost:8080/api/health > /dev/null 2>&1; then
        print_message "后端服务正常"
    else
        print_warning "后端服务可能还在启动中，请稍后检查"
    fi
    
    # 检查Nginx
    if docker-compose -f $DOCKER_COMPOSE_FILE exec nginx nginx -t > /dev/null 2>&1; then
        print_message "Nginx 配置正常"
    else
        print_error "Nginx 配置有误"
    fi
}

# 重启服务
restart_services() {
    print_message "重启服务..."
    docker-compose -f $DOCKER_COMPOSE_FILE restart
    print_message "服务重启完成"
}

# 停止服务
stop_services() {
    print_message "停止服务..."
    docker-compose -f $DOCKER_COMPOSE_FILE down
    print_message "服务已停止"
}

# 查看日志
show_logs() {
    local service=${1:-""}
    if [ -n "$service" ]; then
        print_message "显示 $service 服务日志..."
        docker-compose -f $DOCKER_COMPOSE_FILE logs -f $service
    else
        print_message "显示所有服务日志..."
        docker-compose -f $DOCKER_COMPOSE_FILE logs -f
    fi
}

# 查看服务状态
show_status() {
    print_message "服务状态："
    docker-compose -f $DOCKER_COMPOSE_FILE ps
    
    print_message "资源使用情况："
    docker stats --no-stream
}

# 生成SSL证书（自签名）
generate_ssl_cert() {
    print_message "生成自签名SSL证书..."
    
    mkdir -p ssl
    
    # 生成私钥
    openssl genrsa -out ssl/key.pem 2048
    
    # 生成证书签名请求
    openssl req -new -key ssl/key.pem -out ssl/cert.csr -subj "/C=CN/ST=Beijing/L=Beijing/O=SocialMeet/OU=IT/CN=119.45.174.10"
    
    # 生成自签名证书
    openssl x509 -req -days 365 -in ssl/cert.csr -signkey ssl/key.pem -out ssl/cert.pem
    
    # 清理临时文件
    rm ssl/cert.csr
    
    print_message "SSL证书生成完成"
    print_warning "这是自签名证书，生产环境请使用正式的SSL证书"
}

# 备份数据
backup_data() {
    local backup_dir="backups/$(date +%Y%m%d_%H%M%S)"
    mkdir -p $backup_dir
    
    print_message "备份数据库..."
    docker-compose -f $DOCKER_COMPOSE_FILE exec mysql mysqldump -u root -p${MYSQL_ROOT_PASSWORD:-SocialMeet2024!} socialmeet > $backup_dir/database.sql
    
    print_message "备份上传文件..."
    if [ -d "uploads" ]; then
        cp -r uploads $backup_dir/
    fi
    
    print_message "数据备份完成: $backup_dir"
}

# 主函数
main() {
    print_header
    
    local action=${1:-"deploy"}
    local environment=${2:-"prod"}
    
    case $action in
        "build")
            check_docker
            check_env_file
            build_backend
            build_docker
            ;;
        "deploy")
            check_docker
            check_env_file
            build_backend
            build_docker
            deploy_services
            ;;
        "restart")
            restart_services
            ;;
        "stop")
            stop_services
            ;;
        "logs")
            show_logs $2
            ;;
        "status")
            show_status
            ;;
        "ssl")
            generate_ssl_cert
            ;;
        "backup")
            backup_data
            ;;
        *)
            echo "使用方法: $0 [操作] [环境]"
            echo "操作: build|deploy|restart|stop|logs|status|ssl|backup"
            echo "环境: dev|prod"
            echo ""
            echo "示例:"
            echo "  $0 deploy prod    # 部署到生产环境"
            echo "  $0 build dev      # 构建开发环境"
            echo "  $0 logs backend   # 查看后端日志"
            echo "  $0 status         # 查看服务状态"
            echo "  $0 ssl            # 生成SSL证书"
            echo "  $0 backup         # 备份数据"
            exit 1
            ;;
    esac
}

# 执行主函数
main "$@"
