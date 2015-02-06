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
package ru.mystamps.web.dao;

import ru.mystamps.web.service.dto.SeriesInfoDto;
import ru.mystamps.web.service.dto.SitemapInfoDto;

public interface JdbcSeriesDao {
	Iterable<SitemapInfoDto> findAllForSitemap();
	Iterable<SeriesInfoDto> findLastAdded(int quantity, String lang);
	Iterable<SeriesInfoDto> findByCategoryIdAsSeriesInfo(Integer categoryId, String lang);
	Iterable<SeriesInfoDto> findByCountryIdAsSeriesInfo(Integer countryId, String lang);
	Iterable<SeriesInfoDto> findByCollectionIdAsSeriesInfo(Integer collectionId, String lang);
	long countAll();
	long countAllStamps();
	long countSeriesOfCollection(Integer collectionId);
	long countStampsOfCollection(Integer collectionId);
	long countByMichelNumberCode(String michelNumber);
	long countByScottNumberCode(String scottNumber);
	long countByYvertNumberCode(String yvertNumber);
	long countByGibbonsNumberCode(String gibbonsNumber);
}
