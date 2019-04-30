package com.analyzer.framework.model;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Stock {
	
	@Id
	String symbol;
	double open;
	double high;
	double low;
	double close;
	double divAmt;
	long volume;
	Timestamp updtDt;
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
	public void setLow(double low) {
		this.low = low;
	}
	public double getClose() {
		return close;
	}
	public void setClose(double close) {
		this.close = close;
	}
	public double getDivAmt() {
		return divAmt;
	}
	public void setDivAmt(double divAmt) {
		this.divAmt = divAmt;
	}
	public long getVolume() {
		return volume;
	}
	public void setVolume(long volume) {
		this.volume = volume;
	}
	public Timestamp getUpdtDt() {
		return updtDt;
	}
	public void setUpdtDt(Timestamp updtDt) {
		this.updtDt = updtDt;
	}
	
}
