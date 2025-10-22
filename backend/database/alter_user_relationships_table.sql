-- 修改 user_relationships 表结构,添加缺失的列

USE socialmeet;

-- 首先检查表结构
DESCRIBE user_relationships;

-- 确保 relationship_type 列长度足够 (先执行这个,因为它总是安全的)
ALTER TABLE user_relationships
MODIFY COLUMN relationship_type VARCHAR(50) NOT NULL COMMENT '关系类型: FRIEND(知友), LIKE(喜欢), INTIMATE(亲密), BLACKLIST(黑名单), SUBSCRIBE(订阅)';

-- 显示更新后的表结构
DESCRIBE user_relationships;

SELECT 'user_relationships 表的 relationship_type 列已更新为 VARCHAR(50)' AS message;
