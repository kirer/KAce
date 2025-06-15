-- 创建系统配置表
CREATE TABLE IF NOT EXISTS system_configs (
    id BIGSERIAL PRIMARY KEY,
    key VARCHAR(255) NOT NULL UNIQUE,
    value TEXT NOT NULL,
    type VARCHAR(50) NOT NULL,
    description TEXT DEFAULT '',
    category VARCHAR(100) DEFAULT 'DEFAULT',
    editable BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

-- 创建默认系统配置
INSERT INTO system_configs (key, value, type, description, category, editable, created_at, updated_at)
VALUES
    ('system.name', 'KAce', 'STRING', '系统名称', 'SYSTEM', false, NOW(), NOW()),
    ('system.version', '1.0.0', 'STRING', '系统版本', 'SYSTEM', false, NOW(), NOW()),
    ('system.description', 'KAce平台 - 基于Kotlin Multiplatform的跨平台CMS管理系统', 'STRING', '系统描述', 'SYSTEM', true, NOW(), NOW()),
    ('system.logo', '/assets/logo.png', 'STRING', '系统Logo路径', 'UI', true, NOW(), NOW()),
    ('system.theme.primary', '#1976D2', 'STRING', '主题主色', 'UI', true, NOW(), NOW()),
    ('system.theme.secondary', '#424242', 'STRING', '主题辅助色', 'UI', true, NOW(), NOW()),
    ('system.maintenance', 'false', 'BOOLEAN', '是否处于维护模式', 'SYSTEM', true, NOW(), NOW()),
    ('system.registration.enabled', 'true', 'BOOLEAN', '是否允许用户注册', 'REGISTRATION', true, NOW(), NOW()),
    ('system.registration.approval', 'false', 'BOOLEAN', '新用户注册是否需要审批', 'REGISTRATION', true, NOW(), NOW()),
    ('system.email.sender', 'noreply@kace.com', 'EMAIL', '系统邮件发送地址', 'EMAIL', true, NOW(), NOW());