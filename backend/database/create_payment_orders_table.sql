-- 创建支付订单表
CREATE TABLE IF NOT EXISTS payment_orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id VARCHAR(64) NOT NULL UNIQUE COMMENT '订单ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    package_id VARCHAR(32) NOT NULL COMMENT '套餐ID',
    coins BIGINT NOT NULL COMMENT '金币数量',
    amount DECIMAL(10,2) NOT NULL COMMENT '支付金额',
    payment_method VARCHAR(16) NOT NULL COMMENT '支付方式',
    alipay_trade_no VARCHAR(64) NULL COMMENT '支付宝交易号',
    alipay_out_trade_no VARCHAR(64) NULL COMMENT '支付宝商户订单号',
    status VARCHAR(16) NOT NULL DEFAULT 'pending' COMMENT '订单状态',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    paid_at TIMESTAMP NULL COMMENT '支付时间',
    expired_at TIMESTAMP NULL COMMENT '过期时间',
    callback_data TEXT NULL COMMENT '回调数据',
    description VARCHAR(255) NULL COMMENT '订单描述',
    transaction_id VARCHAR(64) NULL COMMENT '交易ID',
    failure_reason VARCHAR(500) NULL COMMENT '失败原因',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 创建索引
CREATE INDEX idx_payment_orders_user_id ON payment_orders(user_id);
CREATE INDEX idx_payment_orders_order_id ON payment_orders(order_id);
CREATE INDEX idx_payment_orders_alipay_trade_no ON payment_orders(alipay_trade_no);
CREATE INDEX idx_payment_orders_alipay_out_trade_no ON payment_orders(alipay_out_trade_no);
CREATE INDEX idx_payment_orders_status ON payment_orders(status);
CREATE INDEX idx_payment_orders_created_at ON payment_orders(created_at);
