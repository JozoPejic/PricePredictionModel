package com.jozo.pricepredictionmodel.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jozo.pricepredictionmodel.model.StockPrice;
import com.jozo.pricepredictionmodel.repository.StockPriceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.Date;
import java.util.Iterator;
import java.util.List;

@Service
public class StockPriceFetchService {

    @Autowired
    private StockPriceRepository stockPriceRepository;
    @Value("alphavantage.api.key")
    private String apiKey;


    public void fetchAndSaveStockData(String symbol) throws Exception {
        String url = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=" + symbol + "&outputsize=full&apikey=" + apiKey;

        RestTemplate restTemplate = new RestTemplate();
        String json = restTemplate.getForObject(url, String.class);

        //System.out.println(json);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(json);

        JsonNode timeSeries = jsonNode.get("Time Series (Daily)");

        for (Iterator<String> it = timeSeries.fieldNames(); it.hasNext(); ) {
            String dateStr = it.next();
            JsonNode dailyData = timeSeries.get(dateStr);
            Date sqlDate = Date.valueOf(dateStr);

            if(!stockPriceRepository.existsByDateAndSymbol(sqlDate, symbol)) {
                StockPrice stockPrice = new StockPrice();
                stockPrice.setDate(java.sql.Date.valueOf(dateStr));
                stockPrice.setOpen(dailyData.get("1. open").asDouble());
                stockPrice.setHigh(dailyData.get("2. high").asDouble());
                stockPrice.setLow(dailyData.get("3. low").asDouble());
                stockPrice.setClose(dailyData.get("4. close").asDouble());
                //stockPrice.setAdjClose(dailyData.get("5. adjusted close").asDouble());
                stockPrice.setVolume(dailyData.get("5. volume").asInt());
                stockPrice.setSymbol(symbol);

                stockPriceRepository.save(stockPrice);
            }
        }
    }

    public List<StockPrice> getHistoricalStockPrices(String symbol) {
        return stockPriceRepository.findBySymbolOrderByDateAsc(symbol);
    }
}
