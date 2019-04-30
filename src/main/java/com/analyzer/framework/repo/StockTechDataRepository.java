package com.analyzer.framework.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.analyzer.framework.model.StockTechData;

public interface StockTechDataRepository extends JpaRepository<StockTechData, String>{

}
