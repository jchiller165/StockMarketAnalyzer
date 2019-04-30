package com.analyzer.framework.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.analyzer.framework.model.High;

public interface HighRepository extends JpaRepository<High, String> {

}
