package com.jozo.pricepredictionmodel.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jozo.pricepredictionmodel.model.ModelInputDTO;
import com.jozo.pricepredictionmodel.model.News;
import com.jozo.pricepredictionmodel.model.StockPrice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service

public class CombineAndSendTodaysDataForPrediction {

    @Autowired
    FetchTodaysDataForPrediction fetchTodaysDataForPrediction;

    public String CombineAndSendTodaysDataForPrediction(String symbol) {
        try {
            StockPrice stockPrice = fetchTodaysDataForPrediction.getTodaysDataForPrediction(symbol);
            List<News> newsList = fetchTodaysDataForPrediction.getNews(symbol);


            double totalSentimentScore = 0.0;
            if (newsList != null && !newsList.isEmpty()) {
                for (News newsItem : newsList) {
                    totalSentimentScore += newsItem.getOverall_sentiment_score();
                }
                if (!newsList.isEmpty()) {
                    totalSentimentScore /= newsList.size();
                }
            }

            ModelInputDTO modelInputDTO = new ModelInputDTO();
            modelInputDTO.setSymbol(symbol);
            modelInputDTO.setOpen(stockPrice.getOpen());
            modelInputDTO.setHigh(stockPrice.getHigh());
            modelInputDTO.setLow(stockPrice.getLow());
            modelInputDTO.setClose(stockPrice.getClose());
            modelInputDTO.setVolume(stockPrice.getVolume());
            modelInputDTO.setDate(stockPrice.getDate());
            modelInputDTO.setOverallSentimentScore(totalSentimentScore);
            modelInputDTO.setLabel(0);

            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(new File(symbol + "_today_model.json"), modelInputDTO);

            HttpClient client = HttpClient.newHttpClient();

            Path filePath = Path.of(symbol + "_today_model.json");
            String json = Files.readString(filePath);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8000/predict?symbol=" + symbol))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Status: " + response.statusCode());
            System.out.println("Response: " + response.body());

            Files.deleteIfExists(filePath);

            return response.body();

        }
        catch (Exception e) {
            return e.getMessage();
        }
    }
}
