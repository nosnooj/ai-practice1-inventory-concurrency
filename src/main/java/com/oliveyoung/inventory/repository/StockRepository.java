package com.oliveyoung.inventory.repository;

import com.oliveyoung.inventory.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {

    Optional<Stock> findBySkuAndSource(String sku, String source);

    List<Stock> findBySku(String sku);
}
