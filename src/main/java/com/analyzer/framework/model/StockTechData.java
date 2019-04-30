package com.analyzer.framework.model;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class StockTechData {
	@Id
	String symbol;
	double open;
	double high;
	double low;
	double close;
	int volume;
	double tenDaySMA;
	double fiftyDaySMA;
	double twoHundredDaySMA;
	Timestamp lastUpdtDt;
	
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public double getOpen() {
		return open;
	}
	public void setOpen(double open) {
		this.open = open;
	}
	public double getHigh() {
		return high;
	}
	public void setHigh(double high) {
		this.high = high;
	}
	public double getLow() {
		return low;
	}
	public void setVolume(int volume) {
		this.volume = volume;
	}
	public double getVolume() {
		return volume;
	}
	public void setLow(double low) {
		this.low = low;
	}
	public double getClose() {
		return close;
	}
	public void setClose(double close) {
		this.close = close;
	}
	public double getTenDaySMA() {
		return tenDaySMA;
	}
	public void setTenDaySMA(double tenDaySMA) {
		this.tenDaySMA = tenDaySMA;
	}
	public double getFiftyDaySMA() {
		return fiftyDaySMA;
	}
	public void setFiftyDaySMA(double fiftyDaySMA) {
		this.fiftyDaySMA = fiftyDaySMA;
	}
	public double getTwoHundredDaySMA() {
		return twoHundredDaySMA;
	}
	public void setTwoHundredDaySMA(double twoHundredDaySMA) {
		this.twoHundredDaySMA = twoHundredDaySMA;
	}
	public void setUpdtDt(Timestamp date) {
		this.lastUpdtDt = date;
	}
	public Timestamp getUpdateDt() {
		return this.lastUpdtDt;
	}
	
}
