package com.analyzer.framework.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.analyzer.framework.model.Close;

public interface CloseRepository extends JpaRepository<Close, String> {

}
