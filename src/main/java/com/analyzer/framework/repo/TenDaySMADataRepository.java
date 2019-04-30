package com.analyzer.framework.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.analyzer.framework.model.TenDaySMAData;

public interface TenDaySMADataRepository extends JpaRepository<TenDaySMAData, String> {

}
