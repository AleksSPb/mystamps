/*
 * Copyright (C) 2009-2016 Slava Semushin <slava.semushin@gmail.com>
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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.dao.JdbcImageDao;

@RequiredArgsConstructor
public class JdbcImageDaoImpl implements JdbcImageDao {
	
	private final NamedParameterJdbcTemplate jdbcTemplate;
	
	@Value("${series_image.add}")
	private String addImageToSeriesSql;
	
	@Value("${series_image.find_by_series_id}")
	private String findBySeriesIdSql;
	
	@Override
	public void addToSeries(Integer seriesId, Integer imageId) {
		Map<String, Object> params = new HashMap<>();
		params.put("series_id", seriesId);
		params.put("image_id", imageId);
		
		jdbcTemplate.update(addImageToSeriesSql, params);
	}
	
	@Override
	public List<Integer> findBySeriesId(Integer seriesId) {
		return jdbcTemplate.queryForList(
			findBySeriesIdSql,
			Collections.singletonMap("series_id", seriesId),
			Integer.class
		);
	}
	
}
