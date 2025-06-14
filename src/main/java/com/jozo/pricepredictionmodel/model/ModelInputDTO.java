package com.jozo.pricepredictionmodel.model;

import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
public class ModelInputDTO {

    private Date date;
    private String symbol;
    private Double open;
    private Double high;
    private Double low;
    private Double close;
    private int volume;
    private Double overallSentimentScore;
    private int label;
}
