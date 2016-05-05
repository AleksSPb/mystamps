function initPage(seriesOfCollection) {

	makeCategoriesList('seriesList',seriesOfCollection, 'category', 'country');
}

function makeUL(array, groupping, name, slugfield) {
console.log(array);
    // Create the list element:
    var list = document.createElement('ul');

    for(i in array) {
        // Create the list item:
        console.log("Name:" + i);
        console.log(array[i]);
        var item = document.createElement('li');

        // Set its contents:
        var linke = document.createElement("a");
        		linke.href = '/'+groupping+'/' + array[i][0][groupping].id+ '/' + array[i][0][groupping].slug;
        		linke.innerHTML = array[i][0][groupping].name;
        item.appendChild(linke);
        // Add it to the list:
        list.appendChild(item);
    }

    // Finally, return the constructed list:
    return list;
}

function makeCategoriesList(elementId, categoriesList, firstGrouping, secondGrouping) {
	var parentElement = document.getElementById(elementId);
	parentElement.innerHTML = 'It a text before list';
	var list = document.createElement("ul");
	var level1_group = _.groupBy(_.sortBy(categoriesList,firstGrouping + '.name'), firstGrouping + '.name');
	
	console.log('Try to print names')
	for (key in level1_group) {
		console.log(key);
		console.log(level1_group[key]);
	}
	
	console.log('End of objects');
	
	
	for (key in level1_group) {
	var item = document.createElement('li');
		var elemText = document.createElement("a");
		elemText.href = '/'+firstGrouping+'/' + level1_group[key][0].category.id+ '/' + level1_group[key][0].category.slug;
		elemText.innerHTML = level1_group[key][0].category.name;
		item.appendChild(elemText);
		var level2_group = _.groupBy(_.sortBy(level1_group[key], secondGrouping + '.name'),secondGrouping + '.name');
		item.appendChild(makeUL(level2_group, 'country', 'name', 'slug'))
		parentElement.appendChild(item);
		
	}
};
