CREATE TABLE user_stock(

    id SERIAL PRIMARY KEY,
    user_id INT,
    stock_id int,

    CONSTRAINT fk_user
                       FOREIGN KEY (user_id)
                       REFERENCES users(id)
                       ON DELETE CASCADE,
    CONSTRAINT fk_stock
                       FOREIGN KEY (stock_id)
                       REFERENCES stock_price(id)
                       ON DELETE CASCADE
);