-- 创建媒体文件夹表
CREATE TABLE IF NOT EXISTS media_folder (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    parent_id UUID,
    path VARCHAR(500) NOT NULL,
    created_by UUID NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    FOREIGN KEY (parent_id) REFERENCES media_folder(id) ON DELETE CASCADE
);

-- 创建媒体表
CREATE TABLE IF NOT EXISTS media (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    type VARCHAR(20) NOT NULL,
    mime_type VARCHAR(100) NOT NULL,
    size BIGINT NOT NULL,
    path VARCHAR(500) NOT NULL,
    url VARCHAR(500) NOT NULL,
    thumbnail_url VARCHAR(500),
    width INTEGER,
    height INTEGER,
    duration BIGINT,
    status VARCHAR(20) NOT NULL,
    metadata TEXT,
    folder_id UUID,
    created_by UUID NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    FOREIGN KEY (folder_id) REFERENCES media_folder(id) ON DELETE SET NULL
);

-- 创建媒体标签表
CREATE TABLE IF NOT EXISTS media_tag (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(100) NOT NULL,
    created_by UUID NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT media_tag_name_unique UNIQUE (name),
    CONSTRAINT media_tag_slug_unique UNIQUE (slug)
);

-- 创建媒体标签映射表
CREATE TABLE IF NOT EXISTS media_tag_mapping (
    media_id UUID NOT NULL,
    tag_id UUID NOT NULL,
    PRIMARY KEY (media_id, tag_id),
    FOREIGN KEY (media_id) REFERENCES media(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES media_tag(id) ON DELETE CASCADE
);

-- 创建媒体处理任务表
CREATE TABLE IF NOT EXISTS media_processing_task (
    id UUID PRIMARY KEY,
    media_id UUID NOT NULL,
    type VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL,
    parameters TEXT,
    result TEXT,
    error_message TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    completed_at TIMESTAMP,
    FOREIGN KEY (media_id) REFERENCES media(id) ON DELETE CASCADE
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_media_folder_parent_id ON media_folder(parent_id);
CREATE INDEX IF NOT EXISTS idx_media_folder_path ON media_folder(path);
CREATE INDEX IF NOT EXISTS idx_media_folder_name ON media_folder(name);

CREATE INDEX IF NOT EXISTS idx_media_folder_id ON media(folder_id);
CREATE INDEX IF NOT EXISTS idx_media_type ON media(type);
CREATE INDEX IF NOT EXISTS idx_media_status ON media(status);
CREATE INDEX IF NOT EXISTS idx_media_name ON media(name);
CREATE INDEX IF NOT EXISTS idx_media_created_at ON media(created_at);

CREATE INDEX IF NOT EXISTS idx_media_tag_name ON media_tag(name);
CREATE INDEX IF NOT EXISTS idx_media_tag_slug ON media_tag(slug);

CREATE INDEX IF NOT EXISTS idx_media_processing_task_media_id ON media_processing_task(media_id);
CREATE INDEX IF NOT EXISTS idx_media_processing_task_status ON media_processing_task(status);
CREATE INDEX IF NOT EXISTS idx_media_processing_task_type ON media_processing_task(type); 