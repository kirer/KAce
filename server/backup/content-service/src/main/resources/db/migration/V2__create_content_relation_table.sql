-- 创建内容关联表
CREATE TABLE IF NOT EXISTS content_relations (
    id UUID PRIMARY KEY,
    source_content_id UUID NOT NULL,
    target_content_id UUID NOT NULL,
    type VARCHAR(50) NOT NULL,
    metadata JSONB NOT NULL DEFAULT '{}',
    created_by UUID NOT NULL,
    created_at BIGINT NOT NULL,
    updated_at BIGINT NOT NULL
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_content_relations_source_content_id ON content_relations(source_content_id);
CREATE INDEX IF NOT EXISTS idx_content_relations_target_content_id ON content_relations(target_content_id);
CREATE INDEX IF NOT EXISTS idx_content_relations_source_type ON content_relations(source_content_id, type);
CREATE INDEX IF NOT EXISTS idx_content_relations_target_type ON content_relations(target_content_id, type);
CREATE UNIQUE INDEX IF NOT EXISTS idx_content_relations_source_target_type ON content_relations(source_content_id, target_content_id, type);

-- 添加外键约束
ALTER TABLE content_relations
    ADD CONSTRAINT fk_content_relations_source_content_id
    FOREIGN KEY (source_content_id)
    REFERENCES contents(id)
    ON DELETE CASCADE;

ALTER TABLE content_relations
    ADD CONSTRAINT fk_content_relations_target_content_id
    FOREIGN KEY (target_content_id)
    REFERENCES contents(id)
    ON DELETE CASCADE;

-- 添加注释
COMMENT ON TABLE content_relations IS '内容关联表';
COMMENT ON COLUMN content_relations.id IS '关联ID';
COMMENT ON COLUMN content_relations.source_content_id IS '源内容ID';
COMMENT ON COLUMN content_relations.target_content_id IS '目标内容ID';
COMMENT ON COLUMN content_relations.type IS '关联类型';
COMMENT ON COLUMN content_relations.metadata IS '元数据';
COMMENT ON COLUMN content_relations.created_by IS '创建者ID';
COMMENT ON COLUMN content_relations.created_at IS '创建时间';
COMMENT ON COLUMN content_relations.updated_at IS '更新时间'; 