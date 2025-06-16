-- 系统指标表
CREATE TABLE system_metrics (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL,
    value DOUBLE PRECISION NOT NULL,
    unit VARCHAR(20),
    service_id VARCHAR(100) NOT NULL,
    timestamp TIMESTAMP WITH TIME ZONE NOT NULL,
    tags JSONB
);

-- 为指标表创建索引
CREATE INDEX idx_metrics_name ON system_metrics(name);
CREATE INDEX idx_metrics_service_id ON system_metrics(service_id);
CREATE INDEX idx_metrics_timestamp ON system_metrics(timestamp);
CREATE INDEX idx_metrics_name_service ON system_metrics(name, service_id);
CREATE INDEX idx_metrics_name_timestamp ON system_metrics(name, timestamp);

-- 系统健康状态表
CREATE TABLE system_health (
    id BIGSERIAL PRIMARY KEY,
    service_id VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL,
    details JSONB,
    timestamp TIMESTAMP WITH TIME ZONE NOT NULL
);

-- 为健康状态表创建索引
CREATE INDEX idx_health_service_id ON system_health(service_id);
CREATE INDEX idx_health_status ON system_health(status);
CREATE INDEX idx_health_timestamp ON system_health(timestamp);

-- 告警规则表
CREATE TABLE alert_rules (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    metric_name VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL,
    operator VARCHAR(50) NOT NULL,
    threshold DOUBLE PRECISION NOT NULL,
    level VARCHAR(20) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    service_pattern VARCHAR(100) NOT NULL DEFAULT '*',
    consecutive_data_points INT NOT NULL DEFAULT 1,
    message TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT uk_alert_rule_name UNIQUE (name)
);

-- 为告警规则表创建索引
CREATE INDEX idx_alert_rule_metric_name ON alert_rules(metric_name);
CREATE INDEX idx_alert_rule_enabled ON alert_rules(enabled);

-- 系统告警表
CREATE TABLE system_alerts (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    level VARCHAR(20) NOT NULL,
    message TEXT NOT NULL,
    service_id VARCHAR(100) NOT NULL,
    metric_name VARCHAR(100) NOT NULL,
    threshold DOUBLE PRECISION NOT NULL,
    current_value DOUBLE PRECISION NOT NULL,
    timestamp TIMESTAMP WITH TIME ZONE NOT NULL,
    acknowledged BOOLEAN NOT NULL DEFAULT FALSE,
    resolved_at TIMESTAMP WITH TIME ZONE
);

-- 为告警表创建索引
CREATE INDEX idx_alert_service_id ON system_alerts(service_id);
CREATE INDEX idx_alert_level ON system_alerts(level);
CREATE INDEX idx_alert_timestamp ON system_alerts(timestamp);
CREATE INDEX idx_alert_resolved ON system_alerts(resolved_at);
CREATE INDEX idx_alert_active ON system_alerts(resolved_at) WHERE resolved_at IS NULL; 