package com.jozo.pricepredictionmodel.service;

import com.jozo.pricepredictionmodel.model.News;
import com.jozo.pricepredictionmodel.model.StockPrice;
import com.jozo.pricepredictionmodel.model.User;
import com.jozo.pricepredictionmodel.model.UserStock;
import com.jozo.pricepredictionmodel.repository.UserRepository;
import com.jozo.pricepredictionmodel.repository.UserStockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NewsDetailDisplayService {

    @Autowired
    FetchTodaysDataForPrediction fetchTodaysDataForPrediction;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserStockRepository userStockRepository;

    public List<News> displayNews(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<UserStock> userStocks = userStockRepository.findByUser(user);

        List<News> allNews = new ArrayList<>();

        for (UserStock userStock : userStocks) {
            String symbol = userStock.getSymbol();
            try {
                List<News> newsForStock = fetchTodaysDataForPrediction.getNews(symbol);
                allNews.addAll(newsForStock);
            } catch (Exception e) {
                System.err.println("Error fetching news for symbol " + symbol + ": " + e.getMessage());
            }
        }
        return allNews;
    }
}
