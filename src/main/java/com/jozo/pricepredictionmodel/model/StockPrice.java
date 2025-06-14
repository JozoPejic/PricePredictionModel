package com.jozo.pricepredictionmodel.model;

import jakarta.persistence.*;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.time.LocalDate;

@Entity
@Table(
        name = "stock_price",
        uniqueConstraints = @UniqueConstraint(columnNames = {"date", "symbol"})
)
@Getter
@Setter
public class StockPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private Date date;

    private double open;
    private double high;
    private double low;
    private double close;

    private String symbol;

    //@Column(name="adj_close")
    //private double adjClose;

    private int volume;
}
