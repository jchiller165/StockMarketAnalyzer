package com.analyzer.framework;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableScheduling
@SpringBootApplication
public class StockMarketAnalyzer1Application {

	public static void main(String[] args) {
		SpringApplication.run(StockMarketAnalyzer1Application.class, args);
	}

}
