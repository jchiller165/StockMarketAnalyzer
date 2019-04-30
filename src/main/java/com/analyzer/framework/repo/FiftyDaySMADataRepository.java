package com.analyzer.framework.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.analyzer.framework.model.FiftyDaySMAData;

public interface FiftyDaySMADataRepository extends JpaRepository<FiftyDaySMAData, String> {

}
