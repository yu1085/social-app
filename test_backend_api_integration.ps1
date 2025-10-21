# 后端API集成测试脚本
# 测试"设置来电及价格"功能的后端API集成

Write-Host "=== 后端API集成测试 ===" -ForegroundColor Green

Write-Host "`n1. 数据加载流程:" -ForegroundColor Yellow
Write-Host "   - ProfileViewModel初始化时自动调用loadUserSettings()"
Write-Host "   - 调用GET /api/profile/settings API获取用户设置"
Write-Host "   - 解析响应数据更新UI状态"
Write-Host "   - 如果API失败，使用默认值"

Write-Host "`n2. 设置更新流程:" -ForegroundColor Yellow
Write-Host "   - 用户点击开关时调用updateSettingsToBackend()"
Write-Host "   - 构建UserSettingsDTO对象"
Write-Host "   - 调用PUT /api/profile/settings API更新设置"
Write-Host "   - 成功后更新本地状态"

Write-Host "`n3. 价格更新流程:" -ForegroundColor Yellow
Write-Host "   - 用户在价格设置页面点击保存"
Write-Host "   - 调用savePriceSettings()方法"
Write-Host "   - 构建UserSettingsDTO对象包含价格信息"
Write-Host "   - 调用PUT /api/profile/settings API更新价格"
Write-Host "   - 显示保存结果并返回"

Write-Host "`n4. API端点使用:" -ForegroundColor Yellow
Write-Host "   - GET /api/profile/settings - 获取用户设置"
Write-Host "   - PUT /api/profile/settings - 更新用户设置"
Write-Host "   - 使用JWT认证头进行身份验证"

Write-Host "`n5. 错误处理:" -ForegroundColor Yellow
Write-Host "   - 网络异常时显示错误日志"
Write-Host "   - API失败时使用默认设置"
Write-Host "   - 价格保存失败时显示Toast提示"

Write-Host "`n6. 数据同步:" -ForegroundColor Yellow
Write-Host "   - 所有设置变更都同步到后端"
Write-Host "   - 本地状态与后端数据保持一致"
Write-Host "   - 支持离线时的默认值显示"

Write-Host "`n=== 测试步骤 ===" -ForegroundColor Green
Write-Host "`n1. 启动后端服务:"
Write-Host "   cd backend"
Write-Host "   mvn spring-boot:run"

Write-Host "`n2. 启动Android应用:"
Write-Host "   - 进入'我的'页面"
Write-Host "   - 查看设置是否正确加载"

Write-Host "`n3. 测试设置更新:"
Write-Host "   - 切换语音接听开关"
Write-Host "   - 切换视频接听开关"
Write-Host "   - 切换私信收费开关"
Write-Host "   - 检查后端数据库是否更新"

Write-Host "`n4. 测试价格设置:"
Write-Host "   - 点击价格区域进入设置页面"
Write-Host "   - 修改语音和视频通话价格"
Write-Host "   - 点击保存按钮"
Write-Host "   - 检查价格是否正确更新"

Write-Host "`n5. 验证数据持久化:"
Write-Host "   - 重启应用"
Write-Host "   - 检查设置是否正确恢复"
Write-Host "   - 检查价格是否正确显示"

Write-Host "`n=== 预期结果 ===" -ForegroundColor Green
Write-Host "✓ 应用启动时自动加载用户设置"
Write-Host "✓ 开关切换时实时更新到后端"
Write-Host "✓ 价格设置保存到后端数据库"
Write-Host "✓ 数据在应用重启后正确恢复"
Write-Host "✓ 网络异常时有适当的错误处理"

Write-Host "`n=== 测试完成 ===" -ForegroundColor Green












