# 系统级代理配置工具包

本工具包提供了完整的系统级代理配置解决方案，适用于整个计算机的代理设置。

## 文件说明

### 主要脚本
- `system_proxy_setup.ps1` - 系统级代理配置脚本（**推荐使用**）
- `system_proxy_remove.ps1` - 系统级代理移除脚本
- `system_proxy_verify.ps1` - 系统级代理验证脚本

### 项目级脚本（可选）
- `quick_proxy_setup.ps1` - 项目级一键配置
- `setup_proxy.ps1` - 项目级基础配置
- `verify_proxy.ps1` - 项目级验证

## 快速开始

### 1. 系统级配置（推荐）
```powershell
# 以管理员身份运行PowerShell
.\system_proxy_setup.ps1
```

### 2. 验证配置
```powershell
# 验证系统级代理配置
.\system_proxy_verify.ps1
```

### 3. 移除配置
```powershell
# 以管理员身份运行PowerShell
.\system_proxy_remove.ps1
```

## 系统级配置内容

### 环境变量配置
- **系统级环境变量**：`http_proxy`, `https_proxy`, `HTTP_PROXY`, `HTTPS_PROXY`
- **用户级环境变量**：同上
- **当前会话环境变量**：同上
- **绕过代理**：`no_proxy`, `NO_PROXY` (localhost, 127.0.0.1)

### 应用程序配置
- **Windows系统代理**：Internet设置中的代理配置
- **Git全局配置**：`git config --global`
- **NPM全局配置**：`npm config set`
- **Yarn全局配置**：`yarn config set`
- **Maven全局配置**：`settings.xml`文件
- **Docker代理配置**：`~/.docker/config.json`

## 配置的域名

以下域名将通过代理访问：
- anthropic.com
- api.anthropic.com
- claude.ai
- openai.com
- cursor.so
- api.cursor.so
- google.com

## 使用步骤

### 第一步：准备代理软件
1. 确保您的代理软件正在运行
2. 确认代理端口为 `127.0.0.1:1087`
3. 在代理软件中添加上述域名规则

### 第二步：运行配置脚本
```powershell
# 以管理员身份运行PowerShell
.\system_proxy_setup.ps1
```

### 第三步：验证配置
```powershell
.\system_proxy_verify.ps1
```

### 第四步：重启计算机
建议重启计算机以确保所有配置完全生效。

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

## 验证清单

运行 `system_proxy_verify.ps1` 后，检查以下项目：

### ✅ 环境变量
- [ ] 系统级环境变量已设置
- [ ] 用户级环境变量已设置
- [ ] 当前会话环境变量已设置

### ✅ 应用程序配置
- [ ] Windows系统代理已启用
- [ ] Git全局代理已配置
- [ ] NPM全局代理已配置
- [ ] Yarn全局代理已配置
- [ ] Maven全局代理已配置
- [ ] Docker代理已配置

### ✅ 网络连接
- [ ] Google连接成功
- [ ] Anthropic连接成功
- [ ] Cursor连接成功
- [ ] OpenAI连接成功

### ✅ 代理软件
- [ ] 端口1087有活动连接
- [ ] 代理软件正在运行

## 故障排除

### 常见问题

#### 1. 配置不生效
- **原因**：需要管理员权限
- **解决**：以管理员身份运行PowerShell

#### 2. 网络连接失败
- **原因**：代理软件未运行
- **解决**：启动代理软件并检查端口

#### 3. 部分应用无法使用代理
- **原因**：应用需要单独配置
- **解决**：检查应用特定的代理设置

#### 4. 环境变量不生效
- **原因**：需要重启终端或计算机
- **解决**：重启终端或计算机

### 调试步骤

1. **检查代理软件状态**
   ```powershell
   netstat -an | findstr :1087
   ```

2. **检查环境变量**
   ```powershell
   echo $env:http_proxy
   echo $env:https_proxy
   ```

3. **测试网络连接**
   ```powershell
   curl -I https://www.google.com
   ```

4. **运行验证脚本**
   ```powershell
   .\system_proxy_verify.ps1
   ```

## 注意事项

### ⚠️ 重要提醒
1. **管理员权限**：系统级配置需要管理员权限
2. **代理软件**：确保代理软件正在运行
3. **端口配置**：默认使用1087端口，如需修改请编辑脚本
4. **重启计算机**：配置完成后建议重启计算机
5. **备份配置**：移除脚本会自动备份原配置文件

### 🔧 自定义配置
如需修改端口或其他设置，请编辑脚本中的相应部分：
- 端口号：`127.0.0.1:1087`
- 绕过代理：`localhost,127.0.0.1`
- 代理域名：在代理软件中配置

## 支持的工具

本工具包支持以下开发工具的代理配置：
- Windows系统代理
- Git
- NPM
- Yarn
- Maven
- Docker
- PowerShell
- 命令行工具

## 联系支持

如有问题，请：
1. 运行 `system_proxy_verify.ps1` 检查配置状态
2. 检查代理软件是否正常运行
3. 验证端口配置是否正确
4. 确认防火墙设置
