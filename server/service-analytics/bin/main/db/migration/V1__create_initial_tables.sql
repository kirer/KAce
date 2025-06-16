-- 创建事件表
CREATE TABLE IF NOT EXISTS events (
    id VARCHAR(36) PRIMARY KEY,
    type VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    user_id VARCHAR(36),
    session_id VARCHAR(50),
    properties TEXT NOT NULL,
    timestamp TIMESTAMP WITH TIME ZONE NOT NULL,
    app_version VARCHAR(20),
    device_info TEXT,
    source VARCHAR(50)
);

-- 创建事件时间戳索引
CREATE INDEX IF NOT EXISTS idx_events_timestamp ON events (timestamp);

-- 创建事件类型索引
CREATE INDEX IF NOT EXISTS idx_events_type ON events (type);

-- 创建事件名称索引
CREATE INDEX IF NOT EXISTS idx_events_name ON events (name);

-- 创建用户ID索引
CREATE INDEX IF NOT EXISTS idx_events_user_id ON events (user_id);

-- 创建会话ID索引
CREATE INDEX IF NOT EXISTS idx_events_session_id ON events (session_id);

-- 创建指标表
CREATE TABLE IF NOT EXISTS metrics (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    value DOUBLE PRECISION NOT NULL,
    unit VARCHAR(20),
    timestamp TIMESTAMP WITH TIME ZONE NOT NULL,
    dimensions JSONB DEFAULT '{}'::jsonb,
    tags TEXT[]
);

-- 创建指标时间戳索引
CREATE INDEX IF NOT EXISTS idx_metrics_timestamp ON metrics (timestamp);

-- 创建指标名称索引
CREATE INDEX IF NOT EXISTS idx_metrics_name ON metrics (name);

-- 创建指标维度GIN索引
CREATE INDEX IF NOT EXISTS idx_metrics_dimensions ON metrics USING GIN (dimensions);

-- 创建报表表
CREATE TABLE IF NOT EXISTS reports (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    created_by VARCHAR(36) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    schedule JSONB,
    parameters JSONB NOT NULL,
    visualizations JSONB DEFAULT '[]'::jsonb
);

-- 创建报表创建者索引
CREATE INDEX IF NOT EXISTS idx_reports_created_by ON reports (created_by);

-- 创建报表创建时间索引
CREATE INDEX IF NOT EXISTS idx_reports_created_at ON reports (created_at); 