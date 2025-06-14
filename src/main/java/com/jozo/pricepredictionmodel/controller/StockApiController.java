package com.jozo.pricepredictionmodel.controller;

import com.jozo.pricepredictionmodel.DTO.AlphaVantageBestMatch;
import com.jozo.pricepredictionmodel.DTO.UserStockDTO;
import com.jozo.pricepredictionmodel.model.News;
import com.jozo.pricepredictionmodel.model.StockPrice;
import com.jozo.pricepredictionmodel.model.User;
import com.jozo.pricepredictionmodel.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class StockApiController {

    @Autowired
    private ExportToJson exportToJson;
    @Autowired
    private NewsFetchService newsFetchService;
    @Autowired
    private CombineAndSendTodaysDataForPrediction combineTodaysDataForPrediction;
    @Autowired
    private SendTrainingData sendTrainingData;
    @Autowired
    private StockPriceFetchService stockPriceFetchService;
    @Autowired
    private AlphaVantageBestMatchService alphaVantageBestMatchService;
    @Autowired
    private UserStockService userStockService;
    @Autowired
    private StockDetailDisplayService stockDetailDisplayService;
    @Autowired
    private NewsDetailDisplayService newsDetailDisplayService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private StockModelEvaluatorService stockModelEvaluatorService;

    @DeleteMapping("/admin/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        try {
            adminService.deleteUser(userId);
            return ResponseEntity.ok("User with ID " + userId + " deleted successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting user: " + e.getMessage());
        }
    }

    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        try {
            List<User> users = adminService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/stocks/fetch/{symbol}")
    public String fetchStockPrice(@PathVariable String symbol) {
        try {
            stockPriceFetchService.fetchAndSaveStockData(symbol.toUpperCase());
            return "Data saved for: " + symbol.toUpperCase();
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @GetMapping("/news/fetch/{symbol}")
    @PreAuthorize("isAuthenticated()")
    public String fetchNews(@PathVariable String symbol){
        try{
            newsFetchService.fetchAndSaveNewsData(symbol.toUpperCase());
            return "News saved for " + symbol.toUpperCase();
        }catch(Exception e){
            return e.getMessage();
        }
    }

    @GetMapping("/stocks/history/{symbol}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<StockPrice>> getHistoricalPrices(@PathVariable String symbol) {
        try {
            List<StockPrice> historicalPrices = stockPriceFetchService.getHistoricalStockPrices(symbol.toUpperCase());
            if (historicalPrices.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok(historicalPrices);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/evaluate/{symbol}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> evaluateModel(@PathVariable String symbol, Authentication authentication) {
        try {
            String evaluationResult = stockModelEvaluatorService.evaluatePredictionModel(symbol.toUpperCase());
            return ResponseEntity.ok(evaluationResult);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Greška evaluacije: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Došlo je do interne greške prilikom evaluacije modela: " + e.getMessage());
        }
    }

    @GetMapping("/stocks/search")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<AlphaVantageBestMatch>> searchStocks(
            @RequestParam String query,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(List.of());
        }

        List<AlphaVantageBestMatch> results = alphaVantageBestMatchService.searchSymbols(query);
        return ResponseEntity.ok(results);
    }


    @GetMapping("/portfolio")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<UserStockDTO>> getMyStocks(Authentication authentication) {
        String userEmail = authentication.getName();
        List<UserStockDTO> portfolioItems = userStockService.getUserPortfolioDTO(userEmail);
        return ResponseEntity.ok(portfolioItems);
    }

    @GetMapping("/portfolio/details")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<StockPrice>> getStockDetails(Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            List<StockPrice> stockPriceList = stockDetailDisplayService.getStockDetail(userEmail);
            return ResponseEntity.ok(stockPriceList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/portfolio/add/{symbol}")
    public ResponseEntity<String> addStockToPortfolio(@PathVariable String symbol, Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            userStockService.addStockToUserPortfolio(symbol, userEmail);
            return ResponseEntity.ok("Stock " + symbol + " added to portfolio and data fetching initiated.");
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error adding stock: " + e.getMessage());
        }
    }

    @DeleteMapping("/portfolio/remove/{symbol}")
    public ResponseEntity<String> removeStockFromPortfolio(@PathVariable String symbol, Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            userStockService.removeStockFromUserPortfolio(symbol, userEmail);
            return ResponseEntity.ok("Stock " + symbol + " removed from portfolio.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error removing stock: " + e.getMessage());
        }
    }


    @GetMapping("/news/display-news")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<News>> displayNews(Authentication authentication){
        try {
            String userEmail = authentication.getName();
            List<News> newsList = newsDetailDisplayService.displayNews(userEmail);
            return ResponseEntity.ok(newsList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/stocks/get-prediction-data/{symbol}")
    public ResponseEntity<String> sendPredictionData(@PathVariable String symbol) {
        try {
            String response = combineTodaysDataForPrediction.CombineAndSendTodaysDataForPrediction(symbol);
            return ResponseEntity.ok(response);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/stocks/send-data/{symbol}")
    public ResponseEntity<String> sendTrainingData(@PathVariable String symbol) {
        try {
            String response = sendTrainingData.sendData(symbol.toUpperCase());
            return ResponseEntity.ok(response);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/stocks/combine-data/{symbol}")
    private void getData(@PathVariable("symbol") String symbol) {
        try{
            exportToJson.exportToJson(symbol);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}