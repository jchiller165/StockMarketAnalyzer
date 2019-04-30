package com.analyzer.application.output.timeseries.data;

import java.sql.Timestamp;

public class SymbolData {
	private Timestamp timeStamp;
	private String symbol;
	private String industry;
	private String sector;
	public SymbolData(Timestamp timeStamp, 
			String symbol, 
			String industry, 
			String sector) {
		super();
		this.timeStamp = timeStamp;
		this.symbol = symbol;
		this.industry = industry;
		this.sector = sector;
	}
	public Timestamp getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(Timestamp timeStamp) {
		this.timeStamp = timeStamp;
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
	
}
