CREATE TABLE stock_price(

    id SERIAL PRIMARY KEY,
    date DATE NOT NULL,
    open DOUBLE PRECISION,
    high DOUBLE PRECISION,
    low DOUBLE PRECISION,
    close DOUBLE PRECISION,
    adj_clsoe DOUBLE PRECISION,
    volume BIGINT
);