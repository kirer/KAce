-- 创建媒体权限表
CREATE TABLE IF NOT EXISTS media_permissions (
    id UUID PRIMARY KEY,
    media_id UUID REFERENCES media(id) ON DELETE CASCADE,
    folder_id UUID REFERENCES media_folder(id) ON DELETE CASCADE,
    permission_type VARCHAR(20) NOT NULL,
    grantee_type VARCHAR(20) NOT NULL,
    grantee_id UUID,
    is_inherited BOOLEAN NOT NULL DEFAULT FALSE,
    expires_at TIMESTAMP,
    created_by UUID NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    
    -- 确保media_id和folder_id至少有一个不为NULL
    CONSTRAINT media_permissions_target_check CHECK (
        (media_id IS NOT NULL OR folder_id IS NOT NULL) 
        AND NOT (media_id IS NOT NULL AND folder_id IS NOT NULL)
    ),
    
    -- 确保PUBLIC权限的grantee_id为NULL
    CONSTRAINT media_permissions_public_check CHECK (
        (grantee_type != 'PUBLIC' OR grantee_id IS NULL)
    ),
    
    -- 确保非PUBLIC权限的grantee_id不为NULL
    CONSTRAINT media_permissions_nonpublic_check CHECK (
        (grantee_type = 'PUBLIC' OR grantee_id IS NOT NULL)
    )
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_media_permissions_media_id ON media_permissions(media_id);
CREATE INDEX IF NOT EXISTS idx_media_permissions_folder_id ON media_permissions(folder_id);
CREATE INDEX IF NOT EXISTS idx_media_permissions_grantee_type_id ON media_permissions(grantee_type, grantee_id);
CREATE INDEX IF NOT EXISTS idx_media_permissions_expires_at ON media_permissions(expires_at);

-- 创建用于权限检查的函数
CREATE OR REPLACE FUNCTION check_media_permission(
    p_media_id UUID, 
    p_permission_type VARCHAR, 
    p_grantee_type VARCHAR,
    p_grantee_id UUID
) RETURNS BOOLEAN AS $$
DECLARE
    v_has_permission BOOLEAN;
    v_folder_id UUID;
BEGIN
    -- 检查是否有直接权限（包括ALL权限）
    SELECT EXISTS (
        SELECT 1 FROM media_permissions
        WHERE media_id = p_media_id
        AND (permission_type = p_permission_type OR permission_type = 'ALL')
        AND grantee_type = p_grantee_type
        AND (grantee_id = p_grantee_id OR (grantee_type = 'PUBLIC' AND grantee_id IS NULL))
        AND (expires_at IS NULL OR expires_at > NOW())
    ) INTO v_has_permission;
    
    IF v_has_permission THEN
        RETURN TRUE;
    END IF;
    
    -- 如果没有直接权限，则检查文件夹权限
    SELECT folder_id INTO v_folder_id FROM media WHERE id = p_media_id;
    
    IF v_folder_id IS NULL THEN
        RETURN FALSE;
    END IF;
    
    RETURN check_folder_permission(v_folder_id, p_permission_type, p_grantee_type, p_grantee_id);
END;
$$ LANGUAGE plpgsql;

-- 创建文件夹权限检查函数
CREATE OR REPLACE FUNCTION check_folder_permission(
    p_folder_id UUID, 
    p_permission_type VARCHAR, 
    p_grantee_type VARCHAR,
    p_grantee_id UUID
) RETURNS BOOLEAN AS $$
DECLARE
    v_has_permission BOOLEAN;
    v_parent_id UUID;
BEGIN
    -- 检查是否有当前文件夹的权限
    SELECT EXISTS (
        SELECT 1 FROM media_permissions
        WHERE folder_id = p_folder_id
        AND (permission_type = p_permission_type OR permission_type = 'ALL')
        AND grantee_type = p_grantee_type
        AND (grantee_id = p_grantee_id OR (grantee_type = 'PUBLIC' AND grantee_id IS NULL))
        AND (expires_at IS NULL OR expires_at > NOW())
    ) INTO v_has_permission;
    
    IF v_has_permission THEN
        RETURN TRUE;
    END IF;
    
    -- 如果没有当前文件夹的权限，则检查父文件夹权限
    SELECT parent_id INTO v_parent_id FROM media_folder WHERE id = p_folder_id;
    
    IF v_parent_id IS NULL THEN
        RETURN FALSE;
    END IF;
    
    -- 递归检查父文件夹
    RETURN check_folder_permission(v_parent_id, p_permission_type, p_grantee_type, p_grantee_id);
END;
$$ LANGUAGE plpgsql; 