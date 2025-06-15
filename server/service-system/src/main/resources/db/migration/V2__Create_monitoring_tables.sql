-- 创建系统指标表
CREATE TABLE IF NOT EXISTS system_metrics (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    value DOUBLE PRECISION NOT NULL,
    unit VARCHAR(50) DEFAULT '',
    service_id VARCHAR(100) DEFAULT 'system',
    timestamp TIMESTAMP WITH TIME ZONE NOT NULL,
    tags JSONB
);

-- 创建系统健康记录表
CREATE TABLE IF NOT EXISTS system_health_records (
    id BIGSERIAL PRIMARY KEY,
    service_id VARCHAR(100) NOT NULL,
    status VARCHAR(50) NOT NULL,
    details JSONB,
    timestamp TIMESTAMP WITH TIME ZONE NOT NULL
);

-- 创建系统告警表
CREATE TABLE IF NOT EXISTS system_alerts (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    level VARCHAR(50) NOT NULL,
    message TEXT NOT NULL,
    service_id VARCHAR(100) NOT NULL,
    metric_name VARCHAR(255) NOT NULL,
    threshold DOUBLE PRECISION NOT NULL,
    current_value DOUBLE PRECISION NOT NULL,
    timestamp TIMESTAMP WITH TIME ZONE NOT NULL,
    acknowledged BOOLEAN DEFAULT FALSE,
    resolved_at TIMESTAMP WITH TIME ZONE
);

-- 创建告警规则表
CREATE TABLE IF NOT EXISTS alert_rules (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    metric_name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    operator VARCHAR(50) NOT NULL,
    threshold DOUBLE PRECISION NOT NULL,
    level VARCHAR(50) NOT NULL,
    enabled BOOLEAN DEFAULT TRUE,
    service_pattern VARCHAR(255) DEFAULT '*',
    consecutive_data_points INTEGER DEFAULT 1,
    message TEXT DEFAULT '',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

-- 创建指标表的索引
CREATE INDEX idx_metrics_name ON system_metrics(name);
CREATE INDEX idx_metrics_service_id ON system_metrics(service_id);
CREATE INDEX idx_metrics_timestamp ON system_metrics(timestamp);
CREATE INDEX idx_metrics_type ON system_metrics(type);

-- 创建健康记录表的索引
CREATE INDEX idx_health_service_id ON system_health_records(service_id);
CREATE INDEX idx_health_timestamp ON system_health_records(timestamp);
CREATE INDEX idx_health_status ON system_health_records(status);

-- 创建告警表的索引
CREATE INDEX idx_alerts_service_id ON system_alerts(service_id);
CREATE INDEX idx_alerts_metric_name ON system_alerts(metric_name);
CREATE INDEX idx_alerts_level ON system_alerts(level);
CREATE INDEX idx_alerts_timestamp ON system_alerts(timestamp);
CREATE INDEX idx_alerts_resolved ON system_alerts(resolved_at NULLS FIRST);

-- 创建告警规则表的索引
CREATE INDEX idx_alert_rules_metric_name ON alert_rules(metric_name);
CREATE INDEX idx_alert_rules_enabled ON alert_rules(enabled);

-- 插入默认告警规则
INSERT INTO alert_rules (
    name, 
    metric_name, 
    type, 
    operator, 
    threshold, 
    level, 
    enabled, 
    service_pattern, 
    consecutive_data_points, 
    message, 
    created_at, 
    updated_at
) VALUES
    ('CPU使用率过高', 'cpu.usage', 'THRESHOLD', 'GREATER_THAN', 90, 'WARNING', true, '*', 3, 'CPU使用率超过90%', NOW(), NOW()),
    ('内存使用率过高', 'memory.usage', 'THRESHOLD', 'GREATER_THAN', 85, 'WARNING', true, '*', 3, '内存使用率超过85%', NOW(), NOW()),
    ('磁盘使用率过高', 'disk.usage', 'THRESHOLD', 'GREATER_THAN', 90, 'WARNING', true, '*', 1, '磁盘使用率超过90%', NOW(), NOW()),
    ('服务响应时间过长', 'response.time', 'THRESHOLD', 'GREATER_THAN', 1000, 'WARNING', true, '*', 3, '服务响应时间超过1000ms', NOW(), NOW()),
    ('错误率过高', 'error.rate', 'THRESHOLD', 'GREATER_THAN', 5, 'ERROR', true, '*', 3, '服务错误率超过5%', NOW(), NOW()),
    ('数据库连接数过高', 'db.connections', 'THRESHOLD', 'GREATER_THAN', 80, 'WARNING', true, '*', 1, '数据库连接数超过80%', NOW(), NOW());