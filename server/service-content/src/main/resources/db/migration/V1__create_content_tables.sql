-- 内容类型表
CREATE TABLE IF NOT EXISTS content_types (
    id UUID PRIMARY KEY,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    created_by UUID NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    is_system BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT content_types_code_unique UNIQUE (code)
);

-- 内容类型字段表
CREATE TABLE IF NOT EXISTS content_type_fields (
    id UUID PRIMARY KEY,
    content_type_id UUID NOT NULL REFERENCES content_types(id) ON DELETE CASCADE,
    name VARCHAR(50) NOT NULL,
    code VARCHAR(50) NOT NULL,
    description TEXT,
    field_type VARCHAR(50) NOT NULL,
    is_required BOOLEAN NOT NULL DEFAULT FALSE,
    default_value TEXT,
    validation_rules TEXT,
    ordering INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT content_type_field_unique_idx UNIQUE (content_type_id, code)
);

-- 内容表
CREATE TABLE IF NOT EXISTS contents (
    id UUID PRIMARY KEY,
    content_type_id UUID NOT NULL REFERENCES content_types(id),
    title VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_by UUID NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    published_at TIMESTAMP,
    version INTEGER NOT NULL DEFAULT 1,
    language_code VARCHAR(10) NOT NULL,
    CONSTRAINT contents_slug_unique UNIQUE (slug)
);

-- 内容字段表
CREATE TABLE IF NOT EXISTS content_fields (
    id UUID PRIMARY KEY,
    content_id UUID NOT NULL REFERENCES contents(id) ON DELETE CASCADE,
    field_code VARCHAR(50) NOT NULL,
    field_value TEXT NOT NULL
);

-- 内容版本表
CREATE TABLE IF NOT EXISTS content_versions (
    id UUID PRIMARY KEY,
    content_id UUID NOT NULL REFERENCES contents(id) ON DELETE CASCADE,
    version INTEGER NOT NULL,
    created_by UUID NOT NULL,
    created_at TIMESTAMP NOT NULL,
    comment TEXT,
    CONSTRAINT content_version_unique_idx UNIQUE (content_id, version)
);

-- 内容版本字段表
CREATE TABLE IF NOT EXISTS content_version_fields (
    id UUID PRIMARY KEY,
    content_version_id UUID NOT NULL REFERENCES content_versions(id) ON DELETE CASCADE,
    field_code VARCHAR(50) NOT NULL,
    field_value TEXT NOT NULL
);

-- 分类表
CREATE TABLE IF NOT EXISTS categories (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(100) NOT NULL,
    description TEXT,
    parent_id UUID,
    created_by UUID NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT categories_slug_unique UNIQUE (slug)
);

-- 标签表
CREATE TABLE IF NOT EXISTS tags (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(100) NOT NULL,
    description TEXT,
    created_by UUID NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT tags_slug_unique UNIQUE (slug)
);

-- 内容分类关联表
CREATE TABLE IF NOT EXISTS content_categories (
    id UUID PRIMARY KEY,
    content_id UUID NOT NULL REFERENCES contents(id) ON DELETE CASCADE,
    category_id UUID NOT NULL REFERENCES categories(id) ON DELETE CASCADE,
    CONSTRAINT content_category_unique_idx UNIQUE (content_id, category_id)
);

-- 内容标签关联表
CREATE TABLE IF NOT EXISTS content_tags (
    id UUID PRIMARY KEY,
    content_id UUID NOT NULL REFERENCES contents(id) ON DELETE CASCADE,
    tag_id UUID NOT NULL REFERENCES tags(id) ON DELETE CASCADE,
    CONSTRAINT content_tag_unique_idx UNIQUE (content_id, tag_id)
);

-- 创建索引
CREATE INDEX IF NOT EXISTS content_type_id_idx ON contents(content_type_id);
CREATE INDEX IF NOT EXISTS content_status_idx ON contents(status);
CREATE INDEX IF NOT EXISTS content_language_code_idx ON contents(language_code);
CREATE INDEX IF NOT EXISTS content_created_by_idx ON contents(created_by);
CREATE INDEX IF NOT EXISTS content_field_content_id_idx ON content_fields(content_id);
CREATE INDEX IF NOT EXISTS content_version_content_id_idx ON content_versions(content_id);
CREATE INDEX IF NOT EXISTS content_version_field_version_id_idx ON content_version_fields(content_version_id);
CREATE INDEX IF NOT EXISTS category_parent_id_idx ON categories(parent_id);
CREATE INDEX IF NOT EXISTS content_category_content_id_idx ON content_categories(content_id);
CREATE INDEX IF NOT EXISTS content_category_category_id_idx ON content_categories(category_id);
CREATE INDEX IF NOT EXISTS content_tag_content_id_idx ON content_tags(content_id);
CREATE INDEX IF NOT EXISTS content_tag_tag_id_idx ON content_tags(tag_id);