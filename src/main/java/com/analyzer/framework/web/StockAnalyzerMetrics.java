package com.analyzer.framework.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;


@Component
public class StockAnalyzerMetrics {
	String currClose;
	String tenDay;
	String fiftyDay;
	String twoHundred;
	
	public Map<String, String> analyzeClosePrices(List<Double> stockClose,List<Double> tenDayClose, List<Double> fiftyDayClose, List<Double> twoHundredDayClose) {
		Map<String, String> trends = new HashMap<String, String>();
		
		stockClose = stockClose.subList(0, 10);
		tenDayClose = tenDayClose.subList(0, 10);
		fiftyDayClose = fiftyDayClose.subList(0, 10);
		twoHundredDayClose = twoHundredDayClose.subList(0, 10);
		
		if (stockClose.get(0) >= stockClose.get(5)) {
			currClose = "Up";
		}else {
			currClose = "Down";
		}
		trends.put("currentClose", currClose);
		
		if (tenDayClose.get(0) >= tenDayClose.get(5)) {
			tenDay = "Up";
		}else {
			tenDay = "Down";
		}
		trends.put("tenDayClose", tenDay);
		if (fiftyDayClose.get(0) >= fiftyDayClose.get(5)) {
			fiftyDay = "Up";
		}else {
			fiftyDay = "Down";
		}
		trends.put("fiftyDayClose", fiftyDay);
		if (twoHundredDayClose.get(0) >= twoHundredDayClose.get(5)) {
			twoHundred = "Up";
		}else {
			twoHundred = "Down";
		}
		trends.put("twoHundredClose", twoHundred);
		
		return trends;
	}
}
