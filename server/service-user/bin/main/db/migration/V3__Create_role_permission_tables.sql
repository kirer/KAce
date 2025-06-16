-- 角色表
CREATE TABLE IF NOT EXISTS roles (
    id UUID PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description TEXT,
    permissions TEXT NOT NULL,  -- JSON格式的权限ID列表
    is_system BOOLEAN NOT NULL DEFAULT FALSE,
    organization_id UUID,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    UNIQUE (name, organization_id)  -- 组织内角色名称唯一
);

-- 权限表
CREATE TABLE IF NOT EXISTS permissions (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    category VARCHAR(50) NOT NULL,
    is_system BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- 用户角色关联表 (替换之前的简化版本)
CREATE TABLE IF NOT EXISTS user_roles (
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id UUID NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    assigned_at TIMESTAMP NOT NULL,
    assigned_by UUID NOT NULL REFERENCES users(id),
    PRIMARY KEY (user_id, role_id)
);

-- 角色权限关联表
CREATE TABLE IF NOT EXISTS role_permissions (
    role_id UUID NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    permission_id UUID NOT NULL REFERENCES permissions(id) ON DELETE CASCADE,
    assigned_at TIMESTAMP NOT NULL,
    assigned_by UUID NOT NULL REFERENCES users(id),
    PRIMARY KEY (role_id, permission_id)
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_roles_name ON roles(name);
CREATE INDEX IF NOT EXISTS idx_roles_organization_id ON roles(organization_id);
CREATE INDEX IF NOT EXISTS idx_permissions_code ON permissions(code);
CREATE INDEX IF NOT EXISTS idx_permissions_category ON permissions(category);
CREATE INDEX IF NOT EXISTS idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX IF NOT EXISTS idx_user_roles_role_id ON user_roles(role_id);
CREATE INDEX IF NOT EXISTS idx_role_permissions_role_id ON role_permissions(role_id);
CREATE INDEX IF NOT EXISTS idx_role_permissions_permission_id ON role_permissions(permission_id);

-- 插入系统角色
INSERT INTO roles (id, name, description, permissions, is_system, created_at, updated_at)
VALUES 
    (gen_random_uuid(), 'ADMIN', '系统管理员', '[]', TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'USER', '普通用户', '[]', TRUE, NOW(), NOW()),
    (gen_random_uuid(), 'GUEST', '访客', '[]', TRUE, NOW(), NOW())
ON CONFLICT (name, organization_id) DO NOTHING;

-- 插入系统权限
INSERT INTO permissions (id, name, code, description, category, is_system, created_at, updated_at)
VALUES 
    -- 用户管理权限
    (gen_random_uuid(), '创建用户', 'user:create', '创建新用户', 'USER_MANAGEMENT', TRUE, NOW(), NOW()),
    (gen_random_uuid(), '查看用户', 'user:read', '查看用户信息', 'USER_MANAGEMENT', TRUE, NOW(), NOW()),
    (gen_random_uuid(), '更新用户', 'user:update', '更新用户信息', 'USER_MANAGEMENT', TRUE, NOW(), NOW()),
    (gen_random_uuid(), '删除用户', 'user:delete', '删除用户', 'USER_MANAGEMENT', TRUE, NOW(), NOW()),
    
    -- 角色管理权限
    (gen_random_uuid(), '创建角色', 'role:create', '创建新角色', 'ROLE_MANAGEMENT', TRUE, NOW(), NOW()),
    (gen_random_uuid(), '查看角色', 'role:read', '查看角色信息', 'ROLE_MANAGEMENT', TRUE, NOW(), NOW()),
    (gen_random_uuid(), '更新角色', 'role:update', '更新角色信息', 'ROLE_MANAGEMENT', TRUE, NOW(), NOW()),
    (gen_random_uuid(), '删除角色', 'role:delete', '删除角色', 'ROLE_MANAGEMENT', TRUE, NOW(), NOW()),
    (gen_random_uuid(), '分配角色', 'role:assign', '为用户分配角色', 'ROLE_MANAGEMENT', TRUE, NOW(), NOW()),
    
    -- 权限管理权限
    (gen_random_uuid(), '创建权限', 'permission:create', '创建新权限', 'ROLE_MANAGEMENT', TRUE, NOW(), NOW()),
    (gen_random_uuid(), '查看权限', 'permission:read', '查看权限信息', 'ROLE_MANAGEMENT', TRUE, NOW(), NOW()),
    (gen_random_uuid(), '更新权限', 'permission:update', '更新权限信息', 'ROLE_MANAGEMENT', TRUE, NOW(), NOW()),
    (gen_random_uuid(), '删除权限', 'permission:delete', '删除权限', 'ROLE_MANAGEMENT', TRUE, NOW(), NOW()),
    
    -- 组织管理权限
    (gen_random_uuid(), '创建组织', 'organization:create', '创建新组织', 'ORGANIZATION_MANAGEMENT', TRUE, NOW(), NOW()),
    (gen_random_uuid(), '查看组织', 'organization:read', '查看组织信息', 'ORGANIZATION_MANAGEMENT', TRUE, NOW(), NOW()),
    (gen_random_uuid(), '更新组织', 'organization:update', '更新组织信息', 'ORGANIZATION_MANAGEMENT', TRUE, NOW(), NOW()),
    (gen_random_uuid(), '删除组织', 'organization:delete', '删除组织', 'ORGANIZATION_MANAGEMENT', TRUE, NOW(), NOW())
ON CONFLICT (code) DO NOTHING; 