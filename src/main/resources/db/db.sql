-- ロールテーブルを作成
CREATE TABLE account_role (
    role_id SERIAL PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL UNIQUE
);

-- アカウントテーブルを作成
CREATE TABLE account (
    user_id SERIAL PRIMARY KEY,
    user_name VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role_id INT NOT NULL,
    FOREIGN KEY (role_id) REFERENCES account_role(role_id)
);

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
