-- CNI-BBS-TitanCore 数据库初始化脚本
-- 此脚本在 MySQL 容器首次启动时自动执行

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `titan_bbs` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 创建应用用户并授权
CREATE USER IF NOT EXISTS 'titan_bbs'@'%' IDENTIFIED BY 'titan_bbs_2026';
GRANT ALL PRIVILEGES ON `titan_bbs`.* TO 'titan_bbs'@'%';

-- 刷新权限
FLUSH PRIVILEGES;

-- 切换到 titan_bbs 数据库
USE `titan_bbs`;

-- 提示信息
SELECT 'Database titan_bbs created successfully!' AS message;
SELECT 'User titan_bbs created with password: titan_bbs_2026' AS message;
SELECT 'Please import the latest SQL schema from sql/ directory' AS message;
