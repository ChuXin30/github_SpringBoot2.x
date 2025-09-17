-- 用户表建表语句
-- 基于Rust风格接口的User模型设计

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS learn_springboot 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE learn_springboot;

-- 删除表（如果存在）
DROP TABLE IF EXISTS `user`;

-- 创建用户表
CREATE TABLE `user` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID，主键',
    `name` VARCHAR(100) NOT NULL COMMENT '用户姓名',
    `email` VARCHAR(255) NOT NULL COMMENT '用户邮箱',
    `age` INT(3) NULL DEFAULT NULL COMMENT '用户年龄',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_email` (`email`) COMMENT '邮箱唯一索引',
    KEY `idx_user_name` (`name`) COMMENT '姓名索引',
    KEY `idx_user_created_at` (`created_at`) COMMENT '创建时间索引',
    KEY `idx_user_updated_at` (`updated_at`) COMMENT '更新时间索引'
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_unicode_ci 
  COMMENT='用户信息表';

-- 创建用户状态表（扩展功能）
DROP TABLE IF EXISTS `user_status`;

CREATE TABLE `user_status` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '状态ID，主键',
    `user_id` BIGINT(20) NOT NULL COMMENT '用户ID，外键',
    `status` ENUM('ACTIVE', 'INACTIVE', 'SUSPENDED', 'DELETED') NOT NULL DEFAULT 'ACTIVE' COMMENT '用户状态',
    `status_reason` VARCHAR(500) NULL DEFAULT NULL COMMENT '状态变更原因',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '状态创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '状态更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_status_user_id` (`user_id`) COMMENT '用户状态唯一索引',
    KEY `idx_user_status_status` (`status`) COMMENT '状态索引',
    CONSTRAINT `fk_user_status_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_unicode_ci 
  COMMENT='用户状态表';

-- 创建用户配置表（扩展功能）
DROP TABLE IF EXISTS `user_config`;

CREATE TABLE `user_config` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '配置ID，主键',
    `user_id` BIGINT(20) NOT NULL COMMENT '用户ID，外键',
    `config_key` VARCHAR(100) NOT NULL COMMENT '配置键',
    `config_value` TEXT NULL DEFAULT NULL COMMENT '配置值',
    `config_type` ENUM('STRING', 'NUMBER', 'BOOLEAN', 'JSON') NOT NULL DEFAULT 'STRING' COMMENT '配置类型',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '配置创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '配置更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_config_user_key` (`user_id`, `config_key`) COMMENT '用户配置唯一索引',
    KEY `idx_user_config_key` (`config_key`) COMMENT '配置键索引',
    CONSTRAINT `fk_user_config_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_unicode_ci 
  COMMENT='用户配置表';

-- 插入示例数据
INSERT INTO `user` (`name`, `email`, `age`) VALUES
('张三', 'zhangsan@example.com', 25),
('李四', 'lisi@example.com', 30),
('王五', 'wangwu@example.com', 28),
('赵六', 'zhaoliu@example.com', 35),
('钱七', 'qianqi@example.com', 22);

-- 插入用户状态数据
INSERT INTO `user_status` (`user_id`, `status`) VALUES
(1, 'ACTIVE'),
(2, 'ACTIVE'),
(3, 'INACTIVE'),
(4, 'ACTIVE'),
(5, 'SUSPENDED');

-- 插入用户配置数据
INSERT INTO `user_config` (`user_id`, `config_key`, `config_value`, `config_type`) VALUES
(1, 'theme', 'dark', 'STRING'),
(1, 'language', 'zh-CN', 'STRING'),
(1, 'notifications', 'true', 'BOOLEAN'),
(2, 'theme', 'light', 'STRING'),
(2, 'language', 'en-US', 'STRING'),
(3, 'theme', 'auto', 'STRING');

-- 创建视图：用户完整信息视图
CREATE OR REPLACE VIEW `v_user_complete` AS
SELECT 
    u.id,
    u.name,
    u.email,
    u.age,
    u.created_at,
    u.updated_at,
    us.status,
    us.status_reason,
    us.created_at as status_created_at,
    us.updated_at as status_updated_at
FROM `user` u
LEFT JOIN `user_status` us ON u.id = us.user_id;

-- 创建存储过程：获取用户完整信息
DELIMITER //
CREATE PROCEDURE `sp_get_user_complete`(IN p_user_id BIGINT)
BEGIN
    SELECT 
        u.id,
        u.name,
        u.email,
        u.age,
        u.created_at,
        u.updated_at,
        us.status,
        us.status_reason,
        us.created_at as status_created_at,
        us.updated_at as status_updated_at
    FROM `user` u
    LEFT JOIN `user_status` us ON u.id = us.user_id
    WHERE u.id = p_user_id;
END //
DELIMITER ;

-- 创建存储过程：批量创建用户
DELIMITER //
CREATE PROCEDURE `sp_batch_create_users`(
    IN p_names TEXT,
    IN p_emails TEXT,
    IN p_ages TEXT
)
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE v_name VARCHAR(100);
    DECLARE v_email VARCHAR(255);
    DECLARE v_age INT;
    DECLARE v_user_id BIGINT;
    
    -- 声明游标
    DECLARE name_cursor CURSOR FOR 
        SELECT TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(p_names, ',', numbers.n), ',', -1)) as name
        FROM (SELECT 1 n UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5) numbers
        WHERE CHAR_LENGTH(p_names) - CHAR_LENGTH(REPLACE(p_names, ',', '')) >= numbers.n - 1;
    
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
    -- 开始事务
    START TRANSACTION;
    
    OPEN name_cursor;
    
    read_loop: LOOP
        FETCH name_cursor INTO v_name;
        IF done THEN
            LEAVE read_loop;
        END IF;
        
        -- 这里可以添加更多的逻辑来处理批量插入
        -- 为了简化，这里只是示例
        
    END LOOP;
    
    CLOSE name_cursor;
    
    COMMIT;
END //
DELIMITER ;

-- 创建触发器：用户创建后自动创建状态记录
DELIMITER //
CREATE TRIGGER `tr_user_after_insert` 
AFTER INSERT ON `user`
FOR EACH ROW
BEGIN
    INSERT INTO `user_status` (`user_id`, `status`) 
    VALUES (NEW.id, 'ACTIVE');
END //
DELIMITER ;

-- 创建触发器：用户删除前清理相关数据
DELIMITER //
CREATE TRIGGER `tr_user_before_delete` 
BEFORE DELETE ON `user`
FOR EACH ROW
BEGIN
    -- 删除用户配置
    DELETE FROM `user_config` WHERE `user_id` = OLD.id;
    -- 删除用户状态
    DELETE FROM `user_status` WHERE `user_id` = OLD.id;
END //
DELIMITER ;

-- 显示表结构
DESCRIBE `user`;
DESCRIBE `user_status`;
DESCRIBE `user_config`;

-- 显示索引信息
SHOW INDEX FROM `user`;
SHOW INDEX FROM `user_status`;
SHOW INDEX FROM `user_config`;

-- 显示视图
SHOW CREATE VIEW `v_user_complete`;

-- 显示存储过程
SHOW CREATE PROCEDURE `sp_get_user_complete`;
SHOW CREATE PROCEDURE `sp_batch_create_users`;

-- 显示触发器
SHOW TRIGGERS LIKE 'user';
