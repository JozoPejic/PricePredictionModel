package com.jozo.pricepredictionmodel.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jozo.pricepredictionmodel.model.News;
import com.jozo.pricepredictionmodel.repository.NewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Iterator;

@Service
public class NewsFetchService {

    @Autowired
    NewsRepository newsRepository;
    @Value("alphavantage.api.key")
    private String apiKey;

    public void fetchAndSaveNewsData(String symbol) throws Exception{

        //if (limit >= 1000) limit = 1000;

        String url = "https://www.alphavantage.co/query?function=NEWS_SENTIMENT&tickers="+ symbol +"&apikey=" + apiKey +"&limit=100";

        RestTemplate restTemplate = new RestTemplate();
        String json = restTemplate.getForObject(url, String.class);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(json);

        JsonNode feed = jsonNode.get("feed");

        if(feed != null && feed.isArray()){
            for(JsonNode rowNode : feed) {
                String dateStr = rowNode.get("time_published").asText().substring(0, 8);
                java.sql.Date sqlDate = java.sql.Date.valueOf(dateStr.substring(0, 4) + "-" + dateStr.substring(4, 6) + "-" + dateStr.substring(6));

                if(!newsRepository.existsByTickerAndDate(symbol, sqlDate)){
                    News news = new News();
                    news.setDate(sqlDate);
                    news.setTitle(rowNode.get("title").asText());
                    news.setSummary(rowNode.get("summary").asText());
                    news.setOverall_sentiment_score(rowNode.get("overall_sentiment_score").asDouble());
                    news.setOverall_sentiment_label(rowNode.get("overall_sentiment_label").asText());
                    news.setTicker(symbol);

                    JsonNode tickerSentimentRow = rowNode.get("ticker_sentiment");

                    if (tickerSentimentRow != null && tickerSentimentRow.isArray()) {
                        for (JsonNode ts : tickerSentimentRow) {
                            if (ts.get("ticker").asText().equals(symbol)) {
                                news.setRelevance_score(ts.get("relevance_score").asDouble());
                                news.setTicker_sentiment_score(ts.get("ticker_sentiment_score").asDouble());
                                news.setTicker_sentiment_label(ts.get("ticker_sentiment_label").asText());
                                break;
                            }
                        }
                    }

                    newsRepository.save(news);
                }
            }
        }
    }
}
