-- 微服务认证架构数据库初始化脚本

-- 创建用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 创建角色表
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(200),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建用户角色关联表
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT,
    role_id BIGINT,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- 创建权限表
CREATE TABLE IF NOT EXISTS permissions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(200),
    resource VARCHAR(100),
    action VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建角色权限关联表
CREATE TABLE IF NOT EXISTS role_permissions (
    role_id BIGINT,
    permission_id BIGINT,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
);

-- 创建审计日志表
CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    action VARCHAR(100) NOT NULL,
    resource VARCHAR(100),
    resource_id VARCHAR(100),
    ip_address VARCHAR(45),
    user_agent TEXT,
    status VARCHAR(20),
    message TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 插入基础角色数据
INSERT IGNORE INTO roles (name, description) VALUES 
('ADMIN', '系统管理员'),
('USER', '普通用户'),
('MANAGER', '管理者');

-- 插入基础权限数据
INSERT IGNORE INTO permissions (name, description, resource, action) VALUES 
('USER_READ', '读取用户信息', 'user', 'read'),
('USER_WRITE', '修改用户信息', 'user', 'write'),
('USER_DELETE', '删除用户', 'user', 'delete'),
('ADMIN_READ', '读取管理信息', 'admin', 'read'),
('ADMIN_WRITE', '修改管理信息', 'admin', 'write');

-- 分配角色权限
INSERT IGNORE INTO role_permissions (role_id, permission_id) VALUES 
-- 管理员拥有所有权限
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5),
-- 普通用户只有读取权限
(2, 1),
-- 管理者有读写权限但不能删除用户
(3, 1), (3, 2), (3, 4), (3, 5);

-- 插入测试用户数据（密码已加密）
INSERT IGNORE INTO users (username, password, email, phone) VALUES 
('admin', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'admin@example.com', '13800138000'),
('testuser', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'test@example.com', '13800138001'),
('manager', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'manager@example.com', '13800138002');

-- 分配用户角色
INSERT IGNORE INTO user_roles (user_id, role_id) VALUES 
(1, 1), -- admin用户拥有管理员角色
(2, 2), -- testuser用户拥有普通用户角色
(3, 3); -- manager用户拥有管理者角色

-- 创建索引优化查询性能
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_audit_logs_user_id ON audit_logs(user_id);
CREATE INDEX IF NOT EXISTS idx_audit_logs_created_at ON audit_logs(created_at);

-- 显示初始化完成信息
SELECT '数据库初始化完成！' as message;
SELECT COUNT(*) as user_count FROM users;
SELECT COUNT(*) as role_count FROM roles;
SELECT COUNT(*) as permission_count FROM permissions;
