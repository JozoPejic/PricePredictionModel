package com.jozo.pricepredictionmodel.service;

import com.jozo.pricepredictionmodel.DTO.UserStockDTO;
import com.jozo.pricepredictionmodel.model.User;
import com.jozo.pricepredictionmodel.model.UserStock;
import com.jozo.pricepredictionmodel.repository.UserRepository;
import com.jozo.pricepredictionmodel.repository.UserStockRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserStockService {

    private final UserStockRepository userStockRepository;
    private final StockPriceFetchService stockPriceFetchService;
    private final UserRepository userRepository;
    private final NewsFetchService newsFetchService;

    @Value("${user.stock.limit:3}")
    private int userStockLimit;

    @Transactional
    public UserStock addStockToUserPortfolio(String symbol, String userEmail) throws Exception {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + userEmail));

        if (userStockRepository.existsByUserAndSymbol(user, symbol)) {
            throw new IllegalArgumentException("User already follows stock: " + symbol);
        }

        List<UserStock> currentUserStocks = userStockRepository.findByUser(user);
        if(currentUserStocks.size() >= userStockLimit) {
            throw new IllegalArgumentException("Stock limit exceeded");
        }

        UserStock userStock = new UserStock();
        userStock.setUser(user);
        userStock.setSymbol(symbol);

        stockPriceFetchService.fetchAndSaveStockData(symbol);
        newsFetchService.fetchAndSaveNewsData(symbol);

        return userStockRepository.save(userStock);
    }
    @Transactional
    public void removeStockFromUserPortfolio(String symbol, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + userEmail));

        userStockRepository.deleteByUserAndSymbol(user, symbol);
    }

    public List<UserStockDTO> getUserPortfolioDTO(String userEmail) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + userEmail));

        return userStockRepository.findByUser(user)
                .stream()
                .map(userStock -> {
                    UserStockDTO dto = new UserStockDTO();
                    dto.setSymbol(userStock.getSymbol());
                    return dto;
                })
                .collect(Collectors.toList());

    }

}