-- Nacos初始化脚本
-- 创建默认用户和权限

-- 插入默认用户 (用户名: nacos, 密码: nacos)
INSERT INTO users (username, password, enabled) VALUES ('nacos', '$2a$10$EuWPZHzz32dJN7jexM34MOeYirDdFAZm2kuWj7VEOJhhZkDrxfvUu', TRUE)
ON DUPLICATE KEY UPDATE password = '$2a$10$EuWPZHzz32dJN7jexM34MOeYirDdFAZm2kuWj7VEOJhhZkDrxfvUu';

-- 插入角色权限
INSERT INTO roles (username, role) VALUES ('nacos', 'ROLE_ADMIN')
ON DUPLICATE KEY UPDATE role = 'ROLE_ADMIN';

-- 创建命名空间权限 (如果表存在的话)
-- INSERT IGNORE INTO permissions (role, resource, action) VALUES ('ROLE_ADMIN', '*', '*');

-- 说明：
-- 1. 默认用户名: nacos
-- 2. 默认密码: nacos (BCrypt加密后的hash)
-- 3. 角色: ROLE_ADMIN (管理员权限)
-- 4. 微服务可以使用这个用户名密码进行注册
