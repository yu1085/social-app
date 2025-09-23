-- 为现有动态添加示例图片URL
UPDATE posts 
SET image_url = CASE 
    WHEN id % 3 = 0 THEN 'https://picsum.photos/400/300?random=1'
    WHEN id % 3 = 1 THEN 'https://picsum.photos/400/300?random=2'
    WHEN id % 3 = 2 THEN 'https://picsum.photos/400/300?random=3'
    ELSE NULL
END
WHERE image_url IS NULL;

-- 查看更新结果
SELECT id, content, image_url, created_at 
FROM posts 
ORDER BY created_at DESC 
LIMIT 10;
