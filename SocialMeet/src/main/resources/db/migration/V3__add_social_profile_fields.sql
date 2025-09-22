-- 添加社交应用用户资料字段
-- 版本: V3
-- 描述: 为用户表添加主流社交应用的用户资料字段

-- 添加真实姓名字段
ALTER TABLE users ADD COLUMN real_name VARCHAR(50);

-- 添加星座字段
ALTER TABLE users ADD COLUMN zodiac_sign VARCHAR(20);

-- 添加职业字段
ALTER TABLE users ADD COLUMN occupation VARCHAR(100);

-- 添加情感状态字段
ALTER TABLE users ADD COLUMN relationship_status VARCHAR(50);

-- 添加居住情况字段
ALTER TABLE users ADD COLUMN residence_status VARCHAR(50);

-- 添加购房情况字段
ALTER TABLE users ADD COLUMN house_ownership BOOLEAN DEFAULT FALSE;

-- 添加购车情况字段
ALTER TABLE users ADD COLUMN car_ownership BOOLEAN DEFAULT FALSE;

-- 添加兴趣爱好字段
ALTER TABLE users ADD COLUMN hobbies VARCHAR(500);

-- 添加掌握语言字段（JSON字符串）
ALTER TABLE users ADD COLUMN languages VARCHAR(200);

-- 添加血型字段
ALTER TABLE users ADD COLUMN blood_type VARCHAR(10);

-- 添加吸烟情况字段
ALTER TABLE users ADD COLUMN smoking BOOLEAN DEFAULT FALSE;

-- 添加饮酒情况字段
ALTER TABLE users ADD COLUMN drinking BOOLEAN DEFAULT FALSE;

-- 添加兴趣标签字段（JSON字符串）
ALTER TABLE users ADD COLUMN tags VARCHAR(1000);

-- 添加最后登录时间字段
ALTER TABLE users ADD COLUMN last_login_at TIMESTAMP;

-- 添加注释
COMMENT ON COLUMN users.real_name IS '真实姓名';
COMMENT ON COLUMN users.zodiac_sign IS '星座';
COMMENT ON COLUMN users.occupation IS '职业';
COMMENT ON COLUMN users.relationship_status IS '情感状态';
COMMENT ON COLUMN users.residence_status IS '居住情况';
COMMENT ON COLUMN users.house_ownership IS '是否购房';
COMMENT ON COLUMN users.car_ownership IS '是否购车';
COMMENT ON COLUMN users.hobbies IS '兴趣爱好';
COMMENT ON COLUMN users.languages IS '掌握语言（JSON字符串）';
COMMENT ON COLUMN users.blood_type IS '血型';
COMMENT ON COLUMN users.smoking IS '是否吸烟';
COMMENT ON COLUMN users.drinking IS '是否饮酒';
COMMENT ON COLUMN users.tags IS '兴趣标签（JSON字符串）';
COMMENT ON COLUMN users.last_login_at IS '最后登录时间';
