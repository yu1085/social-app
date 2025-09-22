-- 修复 device_tokens 表中的无效日期时间值
-- 将 '0000-00-00 00:00:00' 替换为 NULL 或当前时间

-- 1. 首先备份数据（可选）
-- CREATE TABLE device_tokens_backup AS SELECT * FROM device_tokens;

-- 2. 修复 created_at 字段的无效值
UPDATE device_tokens 
SET created_at = NULL 
WHERE created_at = '0000-00-00 00:00:00' OR created_at = '0000-00-00 00:00:00.000000';

-- 3. 修复 updated_at 字段的无效值
UPDATE device_tokens 
SET updated_at = NULL 
WHERE updated_at = '0000-00-00 00:00:00' OR updated_at = '0000-00-00 00:00:00.000000';

-- 4. 如果 created_at 为 NULL，设置为当前时间
UPDATE device_tokens 
SET created_at = NOW() 
WHERE created_at IS NULL;

-- 5. 如果 updated_at 为 NULL，设置为当前时间
UPDATE device_tokens 
SET updated_at = NOW() 
WHERE updated_at IS NULL;

-- 6. 验证修复结果
SELECT 
    COUNT(*) as total_records,
    COUNT(created_at) as valid_created_at,
    COUNT(updated_at) as valid_updated_at,
    SUM(CASE WHEN created_at = '0000-00-00 00:00:00' THEN 1 ELSE 0 END) as invalid_created_at,
    SUM(CASE WHEN updated_at = '0000-00-00 00:00:00' THEN 1 ELSE 0 END) as invalid_updated_at
FROM device_tokens;
