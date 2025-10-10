# 代理配置工具包

本工具包提供了完整的代理配置解决方案，包括终端代理设置和PAC规则配置。

## 文件说明

### 主要脚本
- `quick_proxy_setup.ps1` - 一键配置所有代理设置（推荐）
- `setup_proxy.ps1` - 基础代理环境变量设置
- `setup_proxy.bat` - Windows批处理版本
- `verify_proxy.ps1` - 验证代理配置是否生效
- `remove_proxy.ps1` - 移除所有代理配置

### 配置文件
- `pac_rules.txt` - PAC规则域名列表
- `proxy_config_guide.md` - 详细配置指南

## 快速开始

### 1. 一键配置（推荐）
```powershell
# 以管理员身份运行PowerShell
.\quick_proxy_setup.ps1
```

### 2. 基础配置
```powershell
# 设置代理环境变量
.\setup_proxy.ps1

# 验证配置
.\verify_proxy.ps1
```

### 3. 移除配置
```powershell
# 移除所有代理配置
.\remove_proxy.ps1
```

## 配置的域名

以下域名将通过代理访问：
- anthropic.com
- api.anthropic.com
- claude.ai
- openai.com
- cursor.so
- api.cursor.so
- google.com

## 代理软件配置

### Clash配置示例
在Clash的规则部分添加：
```
DOMAIN-SUFFIX,anthropic.com,PROXY
DOMAIN-SUFFIX,api.anthropic.com,PROXY
DOMAIN-SUFFIX,claude.ai,PROXY
DOMAIN-SUFFIX,openai.com,PROXY
DOMAIN-SUFFIX,cursor.so,PROXY
DOMAIN-SUFFIX,api.cursor.so,PROXY
DOMAIN-SUFFIX,google.com,PROXY
```

### V2Ray配置示例
在路由规则中添加上述域名。

## 注意事项

1. **代理端口**：默认使用 `127.0.0.1:1087`，如需修改请编辑脚本
2. **管理员权限**：建议以管理员身份运行脚本
3. **代理软件**：确保代理软件正在运行
4. **重启终端**：配置完成后建议重启终端

## 故障排除

### 连接失败
1. 检查代理软件是否运行
2. 验证端口号是否正确
3. 检查防火墙设置

### 配置不生效
1. 重启终端
2. 检查环境变量设置
3. 验证代理软件规则配置

## 支持的工具

本工具包支持以下开发工具的代理配置：
- Git
- NPM
- Maven
- PowerShell
- 命令行工具

## 联系支持

如有问题，请检查：
1. 代理软件是否正常运行
2. 端口配置是否正确
3. 防火墙是否阻止连接
