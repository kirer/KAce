-- 创建内容权限表
CREATE TABLE IF NOT EXISTS content_permissions (
    id UUID PRIMARY KEY,
    content_id UUID,
    content_type_id UUID,
    permission_type VARCHAR(50) NOT NULL,
    subject_type VARCHAR(50) NOT NULL,
    subject_id UUID,
    created_by UUID NOT NULL,
    created_at BIGINT NOT NULL,
    updated_at BIGINT NOT NULL,
    
    -- 约束：内容ID和内容类型ID不能同时为空
    CONSTRAINT content_id_or_content_type_id_not_null CHECK (
        (content_id IS NOT NULL) OR (content_type_id IS NOT NULL)
    ),
    
    -- 约束：如果主体类型不是PUBLIC，则主体ID不能为空
    CONSTRAINT subject_id_not_null_if_not_public CHECK (
        (subject_type <> 'PUBLIC' AND subject_id IS NOT NULL) OR
        (subject_type = 'PUBLIC')
    )
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_content_permissions_content_id ON content_permissions(content_id);
CREATE INDEX IF NOT EXISTS idx_content_permissions_content_type_id ON content_permissions(content_type_id);
CREATE INDEX IF NOT EXISTS idx_content_permissions_content_permission ON content_permissions(content_id, permission_type, subject_type);
CREATE INDEX IF NOT EXISTS idx_content_permissions_content_type_permission ON content_permissions(content_type_id, permission_type, subject_type);
CREATE INDEX IF NOT EXISTS idx_content_permissions_subject ON content_permissions(subject_type, subject_id);

-- 创建唯一约束
CREATE UNIQUE INDEX IF NOT EXISTS idx_content_permissions_unique_content ON content_permissions(content_id, permission_type, subject_type, subject_id) 
WHERE content_id IS NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS idx_content_permissions_unique_content_type ON content_permissions(content_type_id, permission_type, subject_type, subject_id) 
WHERE content_type_id IS NOT NULL; 