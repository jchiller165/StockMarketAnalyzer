package com.analyzer.framework.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.analyzer.framework.model.Stock;

public interface StockRepository extends JpaRepository<Stock, String> {

}
