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
			<a class="navbar-brand" href="home" style="color: white;">Stock Market Analyzer</a>
			<ul class="nav navbar-nav" style="float: right;">
				<li class="nav navbar-nav"><a href="/home" style="color: white;">Portfolio</a>
				<li class="nav navbar-nav"><a href="/home" style="color: white;">Industries</a>
				<li class="nav navbar-nav"><a href="/home" style="color: white;">Home</a>
			</ul>
		</nav>
	</div>
	<div class="container border" style="text-align: center;">
		<h2>Stock Technical Data</h2>
		<table id="stockTechDataTable" class="table table-striped table-bordered" style="text-align: center;" cellspacing="0" width="90%">
			<thead class="bg-primary text-white">
				<tr>
				  <th>Symbol</th>
				  <th>Open</th>
				  <th>High</th>
				  <th>Low</th>
				  <th>Close</th>
				  <th>10 Day SMA</th>
				  <th>50 Day SMA</th>
				  <th>200 Day SMA</th>
				  <th>Golden Cross</th>
				  <th>% Difference</th>
				</tr>  
			</thead>
		</table>
		<script type="text/javascript">
			$(document).ready( function () {
				 var table = $('#stockTechDataTable').DataTable({
					"sAjaxSource": "/stock_tech_data",
					"type" : "GET",
					"Processing": true,
			        "ServerSide": true,
					"dataSrc": "",
					"sAjaxDataProp": "",
					'bJQueryUI': true,
					"order": [[ 0, "asc" ]],
					"columns": [
						{ "data": "symbol",
					         "render": function(data, type, row, meta){
					             if(type === 'display'){
					                 data = '<a href="/stock/' + data + '">' + data + '</a>';
					             }
					             return data;
					          }
						},	
						{ "data": "open"},
						{ "data": "high"},
						{ "data": "low"},
						{ "data": "close"},	
						{ "data": "tenDaySMA"},	
						{ "data": "fiftyDaySMA"},	
						{ "data": "twoHundredDaySMA"},
						{ "data": function(data){
								if (data.fiftyDaySMA > data.twoHundredDaySMA){
									return "Pass"
								}else {
									return "Fail"
								}
							}
						},
						{ "data" : function (data){
								var dif = ((data.fiftyDaySMA - data.twoHundredDaySMA) / data.twoHundredDaySMA) * 100
								if (!isFinite(dif)){
									return "% " + 0.00
								}else{
									return "% " + dif.toFixed(2)
								}
							}
						}
					]
				 })
			});	
		</script>
	</div>
</body>
</html>
