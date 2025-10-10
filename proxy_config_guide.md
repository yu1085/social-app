# 代理配置指南

## 终端代理配置

### 方法一：使用PowerShell脚本（推荐）
1. 右键点击 `setup_proxy.ps1` 文件
2. 选择"使用PowerShell运行"
3. 或者在PowerShell中执行：`.\setup_proxy.ps1`

### 方法二：使用批处理文件
1. 双击 `setup_proxy.bat` 文件
2. 按照提示完成配置

### 方法三：手动设置
在PowerShell或命令提示符中执行：
```bash
export http_proxy=http://127.0.0.1:1087
export https_proxy=http://127.0.0.1:1087
```

## PAC规则配置

### 需要代理的域名列表
请将以下域名添加到您的代理软件中：

- anthropic.com
- api.anthropic.com
- claude.ai
- openai.com
- cursor.so
- api.cursor.so
- google.com

### 代理软件配置步骤

#### Clash配置
1. 打开Clash配置
2. 在"规则"部分添加：
```
DOMAIN-SUFFIX,anthropic.com,PROXY
DOMAIN-SUFFIX,api.anthropic.com,PROXY
DOMAIN-SUFFIX,claude.ai,PROXY
DOMAIN-SUFFIX,openai.com,PROXY
DOMAIN-SUFFIX,cursor.so,PROXY
DOMAIN-SUFFIX,api.cursor.so,PROXY
DOMAIN-SUFFIX,google.com,PROXY
```

#### V2Ray配置
1. 打开V2Ray配置
2. 在路由规则中添加上述域名

## 验证配置

### 检查代理是否生效
在PowerShell中执行：
```powershell
echo $env:http_proxy
echo $env:https_proxy
```

### 测试网络连接
```powershell
curl -I https://www.google.com
```

## 注意事项

1. 确保代理软件正在运行在 `127.0.0.1:1087`
2. 如果端口不同，请修改脚本中的端口号
3. 配置完成后需要重启终端或重新加载环境变量
4. 某些应用可能需要单独配置代理设置
