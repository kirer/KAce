-- 插入系统角色
INSERT INTO roles (id, name, description, is_system, created_at, updated_at)
VALUES 
    ('11111111-1111-1111-1111-111111111111', 'ADMIN', '系统管理员，拥有所有权限', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('22222222-2222-2222-2222-222222222222', 'USER', '普通用户', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('33333333-3333-3333-3333-333333333333', 'EDITOR', '内容编辑', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('44444444-4444-4444-4444-444444444444', 'VIEWER', '内容查看者', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (name) DO NOTHING;

-- 插入系统权限（用户管理）
INSERT INTO permissions (id, name, description, resource, action, is_system, created_at, updated_at)
VALUES 
    ('10000000-0000-0000-0000-000000000001', 'user:create', '创建用户', 'user', 'create', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('10000000-0000-0000-0000-000000000002', 'user:read', '查看用户', 'user', 'read', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('10000000-0000-0000-0000-000000000003', 'user:update', '更新用户', 'user', 'update', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('10000000-0000-0000-0000-000000000004', 'user:delete', '删除用户', 'user', 'delete', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('10000000-0000-0000-0000-000000000005', 'user:list', '列出所有用户', 'user', 'list', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (name) DO NOTHING;

-- 插入系统权限（角色管理）
INSERT INTO permissions (id, name, description, resource, action, is_system, created_at, updated_at)
VALUES 
    ('20000000-0000-0000-0000-000000000001', 'role:create', '创建角色', 'role', 'create', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('20000000-0000-0000-0000-000000000002', 'role:read', '查看角色', 'role', 'read', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('20000000-0000-0000-0000-000000000003', 'role:update', '更新角色', 'role', 'update', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('20000000-0000-0000-0000-000000000004', 'role:delete', '删除角色', 'role', 'delete', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('20000000-0000-0000-0000-000000000005', 'role:list', '列出所有角色', 'role', 'list', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (name) DO NOTHING;

-- 插入系统权限（权限管理）
INSERT INTO permissions (id, name, description, resource, action, is_system, created_at, updated_at)
VALUES 
    ('30000000-0000-0000-0000-000000000001', 'permission:create', '创建权限', 'permission', 'create', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('30000000-0000-0000-0000-000000000002', 'permission:read', '查看权限', 'permission', 'read', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('30000000-0000-0000-0000-000000000003', 'permission:update', '更新权限', 'permission', 'update', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('30000000-0000-0000-0000-000000000004', 'permission:delete', '删除权限', 'permission', 'delete', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('30000000-0000-0000-0000-000000000005', 'permission:list', '列出所有权限', 'permission', 'list', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (name) DO NOTHING;

-- 插入系统权限（内容管理）
INSERT INTO permissions (id, name, description, resource, action, is_system, created_at, updated_at)
VALUES 
    ('40000000-0000-0000-0000-000000000001', 'content:create', '创建内容', 'content', 'create', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('40000000-0000-0000-0000-000000000002', 'content:read', '查看内容', 'content', 'read', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('40000000-0000-0000-0000-000000000003', 'content:update', '更新内容', 'content', 'update', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('40000000-0000-0000-0000-000000000004', 'content:delete', '删除内容', 'content', 'delete', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('40000000-0000-0000-0000-000000000005', 'content:list', '列出所有内容', 'content', 'list', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('40000000-0000-0000-0000-000000000006', 'content:publish', '发布内容', 'content', 'publish', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('40000000-0000-0000-0000-000000000007', 'content:unpublish', '取消发布内容', 'content', 'unpublish', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (name) DO NOTHING;

-- 为角色分配权限

-- 管理员角色拥有所有权限
INSERT INTO role_permissions (role_id, permission_id)
SELECT '11111111-1111-1111-1111-111111111111', id FROM permissions
ON CONFLICT (role_id, permission_id) DO NOTHING;

-- 普通用户角色权限
INSERT INTO role_permissions (role_id, permission_id)
VALUES 
    ('22222222-2222-2222-2222-222222222222', '40000000-0000-0000-0000-000000000002'), -- content:read
    ('22222222-2222-2222-2222-222222222222', '40000000-0000-0000-0000-000000000005')  -- content:list
ON CONFLICT (role_id, permission_id) DO NOTHING;

-- 编辑角色权限
INSERT INTO role_permissions (role_id, permission_id)
VALUES 
    ('33333333-3333-3333-3333-333333333333', '40000000-0000-0000-0000-000000000001'), -- content:create
    ('33333333-3333-3333-3333-333333333333', '40000000-0000-0000-0000-000000000002'), -- content:read
    ('33333333-3333-3333-3333-333333333333', '40000000-0000-0000-0000-000000000003'), -- content:update
    ('33333333-3333-3333-3333-333333333333', '40000000-0000-0000-0000-000000000005'), -- content:list
    ('33333333-3333-3333-3333-333333333333', '40000000-0000-0000-0000-000000000006'), -- content:publish
    ('33333333-3333-3333-3333-333333333333', '40000000-0000-0000-0000-000000000007')  -- content:unpublish
ON CONFLICT (role_id, permission_id) DO NOTHING;

-- 查看者角色权限
INSERT INTO role_permissions (role_id, permission_id)
VALUES 
    ('44444444-4444-4444-4444-444444444444', '40000000-0000-0000-0000-000000000002'), -- content:read
    ('44444444-4444-4444-4444-444444444444', '40000000-0000-0000-0000-000000000005')  -- content:list
ON CONFLICT (role_id, permission_id) DO NOTHING;

-- 创建默认管理员用户（密码：admin123）
INSERT INTO users (id, username, email, password_hash, first_name, last_name, active, verified, created_at, updated_at)
VALUES (
    '00000000-0000-0000-0000-000000000001',
    'admin',
    'admin@kace.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',  -- 加密后的 admin123
    'Admin',
    'User',
    TRUE,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
)
ON CONFLICT (username) DO NOTHING;

-- 为管理员分配管理员角色
INSERT INTO user_roles (user_id, role_id)
VALUES ('00000000-0000-0000-0000-000000000001', '11111111-1111-1111-1111-111111111111')
ON CONFLICT (user_id, role_id) DO NOTHING; 