function initPage(statByCategories, statByCountries, seriesOfCollection) {
	var chartsVersion = '44';
	google.charts.load(chartsVersion, {'packages':['corechart']});
	google.charts.setOnLoadCallback(function drawCharts() {
		drawChart('categories-chart', createDataTable(statByCategories));
		drawChart('countries-chart', createDataTable(statByCountries));
	});
	makeCategoriesList('seriesList',seriesOfCollection, 'category.name', 'country.name');
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

function makeCategoriesList(elementId, categoriesList, firstGrouping, secondGrouping) {
	var element = document.getElementById(elementId);
	element.innerHTML = 'It a text before ist';
	var list = document.createElement("ul");
	var level1_group = _.groupBy(categoriesList, firstGrouping);
	console.log(level1_group);
	console.log('End grouped object')
	console.log(Object.keys(level1_group)); 
	
	console.log('Try to print names')
	for(var j in level1_group) {
	console.log(j);
	console.log(level1_group[j]);
	}
	

	console.log('End of objects')
	for(var i in categoriesList) {
	var elemText = document.createElement("a");
	elemText.href = '/category/' + categoriesList[i].category.id+ '/' + categoriesList[i].category.slug;
	elemText.innerHTML = categoriesList[i].category.name;
	element.appendChild(elemText);
	
	var elem = document.createElement("li");
	elem.appendChild(elemText);
	list.appendChild(elem);
	element.appendChild(list);
	}
};
