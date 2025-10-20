@echo off
echo 正在配置 VisionCoder 环境变量...
echo.

REM 设置用户环境变量
setx ANTHROPIC_API_KEY "sk-HYGIFuuPhwXmuQpzXh8mO898Uyo0WzesEf03E038D30e4d57AaFf532926B11e38"
setx ANTHROPIC_AUTH_TOKEN "sk-HYGIFuuPhwXmuQpzXh8mO898Uyo0WzesEf03E038D30e4d57AaFf532926B11e38"
setx ANTHROPIC_BASE_URL "https://coder.api.visioncoder.cn"
setx CLAUDE_CODE_DISABLE_NONESSENTIAL_TRAFFIC "1"
setx CLAUDE_MODEL "claude-sonnet-4-20250514"

echo.
echo 环境变量配置完成！
echo.
echo 请重新打开命令提示符或PowerShell以使环境变量生效。
echo.
echo 验证配置，请运行：
echo echo %%ANTHROPIC_API_KEY%%
echo claude --version
echo.
pause
