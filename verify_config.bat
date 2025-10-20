@echo off
echo 验证 VisionCoder 环境变量配置...
echo.

echo API密钥: %ANTHROPIC_API_KEY%
echo 认证令牌: %ANTHROPIC_AUTH_TOKEN%
echo API地址: %ANTHROPIC_BASE_URL%
echo 禁用非必要流量: %CLAUDE_CODE_DISABLE_NONESSENTIAL_TRAFFIC%
echo 模型: %CLAUDE_MODEL%

echo.
echo 测试 Claude Code...
claude --version

echo.
echo 配置验证完成！
pause

