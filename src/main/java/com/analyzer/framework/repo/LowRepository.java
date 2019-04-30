package com.analyzer.framework.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.analyzer.framework.model.Low;

public interface LowRepository extends JpaRepository<Low, String> {

}
