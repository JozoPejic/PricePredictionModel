package com.jozo.pricepredictionmodel.repository;

import com.jozo.pricepredictionmodel.model.User;
import com.jozo.pricepredictionmodel.model.UserStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserStockRepository extends JpaRepository<UserStock, Integer> {

    boolean existsByUserAndSymbol(User user, String symbol);
    List<UserStock> findByUser(User user);
    UserStock findByUserAndSymbol(User user, String symbol);
    void deleteByUserAndSymbol(User user, String symbol);
}
