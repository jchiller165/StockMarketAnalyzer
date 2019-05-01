package com.analyzer.framework.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChartDataRetreiver {
	Map<Object,Object> map = null;
	List<Map<Object,Object>> list = new ArrayList<Map<Object,Object>>();
	
	public List<Map<Object,Object>> getData(List<Double> d){
		
		for(int i = 100; i > 0; i --) {
			map = new HashMap<Object,Object>(); map.put("label", i); map.put("y", d.get(i-1)); 
			list.add(map);
		}
		
		return list;
	}
}
