function initPage1(seriesOfCollection) {

	makeGroupedCollectionsList1('seriesList',seriesOfCollection, 'category', 'country');
}

function makeItemsSublist1(array) {
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

function makeSublist1(array, groupping) {
    var list = document.createElement('ul');
    for(i in array) {
        var item = document.createElement('li');
        // Set its contents:
        var linke = document.createElement("a");
        		linke.href = '/'+groupping+'/' + array[i][0][groupping].id+ '/' + array[i][0][groupping].slug;
        		linke.innerHTML = array[i][0][groupping].name;
        item.appendChild(linke);
        item.appendChild(makeItemsSublist1(array[i]));
        list.appendChild(item);
    }
    return list;
}

function makeGroupedCollectionsList1(elementId, collectioinsList, firstGrouping, secondGrouping) {
	var parentElement = document.getElementById(elementId);
	var list = document.createElement("ul");
	var level1_group = _.groupBy(_.sortBy(collectioinsList,[firstGrouping + '.name', secondGrouping + '.name', 'releaseYear']), firstGrouping + '.name');
	
	console.log('Try to print names')
	for (key in level1_group) {
		console.log(key);
		console.log(level1_group[key]);
	}
	console.log('End of objects');
	
	for (key in level1_group) {
		var item = document.createElement('li');
		var elemText = document.createElement("a");
		elemText.href = '/'+firstGrouping+'/' + level1_group[key][0][firstGrouping].id+ '/' + level1_group[key][0][firstGrouping].slug;
		elemText.innerHTML = level1_group[key][0][firstGrouping].name;
		item.appendChild(elemText);
		item.appendChild(makeSublist1(_.groupBy(level1_group[key], secondGrouping + '.name'), secondGrouping))
		parentElement.appendChild(item);
	}
};
