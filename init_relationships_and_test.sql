-- 初始化用户关系表并添加测试数据
USE socialmeet;

-- 显示当前数据库
SELECT DATABASE() AS 'Current Database';

-- 执行建表SQL
SOURCE backend/database/create_user_relationships_table.sql;

-- 验证表是否创建成功
SHOW TABLES LIKE 'user_relationships';

-- 显示表结构
DESCRIBE user_relationships;

-- 提示
SELECT '✅ 用户关系表创建成功！现在可以启动后端服务器测试API了' AS 'Status';
SELECT 'ℹ️  提示：' AS '', '使用 test_relationships_api.ps1 测试所有关系功能' AS 'Next Step';
