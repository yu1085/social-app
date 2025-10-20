-- 更新transactions表结构
ALTER TABLE transactions MODIFY COLUMN transaction_type VARCHAR(50) NOT NULL;
ALTER TABLE transactions MODIFY COLUMN coin_source VARCHAR(50) NOT NULL;
ALTER TABLE transactions MODIFY COLUMN status VARCHAR(20) NOT NULL DEFAULT 'SUCCESS';
ALTER TABLE transactions MODIFY COLUMN order_id VARCHAR(100);
ALTER TABLE transactions MODIFY COLUMN description TEXT;
