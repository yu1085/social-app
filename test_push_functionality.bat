@echo off
echo ========================================
echo 测试JPush推送功能
echo ========================================

echo.
echo 当前Registration ID: 100d8559086375f6db0
echo.

echo 请按照以下步骤测试：
echo.
echo 1. 在极光控制台中：
echo    - 进入"推送管理" → "通知消息"
echo    - 选择"Registration ID"方式
echo    - 输入Registration ID: 100d8559086375f6db0
echo    - 输入标题: "测试推送"
echo    - 输入内容: "这是一条测试推送消息"
echo    - 点击"立即发送"
echo.
echo 2. 观察设备是否收到推送通知
echo.
echo 3. 如果收到通知，说明JPush配置成功
echo    如果没收到通知，需要进一步调试
echo.

echo ========================================
echo 重要说明：
echo - 即使日志显示"check config failed"
echo   推送功能可能仍然正常工作
echo - 请先测试实际推送功能
echo ========================================
pause
