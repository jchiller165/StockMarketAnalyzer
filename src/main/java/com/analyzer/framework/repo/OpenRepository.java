package com.analyzer.framework.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.analyzer.framework.model.Open;


public interface OpenRepository extends JpaRepository<Open, String> {

}
