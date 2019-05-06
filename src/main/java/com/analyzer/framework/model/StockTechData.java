package com.analyzer.framework.model;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class StockTechData {
	@Id
	String symbol;
	String name;
	double open;
	double high;
	double low;
	double close;
	int volume;
	double tenDaySMA;
	double fiftyDaySMA;
	double twoHundredDaySMA;
	String currTrend;
	String tenDayTrend;
	String fiftyDayTrend;
	String twoHundredTrend;
	Timestamp lastUpdtDt;
	
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
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
	public String getCurrTrend() {
		return currTrend;
	}
	public String getTenDayTrend() {
		return tenDayTrend;
	}
	public String getFiftyDayTrend() {
		return fiftyDayTrend;
	}
	public String getTwoHundredTrend() {
		return twoHundredTrend;
	}
	public Timestamp getLastUpdtDt() {
		return lastUpdtDt;
	}
	public void setCurrTrend(String currTrend) {
		this.currTrend = currTrend;
	}
	public void setTenDayTrend(String tenDayTrend) {
		this.tenDayTrend = tenDayTrend;
	}
	public void setFiftyDayTrend(String fiftyDayTrend) {
		this.fiftyDayTrend = fiftyDayTrend;
	}
	public void setTwoHundredTrend(String twoHundredTrend) {
		this.twoHundredTrend = twoHundredTrend;
	}
	public void setLastUpdtDt(Timestamp lastUpdtDt) {
		this.lastUpdtDt = lastUpdtDt;
	}
}
