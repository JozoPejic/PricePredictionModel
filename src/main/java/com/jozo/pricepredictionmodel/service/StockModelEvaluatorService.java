package com.jozo.pricepredictionmodel.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jozo.pricepredictionmodel.model.ModelInputDTO;
import com.jozo.pricepredictionmodel.model.StockPrice;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class StockModelEvaluatorService {

    @Autowired
    private StockPriceFetchService stockPriceFetchService;

    private static final int NUM_RANDOM_DAYS_FOR_EVALUATION = 100;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public String evaluatePredictionModel(String symbol) throws Exception {
        List<StockPrice> historicalPrices = stockPriceFetchService.getHistoricalStockPrices(symbol.toUpperCase());

        if (historicalPrices == null || historicalPrices.size() < NUM_RANDOM_DAYS_FOR_EVALUATION + 2) {
            throw new IllegalArgumentException("Nedovoljno povijesnih podataka za evaluaciju za simbol: " + symbol +
                    ". Potrebno " + (NUM_RANDOM_DAYS_FOR_EVALUATION + 2) + " dana.");
        }

        Random random = new Random();
        int correctPredictions = 0;
        int totalEvaluations = 0;

        int maxStartIndex = historicalPrices.size() - 2;

        if (maxStartIndex < 0) {
            throw new IllegalArgumentException("Nedovoljno podataka za odabir uzoraka za evaluaciju.");
        }

        for (int i = 0; i < NUM_RANDOM_DAYS_FOR_EVALUATION; i++) {
            int randomIndex = random.nextInt(maxStartIndex + 1);
            StockPrice currentDayData = historicalPrices.get(randomIndex);
            StockPrice nextDayData = historicalPrices.get(randomIndex + 1);

            ModelInputDTO modelInputDTO = new ModelInputDTO();
            modelInputDTO.setSymbol(symbol.toUpperCase());
            modelInputDTO.setOpen(currentDayData.getOpen());
            modelInputDTO.setHigh(currentDayData.getHigh());
            modelInputDTO.setLow(currentDayData.getLow());
            modelInputDTO.setClose(currentDayData.getClose());
            modelInputDTO.setVolume(currentDayData.getVolume());
            modelInputDTO.setDate(currentDayData.getDate());

            modelInputDTO.setOverallSentimentScore(0.0);

            modelInputDTO.setLabel(0);

            int predictedDirection = callPredictionMicroservice(symbol.toUpperCase(), modelInputDTO);

            double actualChange = nextDayData.getClose() - currentDayData.getClose();
            int actualDirection = (actualChange > 0) ? 1 : 0;

            if (actualDirection == predictedDirection) {
                correctPredictions++;
            }
            totalEvaluations++;
        }

        return String.format("Evaluacija za %s: Model je pogodio smjer cijene %d/%d puta. Preciznost: %.2f%%",
                symbol.toUpperCase(), correctPredictions, totalEvaluations,
                (double) correctPredictions / totalEvaluations * 100);
    }

    private int callPredictionMicroservice(String symbol, ModelInputDTO modelInputDTO)
            throws IOException, InterruptedException {

        Path filePath = Files.createTempFile(symbol + "_model_input_", ".json");
        try {
            objectMapper.writeValue(filePath.toFile(), modelInputDTO);

            String json = Files.readString(filePath);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8000/predict?symbol=" + symbol))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode rootNode = objectMapper.readTree(response.body());
                JsonNode predictionNode = rootNode.get("prediction");

                if (predictionNode != null && predictionNode.isInt()) {
                    return predictionNode.asInt();
                } else {
                    throw new IOException("Microservice response did not contain a valid 'prediction' integer: " + response.body());
                }
            } else {
                throw new IOException("Microservice returned error status: " + response.statusCode() + " - " + response.body());
            }
        } finally {
            Files.deleteIfExists(filePath);
        }
    }
}