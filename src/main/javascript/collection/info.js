function initPage(statByCategories, statByCountries, seriesOfCollection) {
	var chartsVersion = '44';
	google.charts.load(chartsVersion, {'packages':['corechart']});
	google.charts.setOnLoadCallback(function drawCharts() {
		drawChart('categories-chart', createDataTable(statByCategories));
		drawChart('countries-chart', createDataTable(statByCountries));
	});
	makeCategoriesList('seriesList',seriesOfCollection);
}

function drawChart(containerId, dataTable) {
	var options = {
		pieHole: 0.3
	};
	var chart = new google.visualization.PieChart(document.getElementById(containerId));
	chart.draw(dataTable, options);
};

function createDataTable(stat) {
	var table = new google.visualization.DataTable();
	table.addColumn('string', 'Category/Country');
	table.addColumn('number', 'Quantity of stamps');
	table.addRows(stat);
	return table;
};

function makeCategoriesList(elementId, categoriesList) {
	var element = document.getElementById(elementId);
	element.innerHTML = '';
	element.innerHTML = 'It a text before list';
	var list = document.createElement("ul");
	for(var i in categoriesList) {
	var elemText = document.createElement("a");
	elemText.innerHTML = categoriesList[i].category.name;
	element.appendChild(elemText);
	
	var elem = document.createElement("li");
	elem.appendChild(elemText);
	list.appendChild(elem);
	element.appendChild(list);
	}
	
	
	var table = new google.visualization.DataTable();
	table.addColumn('string', 'Category/Country');
	table.addColumn('number', 'Quantity of stamps');
	table.addRows(stat);
	return table;
};
