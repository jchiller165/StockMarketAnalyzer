package com.analyzer.framework.web;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.analyzer.framework.model.StockTechData;
import com.analyzer.framework.repo.CloseRepository;
import com.analyzer.framework.repo.FiftyDaySMADataRepository;
import com.analyzer.framework.repo.StockRepository;
import com.analyzer.framework.repo.StockTechDataRepository;
import com.analyzer.framework.repo.SymbolRepository;
import com.analyzer.framework.repo.TenDaySMADataRepository;
import com.analyzer.framework.repo.TwoHundredDaySMADataRepository;
import com.google.gson.Gson;

@Controller
public class MainController {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	StockRepository stockRepo;
	@Autowired 
	SymbolRepository symbolRepo;
	@Autowired 
	CloseRepository closeRepo;
	@Autowired
	TenDaySMADataRepository tenDayRepo;
	@Autowired 
	FiftyDaySMADataRepository fiftyDayRepo;
	@Autowired
	TwoHundredDaySMADataRepository twoHundredRepo;
	@Autowired
	StockTechDataRepository stockTechDataRepo;
	
	@RequestMapping(value = {"/", "/home"}, method = RequestMethod.GET)
	public String welcome(Model model) {
		List<StockTechData> std = stockTechDataRepo.findAll();
		model.addAttribute("std", std);
		return "home"; 	    		
	}
	
	@RequestMapping(value = {"/stock/{id}"}, method = RequestMethod.GET)
	public String getStock(@PathVariable("id") String id, Model model) {
		Gson gsonObj = new Gson();
		
		model.addAttribute("stock", stockRepo.findById(id).orElse(null));
		model.addAttribute("symbol", symbolRepo.findById(id).orElse(null));
		
		String dataPoints = gsonObj.toJson(new ChartDataRetreiver()
				.getData(closeRepo.findById(id).orElse(null).get100DayClose()));
		logger.info("" + dataPoints);
		model.addAttribute("closePrices", dataPoints);
		
		String dataPoints1 = gsonObj.toJson(new ChartDataRetreiver()
				.getData(tenDayRepo.findById(id).orElse(null).get100DayClose()));
		logger.info("" + dataPoints1);
		model.addAttribute("tenDayPrices", dataPoints1);
		
		String dataPoints2 = gsonObj.toJson(new ChartDataRetreiver()
				.getData(fiftyDayRepo.findById(id).orElse(null).get100DayClose()));
		logger.info("" + dataPoints2);
		model.addAttribute("fiftyDayPrices", dataPoints2);
		
		String dataPoints3 = gsonObj.toJson(new ChartDataRetreiver()
				.getData(twoHundredRepo.findById(id).orElse(null).get100DayClose()));
		logger.info("" + dataPoints3);
		model.addAttribute("twoHundredDayPrices", dataPoints3);
		
		model.addAttribute("trends", new StockAnalyzerMetrics()
				.analyzeClosePrices(closeRepo.findById(id).orElse(null).get100DayClose(), 
				tenDayRepo.findById(id).orElse(null).get100DayClose(), 
				fiftyDayRepo.findById(id).orElse(null).get100DayClose(), 
				twoHundredRepo.findById(id).orElse(null).get100DayClose()));
		
		return "stock";
	}
}
