package com.jozo.pricepredictionmodel.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jozo.pricepredictionmodel.model.News;
import com.jozo.pricepredictionmodel.model.StockPrice;
import com.jozo.pricepredictionmodel.repository.NewsRepository;
import com.jozo.pricepredictionmodel.repository.StockPriceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class FetchTodaysDataForPrediction {

    @Autowired
    StockPriceRepository stockPriceRepository;
    @Autowired
    NewsRepository newsRepository;
    @Value("${alphavantage.api.key}")
    private String apiKey;

    public StockPrice getTodaysDataForPrediction(String symbol) throws Exception {

        RestTemplate restTemplate = new RestTemplate();
        StockPrice stockPrice = new StockPrice();
        String url = "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol="+symbol+"&apikey=demo";// + apiKey;
        String json = restTemplate.getForObject(url, String.class);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(json);

        JsonNode globalQuoteNode = rootNode.get("Global Quote");

        if (globalQuoteNode != null && globalQuoteNode.isObject()) {
            stockPrice.setSymbol(symbol);
            stockPrice.setDate(Date.valueOf(LocalDate.now()));

            stockPrice.setOpen(globalQuoteNode.get("02. open").asDouble());
            stockPrice.setHigh(globalQuoteNode.get("03. high").asDouble());
            stockPrice.setLow(globalQuoteNode.get("04. low").asDouble());
            stockPrice.setClose(globalQuoteNode.get("05. price").asDouble());
            stockPrice.setVolume(globalQuoteNode.get("06. volume").asInt());
        } else {
            throw new RuntimeException("Invalid or missing 'Global Quote' data in API response for symbol: " + symbol);
        }

        return stockPrice;
    }

    public List<News> getNews(String symbol) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        List<News> newsList = new ArrayList<>();

        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmm");
        String timeFrom = startOfToday.format(formatter);
        String url = "https://www.alphavantage.co/query?function=NEWS_SENTIMENT"+
                "&tickers="+ symbol +
                //"&time_from="+ timeFrom +
                "&apikey=demo";// + apiKey;

        String json = restTemplate.getForObject(url, String.class);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode;
        try {
            rootNode = mapper.readTree(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse news JSON: " + e.getMessage(), e);
        }

        JsonNode feed = rootNode.get("feed");

        if(feed != null && feed.isArray()) {
            for(JsonNode rowNode : feed) {
                News news = new News();

                String dateStr = rowNode.get("time_published").asText().substring(0, 8);
                java.sql.Date sqlDate = java.sql.Date.valueOf(dateStr.substring(0, 4) + "-" + dateStr.substring(4, 6) + "-" + dateStr.substring(6));
                news.setDate(sqlDate);

                news.setOverall_sentiment_score(rowNode.get("overall_sentiment_score").asDouble());

                news.setOverall_sentiment_label(null);

                news.setTicker(symbol);
                news.setTicker_sentiment_score(0.0);
                news.setTicker_sentiment_label(null);
                news.setRelevance_score(0.0);
                news.setSummary(rowNode.get("summary").asText());
                news.setTitle(rowNode.get("title").asText());

                newsList.add(news);
            }
        }
        return newsList;
    }
}
