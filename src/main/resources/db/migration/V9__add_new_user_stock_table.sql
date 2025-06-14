CREATE TABLE user_stock (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    symbol VARCHAR(10) NOT NULL,

    CONSTRAINT fk_user_stock
                        FOREIGN KEY (user_id)
                        REFERENCES users(id)
                        ON DELETE CASCADE,
    CONSTRAINT uk_user_stock_symbol
                        UNIQUE (user_id, symbol)
);