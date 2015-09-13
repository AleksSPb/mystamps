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

import org.apache.commons.lang3.Validate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.dao.CollectionDao;
import ru.mystamps.web.dao.JdbcCollectionDao;
import ru.mystamps.web.dao.dto.AddCollectionDbDto;
import ru.mystamps.web.entity.Collection;
import ru.mystamps.web.entity.Series;
import ru.mystamps.web.entity.User;
import ru.mystamps.web.service.dto.LinkEntityDto;
import ru.mystamps.web.util.SlugUtils;

@RequiredArgsConstructor
public class CollectionServiceImpl implements CollectionService {
	private static final Logger LOG = LoggerFactory.getLogger(CollectionServiceImpl.class);
	
	private final CollectionDao collectionDao;
	private final JdbcCollectionDao jdbcCollectionDao;
	
	@Override
	@Transactional
	public void createCollection(User user) {
		Validate.isTrue(user != null, "User must be non null");
		
		AddCollectionDbDto collection = new AddCollectionDbDto();
		collection.setOwnerId(user.getId());
		
		String slug = SlugUtils.slugify(user.getLogin());
		Validate.isTrue(slug != null, "Slug for string '%s' is null", user.getLogin());
		collection.setSlug(slug);
		
		Integer id = jdbcCollectionDao.add(collection);
		
		LOG.info("Collection #{} has been created ({})", id, collection);
	}
	
	@Override
	@Transactional
	@PreAuthorize("hasAuthority('UPDATE_COLLECTION')")
	public Collection addToCollection(User user, Series series) {
		Validate.isTrue(user != null, "User must be non null");
		Validate.isTrue(series != null, "Series must be non null");
		
		// We can't just invoke user.getCollection().getSeries() because
		// it will lead to LazyInitializationException. To workaround this
		// we are loading collection by invoking dao.
		Collection collection = collectionDao.findOne(user.getCollection().getId());
		
		collection.getSeries().add(series);
		
		LOG.info(
			"Series #{} has been added to collection of user #{}",
			series.getId(),
			user.getId()
		);
		
		return collection;
	}
	
	@Override
	@Transactional
	@PreAuthorize("hasAuthority('UPDATE_COLLECTION')")
	public Collection removeFromCollection(User user, Series series) {
		Validate.isTrue(user != null, "User must be non null");
		Validate.isTrue(series != null, "Series must be non null");
		
		// We can't just invoke user.getCollection().getSeries() because
		// it will lead to LazyInitializationException. To workaround this
		// we are loading collection by invoking dao.
		Collection collection = collectionDao.findOne(user.getCollection().getId());
		
		collection.getSeries().remove(series);
		
		LOG.info(
			"Series #{} has been removed from collection of user #{}",
			series.getId(),
			user.getId()
		);
		
		return collection;
	}
	
	@Override
	@Transactional(readOnly = false)
	public boolean isSeriesInCollection(Integer userId, Integer seriesId) {
		Validate.isTrue(seriesId != null, "Series id must be non null");
		
		if (userId == null) {
			// Anonymous user doesn't have collection
			return false;
		}
		
		boolean isSeriesInCollection = jdbcCollectionDao.isSeriesInUserCollection(userId, seriesId);
		
		LOG.debug(
			"Series #{} belongs to collection of user #{}: {}",
			seriesId,
			userId,
			isSeriesInCollection
		);
		
		return isSeriesInCollection;
	}
	
	@Override
	@Transactional(readOnly = true)
	public long countCollectionsOfUsers() {
		return jdbcCollectionDao.countCollectionsOfUsers();
	}
	
	@Override
	@Transactional(readOnly = true)
	public Iterable<LinkEntityDto> findRecentlyCreated(int quantity) {
		Validate.isTrue(quantity > 0, "Quantity must be greater than 0");
		
		return jdbcCollectionDao.findLastCreated(quantity);
	}
	
}
