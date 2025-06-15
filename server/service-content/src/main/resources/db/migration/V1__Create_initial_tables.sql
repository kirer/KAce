-- 内容类型表
CREATE TABLE IF NOT EXISTS content_types (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- 内容类型字段表
CREATE TABLE IF NOT EXISTS content_type_fields (
    id UUID PRIMARY KEY,
    content_type_id UUID NOT NULL REFERENCES content_types(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL,
    required BOOLEAN NOT NULL DEFAULT FALSE,
    default_value TEXT,
    validations TEXT,
    UNIQUE (content_type_id, name)
);

-- 内容表
CREATE TABLE IF NOT EXISTS contents (
    id UUID PRIMARY KEY,
    content_type_id UUID NOT NULL REFERENCES content_types(id),
    title VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL,
    created_by UUID NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    published_at TIMESTAMP,
    version INTEGER NOT NULL DEFAULT 1,
    language_code VARCHAR(10) NOT NULL
);

-- 内容字段表
CREATE TABLE IF NOT EXISTS content_fields (
    id UUID PRIMARY KEY,
    content_id UUID NOT NULL REFERENCES contents(id) ON DELETE CASCADE,
    field_name VARCHAR(100) NOT NULL,
    field_value TEXT NOT NULL,
    UNIQUE (content_id, field_name)
);

-- 内容版本表
CREATE TABLE IF NOT EXISTS content_versions (
    id UUID PRIMARY KEY,
    content_id UUID NOT NULL REFERENCES contents(id) ON DELETE CASCADE,
    version INTEGER NOT NULL,
    created_by UUID NOT NULL,
    created_at TIMESTAMP NOT NULL,
    comment TEXT,
    UNIQUE (content_id, version)
);

-- 分类表
CREATE TABLE IF NOT EXISTS categories (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    parent_id UUID REFERENCES categories(id),
    slug VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- 标签表
CREATE TABLE IF NOT EXISTS tags (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    slug VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- 内容分类关联表
CREATE TABLE IF NOT EXISTS content_categories (
    content_id UUID NOT NULL REFERENCES contents(id) ON DELETE CASCADE,
    category_id UUID NOT NULL REFERENCES categories(id) ON DELETE CASCADE,
    PRIMARY KEY (content_id, category_id)
);

-- 内容标签关联表
CREATE TABLE IF NOT EXISTS content_tags (
    content_id UUID NOT NULL REFERENCES contents(id) ON DELETE CASCADE,
    tag_id UUID NOT NULL REFERENCES tags(id) ON DELETE CASCADE,
    PRIMARY KEY (content_id, tag_id)
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_contents_content_type_id ON contents(content_type_id);
CREATE INDEX IF NOT EXISTS idx_contents_status ON contents(status);
CREATE INDEX IF NOT EXISTS idx_contents_created_by ON contents(created_by);
CREATE INDEX IF NOT EXISTS idx_contents_language_code ON contents(language_code);
CREATE INDEX IF NOT EXISTS idx_content_fields_content_id ON content_fields(content_id);
CREATE INDEX IF NOT EXISTS idx_content_versions_content_id ON content_versions(content_id);
CREATE INDEX IF NOT EXISTS idx_categories_parent_id ON categories(parent_id);
CREATE INDEX IF NOT EXISTS idx_content_categories_content_id ON content_categories(content_id);
CREATE INDEX IF NOT EXISTS idx_content_categories_category_id ON content_categories(category_id);
CREATE INDEX IF NOT EXISTS idx_content_tags_content_id ON content_tags(content_id);
CREATE INDEX IF NOT EXISTS idx_content_tags_tag_id ON content_tags(tag_id); 