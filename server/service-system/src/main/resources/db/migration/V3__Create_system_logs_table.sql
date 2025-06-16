-- 创建系统日志表
CREATE TABLE IF NOT EXISTS system_logs (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    type VARCHAR(20) NOT NULL,
    module VARCHAR(50) NOT NULL,
    operation VARCHAR(100) NOT NULL,
    content TEXT NOT NULL,
    user_id VARCHAR(36),
    client_ip VARCHAR(50),
    execution_time BIGINT,
    status VARCHAR(20),
    extra_params TEXT,
    created_at TIMESTAMP NOT NULL
);

-- 创建索引以提高查询性能
CREATE INDEX idx_system_logs_type ON system_logs(type);
CREATE INDEX idx_system_logs_module ON system_logs(module);
CREATE INDEX idx_system_logs_user_id ON system_logs(user_id);
CREATE INDEX idx_system_logs_created_at ON system_logs(created_at);

-- 创建系统日志保留策略表（用于配置日志保留时间）
CREATE TABLE IF NOT EXISTS system_log_retention_policies (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    log_type VARCHAR(20) NOT NULL UNIQUE,
    retention_days INT NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- 插入默认保留策略
INSERT INTO system_log_retention_policies (id, log_type, retention_days, enabled, created_at, updated_at)
VALUES 
    (UUID(), 'INFO', 30, true, NOW(), NOW()),
    (UUID(), 'WARNING', 90, true, NOW(), NOW()),
    (UUID(), 'ERROR', 180, true, NOW(), NOW()),
    (UUID(), 'DEBUG', 7, true, NOW(), NOW()),
    (UUID(), 'SECURITY', 365, true, NOW(), NOW()),
    (UUID(), 'AUDIT', 730, true, NOW(), NOW())
ON DUPLICATE KEY UPDATE updated_at = NOW(); 