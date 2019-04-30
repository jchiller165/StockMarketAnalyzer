<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="en">

<head>
<link href="${contextPath}/resources/css/bootstrap.min.css" rel="stylesheet">
<link href="${contextPath}/resources/css/common.css" rel="stylesheet">
<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
<link type="text/css" rel="stylesheet" href="@{https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css}" />
<link type="text/css" rel="stylesheet" href="@{https://cdn.datatables.net/1.10.16/css/dataTables.bootstrap.min.css}" />
<script type="text/javascript" src="https://cdn.datatables.net/1.10.16/js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="https://cdn.datatables.net/1.10.16/js/dataTables.bootstrap.min.js"></script>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
<meta name="description" content="">
<meta name="author" content="">

<title>Stock Analyzer</title>

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
	<div class="container border" style="text-align: left;">
		<h2>${stock.symbol} </h2>
			<h4>Name: ${symbol.getName()}</h4>
			<h4>Industry: ${symbol.getIndustry()}</h4>
			<h4>Sector: ${symbol.getSector()}</h4>
		<table class="table table-striped table-bordered" style="text-align: center;">
		  <thead class="bg-primary text-white">
		    <tr>
		      <th scope="col">Open</th>
		      <th scope="col">High</th>
		      <th scope="col">Low</th>
		      <th scope="col">Close</th>
		      <th scope="col">Dividend Amount</th>
		      <th scope="col">Volume</th>
		      <th scope="col">Last Update Date</th>
		    </tr>
		  </thead>
			  <tbody>
			  <tr>
			  	<td>$${stock.open}</td>
			  	<td>$${stock.high}</td>
			  	<td>$${stock.low}</td>
			  	<td>$${stock.close}</td>
			  	<td>%${stock.divAmt}</td>
			  	<td>${stock.volume}</td>
			  	<td>${stock.updtDt}</td>
			  </tr>
			  </tbody>
		  </table>
	</div>
	<div class="container border" style="text-align: left;">

	</div>
</body>