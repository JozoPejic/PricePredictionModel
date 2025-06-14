package com.jozo.pricepredictionmodel.repository;

import com.jozo.pricepredictionmodel.model.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Integer> {

    List<News> findAllByTickerAndDate(String ticker, Date date);
    List<News> findAllByDate(Date date);
    boolean existsByTickerAndDate(String ticker, Date date);
}
