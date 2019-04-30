package com.analyzer.framework.model;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Symbol {
	@Id
	String symbol;
	String name;
	String industry;
	String sector;
	Timestamp date;
	
	public Symbol() {
		super();
	}
	
	public Symbol(String symbol,String name, String industry, String sector, Timestamp date) {
		this.symbol = symbol;
		this.name = name;
		this.industry = industry;
		this.sector = sector;
		this.date = date;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public String getIndustry() {
		return industry;
	}
	public void setIndustry(String industry) {
		this.industry = industry;
	}
	public String getSector() {
		return sector;
	}
	public void setSector(String sector) {
		this.sector = sector;
	}
	public Timestamp getDate() {
		return date;
	}
	public void setDate(Timestamp date) {
		this.date = date;
	}
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
