-- 初始化管理員用戶的 SQL 腳本
-- 密碼是 "admin123" 的 MD5 值

INSERT INTO t_user (id, username, password, nickname, email, avatar, type, create_time, update_time)
VALUES (1, 'admin', '0192023a7bbd73250516f069df18b500', '管理員', 'admin@example.com', '/images/avatar.png', 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE username = username;

-- 注意：如果表使用自動生成 ID，可能需要調整上面的 SQL
-- 密碼 "admin123" 的 MD5 值是：0192023a7bbd73250516f069df18b500
