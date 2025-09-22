-- 创建第三方认证表
CREATE TABLE third_party_auths (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    auth_id VARCHAR(64) NOT NULL UNIQUE COMMENT '认证ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    auth_type VARCHAR(20) NOT NULL COMMENT '认证类型：ALIPAY, WECHAT',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '认证状态：PENDING, SUCCESS, FAILED, CANCELLED',
    real_name VARCHAR(50) COMMENT '真实姓名',
    id_card_number VARCHAR(20) COMMENT '身份证号',
    phone_number VARCHAR(20) COMMENT '手机号',
    auth_url TEXT COMMENT '认证URL',
    qr_code TEXT COMMENT '二维码',
    redirect_url VARCHAR(500) COMMENT '回调URL',
    extra_data TEXT COMMENT '额外数据',
    reject_reason VARCHAR(500) COMMENT '拒绝原因',
    message VARCHAR(500) COMMENT '状态消息',
    third_party_id VARCHAR(100) COMMENT '第三方平台认证ID',
    third_party_response TEXT COMMENT '第三方平台响应数据',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    expires_at TIMESTAMP COMMENT '过期时间',
    completed_at TIMESTAMP COMMENT '完成时间',
    
    INDEX idx_user_id (user_id),
    INDEX idx_auth_type (auth_type),
    INDEX idx_status (status),
    INDEX idx_third_party_id (third_party_id),
    INDEX idx_expires_at (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='第三方认证表';
