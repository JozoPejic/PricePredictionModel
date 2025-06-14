package com.jozo.pricepredictionmodel.repository;

import com.jozo.pricepredictionmodel.model.StockPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface StockPriceRepository extends JpaRepository<StockPrice, Integer> {

    List<StockPrice> findAllBySymbolOrderByDateAsc(String symbol);
    Optional<StockPrice> findAllBySymbolAndDate(String symbol, Date date);
    boolean existsByDateAndSymbol(Date date, String symbol);
    List<StockPrice> findBySymbolOrderByDateAsc(String symbol);
}
