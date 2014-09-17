/*
 * Copyright (C) 2009-2014 Slava Semushin <slava.semushin@gmail.com>
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

import java.util.Date;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import org.apache.commons.lang3.Validate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.dao.JdbcCountryDao;
import ru.mystamps.web.entity.Collection;
import ru.mystamps.web.entity.Country;
import ru.mystamps.web.entity.User;
import ru.mystamps.web.dao.CountryDao;
import ru.mystamps.web.service.dto.AddCountryDto;
import ru.mystamps.web.service.dto.SelectEntityDto;
import ru.mystamps.web.util.SlugUtils;

@RequiredArgsConstructor
public class CountryServiceImpl implements CountryService {
	private static final Logger LOG = LoggerFactory.getLogger(CountryServiceImpl.class);
	
	private final CountryDao countryDao;
	private final JdbcCountryDao jdbcCountryDao;
	
	@Override
	@Transactional
	@PreAuthorize("hasAuthority('CREATE_COUNTRY')")
	public Country add(AddCountryDto dto, User user) {
		Validate.isTrue(dto != null, "DTO should be non null");
		Validate.isTrue(dto.getName() != null, "Country name on English should be non null");
		Validate.isTrue(dto.getNameRu() != null, "Country name on Russian should be non null");
		Validate.isTrue(user != null, "Current user must be non null");
		
		Country country = new Country();
		country.setName(dto.getName());
		country.setNameRu(dto.getNameRu());
		
		String slug = SlugUtils.slugify(dto.getName());
		Validate.isTrue(slug != null, "Slug for string '%s' is null", dto.getName());
		country.setSlug(slug);
		
		Date now = new Date();
		country.getMetaInfo().setCreatedAt(now);
		country.getMetaInfo().setUpdatedAt(now);
		
		country.getMetaInfo().setCreatedBy(user);
		country.getMetaInfo().setUpdatedBy(user);

		Country entity = countryDao.save(country);
		LOG.info("Country has been created ({})", entity.toLongString());
		
		return entity;
	}
	
	@Override
	@Transactional(readOnly = true)
	public Iterable<SelectEntityDto> findAll(String lang) {
		return countryDao.findAllAsSelectEntries(lang);
	}
	
	@Override
	@Transactional(readOnly = true)
	public long countAll() {
		return countryDao.count();
	}
	
	@Override
	@Transactional(readOnly = true)
	public long countCountriesOf(Collection collection) {
		Validate.isTrue(collection != null, "Collection must be non null");
		Validate.isTrue(collection.getId() != null, "Collection id must be non null");
		
		return jdbcCountryDao.countCountriesOfCollection(collection.getId());
	}
	
	@Override
	@Transactional(readOnly = true)
	public int countByName(String name) {
		Validate.isTrue(name != null, "Name should be non null");
		return countryDao.countByName(name);
	}
	
	@Override
	@Transactional(readOnly = true)
	public int countByNameRu(String name) {
		Validate.isTrue(name != null, "Name on Russian should be non null");
		return countryDao.countByNameRu(name);
	}
	
}
