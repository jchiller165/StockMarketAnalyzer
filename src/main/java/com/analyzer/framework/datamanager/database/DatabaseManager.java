package com.analyzer.framework.datamanager.database;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.analyzer.application.alpha.AlphaVantageConnector;
import com.analyzer.application.alpha.TechnicalIndicators;
import com.analyzer.application.alpha.TimeSeries;
import com.analyzer.application.input.technicalindicators.Interval;
import com.analyzer.application.input.technicalindicators.SeriesType;
import com.analyzer.application.input.technicalindicators.TimePeriod;
import com.analyzer.application.input.timeseries.OutputSize;
import com.analyzer.application.output.technicalindicators.SMA;
import com.analyzer.application.output.technicalindicators.data.IndicatorData;
import com.analyzer.application.output.timeseries.Daily;
import com.analyzer.application.output.timeseries.data.StockData;
import com.analyzer.framework.model.Close;
import com.analyzer.framework.model.DividendAmount;
import com.analyzer.framework.model.FiftyDaySMAData;
import com.analyzer.framework.model.High;
import com.analyzer.framework.model.Low;
import com.analyzer.framework.model.Open;
import com.analyzer.framework.model.Stock;
import com.analyzer.framework.model.StockTechData;
import com.analyzer.framework.model.Symbol;
import com.analyzer.framework.model.TenDaySMAData;
import com.analyzer.framework.model.TwoHundredDaySMAData;
import com.analyzer.framework.model.Volume;
import com.analyzer.framework.repo.CloseRepository;
import com.analyzer.framework.repo.DividendRepository;
import com.analyzer.framework.repo.FiftyDaySMADataRepository;
import com.analyzer.framework.repo.HighRepository;
import com.analyzer.framework.repo.LowRepository;
import com.analyzer.framework.repo.OpenRepository;
import com.analyzer.framework.repo.StockRepository;
import com.analyzer.framework.repo.StockTechDataRepository;
import com.analyzer.framework.repo.SymbolRepository;
import com.analyzer.framework.repo.TenDaySMADataRepository;
import com.analyzer.framework.repo.TwoHundredDaySMADataRepository;
import com.analyzer.framework.repo.VolumeRepository;
import com.analyzer.framework.web.StockAnalyzerMetrics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Component
public class DatabaseManager implements CommandLineRunner {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private final String nasdaqFileURL = "https://www.nasdaq.com/screening/companies-by-name.aspx?letter=0&exchange=nasdaq&render=download";
	private final String nyseFileURL = "https://www.nasdaq.com/screening/companies-by-name.aspx?letter=0&exchange=nyse&render=download";
	private final String fileDir = "/Users/justin_hiller/eclipse-workspace/StockMarketAnalyzer-1/src/main/resources/data";
	private final String APIKEY = "90D208FY7VR1VMZ8";
    private final int TIMEOUT = 3000;
    private int i;
    
	@Autowired
	SymbolRepository symbolRepo;
	@Autowired
	StockRepository stockRepo;
	@Autowired
	OpenRepository openRepo;
	@Autowired
	HighRepository highRepo;
	@Autowired
	LowRepository lowRepo;
	@Autowired
	CloseRepository closeRepo;
	@Autowired
	VolumeRepository volumeRepo;
	@Autowired
	DividendRepository divRepo;
	@Autowired
	TenDaySMADataRepository tenDayRepo;
	@Autowired
	FiftyDaySMADataRepository fiftyDayRepo;
	@Autowired
	TwoHundredDaySMADataRepository twoHundredDayRepo;
	@Autowired
	StockTechDataRepository stockTechRepo;
	
	@SuppressWarnings({ "unused", "resource" })
	@Scheduled(cron="0 0 0 1 1/1 *", zone = "CST")	
	public void updateSymbols() {
		Timestamp date = new Timestamp(new java.util.Date().getTime());
		int i = 0;
		
		try {
            HttpDownloadUtility.downloadFile(nasdaqFileURL, fileDir + "/symbols/", "nasdaq.csv");
            HttpDownloadUtility.downloadFile(nyseFileURL, fileDir + "/symbols/", "nyse.csv");
            
            try {
        		logger.info("Attempting to modify csv file...");
        		
    			Process p = Runtime.getRuntime()
    					.exec("python " + fileDir + "/scripts/symbols.py");
    	    	logger.info("Done...");
    		} catch (IOException e) {
    			logger.debug("Problem Modifying csv file");
    		}
            
            BufferedReader bReader = new BufferedReader(new FileReader(fileDir + "/symbols/nasdaq_symbols.csv"));
	        String line = ""; 
	        logger.info("Inserting symbols into database. . .");
	        while ((line = bReader.readLine()) != null) {
	        	try {
	        		if (line != null) {
	        			String[] array = line.split(",+");
	                    for(String result:array) {
	                    	symbolRepo.save(new Symbol(
	                    			array[0].replaceAll("[-+.^:,]","").trim(),
	                    			array[1].trim(),
	                    			array[2].trim(),
	                    			array[3].trim(),
	                    			date));
	                    	i++;
	                    }
	        		}
	        	}catch(Exception e) {
	        		logger.debug("Problem inserting symbols into the database");
	        	}
	        }
	        
	        bReader = new BufferedReader(new FileReader(fileDir + "/symbols/nyse_symbols.csv"));
	        line = ""; 
	        while ((line = bReader.readLine()) != null) {
	        	try {
	        		if (line != null) {
	        			String[] array = line.split(",+");
	                    for(String result:array) {
	                    	symbolRepo.save(new Symbol(
	                    			array[0].replaceAll("[-+.^:,]","").trim(),
	                    			array[1].trim(),
	                    			array[2].trim(),
	                    			array[3].trim(),
	                    			date));
	                    	i++;
	                    }
	        		}
	        	}catch(Exception e) {
	        		logger.debug("Problem inserting symbols into the database");
	        	}
	        }
	        logger.info("Successfully inserted: " + i + " Records into the the Symbols table");
        } catch (IOException ex) {
            logger.debug("Problem Downloading Symbol Files");
        }	
	}
	
	@Scheduled(cron = "0 0 17 * * ?", zone = "CST")
	public void updateDailyStockData()  {
		List<Symbol> symbols = symbolRepo.findAll();

		AlphaVantageConnector apiConnector = new AlphaVantageConnector(APIKEY, TIMEOUT);
        TimeSeries stockTimeSeries = new TimeSeries(apiConnector);
        logger.info("Inserting daily stock data");
        for (Symbol sym:symbols) {
        	try {
        		Daily response = stockTimeSeries.daily(sym.getSymbol().toString(), OutputSize.COMPACT);
        		Timestamp date = new Timestamp(new java.util.Date().getTime());	        	
        		List<StockData> stockData = response.getStockData();
	        	ArrayList<Double> open = new ArrayList<Double>(Collections.nCopies(100, 0.0));
	        	ArrayList<Double> high = new ArrayList<Double>(Collections.nCopies(100, 0.0));
	        	ArrayList<Double> low = new ArrayList<Double>(Collections.nCopies(100, 0.0));
	        	ArrayList<Double> close = new ArrayList<Double>(Collections.nCopies(100, 0.0));
	        	ArrayList<Long> volume = new ArrayList<Long>(Collections.nCopies(100, 0L));
	        	ArrayList<Double> divAmt = new ArrayList<Double>(Collections.nCopies(100, 0.0));
	        	i=0;
	        	stockData.forEach(stock -> {
	        		open.set(i, stock.getOpen());
	        		high.set(i, stock.getHigh());
	        		low.set(i, stock.getLow());
	        		close.set(i, stock.getClose());
	        		volume.set(i, stock.getVolume());
	        		divAmt.set(i, stock.getDividendAmount());
	        		i++;
	        	});
	        	
	        	Open o = new Open();
				o.setSymbol(sym.getSymbol().toString());
				o.setDay1Open(open.get(0));o.setDay2Open(open.get(1));o.setDay3Open(open.get(2));o.setDay4Open(open.get(3));
				o.setDay5Open(open.get(4));o.setDay6Open(open.get(5));o.setDay7Open(open.get(6));o.setDay8Open(open.get(7));
				o.setDay9Open(open.get(8));o.setDay10Open(open.get(9));o.setDay11Open(open.get(10));o.setDay12Open(open.get(11));
				o.setDay13Open(open.get(12));o.setDay14Open(open.get(13));o.setDay15Open(open.get(14));o.setDay16Open(open.get(15));
				o.setDay17Open(open.get(16));o.setDay18Open(open.get(17));o.setDay19Open(open.get(18));o.setDay20Open(open.get(19));
				o.setDay21Open(open.get(20));o.setDay22Open(open.get(21));o.setDay23Open(open.get(22));o.setDay24Open(open.get(23));
				o.setDay25Open(open.get(24));o.setDay26Open(open.get(25));o.setDay27Open(open.get(26));o.setDay28Open(open.get(27));
				o.setDay29Open(open.get(28));o.setDay30Open(open.get(29));o.setDay31Open(open.get(30));o.setDay32Open(open.get(31));
				o.setDay33Open(open.get(32));o.setDay34Open(open.get(33));o.setDay35Open(open.get(34));o.setDay36Open(open.get(35));
				o.setDay37Open(open.get(36));o.setDay38Open(open.get(37));o.setDay39Open(open.get(38));o.setDay40Open(open.get(39));
				o.setDay41Open(open.get(40));o.setDay42Open(open.get(41));o.setDay43Open(open.get(42));o.setDay44Open(open.get(43));
				o.setDay45Open(open.get(44));o.setDay46Open(open.get(45));o.setDay47Open(open.get(46));o.setDay48Open(open.get(47));
				o.setDay49Open(open.get(48));o.setDay50Open(open.get(49));o.setDay51Open(open.get(50));o.setDay52Open(open.get(51));
				o.setDay53Open(open.get(52));o.setDay54Open(open.get(53));o.setDay55Open(open.get(54));o.setDay56Open(open.get(55));
				o.setDay57Open(open.get(56));o.setDay58Open(open.get(57));o.setDay59Open(open.get(58));o.setDay60Open(open.get(59));
				o.setDay61Open(open.get(60));o.setDay62Open(open.get(61));o.setDay63Open(open.get(62));o.setDay64Open(open.get(63));
				o.setDay65Open(open.get(64));o.setDay66Open(open.get(65));o.setDay67Open(open.get(66));o.setDay68Open(open.get(67));
				o.setDay69Open(open.get(68));o.setDay70Open(open.get(69));o.setDay71Open(open.get(70));o.setDay72Open(open.get(71));
				o.setDay73Open(open.get(72));o.setDay74Open(open.get(73));o.setDay75Open(open.get(74));o.setDay76Open(open.get(75));
				o.setDay77Open(open.get(76));o.setDay78Open(open.get(77));o.setDay79Open(open.get(78));o.setDay80Open(open.get(79));
				o.setDay81Open(open.get(80));o.setDay82Open(open.get(81));o.setDay83Open(open.get(82));o.setDay84Open(open.get(83));
				o.setDay85Open(open.get(84));o.setDay86Open(open.get(85));o.setDay87Open(open.get(86));o.setDay88Open(open.get(87));
				o.setDay89Open(open.get(88));o.setDay90Open(open.get(89));o.setDay91Open(open.get(90));o.setDay92Open(open.get(91));
				o.setDay93Open(open.get(92));o.setDay94Open(open.get(93));o.setDay95Open(open.get(94));o.setDay96Open(open.get(95));
				o.setDay97Open(open.get(96));o.setDay98Open(open.get(97));o.setDay99Open(open.get(98));o.setDay100Open(open.get(99));	
				o.setUpdtDt(date);
	        	openRepo.save(o);
	        	
	        	High h = new High();
	        	h.setSymbol(sym.getSymbol());
	        	h.setDay1High(high.get(0));h.setDay2High(high.get(1));h.setDay3High(high.get(2));h.setDay4High(high.get(3));
	        	h.setDay5High(high.get(4));h.setDay6High(high.get(5));h.setDay7High(high.get(6));h.setDay8High(high.get(7));
	        	h.setDay9High(high.get(8));h.setDay10High(high.get(9));h.setDay11High(high.get(10));h.setDay12High(high.get(11));
	        	h.setDay13High(high.get(12));h.setDay14High(high.get(13));h.setDay15High(high.get(14));h.setDay16High(high.get(15));
	        	h.setDay17High(high.get(16));h.setDay18High(high.get(17));h.setDay19High(high.get(18));	h.setDay20High(high.get(19));
	        	h.setDay21High(high.get(20));h.setDay22High(high.get(21));h.setDay23High(high.get(22));h.setDay24High(high.get(23));
	        	h.setDay25High(high.get(24));h.setDay26High(high.get(25));h.setDay27High(high.get(26));h.setDay28High(high.get(27));
	        	h.setDay29High(high.get(28));h.setDay30High(high.get(29));h.setDay31High(high.get(30));h.setDay32High(high.get(31));
	        	h.setDay33High(high.get(32));h.setDay34High(high.get(33));h.setDay35High(high.get(34));h.setDay36High(high.get(35));
	        	h.setDay37High(high.get(36));h.setDay38High(high.get(37));h.setDay39High(high.get(38));h.setDay40High(high.get(39));
	        	h.setDay41High(high.get(40));h.setDay42High(high.get(41));h.setDay43High(high.get(42));h.setDay44High(high.get(43));
	        	h.setDay45High(high.get(44));h.setDay46High(high.get(45));h.setDay47High(high.get(46));h.setDay48High(high.get(47));
	        	h.setDay49High(high.get(48));h.setDay50High(high.get(49));h.setDay51High(high.get(50));h.setDay52High(high.get(51));
	        	h.setDay53High(high.get(52));h.setDay54High(high.get(53));h.setDay55High(high.get(54));h.setDay56High(high.get(55));
	        	h.setDay57High(high.get(56));h.setDay58High(high.get(57));h.setDay59High(high.get(58));h.setDay60High(high.get(59));
	        	h.setDay61High(high.get(60));h.setDay62High(high.get(61));h.setDay63High(high.get(62));h.setDay64High(high.get(63));
	        	h.setDay65High(high.get(64));h.setDay66High(high.get(65));h.setDay67High(high.get(66));h.setDay68High(high.get(67));
	        	h.setDay69High(high.get(68));h.setDay70High(high.get(69));h.setDay71High(high.get(70));h.setDay72High(high.get(71));
	        	h.setDay73High(high.get(72));h.setDay74High(high.get(73));h.setDay75High(high.get(74));h.setDay76High(high.get(75));
	        	h.setDay77High(high.get(76));h.setDay78High(high.get(77));h.setDay79High(high.get(78));h.setDay80High(high.get(79));
	        	h.setDay81High(high.get(80));h.setDay82High(high.get(81));h.setDay83High(high.get(82));h.setDay84High(high.get(83));
	        	h.setDay85High(high.get(84));h.setDay86High(high.get(85));h.setDay87High(high.get(86));h.setDay88High(high.get(87));
	        	h.setDay89High(high.get(88));h.setDay90High(high.get(89));h.setDay91High(high.get(90));h.setDay92High(high.get(91));
	        	h.setDay93High(high.get(92));h.setDay94High(high.get(93));h.setDay95High(high.get(94));h.setDay96High(high.get(95));
	        	h.setDay97High(high.get(96));h.setDay98High(high.get(97));h.setDay99High(high.get(98));h.setDay100High(high.get(99));
	        	h.setUpdtDt(date);
	        	highRepo.save(h);
	        	
	        	Low l = new Low();
	        	l.setSymbol(sym.getSymbol());
	        	l.setDay1Low(low.get(0));l.setDay2Low(low.get(1));l.setDay3Low(low.get(2));l.setDay4Low(low.get(3));
	        	l.setDay5Low(low.get(4));l.setDay6Low(low.get(5));l.setDay7Low(low.get(6));l.setDay8Low(low.get(7));
	        	l.setDay9Low(low.get(8));l.setDay10Low(low.get(9));l.setDay11Low(low.get(10));l.setDay12Low(low.get(11));
	        	l.setDay13Low(low.get(12));l.setDay14Low(low.get(13));l.setDay15Low(low.get(14));l.setDay16Low(low.get(15));
	        	l.setDay17Low(low.get(16));l.setDay18Low(low.get(17));l.setDay19Low(low.get(18));	l.setDay20Low(low.get(19));
	        	l.setDay21Low(low.get(20));l.setDay22Low(low.get(21));l.setDay23Low(low.get(22));l.setDay24Low(low.get(23));
	        	l.setDay25Low(low.get(24));l.setDay26Low(low.get(25));l.setDay27Low(low.get(26));l.setDay28Low(low.get(27));
	        	l.setDay29Low(low.get(28));l.setDay30Low(low.get(29));l.setDay31Low(low.get(30));l.setDay32Low(low.get(31));
	        	l.setDay33Low(low.get(32));l.setDay34Low(low.get(33));l.setDay35Low(low.get(34));l.setDay36Low(low.get(35));
	        	l.setDay37Low(low.get(36));l.setDay38Low(low.get(37));l.setDay39Low(low.get(38));l.setDay40Low(low.get(39));
	        	l.setDay41Low(low.get(40));l.setDay42Low(low.get(41));l.setDay43Low(low.get(42));l.setDay44Low(low.get(43));
	        	l.setDay45Low(low.get(44));l.setDay46Low(low.get(45));l.setDay47Low(low.get(46));l.setDay48Low(low.get(47));
	        	l.setDay49Low(low.get(48));l.setDay50Low(low.get(49));l.setDay51Low(low.get(50));l.setDay52Low(low.get(51));
	        	l.setDay53Low(low.get(52));l.setDay54Low(low.get(53));l.setDay55Low(low.get(54));l.setDay56Low(low.get(55));
	        	l.setDay57Low(low.get(56));l.setDay58Low(low.get(57));l.setDay59Low(low.get(58));l.setDay60Low(low.get(59));
	        	l.setDay61Low(low.get(60));l.setDay62Low(low.get(61));l.setDay63Low(low.get(62));l.setDay64Low(low.get(63));
	        	l.setDay65Low(low.get(64));l.setDay66Low(low.get(65));l.setDay67Low(low.get(66));l.setDay68Low(low.get(67));
	        	l.setDay69Low(low.get(68));l.setDay70Low(low.get(69));l.setDay71Low(low.get(70));l.setDay72Low(low.get(71));
	        	l.setDay73Low(low.get(72));l.setDay74Low(low.get(73));l.setDay75Low(low.get(74));l.setDay76Low(low.get(75));
	        	l.setDay77Low(low.get(76));l.setDay78Low(low.get(77));l.setDay79Low(low.get(78));l.setDay80Low(low.get(79));
	        	l.setDay81Low(low.get(80));l.setDay82Low(low.get(81));l.setDay83Low(low.get(82));l.setDay84Low(low.get(83));
	        	l.setDay85Low(low.get(84));l.setDay86Low(low.get(85));l.setDay87Low(low.get(86));l.setDay88Low(low.get(87));
	        	l.setDay89Low(low.get(88));l.setDay90Low(low.get(89));l.setDay91Low(low.get(90));l.setDay92Low(low.get(91));
	        	l.setDay93Low(low.get(92));l.setDay94Low(low.get(93));l.setDay95Low(low.get(94));l.setDay96Low(low.get(95));
	        	l.setDay97Low(low.get(96));l.setDay98Low(low.get(97));l.setDay99Low(low.get(98));l.setDay100Low(low.get(99));
	        	l.setUpdtDt(date);
	        	lowRepo.save(l);
	        	
	        	Close c = new Close();
	        	c.setSymbol(sym.getSymbol().toString());
	        	c.setDay1Close(close.get(0));c.setDay2Close(close.get(1));c.setDay3Close(close.get(2));c.setDay4Close(close.get(3));
	        	c.setDay5Close(close.get(4));c.setDay6Close(close.get(5));c.setDay7Close(close.get(6));c.setDay8Close(close.get(7));
	        	c.setDay9Close(close.get(8));c.setDay10Close(close.get(9));c.setDay11Close(close.get(10));c.setDay12Close(close.get(11));
	        	c.setDay13Close(close.get(12));c.setDay14Close(close.get(13));c.setDay15Close(close.get(14));c.setDay16Close(close.get(15));
	        	c.setDay17Close(close.get(16));c.setDay18Close(close.get(17));c.setDay19Close(close.get(18));	c.setDay20Close(close.get(19));
	        	c.setDay21Close(close.get(20));c.setDay22Close(close.get(21));c.setDay23Close(close.get(22));c.setDay24Close(close.get(23));
	        	c.setDay25Close(close.get(24));c.setDay26Close(close.get(25));c.setDay27Close(close.get(26));c.setDay28Close(close.get(27));
	        	c.setDay29Close(close.get(28));c.setDay30Close(close.get(29));c.setDay31Close(close.get(30));c.setDay32Close(close.get(31));
	        	c.setDay33Close(close.get(32));c.setDay34Close(close.get(33));c.setDay35Close(close.get(34));c.setDay36Close(close.get(35));
	        	c.setDay37Close(close.get(36));c.setDay38Close(close.get(37));c.setDay39Close(close.get(38));c.setDay40Close(close.get(39));
	        	c.setDay41Close(close.get(40));c.setDay42Close(close.get(41));c.setDay43Close(close.get(42));c.setDay44Close(close.get(43));
	        	c.setDay45Close(close.get(44));c.setDay46Close(close.get(45));c.setDay47Close(close.get(46));c.setDay48Close(close.get(47));
	        	c.setDay49Close(close.get(48));c.setDay50Close(close.get(49));c.setDay51Close(close.get(50));c.setDay52Close(close.get(51));
	        	c.setDay53Close(close.get(52));c.setDay54Close(close.get(53));c.setDay55Close(close.get(54));c.setDay56Close(close.get(55));
	        	c.setDay57Close(close.get(56));c.setDay58Close(close.get(57));c.setDay59Close(close.get(58));c.setDay60Close(close.get(59));
	        	c.setDay61Close(close.get(60));c.setDay62Close(close.get(61));c.setDay63Close(close.get(62));c.setDay64Close(close.get(63));
	        	c.setDay65Close(close.get(64));c.setDay66Close(close.get(65));c.setDay67Close(close.get(66));c.setDay68Close(close.get(67));
	        	c.setDay69Close(close.get(68));c.setDay70Close(close.get(69));c.setDay71Close(close.get(70));c.setDay72Close(close.get(71));
	        	c.setDay73Close(close.get(72));c.setDay74Close(close.get(73));c.setDay75Close(close.get(74));c.setDay76Close(close.get(75));
	        	c.setDay77Close(close.get(76));c.setDay78Close(close.get(77));c.setDay79Close(close.get(78));c.setDay80Close(close.get(79));
	        	c.setDay81Close(close.get(80));c.setDay82Close(close.get(81));c.setDay83Close(close.get(82));c.setDay84Close(close.get(83));
	        	c.setDay85Close(close.get(84));c.setDay86Close(close.get(85));c.setDay87Close(close.get(86));c.setDay88Close(close.get(87));
	        	c.setDay89Close(close.get(88));c.setDay90Close(close.get(89));c.setDay91Close(close.get(90));c.setDay92Close(close.get(91));
	        	c.setDay93Close(close.get(92));c.setDay94Close(close.get(93));c.setDay95Close(close.get(94));c.setDay96Close(close.get(95));
	        	c.setDay97Close(close.get(96));c.setDay98Close(close.get(97));c.setDay99Close(close.get(98));c.setDay100Close(close.get(99));
	        	c.setUpdtDt(date);
	        	closeRepo.save(c);
	        	
	        	Volume v = new Volume();
	        	v.setSymbol(sym.getSymbol().toString());
	        	v.setDay1Volume(volume.get(0));v.setDay2Volume(volume.get(1));v.setDay3Volume(volume.get(2));v.setDay4Volume(volume.get(3));
	        	v.setDay5Volume(volume.get(4));v.setDay6Volume(volume.get(5));v.setDay7Volume(volume.get(6));v.setDay8Volume(volume.get(7));
	        	v.setDay9Volume(volume.get(8));v.setDay10Volume(volume.get(9));v.setDay11Volume(volume.get(10));v.setDay12Volume(volume.get(11));
	        	v.setDay13Volume(volume.get(12));v.setDay14Volume(volume.get(13));v.setDay15Volume(volume.get(14));v.setDay16Volume(volume.get(15));
	        	v.setDay17Volume(volume.get(16));v.setDay18Volume(volume.get(17));v.setDay19Volume(volume.get(18));	v.setDay20Volume(volume.get(19));
	        	v.setDay21Volume(volume.get(20));v.setDay22Volume(volume.get(21));v.setDay23Volume(volume.get(22));v.setDay24Volume(volume.get(23));
	        	v.setDay25Volume(volume.get(24));v.setDay26Volume(volume.get(25));v.setDay27Volume(volume.get(26));v.setDay28Volume(volume.get(27));
	        	v.setDay29Volume(volume.get(28));v.setDay30Volume(volume.get(29));v.setDay31Volume(volume.get(30));v.setDay32Volume(volume.get(31));
	        	v.setDay33Volume(volume.get(32));v.setDay34Volume(volume.get(33));v.setDay35Volume(volume.get(34));v.setDay36Volume(volume.get(35));
	        	v.setDay37Volume(volume.get(36));v.setDay38Volume(volume.get(37));v.setDay39Volume(volume.get(38));v.setDay40Volume(volume.get(39));
	        	v.setDay41Volume(volume.get(40));v.setDay42Volume(volume.get(41));v.setDay43Volume(volume.get(42));v.setDay44Volume(volume.get(43));
	        	v.setDay45Volume(volume.get(44));v.setDay46Volume(volume.get(45));v.setDay47Volume(volume.get(46));v.setDay48Volume(volume.get(47));
	        	v.setDay49Volume(volume.get(48));v.setDay50Volume(volume.get(49));v.setDay51Volume(volume.get(50));v.setDay52Volume(volume.get(51));
	        	v.setDay53Volume(volume.get(52));v.setDay54Volume(volume.get(53));v.setDay55Volume(volume.get(54));v.setDay56Volume(volume.get(55));
	        	v.setDay57Volume(volume.get(56));v.setDay58Volume(volume.get(57));v.setDay59Volume(volume.get(58));v.setDay60Volume(volume.get(59));
	        	v.setDay61Volume(volume.get(60));v.setDay62Volume(volume.get(61));v.setDay63Volume(volume.get(62));v.setDay64Volume(volume.get(63));
	        	v.setDay65Volume(volume.get(64));v.setDay66Volume(volume.get(65));v.setDay67Volume(volume.get(66));v.setDay68Volume(volume.get(67));
	        	v.setDay69Volume(volume.get(68));v.setDay70Volume(volume.get(69));v.setDay71Volume(volume.get(70));v.setDay72Volume(volume.get(71));
	        	v.setDay73Volume(volume.get(72));v.setDay74Volume(volume.get(73));v.setDay75Volume(volume.get(74));v.setDay76Volume(volume.get(75));
	        	v.setDay77Volume(volume.get(76));v.setDay78Volume(volume.get(77));v.setDay79Volume(volume.get(78));v.setDay80Volume(volume.get(79));
	        	v.setDay81Volume(volume.get(80));v.setDay82Volume(volume.get(81));v.setDay83Volume(volume.get(82));v.setDay84Volume(volume.get(83));
	        	v.setDay85Volume(volume.get(84));v.setDay86Volume(volume.get(85));v.setDay87Volume(volume.get(86));v.setDay88Volume(volume.get(87));
	        	v.setDay89Volume(volume.get(88));v.setDay90Volume(volume.get(89));v.setDay91Volume(volume.get(90));v.setDay92Volume(volume.get(91));
	        	v.setDay93Volume(volume.get(92));v.setDay94Volume(volume.get(93));v.setDay95Volume(volume.get(94));v.setDay96Volume(volume.get(95));
	        	v.setDay97Volume(volume.get(96));v.setDay98Volume(volume.get(97));v.setDay99Volume(volume.get(98));v.setDay100Volume(volume.get(99));
	        	v.setUpdtDt(date);
	        	volumeRepo.save(v);
	        	
	        	DividendAmount d = new DividendAmount();
	        	d.setSymbol(sym.getSymbol().toString());
	        	d.setDay1DivAmt(divAmt.get(0));d.setDay2DivAmt(divAmt.get(1));d.setDay3DivAmt(divAmt.get(2));d.setDay4DivAmt(divAmt.get(3));
	        	d.setDay5DivAmt(divAmt.get(4));d.setDay6DivAmt(divAmt.get(5));d.setDay7DivAmt(divAmt.get(6));d.setDay8DivAmt(divAmt.get(7));
	        	d.setDay9DivAmt(divAmt.get(8));d.setDay10DivAmt(divAmt.get(9));d.setDay11DivAmt(divAmt.get(10));d.setDay12DivAmt(divAmt.get(11));
	        	d.setDay13DivAmt(divAmt.get(12));d.setDay14DivAmt(divAmt.get(13));d.setDay15DivAmt(divAmt.get(14));d.setDay16DivAmt(divAmt.get(15));
	        	d.setDay17DivAmt(divAmt.get(16));d.setDay18DivAmt(divAmt.get(17));d.setDay19DivAmt(divAmt.get(18));	d.setDay20DivAmt(divAmt.get(19));
	        	d.setDay21DivAmt(divAmt.get(20));d.setDay22DivAmt(divAmt.get(21));d.setDay23DivAmt(divAmt.get(22));d.setDay24DivAmt(divAmt.get(23));
	        	d.setDay25DivAmt(divAmt.get(24));d.setDay26DivAmt(divAmt.get(25));d.setDay27DivAmt(divAmt.get(26));d.setDay28DivAmt(divAmt.get(27));
	        	d.setDay29DivAmt(divAmt.get(28));d.setDay30DivAmt(divAmt.get(29));d.setDay31DivAmt(divAmt.get(30));d.setDay32DivAmt(divAmt.get(31));
	        	d.setDay33DivAmt(divAmt.get(32));d.setDay34DivAmt(divAmt.get(33));d.setDay35DivAmt(divAmt.get(34));d.setDay36DivAmt(divAmt.get(35));
	        	d.setDay37DivAmt(divAmt.get(36));d.setDay38DivAmt(divAmt.get(37));d.setDay39DivAmt(divAmt.get(38));d.setDay40DivAmt(divAmt.get(39));
	        	d.setDay41DivAmt(divAmt.get(40));d.setDay42DivAmt(divAmt.get(41));d.setDay43DivAmt(divAmt.get(42));d.setDay44DivAmt(divAmt.get(43));
	        	d.setDay45DivAmt(divAmt.get(44));d.setDay46DivAmt(divAmt.get(45));d.setDay47DivAmt(divAmt.get(46));d.setDay48DivAmt(divAmt.get(47));
	        	d.setDay49DivAmt(divAmt.get(48));d.setDay50DivAmt(divAmt.get(49));d.setDay51DivAmt(divAmt.get(50));d.setDay52DivAmt(divAmt.get(51));
	        	d.setDay53DivAmt(divAmt.get(52));d.setDay54DivAmt(divAmt.get(53));d.setDay55DivAmt(divAmt.get(54));d.setDay56DivAmt(divAmt.get(55));
	        	d.setDay57DivAmt(divAmt.get(56));d.setDay58DivAmt(divAmt.get(57));d.setDay59DivAmt(divAmt.get(58));d.setDay60DivAmt(divAmt.get(59));
	        	d.setDay61DivAmt(divAmt.get(60));d.setDay62DivAmt(divAmt.get(61));d.setDay63DivAmt(divAmt.get(62));d.setDay64DivAmt(divAmt.get(63));
	        	d.setDay65DivAmt(divAmt.get(64));d.setDay66DivAmt(divAmt.get(65));d.setDay67DivAmt(divAmt.get(66));d.setDay68DivAmt(divAmt.get(67));
	        	d.setDay69DivAmt(divAmt.get(68));d.setDay70DivAmt(divAmt.get(69));d.setDay71DivAmt(divAmt.get(70));d.setDay72DivAmt(divAmt.get(71));
	        	d.setDay73DivAmt(divAmt.get(72));d.setDay74DivAmt(divAmt.get(73));d.setDay75DivAmt(divAmt.get(74));d.setDay76DivAmt(divAmt.get(75));
	        	d.setDay77DivAmt(divAmt.get(76));d.setDay78DivAmt(divAmt.get(77));d.setDay79DivAmt(divAmt.get(78));d.setDay80DivAmt(divAmt.get(79));
	        	d.setDay81DivAmt(divAmt.get(80));d.setDay82DivAmt(divAmt.get(81));d.setDay83DivAmt(divAmt.get(82));d.setDay84DivAmt(divAmt.get(83));
	        	d.setDay85DivAmt(divAmt.get(84));d.setDay86DivAmt(divAmt.get(85));d.setDay87DivAmt(divAmt.get(86));d.setDay88DivAmt(divAmt.get(87));
	        	d.setDay89DivAmt(divAmt.get(88));d.setDay90DivAmt(divAmt.get(89));d.setDay91DivAmt(divAmt.get(90));d.setDay92DivAmt(divAmt.get(91));
	        	d.setDay93DivAmt(divAmt.get(92));d.setDay94DivAmt(divAmt.get(93));d.setDay95DivAmt(divAmt.get(94));d.setDay96DivAmt(divAmt.get(95));
	        	d.setDay97DivAmt(divAmt.get(96));d.setDay98DivAmt(divAmt.get(97));d.setDay99DivAmt(divAmt.get(98));d.setDay100DivAmt(divAmt.get(99));
	        	d.setUpdtDt(date);
	        	divRepo.save(d);
	        	
	        	Stock s = new Stock();
	        	s.setSymbol(sym.getSymbol().toString());
	        	s.setOpen(open.get(0));
	        	s.setHigh(high.get(0));
	        	s.setLow(low.get(0));
	        	s.setClose(close.get(0));
	        	s.setDivAmt(divAmt.get(0));
	        	s.setVolume(volume.get(0));
	        	s.setUpdtDt(date);
	        	stockRepo.save(s);
	        	
	        	logger.info("Successfully inserted " + sym.getSymbol().toString());
	        	Thread.sleep(2000);
        	}catch(Exception e) {
        		logger.debug("Problem occurred inserting " + sym.getSymbol().toString());
        	}
        }
        logger.info("Successfully updated stock data");
	}
	
	@Scheduled(cron = "0 0 22 * * ?", zone = "CST")
	public void updateSMATechData() {
		List<Symbol> symbols = symbolRepo.findAll();
		
		AlphaVantageConnector apiConnector = new AlphaVantageConnector(APIKEY, TIMEOUT);
		TechnicalIndicators technicalIndicators = new TechnicalIndicators(apiConnector);
        logger.info("Inserting SMA Technical data");
        Timestamp date = new Timestamp(new java.util.Date().getTime());
		for(Symbol sym:symbols) {
			try {
				SMA response = technicalIndicators.sma(sym.getSymbol(), Interval.DAILY, TimePeriod.of(10), SeriesType.CLOSE);
				List<IndicatorData> smaData = response.getData().subList(0, 100);
			
				List<Double> sma = new ArrayList<Double>();
				smaData.forEach(smaPoint -> {
					sma.add((Double)smaPoint.getData());
				});
				
				TenDaySMAData t = new TenDaySMAData();
				t.setSymbol(sym.getSymbol());
		        t.setDay1Close(sma.get(0));t.setDay2Close(sma.get(1));t.setDay3Close(sma.get(2));t.setDay4Close(sma.get(3));
		        t.setDay5Close(sma.get(4));t.setDay6Close(sma.get(5));t.setDay7Close(sma.get(6));t.setDay8Close(sma.get(7));
		        t.setDay9Close(sma.get(8));t.setDay10Close(sma.get(9));t.setDay11Close(sma.get(10));t.setDay12Close(sma.get(11));
		        t.setDay13Close(sma.get(12));t.setDay14Close(sma.get(13));t.setDay15Close(sma.get(14));t.setDay16Close(sma.get(15));
		        t.setDay17Close(sma.get(16));t.setDay18Close(sma.get(17));t.setDay19Close(sma.get(18));t.setDay20Close(sma.get(19));
		        t.setDay21Close(sma.get(20));t.setDay22Close(sma.get(21));t.setDay23Close(sma.get(22));t.setDay24Close(sma.get(23));
		        t.setDay25Close(sma.get(24));t.setDay26Close(sma.get(25));t.setDay27Close(sma.get(26));t.setDay28Close(sma.get(27));
		        t.setDay29Close(sma.get(28));t.setDay30Close(sma.get(29));t.setDay31Close(sma.get(30));t.setDay32Close(sma.get(31));
		        t.setDay33Close(sma.get(32));t.setDay34Close(sma.get(33));t.setDay35Close(sma.get(34));t.setDay36Close(sma.get(35));
		        t.setDay37Close(sma.get(36));t.setDay38Close(sma.get(37));t.setDay39Close(sma.get(38));t.setDay40Close(sma.get(39));
		        t.setDay41Close(sma.get(40));t.setDay42Close(sma.get(41));t.setDay43Close(sma.get(42));t.setDay44Close(sma.get(43));
		        t.setDay45Close(sma.get(44));t.setDay46Close(sma.get(45));t.setDay47Close(sma.get(46));t.setDay48Close(sma.get(47));
		        t.setDay49Close(sma.get(48));t.setDay50Close(sma.get(49));t.setDay51Close(sma.get(50));t.setDay52Close(sma.get(51));
		        t.setDay53Close(sma.get(52));t.setDay54Close(sma.get(53));t.setDay55Close(sma.get(54));t.setDay56Close(sma.get(55));
		        t.setDay57Close(sma.get(56));t.setDay58Close(sma.get(57));t.setDay59Close(sma.get(58));t.setDay60Close(sma.get(59));
		        t.setDay61Close(sma.get(60));t.setDay62Close(sma.get(61));t.setDay63Close(sma.get(62));t.setDay64Close(sma.get(63));
		        t.setDay65Close(sma.get(64));t.setDay66Close(sma.get(65));t.setDay67Close(sma.get(66));t.setDay68Close(sma.get(67));
		        t.setDay69Close(sma.get(68));t.setDay70Close(sma.get(69));t.setDay71Close(sma.get(70));t.setDay72Close(sma.get(71));
		        t.setDay73Close(sma.get(72));t.setDay74Close(sma.get(73));t.setDay75Close(sma.get(74));t.setDay76Close(sma.get(75));
		        t.setDay77Close(sma.get(76));t.setDay78Close(sma.get(77));t.setDay79Close(sma.get(78));t.setDay80Close(sma.get(79));
		        t.setDay81Close(sma.get(80));t.setDay82Close(sma.get(81));t.setDay83Close(sma.get(82));t.setDay84Close(sma.get(83));
		        t.setDay85Close(sma.get(84));t.setDay86Close(sma.get(85));t.setDay87Close(sma.get(86));t.setDay88Close(sma.get(87));
		        t.setDay89Close(sma.get(88));t.setDay90Close(sma.get(89));t.setDay91Close(sma.get(90));t.setDay92Close(sma.get(91));
		        t.setDay93Close(sma.get(92));t.setDay94Close(sma.get(93));t.setDay95Close(sma.get(94));t.setDay96Close(sma.get(95));
		        t.setDay97Close(sma.get(96));t.setDay98Close(sma.get(97));t.setDay99Close(sma.get(98));t.setDay100Close(sma.get(99));
		        t.setUpdtDt(date);
		        tenDayRepo.save(t);
		        logger.info("Successfully instered 10 Day SMA for: " + sym.getSymbol());
		        
		        sma.clear();
		        smaData.clear();
		        response = technicalIndicators.sma(sym.getSymbol(), Interval.DAILY, TimePeriod.of(50), SeriesType.CLOSE);
		        smaData = response.getData().subList(0, 100);
		        smaData.forEach(smaPoint -> {
					sma.add((Double)smaPoint.getData());
				});
		        
		        FiftyDaySMAData f = new FiftyDaySMAData();
		        f.setSymbol(sym.getSymbol());
		        f.setDay1Close(sma.get(0));f.setDay2Close(sma.get(1));f.setDay3Close(sma.get(2));f.setDay4Close(sma.get(3));
		        f.setDay5Close(sma.get(4));f.setDay6Close(sma.get(5));f.setDay7Close(sma.get(6));f.setDay8Close(sma.get(7));
		        f.setDay9Close(sma.get(8));f.setDay10Close(sma.get(9));f.setDay11Close(sma.get(10));f.setDay12Close(sma.get(11));
		        f.setDay13Close(sma.get(12));f.setDay14Close(sma.get(13));f.setDay15Close(sma.get(14));f.setDay16Close(sma.get(15));
		        f.setDay17Close(sma.get(16));f.setDay18Close(sma.get(17));f.setDay19Close(sma.get(18));f.setDay20Close(sma.get(19));
		        f.setDay21Close(sma.get(20));f.setDay22Close(sma.get(21));f.setDay23Close(sma.get(22));f.setDay24Close(sma.get(23));
		        f.setDay25Close(sma.get(24));f.setDay26Close(sma.get(25));f.setDay27Close(sma.get(26));f.setDay28Close(sma.get(27));
		        f.setDay29Close(sma.get(28));f.setDay30Close(sma.get(29));f.setDay31Close(sma.get(30));f.setDay32Close(sma.get(31));
		        f.setDay33Close(sma.get(32));f.setDay34Close(sma.get(33));f.setDay35Close(sma.get(34));f.setDay36Close(sma.get(35));
		        f.setDay37Close(sma.get(36));f.setDay38Close(sma.get(37));f.setDay39Close(sma.get(38));f.setDay40Close(sma.get(39));
		        f.setDay41Close(sma.get(40));f.setDay42Close(sma.get(41));f.setDay43Close(sma.get(42));f.setDay44Close(sma.get(43));
		        f.setDay45Close(sma.get(44));f.setDay46Close(sma.get(45));f.setDay47Close(sma.get(46));f.setDay48Close(sma.get(47));
		        f.setDay49Close(sma.get(48));f.setDay50Close(sma.get(49));f.setDay51Close(sma.get(50));f.setDay52Close(sma.get(51));
		        f.setDay53Close(sma.get(52));f.setDay54Close(sma.get(53));f.setDay55Close(sma.get(54));f.setDay56Close(sma.get(55));
		        f.setDay57Close(sma.get(56));f.setDay58Close(sma.get(57));f.setDay59Close(sma.get(58));f.setDay60Close(sma.get(59));
		        f.setDay61Close(sma.get(60));f.setDay62Close(sma.get(61));f.setDay63Close(sma.get(62));f.setDay64Close(sma.get(63));
		        f.setDay65Close(sma.get(64));f.setDay66Close(sma.get(65));f.setDay67Close(sma.get(66));f.setDay68Close(sma.get(67));
		        f.setDay69Close(sma.get(68));f.setDay70Close(sma.get(69));f.setDay71Close(sma.get(70));f.setDay72Close(sma.get(71));
		        f.setDay73Close(sma.get(72));f.setDay74Close(sma.get(73));f.setDay75Close(sma.get(74));f.setDay76Close(sma.get(75));
		        f.setDay77Close(sma.get(76));f.setDay78Close(sma.get(77));f.setDay79Close(sma.get(78));f.setDay80Close(sma.get(79));
		        f.setDay81Close(sma.get(80));f.setDay82Close(sma.get(81));f.setDay83Close(sma.get(82));f.setDay84Close(sma.get(83));
		        f.setDay85Close(sma.get(84));f.setDay86Close(sma.get(85));f.setDay87Close(sma.get(86));f.setDay88Close(sma.get(87));
		        f.setDay89Close(sma.get(88));f.setDay90Close(sma.get(89));f.setDay91Close(sma.get(90));f.setDay92Close(sma.get(91));
		        f.setDay93Close(sma.get(92));f.setDay94Close(sma.get(93));f.setDay95Close(sma.get(94));f.setDay96Close(sma.get(95));
		        f.setDay97Close(sma.get(96));f.setDay98Close(sma.get(97));f.setDay99Close(sma.get(98));f.setDay100Close(sma.get(99));
		        f.setUpdtDt(date);
		        fiftyDayRepo.save(f);
		        logger.info("Successfully instered 50 Day SMA for: " + sym.getSymbol());
		        
		        sma.clear();
		        smaData.clear();
		        response = technicalIndicators.sma(sym.getSymbol(), Interval.DAILY, TimePeriod.of(200), SeriesType.CLOSE);
		        smaData = response.getData().subList(0, 100);
		        smaData.forEach(smaPoint -> {
					sma.add((Double)smaPoint.getData());
				});
		        
		        TwoHundredDaySMAData fd = new TwoHundredDaySMAData();
		        fd.setSymbol(sym.getSymbol());
		        fd.setDay1Close(sma.get(0));fd.setDay2Close(sma.get(1));fd.setDay3Close(sma.get(2));fd.setDay4Close(sma.get(3));
		        fd.setDay5Close(sma.get(4));fd.setDay6Close(sma.get(5));fd.setDay7Close(sma.get(6));fd.setDay8Close(sma.get(7));
		        fd.setDay9Close(sma.get(8));fd.setDay10Close(sma.get(9));fd.setDay11Close(sma.get(10));fd.setDay12Close(sma.get(11));
		        fd.setDay13Close(sma.get(12));fd.setDay14Close(sma.get(13));fd.setDay15Close(sma.get(14));fd.setDay16Close(sma.get(15));
		        fd.setDay17Close(sma.get(16));fd.setDay18Close(sma.get(17));fd.setDay19Close(sma.get(18));fd.setDay20Close(sma.get(19));
		        fd.setDay21Close(sma.get(20));fd.setDay22Close(sma.get(21));fd.setDay23Close(sma.get(22));fd.setDay24Close(sma.get(23));
		        fd.setDay25Close(sma.get(24));fd.setDay26Close(sma.get(25));fd.setDay27Close(sma.get(26));fd.setDay28Close(sma.get(27));
		        fd.setDay29Close(sma.get(28));fd.setDay30Close(sma.get(29));fd.setDay31Close(sma.get(30));fd.setDay32Close(sma.get(31));
		        fd.setDay33Close(sma.get(32));fd.setDay34Close(sma.get(33));fd.setDay35Close(sma.get(34));fd.setDay36Close(sma.get(35));
		        fd.setDay37Close(sma.get(36));fd.setDay38Close(sma.get(37));fd.setDay39Close(sma.get(38));fd.setDay40Close(sma.get(39));
		        fd.setDay41Close(sma.get(40));fd.setDay42Close(sma.get(41));fd.setDay43Close(sma.get(42));fd.setDay44Close(sma.get(43));
		        fd.setDay45Close(sma.get(44));fd.setDay46Close(sma.get(45));fd.setDay47Close(sma.get(46));fd.setDay48Close(sma.get(47));
		        fd.setDay49Close(sma.get(48));fd.setDay50Close(sma.get(49));fd.setDay51Close(sma.get(50));fd.setDay52Close(sma.get(51));
		        fd.setDay53Close(sma.get(52));fd.setDay54Close(sma.get(53));fd.setDay55Close(sma.get(54));fd.setDay56Close(sma.get(55));
		        fd.setDay57Close(sma.get(56));fd.setDay58Close(sma.get(57));fd.setDay59Close(sma.get(58));fd.setDay60Close(sma.get(59));
		        fd.setDay61Close(sma.get(60));fd.setDay62Close(sma.get(61));fd.setDay63Close(sma.get(62));fd.setDay64Close(sma.get(63));
		        fd.setDay65Close(sma.get(64));fd.setDay66Close(sma.get(65));fd.setDay67Close(sma.get(66));fd.setDay68Close(sma.get(67));
		        fd.setDay69Close(sma.get(68));fd.setDay70Close(sma.get(69));fd.setDay71Close(sma.get(70));fd.setDay72Close(sma.get(71));
		        fd.setDay73Close(sma.get(72));fd.setDay74Close(sma.get(73));fd.setDay75Close(sma.get(74));fd.setDay76Close(sma.get(75));
		        fd.setDay77Close(sma.get(76));fd.setDay78Close(sma.get(77));fd.setDay79Close(sma.get(78));fd.setDay80Close(sma.get(79));
		        fd.setDay81Close(sma.get(80));fd.setDay82Close(sma.get(81));fd.setDay83Close(sma.get(82));fd.setDay84Close(sma.get(83));
		        fd.setDay85Close(sma.get(84));fd.setDay86Close(sma.get(85));fd.setDay87Close(sma.get(86));fd.setDay88Close(sma.get(87));
		        fd.setDay89Close(sma.get(88));fd.setDay90Close(sma.get(89));fd.setDay91Close(sma.get(90));fd.setDay92Close(sma.get(91));
		        fd.setDay93Close(sma.get(92));fd.setDay94Close(sma.get(93));fd.setDay95Close(sma.get(94));fd.setDay96Close(sma.get(95));
		        fd.setDay97Close(sma.get(96));fd.setDay98Close(sma.get(97));fd.setDay99Close(sma.get(98));fd.setDay100Close(sma.get(99));
		        fd.setUpdtDt(date);
		        twoHundredDayRepo.save(fd);
		        logger.info("Successfully instered 200 Day SMA for: " + sym.getSymbol());
				Thread.sleep(2000);
			}catch(Exception e) {
				logger.debug("Problem occurred inserting SMA data");
			}
		}
	}
	
	@Scheduled(cron = "0 0 8 * * ?", zone = "CST")
	public void updateStockTechData() {
		List<StockTechData> std = new ArrayList<StockTechData>();
		String url = "jdbc:mysql://hiller-home-network.dynu.net:1991/stock_analyzer_data_warehouse?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=CST";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection(url,"root","Daisy165*"); 
			Statement stmt = con.createStatement();  
			ResultSet rs = stmt.executeQuery("select * from stock_analyzer_data_warehouse.stock_combined_data_view");
			if (rs.next() == false) {
				logger.info("Result set is empty");
			}
			
			while(rs.next())  {
				StockTechData s = new StockTechData();
				Map<String, String> trends = this.analyzeClosePrices(rs.getString("symbol"));
				s.setSymbol(rs.getString("symbol"));
				s.setName(rs.getString("name"));
				s.setOpen(rs.getDouble("open"));
				s.setHigh(rs.getDouble("high"));
				s.setLow(rs.getDouble("low"));
				s.setClose(rs.getDouble("close"));
				s.setVolume(rs.getInt("volume"));
				s.setTenDaySMA(rs.getDouble("ten_day_sma"));
				s.setFiftyDaySMA(rs.getDouble("fifty_day_sma"));
				s.setTwoHundredDaySMA(rs.getDouble("two_hundred_day_sma"));
				s.setCurrTrend(trends.get("currentClose"));
				s.setTenDayTrend(trends.get("tenDayClose"));
				s.setFiftyDayTrend(trends.get("fiftyDayClose"));
				s.setTwoHundredTrend(trends.get("twoHundredClose"));
				s.setUpdtDt(rs.getTimestamp("last_updt_dt"));

				std.add(s);
			}
			con.close();
		} catch (Exception e) {
			logger.debug("Problem occurred receiving stock tech data");
		}  
		stockTechRepo.saveAll(std);
		logger.info("Finished updating view");
	}
	
	public Map<String, String> analyzeClosePrices(String id){
		String currClose;
		String tenDay;
		String fiftyDay;
		String twoHundred;
		Map<String, String> trends = new HashMap<String, String>();
		
		List<Double> stockClose = closeRepo.findById(id).orElse(null).get100DayClose().subList(0, 10);
		List<Double> tenDayClose = tenDayRepo.findById(id).orElse(null).get100DayClose().subList(0, 10);		
		List<Double> fiftyDayClose = fiftyDayRepo.findById(id).orElse(null).get100DayClose().subList(0, 10);
		List<Double> twoHundredDayClose = twoHundredDayRepo.findById(id).orElse(null).get100DayClose().subList(0, 10);
		
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

	@Override
	public void run(String... args) throws Exception {
		this.updateSymbols();
		this.updateDailyStockData();
		this.updateSMATechData();
		this.updateStockTechData();
	}

}
