package com.jozo.pricepredictionmodel.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;


@Entity
@Getter
@Setter

public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String title;
    private String summary;
    private Date date;
    private String ticker;
    private double overall_sentiment_score;
    private String overall_sentiment_label;
    private double relevance_score;
    private double ticker_sentiment_score;
    private String ticker_sentiment_label;
}
