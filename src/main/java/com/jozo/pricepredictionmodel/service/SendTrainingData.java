package com.jozo.pricepredictionmodel.service;

import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class SendTrainingData {
    public String sendData(String symbol) throws Exception {
        HttpClient client = HttpClient.newHttpClient();


        Path filePath = Path.of(symbol + "_data.json");
        String json = Files.readString(filePath);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8000/train?symbol=" + symbol))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Status: " + response.statusCode());
        System.out.println("Response: " + response.body());

        return response.body();
    }
}
