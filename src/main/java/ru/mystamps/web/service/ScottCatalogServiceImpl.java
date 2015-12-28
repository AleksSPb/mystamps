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
package ru.mystamps.web.service;

import java.util.Set;

import org.apache.commons.lang3.Validate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.access.prepost.PreAuthorize;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.dao.ScottCatalogDao;

@RequiredArgsConstructor
public class ScottCatalogServiceImpl implements ScottCatalogService {
	private static final Logger LOG = LoggerFactory.getLogger(ScottCatalogServiceImpl.class);
	
	private final ScottCatalogDao scottCatalogDao;
	
	@Override
	@Transactional
	@PreAuthorize("hasAuthority('CREATE_SERIES')")
	public void add(Set<String> scottNumbers) {
		Validate.isTrue(scottNumbers != null, "Scott numbers must be non null");
		Validate.isTrue(!scottNumbers.isEmpty(), "Scott numbers must be non empty");
		
		scottCatalogDao.add(scottNumbers);
	}
	
	@Override
	@Transactional
	@PreAuthorize("hasAuthority('CREATE_SERIES')")
	public void addToSeries(Integer seriesId, Set<String> scottNumbers) {
		Validate.isTrue(seriesId != null, "Series id must be non null");
		Validate.isTrue(scottNumbers != null, "Scott numbers must be non null");
		Validate.isTrue(!scottNumbers.isEmpty(), "Scott numbers must be non empty");
		
		scottCatalogDao.addToSeries(seriesId, scottNumbers);
		
		LOG.info("Series #{}: scoot numbers {} were added", seriesId, scottNumbers);
	}
	
}
