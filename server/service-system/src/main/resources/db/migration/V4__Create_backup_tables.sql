-- 创建系统备份表
CREATE TABLE IF NOT EXISTS system_backups (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    type VARCHAR(20) NOT NULL,
    service_type VARCHAR(20) NOT NULL,
    service_name VARCHAR(50) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT NOT NULL,
    checksum VARCHAR(64),
    status VARCHAR(20) NOT NULL,
    status_message VARCHAR(500),
    created_by VARCHAR(36),
    created_at TIMESTAMP NOT NULL,
    completed_at TIMESTAMP,
    expires_at TIMESTAMP,
    encrypted BOOLEAN NOT NULL DEFAULT FALSE,
    encryption_algorithm VARCHAR(50),
    compression_ratio DOUBLE,
    parameters TEXT
);

-- 创建索引以提高查询性能
CREATE INDEX idx_system_backups_type ON system_backups(type);
CREATE INDEX idx_system_backups_service_type ON system_backups(service_type);
CREATE INDEX idx_system_backups_status ON system_backups(status);
CREATE INDEX idx_system_backups_created_at ON system_backups(created_at);
CREATE INDEX idx_system_backups_expires_at ON system_backups(expires_at);

-- 创建备份策略表
CREATE TABLE IF NOT EXISTS backup_policies (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    backup_type VARCHAR(20) NOT NULL,
    service_type VARCHAR(20) NOT NULL,
    service_name VARCHAR(50) NOT NULL,
    schedule VARCHAR(50) NOT NULL,
    retention_days INT NOT NULL,
    max_backups INT,
    storage_path_template VARCHAR(500) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    compress BOOLEAN NOT NULL DEFAULT TRUE,
    encrypt BOOLEAN NOT NULL DEFAULT FALSE,
    encryption_algorithm VARCHAR(50),
    pre_backup_command VARCHAR(500),
    post_backup_command VARCHAR(500),
    backup_window_start TIME,
    backup_window_end TIME,
    last_executed TIMESTAMP,
    next_scheduled TIMESTAMP,
    created_by VARCHAR(36) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    parameters TEXT
);

-- 创建索引以提高查询性能
CREATE INDEX idx_backup_policies_service_type ON backup_policies(service_type);
CREATE INDEX idx_backup_policies_service_name ON backup_policies(service_name);
CREATE INDEX idx_backup_policies_enabled ON backup_policies(enabled);
CREATE INDEX idx_backup_policies_next_scheduled ON backup_policies(next_scheduled);

-- 创建备份执行日志表
CREATE TABLE IF NOT EXISTS backup_execution_logs (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    backup_id VARCHAR(36) NOT NULL,
    policy_id VARCHAR(36),
    status VARCHAR(20) NOT NULL,
    started_at TIMESTAMP NOT NULL,
    completed_at TIMESTAMP,
    duration_seconds BIGINT,
    message TEXT,
    details TEXT,
    FOREIGN KEY (backup_id) REFERENCES system_backups(id),
    FOREIGN KEY (policy_id) REFERENCES backup_policies(id)
);

-- 创建索引以提高查询性能
CREATE INDEX idx_backup_execution_logs_backup_id ON backup_execution_logs(backup_id);
CREATE INDEX idx_backup_execution_logs_policy_id ON backup_execution_logs(policy_id);
CREATE INDEX idx_backup_execution_logs_status ON backup_execution_logs(status);
CREATE INDEX idx_backup_execution_logs_started_at ON backup_execution_logs(started_at);

-- 创建备份恢复记录表
CREATE TABLE IF NOT EXISTS backup_restore_logs (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    backup_id VARCHAR(36) NOT NULL,
    status VARCHAR(20) NOT NULL,
    started_at TIMESTAMP NOT NULL,
    completed_at TIMESTAMP,
    duration_seconds BIGINT,
    restored_by VARCHAR(36),
    message TEXT,
    details TEXT,
    FOREIGN KEY (backup_id) REFERENCES system_backups(id)
);

-- 创建索引以提高查询性能
CREATE INDEX idx_backup_restore_logs_backup_id ON backup_restore_logs(backup_id);
CREATE INDEX idx_backup_restore_logs_status ON backup_restore_logs(status);
CREATE INDEX idx_backup_restore_logs_started_at ON backup_restore_logs(started_at);

-- 创建默认服务类型和备份路径配置表
CREATE TABLE IF NOT EXISTS backup_service_configs (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    service_type VARCHAR(20) NOT NULL,
    service_name VARCHAR(50) NOT NULL,
    base_path VARCHAR(500) NOT NULL,
    backup_command VARCHAR(500),
    restore_command VARCHAR(500),
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    UNIQUE (service_type, service_name)
);

-- 插入一些默认的服务配置
INSERT INTO backup_service_configs (id, service_type, service_name, base_path, backup_command, restore_command, enabled, created_at, updated_at)
VALUES
    (UUID(), 'DATABASE', 'MySQL', '/backup/database/mysql', 'mysqldump -u{username} -p{password} {database} > {output}', 'mysql -u{username} -p{password} {database} < {input}', true, NOW(), NOW()),
    (UUID(), 'DATABASE', 'PostgreSQL', '/backup/database/postgresql', 'pg_dump -U {username} {database} > {output}', 'psql -U {username} {database} < {input}', true, NOW(), NOW()),
    (UUID(), 'DATABASE', 'MongoDB', '/backup/database/mongodb', 'mongodump --uri={uri} --out={output}', 'mongorestore --uri={uri} {input}', true, NOW(), NOW()),
    (UUID(), 'FILES', 'UserUploads', '/backup/files/uploads', 'tar -czf {output} {source_dir}', 'tar -xzf {input} -C {target_dir}', true, NOW(), NOW()),
    (UUID(), 'CONFIGURATION', 'AppConfig', '/backup/config', 'tar -czf {output} {source_dir}', 'tar -xzf {input} -C {target_dir}', true, NOW(), NOW())
ON DUPLICATE KEY UPDATE updated_at = NOW(); 