package com.jozo.pricepredictionmodel.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlphaVantageBestMatch {
    private String symbol;
    private String name;
    private String type;
    private String region;
    private String marketOpen;
    private String marketClose;
    private String timezone;
    private String currency;
    private String matchScore;
}