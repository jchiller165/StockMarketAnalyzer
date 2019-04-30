package com.analyzer.framework.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.analyzer.framework.model.Symbol;

public interface SymbolRepository extends JpaRepository<Symbol, String> {

}
