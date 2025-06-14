ALTER TABLE IF EXISTS stock_price RENAME COLUMN adj_clsoe TO adj_close;
ALTER TABLE IF EXISTS stock_price ADD COLUMN symbol VARCHAR(10);