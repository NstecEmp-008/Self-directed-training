
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

-- 株式テーブルを作成
CREATE TABLE IF NOT EXISTS stock (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    symbol VARCHAR(10) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL UNIQUE,
    price DECIMAL(10, 2) NOT NULL,
    change_percentage DECIMAL(5, 2) NOT NULL,
    last_updated TIMESTAMP NOT NULL,
    ALTER TABLE stock ADD COLUMN quantity INT NOT NULL DEFAULT 0;
);

CREATE TABLE wallet (
    user_id INT PRIMARY KEY,
    balance DECIMAL(15, 2) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES account(user_id)
);

CREATE TABLE user_stock (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    stock_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES account(user_id),
    FOREIGN KEY (stock_id) REFERENCES stock(id),
    UNIQUE (user_id, stock_id)
);

CREATE TABLE trade_history (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    stock_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    total_price DECIMAL(15, 2) NOT NULL,
    trade_type VARCHAR(10) NOT NULL, -- BUY or SELL
    trade_time TIMESTAMP NOT NULL DEFAULT NOW(),
    FOREIGN KEY (user_id) REFERENCES account(user_id),
    FOREIGN KEY (stock_id) REFERENCES stock(id)
);
