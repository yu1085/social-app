-- 创建身份证二要素核验表
CREATE TABLE id_card_verify (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    verify_id VARCHAR(64) NOT NULL UNIQUE COMMENT '认证ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    cert_name VARCHAR(32) NOT NULL COMMENT '真实姓名',
    cert_no VARCHAR(32) NOT NULL COMMENT '身份证号码',
    status VARCHAR(20) NOT NULL COMMENT '认证状态: PENDING, SUCCESS, FAILED',
    message VARCHAR(255) COMMENT '认证消息',
    reject_reason VARCHAR(255) COMMENT '拒绝原因',
    certify_id VARCHAR(64) COMMENT '支付宝认证单据号',
    alipay_response TEXT COMMENT '支付宝响应数据',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    completed_at TIMESTAMP NULL COMMENT '完成时间',
    expires_at TIMESTAMP NULL COMMENT '过期时间',
    INDEX idx_user_id (user_id),
    INDEX idx_verify_id (verify_id),
    INDEX idx_certify_id (certify_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='身份证二要素核验表';
