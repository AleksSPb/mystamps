function initPage(statByCategories, statByCountries, seriesOfCollection) {
	makeGroupedCollectionsList('seriesList',seriesOfCollection, 'category', 'country');
	var chartsVersion = '44';
	google.charts.load(chartsVersion, {'packages':['corechart']});
	google.charts.setOnLoadCallback(function drawCharts() {
		drawChart('categories-chart', createDataTable(statByCategories));
		drawChart('countries-chart', createDataTable(statByCountries));
	});
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

function makeItemsSublist(array) {
	var list = document.createElement('ul');
	for(i in array) {
		var item = document.createElement('li');
		// Set its contents:
		var linke = document.createElement("a");
				linke.href = '/series/'+ array[i].id;
				if (array[i].releaseYear) {
					linke.innerHTML = array[i].releaseYear + ', ' + array[i].quantity + ' item(s)';
				} else {
				linke.innerHTML = array[i].quantity + ' item(s)';
				}
		item.appendChild(linke);
		list.appendChild(item);
	}
	return list;
}

function makeSublist(array, groupping) {
	var list = document.createElement('ul');
	for(i in array) {
		var item = document.createElement('li');
		// Set its contents:
		var linke = document.createElement("a");
				linke.href = '/'+groupping+'/' + array[i][0][groupping].id+ '/' + array[i][0][groupping].slug;
				linke.innerHTML = array[i][0][groupping].name;
		item.appendChild(linke);
		item.appendChild(makeItemsSublist(array[i]));
		list.appendChild(item);
	}
	return list;
}

function makeGroupedCollectionsList(elementId, collectioinsList, firstGrouping, secondGrouping) {
	var parentElement = document.getElementById(elementId);
	var list = document.createElement("ul");
	var level1_group = _.groupBy(_.sortBy(collectioinsList,[firstGrouping + '.name', secondGrouping + '.name', 'releaseYear']), firstGrouping + '.name');
	for (key in level1_group) {
		var item = document.createElement('li');
		var elemText = document.createElement("a");
		elemText.href = '/'+firstGrouping+'/' + level1_group[key][0][firstGrouping].id+ '/' + level1_group[key][0][firstGrouping].slug;
		elemText.innerHTML = level1_group[key][0][firstGrouping].name;
		item.appendChild(elemText);
		item.appendChild(makeSublist(_.groupBy(level1_group[key], secondGrouping + '.name'), secondGrouping))
		parentElement.appendChild(item);
	}
};
