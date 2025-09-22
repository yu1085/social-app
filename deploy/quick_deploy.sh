#!/bin/bash

# SocialMeet 快速部署脚本
# 一键部署到云服务器

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
    echo -e "${BLUE}  SocialMeet 快速部署${NC}"
    echo -e "${BLUE}================================${NC}"
}

# 服务器信息
SERVER_IP="119.45.174.10"
SERVER_USER="ubuntu"
SERVER_PASSWORD="Q4!zVBTL5^*p)tkb"

# 检查本地环境
check_local_environment() {
    print_message "检查本地环境..."
    
    # 检查必要工具
    if ! command -v ssh &> /dev/null; then
        print_error "SSH客户端未安装"
        exit 1
    fi
    
    if ! command -v scp &> /dev/null; then
        print_error "SCP客户端未安装"
        exit 1
    fi
    
    # 检查项目文件
    if [ ! -d "../SocialMeet" ]; then
        print_error "SocialMeet项目目录不存在"
        exit 1
    fi
    
    print_message "本地环境检查通过"
}

# 上传项目文件
upload_project() {
    print_message "上传项目文件到服务器..."
    
    # 创建临时目录
    TEMP_DIR=$(mktemp -d)
    
    # 复制项目文件
    cp -r ../SocialMeet $TEMP_DIR/
    cp -r . $TEMP_DIR/deploy/
    
    # 上传到服务器
    print_message "正在上传文件，请稍候..."
    scp -r $TEMP_DIR/* $SERVER_USER@$SERVER_IP:~/
    
    # 清理临时目录
    rm -rf $TEMP_DIR
    
    print_message "文件上传完成"
}

# 远程执行命令
remote_exec() {
    local command="$1"
    sshpass -p "$SERVER_PASSWORD" ssh -o StrictHostKeyChecking=no $SERVER_USER@$SERVER_IP "$command"
}

# 远程执行脚本
remote_exec_script() {
    local script="$1"
    sshpass -p "$SERVER_PASSWORD" ssh -o StrictHostKeyChecking=no $SERVER_USER@$SERVER_IP "bash -s" < "$script"
}

# 检查服务器连接
check_server_connection() {
    print_message "检查服务器连接..."
    
    if ! command -v sshpass &> /dev/null; then
        print_warning "sshpass未安装，将使用交互式SSH"
        return 0
    fi
    
    if ! remote_exec "echo '连接成功'" > /dev/null 2>&1; then
        print_error "无法连接到服务器 $SERVER_IP"
        print_message "请检查："
        print_message "1. 服务器IP地址是否正确"
        print_message "2. 服务器是否已启动"
        print_message "3. 网络连接是否正常"
        exit 1
    fi
    
    print_message "服务器连接正常"
}

# 安装sshpass（如果需要）
install_sshpass() {
    if ! command -v sshpass &> /dev/null; then
        print_message "安装sshpass..."
        
        if [[ "$OSTYPE" == "linux-gnu"* ]]; then
            if command -v apt-get &> /dev/null; then
                sudo apt-get update && sudo apt-get install -y sshpass
            elif command -v yum &> /dev/null; then
                sudo yum install -y sshpass
            elif command -v dnf &> /dev/null; then
                sudo dnf install -y sshpass
            else
                print_warning "无法自动安装sshpass，请手动安装"
                print_message "Ubuntu/Debian: sudo apt-get install sshpass"
                print_message "CentOS/RHEL: sudo yum install sshpass"
                return 1
            fi
        elif [[ "$OSTYPE" == "darwin"* ]]; then
            if command -v brew &> /dev/null; then
                brew install sshpass
            else
                print_warning "请先安装Homebrew，然后运行: brew install sshpass"
                return 1
            fi
        else
            print_warning "不支持的操作系统，请手动安装sshpass"
            return 1
        fi
    fi
}

# 初始化服务器
initialize_server() {
    print_message "初始化服务器环境..."
    
    # 上传并执行初始化脚本
    if [ -f "setup_server.sh" ]; then
        print_message "执行服务器初始化..."
        if command -v sshpass &> /dev/null; then
            remote_exec_script "setup_server.sh"
        else
            print_message "请手动执行以下命令："
            print_message "scp setup_server.sh $SERVER_USER@$SERVER_IP:~/"
            print_message "ssh $SERVER_USER@$SERVER_IP 'chmod +x setup_server.sh && ./setup_server.sh'"
        fi
    else
        print_warning "初始化脚本不存在，跳过服务器初始化"
    fi
}

# 部署应用
deploy_application() {
    print_message "部署应用到服务器..."
    
    # 进入部署目录并执行部署
    local deploy_commands="
        cd ~/deploy &&
        chmod +x deploy.sh &&
        chmod +x setup_server.sh &&
        ./deploy.sh deploy prod
    "
    
    if command -v sshpass &> /dev/null; then
        remote_exec "$deploy_commands"
    else
        print_message "请手动执行以下命令："
        print_message "ssh $SERVER_USER@$SERVER_IP"
        print_message "cd ~/deploy"
        print_message "chmod +x deploy.sh"
        print_message "./deploy.sh deploy prod"
    fi
}

# 验证部署
verify_deployment() {
    print_message "验证部署结果..."
    
    # 等待服务启动
    print_message "等待服务启动..."
    sleep 30
    
    # 检查服务状态
    local check_commands="
        cd ~/deploy &&
        ./deploy.sh status
    "
    
    if command -v sshpass &> /dev/null; then
        remote_exec "$check_commands"
    fi
    
    # 测试API
    print_message "测试API接口..."
    if command -v curl &> /dev/null; then
        if curl -f http://$SERVER_IP/api/health > /dev/null 2>&1; then
            print_message "✅ API接口测试成功"
        else
            print_warning "⚠️ API接口测试失败，请检查服务状态"
        fi
        
        if curl -f https://$SERVER_IP/api/health > /dev/null 2>&1; then
            print_message "✅ HTTPS API接口测试成功"
        else
            print_warning "⚠️ HTTPS API接口测试失败，请检查SSL配置"
        fi
    else
        print_warning "curl未安装，跳过API测试"
    fi
}

# 显示部署结果
show_deployment_result() {
    print_header
    print_message "🎉 部署完成！"
    echo ""
    print_message "服务访问地址："
    echo "  - API接口: http://$SERVER_IP/api/"
    echo "  - HTTPS API: https://$SERVER_IP/api/"
    echo "  - 健康检查: https://$SERVER_IP/api/health"
    echo ""
    print_message "管理命令："
    echo "  - 连接服务器: ssh $SERVER_USER@$SERVER_IP"
    echo "  - 查看状态: cd ~/deploy && ./deploy.sh status"
    echo "  - 查看日志: cd ~/deploy && ./deploy.sh logs"
    echo "  - 重启服务: cd ~/deploy && ./deploy.sh restart"
    echo ""
    print_warning "重要提醒："
    echo "1. 请立即修改服务器登录密码"
    echo "2. 配置SSH密钥认证"
    echo "3. 更新Android应用中的API地址"
    echo "4. 定期备份数据"
    echo ""
    print_message "Android应用配置："
    echo "将API地址更新为: https://$SERVER_IP/api"
}

# 主函数
main() {
    print_header
    
    # 检查参数
    if [ "$1" = "--help" ] || [ "$1" = "-h" ]; then
        echo "使用方法: $0 [选项]"
        echo ""
        echo "选项:"
        echo "  --help, -h     显示帮助信息"
        echo "  --upload-only  仅上传文件，不执行部署"
        echo "  --deploy-only  仅执行部署，不上传文件"
        echo ""
        echo "示例:"
        echo "  $0                    # 完整部署"
        echo "  $0 --upload-only      # 仅上传文件"
        echo "  $0 --deploy-only      # 仅执行部署"
        exit 0
    fi
    
    # 检查本地环境
    check_local_environment
    
    # 安装sshpass（如果需要）
    install_sshpass
    
    # 检查服务器连接
    check_server_connection
    
    # 根据参数执行不同操作
    if [ "$1" = "--upload-only" ]; then
        upload_project
        print_message "文件上传完成"
        exit 0
    elif [ "$1" = "--deploy-only" ]; then
        initialize_server
        deploy_application
        verify_deployment
    else
        # 完整部署流程
        upload_project
        initialize_server
        deploy_application
        verify_deployment
    fi
    
    # 显示部署结果
    show_deployment_result
}

# 执行主函数
main "$@"
