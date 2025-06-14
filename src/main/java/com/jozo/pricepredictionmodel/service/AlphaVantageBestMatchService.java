package com.jozo.pricepredictionmodel.service; // Adjust package as needed

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jozo.pricepredictionmodel.DTO.AlphaVantageBestMatch;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@Service
public class AlphaVantageBestMatchService {

    @Value("${alphavantage.api.key}")
    private String apiKey;

    @Value("${alphavantage.base.url}")
    private String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<AlphaVantageBestMatch> searchSymbols(String keywords) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("function", "SYMBOL_SEARCH")
                .queryParam("keywords", keywords)
                .queryParam("apikey", apiKey)
                .toUriString();

        String jsonResponse = restTemplate.getForObject(url, String.class);

        List<AlphaVantageBestMatch> results = new ArrayList<>();
        try {
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            JsonNode bestMatchesNode = rootNode.get("bestMatches");

            if (bestMatchesNode != null && bestMatchesNode.isArray()) {
                for (JsonNode matchNode : bestMatchesNode) {
                    AlphaVantageBestMatch match = new AlphaVantageBestMatch();
                    match.setSymbol(matchNode.get("1. symbol").asText());
                    match.setName(matchNode.get("2. name").asText());
                    match.setType(matchNode.get("3. type").asText());
                    match.setRegion(matchNode.get("4. region").asText());
                    match.setMarketOpen(matchNode.get("5. marketOpen").asText());
                    match.setMarketClose(matchNode.get("6. marketClose").asText());
                    match.setTimezone(matchNode.get("7. timezone").asText());
                    match.setCurrency(matchNode.get("8. currency").asText());
                    match.setMatchScore(matchNode.get("9. matchScore").asText());
                    results.add(match);
                }
            } else if (rootNode.has("Error Message")) {
                System.err.println("Alpha Vantage API Error: " + rootNode.get("Error Message").asText());
            } else if (rootNode.has("Note")) {
                System.err.println("Alpha Vantage API Note: " + rootNode.get("Note").asText());
            }

        } catch (Exception e) {
            System.err.println("Error parsing Alpha Vantage SYMBOL_SEARCH response: " + e.getMessage());
            e.printStackTrace();
        }
        return results;
    }
}