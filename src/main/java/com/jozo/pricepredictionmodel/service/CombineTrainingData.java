package com.jozo.pricepredictionmodel.service;


import com.jozo.pricepredictionmodel.model.ModelInputDTO;
import com.jozo.pricepredictionmodel.model.News;
import com.jozo.pricepredictionmodel.model.StockPrice;
import com.jozo.pricepredictionmodel.repository.NewsRepository;
import com.jozo.pricepredictionmodel.repository.StockPriceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CombineTrainingData {

    @Autowired
    private StockPriceRepository stockPriceRepository;
    @Autowired
    private NewsRepository newsRepository;


    public List<ModelInputDTO> fetchAndCombineData(String symbol){

        List<StockPrice> prices = stockPriceRepository.findAllBySymbolOrderByDateAsc(symbol);

        Map<LocalDate, Double> closePrices = prices.stream()
                .collect(Collectors.toMap(p -> p.getDate().toLocalDate(), p -> p.getClose()));

        List<ModelInputDTO> modelInputs = new ArrayList<>();

        for(StockPrice price : prices){
            Date date = price.getDate();
            List<News> sentiment = newsRepository.findAllByTickerAndDate(symbol, date);

            Double avgSentimentScore = sentiment.isEmpty() ?
                    0.0
                    : sentiment.stream()
                    .mapToDouble(News::getOverall_sentiment_score)
                    .average()
                    .orElse(0.0);

            Double nextDayClose = closePrices.get(date.toLocalDate().plusDays(1));

            int label = 0;

            if(nextDayClose != null){
                label = nextDayClose > price.getClose() ? 1 : 0;
            }else{
                continue;
            }

            ModelInputDTO modelInputDTO = new ModelInputDTO();

            modelInputDTO.setSymbol(symbol);
            modelInputDTO.setDate(date);
            modelInputDTO.setOverallSentimentScore(avgSentimentScore);
            modelInputDTO.setOpen(price.getOpen());
            modelInputDTO.setClose(price.getClose());
            modelInputDTO.setHigh(price.getHigh());
            modelInputDTO.setLow(price.getLow());
            modelInputDTO.setVolume(price.getVolume());
            modelInputDTO.setLabel(label);

            modelInputs.add(modelInputDTO);
        }

        return modelInputs;
    }
}
