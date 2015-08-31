/*
 * Copyright (C) 2009-2015 Slava Semushin <slava.semushin@gmail.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package ru.mystamps.web.controller;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.Url;
import ru.mystamps.web.entity.Collection;
import ru.mystamps.web.service.CategoryService;
import ru.mystamps.web.service.CountryService;
import ru.mystamps.web.service.SeriesService;
import ru.mystamps.web.service.dto.SeriesInfoDto;
import ru.mystamps.web.util.LocaleUtils;

@Controller
@RequiredArgsConstructor
public class CollectionController {
	
	private final CategoryService categoryService;
	private final CountryService countryService;
	private final SeriesService seriesService;
	private final MessageSource messageSource;
	
	@RequestMapping(Url.INFO_COLLECTION_PAGE)
	public String showInfo(
		@PathVariable("id") Collection collection,
		Model model,
		Locale userLocale,
		HttpServletResponse response)
		throws IOException {
		
		if (collection == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		
		model.addAttribute("ownerName", collection.getOwner().getName());
		
		String lang = LocaleUtils.getLanguageOrNull(userLocale);
		Iterable<SeriesInfoDto> seriesOfCollection = seriesService.findBy(collection, lang);
		model.addAttribute("seriesOfCollection", seriesOfCollection);
		
		if (seriesOfCollection.iterator().hasNext()) {
			model.addAttribute("categoryCounter", categoryService.countCategoriesOf(collection));
			model.addAttribute("countryCounter", countryService.countCountriesOf(collection));
			model.addAttribute("seriesCounter", seriesService.countSeriesOf(collection));
			model.addAttribute("stampsCounter", seriesService.countStampsOf(collection));
			
			model.addAttribute(
				"statOfCollectionByCategories",
				categoryService.getStatisticsOf(collection, lang)
			);
			model.addAttribute(
				"statOfCollectionByCountries",
				getCountriesStatistics(collection, lang)
			);
		}
		
		return "collection/info";
	}

	// false positive
	@SuppressWarnings("PMD.UnusedPrivateMethod")
	private Map<String, Integer> getCountriesStatistics(Collection collection, String lang) {
		Map<String, Integer> countriesStat = countryService.getStatisticsOf(collection, lang);
		
		// manually localize "Unknown" country's name
		if (countriesStat.containsKey("Unknown")) {
			String message = messageSource.getMessage("t_unspecified", null, new Locale(lang));
			countriesStat.put(message, countriesStat.get("Unknown"));
			countriesStat.remove("Unknown");
		}
		
		return countriesStat;
	}

}
