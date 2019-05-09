<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="en">

<head>
<link href="${contextPath}/resources/css/bootstrap.min.css" rel="stylesheet">
<link href="${contextPath}/resources/css/common.css" rel="stylesheet">
<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
<meta name="description" content="">
<meta name="author" content="">


<title>Stock Analyzer</title>
<script type="text/javascript">
	window.onload = function() { 
	
	var chart = new CanvasJS.Chart("closeContainer", {
		theme: "light2",
		title: {
			text: "${symbol.getName()}"
		},
		subtitles: [{
			text: "Close Price Trend"
		}],
		axisY:{
			title: "Closing Price",
			suffix: "$",
			includeZero: false
		},
		data: [{
			label: "Daily Close",
			legendText: "Daily Close",
			type: "line",
			showInLegend: true, 
	        name: "series1",
			toolTipContent: "<b>{label}</b>: {y}",
			dataPoints: ${closePrices}
		},
		{
			label: "10 Day SMA",
			legendText: "10 Day SMA",
			type: "line",
			showInLegend: true, 
	        name: "series2",
			toolTipContent: "<b>{label}</b>: {y}",
			dataPoints: ${tenDayPrices}
		}
		]
	});
	
	var chart2 = new CanvasJS.Chart("macdContainer", {
		theme: "light2",
		title: {
			text: "${symbol.getName()}"
		},
		subtitles: [{
			text: "MACD Trend"
		}],
		axisY:{
			title: "MACD Data",
			suffix: "",
			includeZero: true
		},
		data: [{
			label: "Signal",
			legendText: "Signal",
			type: "line",
			showInLegend: true, 
	        name: "series1",
			toolTipContent: "<b>{label}</b>: {y}",
			dataPoints: ${signal}
		},
		{
			label: "MACD",
			legendText: "MACD",
			type: "line",
			showInLegend: true, 
	        name: "series2",
			toolTipContent: "<b>{label}</b>: {y}",
			dataPoints: ${macd}
		}
		]
	});
	chart.render();
	chart2.render(); 
}
</script>

</head>

<body>
	<div>
		<nav class="navbar bg-primary text-white navbar-light">
			<a class="navbar-brand" href="/home" style="color: white;">Stock Market Analyzer</a>
			<ul class="nav navbar-nav" style="float: right;">
				<li class="nav navbar-nav"><a href="/home" style="color: white;">Portfolio</a>
				<li class="nav navbar-nav"><a href="/home" style="color: white;">Industries</a>
				<li class="nav navbar-nav"><a href="/home" style="color: white;">Home</a>
			</ul>
		</nav>
	</div>
	
	<div class="container" style="text-align: left;">
		<h2>${stock.symbol} </h2>
			<h4>Name: ${symbol.getName()}</h4>
			<h4>Industry: ${symbol.getIndustry()}</h4>
			<h4>Sector: ${symbol.getSector()}</h4>
	</div>
	
	<div class="container">
		<table class="table table-striped table-bordered" style="text-align: center;">
		  <thead class="bg-primary text-white">
		    <tr>
		      <th scope="col" style="text-align: center;">Open</th>
		      <th scope="col" style="text-align: center;">High</th>
		      <th scope="col" style="text-align: center;">Low</th>
		      <th scope="col" style="text-align: center;">Close</th>
		      <th scope="col" style="text-align: center;">Current Trend</th>
		      <th scope="col" style="text-align: center;">Ten Day Trend</th>
		      <th scope="col" style="text-align: center;">Fifty Day Trend</th>
		      <th scope="col" style="text-align: center;">Two Hundred Day Trend</th>
		    </tr>
		  </thead>
			  <tbody>
			  <tr>
			  	<td>$${stock.open}</td>
			  	<td>$${stock.high}</td>
			  	<td>$${stock.low}</td>
			  	<td>$${stock.close}</td>
			  	<c:choose>
			  		<c:when test="${trends.get('currentClose') == 'Up'}">
			  			<td style="color:green;">${trends.get("currentClose")}</td>
			  		</c:when>
			  		<c:otherwise>
			  			<td style="color:red;">${trends.get("currentClose")}</td>
			  		</c:otherwise>
			  	</c:choose>
			  	<c:choose>
			  		<c:when test="${trends.get('tenDayClose') == 'Up'}">
			  			<td style="color:green;">${trends.get("tenDayClose")}</td>
			  		</c:when>
			  		<c:otherwise>
			  			<td style="color:red;">${trends.get("tenDayClose")}</td>
			  		</c:otherwise>
			  	</c:choose>
			  	<c:choose>
			  		<c:when test="${trends.get('fiftyDayClose') == 'Up'}">
			  			<td style="color:green;">${trends.get("fiftyDayClose")}</td>
			  		</c:when>
			  		<c:otherwise>
			  			<td style="color:red;">${trends.get("fiftyDayClose")}</td>
			  		</c:otherwise>
			  	</c:choose>
			  	<c:choose>
			  		<c:when test="${trends.get('twoHundredClose') == 'Up'}">
			  			<td style="color:green;">${trends.get("twoHundredClose")}</td>
			  		</c:when>
			  		<c:otherwise>
			  			<td style="color:red;">${trends.get("twoHundredClose")}</td>
			  		</c:otherwise>
			  	</c:choose>
			  </tr>
			  </tbody>
		  </table>
	</div>
	<div class="container border" style="text-align: left;">
		<div id="closeContainer" style="height: 370px; width: 100%;"></div>
	</div>
	<div class="container border" style="text-align: left;">
		<div id="macdContainer" style="height: 370px; width: 100%;"></div>
	</div>
	<script src="https://canvasjs.com/assets/script/canvasjs.min.js"></script>
</body>