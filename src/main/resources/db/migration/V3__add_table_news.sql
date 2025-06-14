CREATE TABLE news(

    id SERIAL PRIMARY KEY,
    title VARCHAR(255),
    summary TEXT,
    date DATE NOT NULL,
    ticker VARCHAR(10),
    overall_sentiment_score DOUBLE PRECISION,
    relevance_score DOUBLE PRECISION,
    ticker_sentiment_score DOUBLE PRECISION,
    overall_sentiment_label VARCHAR(15),
    ticker_sentiment_label VARCHAR(15)
);