package com.jozo.pricepredictionmodel.service;

import com.jozo.pricepredictionmodel.DTO.UserStockDTO;
import com.jozo.pricepredictionmodel.model.StockPrice;
import com.jozo.pricepredictionmodel.model.User;
import com.jozo.pricepredictionmodel.model.UserStock;
import com.jozo.pricepredictionmodel.repository.StockPriceRepository;
import com.jozo.pricepredictionmodel.repository.UserRepository;
import com.jozo.pricepredictionmodel.repository.UserStockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StockDetailDisplayService {

    @Autowired
    FetchTodaysDataForPrediction fetchTodaysDataForPrediction;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserStockRepository userStockRepository;
    @Autowired
    private StockPriceRepository stockPriceRepository;

    public List<StockPrice> getStockDetail(String userEmail) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<UserStock> userStocks = userStockRepository.findByUser(user);

        List<StockPrice> stockPrices = new ArrayList<>();

        for(UserStock userStock : userStocks) {
            StockPrice stockPrice = new StockPrice();
            try {
                stockPrice = fetchTodaysDataForPrediction.getTodaysDataForPrediction(userStock.getSymbol());
            }
            catch(Exception e) {
                e.printStackTrace();
            }
            stockPrices.add(stockPrice);
        }

        return stockPrices;
    }

    public List<List<StockPrice>> getStockPriceForChart(String userEmail){
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<UserStock> userStocks = userStockRepository.findByUser(user);

        List<List<StockPrice>> stockPrices = new ArrayList<List<StockPrice>>();

        for(UserStock userStock : userStocks) {
            List<StockPrice> stock = stockPriceRepository.findAllBySymbolOrderByDateAsc(userStock.getSymbol());
            stockPrices.add(stock);
        }

        return stockPrices;
    }
}
