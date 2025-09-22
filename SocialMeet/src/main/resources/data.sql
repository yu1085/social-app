-- 修复 device_tokens 表中的无效日期时间值
-- 这个脚本会在应用程序启动时自动执行

-- 修复 created_at 字段的无效值
UPDATE device_tokens 
SET created_at = NOW() 
WHERE created_at = '0000-00-00 00:00:00' OR created_at = '0000-00-00 00:00:00.000000' OR created_at IS NULL;

-- 修复 updated_at 字段的无效值
UPDATE device_tokens 
SET updated_at = NOW() 
WHERE updated_at = '0000-00-00 00:00:00' OR updated_at = '0000-00-00 00:00:00.000000' OR updated_at IS NULL;
