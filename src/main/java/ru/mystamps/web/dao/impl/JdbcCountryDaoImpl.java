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
package ru.mystamps.web.dao.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import ru.mystamps.web.dao.JdbcCountryDao;

public class JdbcCountryDaoImpl implements JdbcCountryDao {
	
	private static final RowMapper<Pair<String, Integer>> STRING_INTEGER_PAIR_ROW_MAPPER =
		new StringIntegerPairRowMapper("name", "counter");
	
	private final NamedParameterJdbcTemplate jdbcTemplate;
	
	@Value("${country.count_countries_of_collection}")
	private String countCountriesOfCollectionSql;
	
	@Value("${country.count_stamps_by_countries}")
	private String countStampsByCountriesSql;
	
	public JdbcCountryDaoImpl(DataSource dataSource) {
		jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	@Override
	public long countCountriesOfCollection(Integer collectionId) {
		return jdbcTemplate.queryForObject(
			countCountriesOfCollectionSql,
			Collections.singletonMap("collection_id", collectionId),
			Long.class
		);
	}
	
	@Override
	public Map<String, Integer> getStatisticsOf(Integer collectionId, String lang) {
		Map<String, Object> params = new HashMap<>();
		params.put("collection_id", collectionId);
		params.put("lang", lang);
		
		// TODO: find a better way of extracting results
		List<Pair<String, Integer>> rawResult = jdbcTemplate.query(
			countStampsByCountriesSql,
			params,
			STRING_INTEGER_PAIR_ROW_MAPPER
		);
		
		Map<String, Integer> result = new HashMap<>(rawResult.size(), 1.0f);
		for (Pair<String, Integer> pair : rawResult) {
			result.put(pair.getFirst(), pair.getSecond());
		}
		
		return result;
	}
	
}
