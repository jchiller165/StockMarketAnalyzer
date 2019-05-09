package com.analyzer.framework.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.analyzer.application.alpha.AlphaVantageConnector;
import com.analyzer.application.alpha.TechnicalIndicators;
import com.analyzer.application.input.technicalindicators.Interval;
import com.analyzer.application.input.technicalindicators.SeriesType;
import com.analyzer.application.input.technicalindicators.TimePeriod;
import com.analyzer.application.output.AlphaVantageException;
import com.analyzer.application.output.technicalindicators.MACD;
import com.analyzer.application.output.technicalindicators.data.MACDData;

@Component
public class MACDRetreiver {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	final String APIKEY = "90D208FY7VR1VMZ8";
    final int TIMEOUT = 3000;
    Map<String, List<Double>> macd = new HashMap<String, List<Double>>();
	List<Double> signal = new ArrayList<Double>(Collections.nCopies(100, 0.0));
    List<Double> macdata = new ArrayList<Double>(Collections.nCopies(100, 0.0));
    List<Double> histo = new ArrayList<Double>(Collections.nCopies(100, 0.0));
    
    public Map<String,List<Double>> getTenDayMACD(String id){
    	
    	AlphaVantageConnector apiConnector = new AlphaVantageConnector(APIKEY, TIMEOUT);
    	TechnicalIndicators technicalIndicators = new TechnicalIndicators(apiConnector);

    	try {
	    	
	        MACD response = technicalIndicators.macd(id, Interval.DAILY, TimePeriod.of(10), SeriesType.CLOSE, null, null, null);
	      
	        List<MACDData> macdData = response.getData();
	      
	        macdData = macdData.subList(0, 100);
	        macdData.forEach(data -> {
	        	signal.add(data.getSignal());
	        	macdata.add(data.getMacd());
	        	histo.add(data.getHist());
	        });
    	} catch (AlphaVantageException e) {
    		System.out.println("something went wrong");
    	}
    	macd.put("signal", signal);
    	macd.put("macd", macdata);
    	macd.put("histogram", histo);
    
    
    	return macd;
    }
}
