package com.analyzer.framework.web;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.analyzer.framework.model.Stock;
import com.analyzer.framework.model.Symbol;
import com.analyzer.framework.repo.StockRepository;
import com.analyzer.framework.repo.SymbolRepository;

@Controller
public class MainController {
	@Autowired
	StockRepository stockRepo;
	@Autowired SymbolRepository symbolRepo;

	
	@RequestMapping(value = {"/", "/home"}, method = RequestMethod.GET)
	public String welcome(Model model) {
  
		return "home"; 	    		
	}
	
	@RequestMapping(value = {"/stock/{id}"}, method = RequestMethod.GET)
	public String getStock(@PathVariable("id") String id, Model model) {
		Stock stock = stockRepo.findById(id).orElse(null);
		Symbol symbol = symbolRepo.findById(id).orElse(null);
		model.addAttribute("stock", stock);
		model.addAttribute("symbol", symbol);
		
		return "stock";
	}
}
