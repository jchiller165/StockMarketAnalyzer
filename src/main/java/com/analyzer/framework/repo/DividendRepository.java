package com.analyzer.framework.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.analyzer.framework.model.DividendAmount;

public interface DividendRepository extends JpaRepository<DividendAmount, String> {

}
