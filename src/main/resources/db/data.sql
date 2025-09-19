-- -- ロールテーブルに初期データを挿入
-- INSERT INTO account_role (role_name) VALUES
--   ('admin'),
--   ('user'),
--   ('guest')
-- ON CONFLICT (role_name) DO NOTHING;

-- -- アカウントデータの挿入（仮のパスワードを使用）
-- -- 実際には passwordEncoder で暗号化された文字列を使うべきです
-- INSERT INTO account (user_name, password, role_id) VALUES
--   ('admin_user', '$2a$10$exampleEncryptedPassword1', (SELECT role_id FROM account_role WHERE role_name = 'admin')),
--   ('normal_user', '$2a$10$exampleEncryptedPassword2', (SELECT role_id FROM account_role WHERE role_name = 'user')),
--   ('guest_user', '$2a$10$exampleEncryptedPassword3', (SELECT role_id FROM account_role WHERE role_name = 'guest'))
-- ON CONFLICT (user_name) DO NOTHING;



-- ロールテーブルに初期データを挿入
-- Spring Securityの仕様に合わせて、ロール名に "ROLE_" プレフィックスを付けます
INSERT INTO account_role (role_name) VALUES
    ('ROLE_ADMIN'),
    ('ROLE_USER')
ON CONFLICT (role_name) DO NOTHING;

-- アカウントデータの挿入
-- `BCryptPasswordEncoder`で暗号化されたパスワードを使用してください
-- この例では、プレースホルダーとして仮の文字列を使用しています
INSERT INTO account (user_name, password, role_id) VALUES
    ('admin_user', '$2a$10$exampleEncryptedPassword1', (SELECT role_id FROM account_role WHERE role_name = 'ROLE_ADMIN')),
    ('normal_user', '$2a$10$exampleEncryptedPassword2', (SELECT role_id FROM account_role WHERE role_name = 'ROLE_USER'))
ON CONFLICT (user_name) DO NOTHING;

-- 株式データの挿入
INSERT INTO stock (symbol, name, price, change_percentage, last_updated) VALUES
('AAPL', 'Apple Inc.', 175.50, 0.50, NOW()),
('MSFT', 'Microsoft Corp.', 340.25, 1.25, NOW()),
('GOOG', 'Alphabet Inc.', 140.75, -0.75, NOW()),
('AMZN', 'Amazon.com, Inc.', 130.10, 2.10, NOW());
