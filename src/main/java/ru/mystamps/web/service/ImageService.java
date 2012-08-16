/*
 * Copyright (C) 2009-2012 Slava Semushin <slava.semushin@gmail.com>
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

import java.io.IOException;
import java.util.Locale;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import  com.google.common.base.Throwables;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import ru.mystamps.web.dao.ImageDao;
import ru.mystamps.web.entity.Image;
import ru.mystamps.web.Url;

@Service
public class ImageService {
	
	@Inject
	private ImageDao imageDao;
	
	// Method called from SeriesService.add()
	protected String save(final MultipartFile file) {
		checkArgument(file != null, "File should be non null");
		checkArgument(file.getSize() > 0, "Image size must be greater than zero");
		
		final String contentType = file.getContentType();
		checkArgument(contentType != null, "File type must be non null");
		
		final String extension = StringUtils.substringAfter(contentType, "/");
		checkState(
			"png".equals(extension) || "jpeg".equals(extension),
			"File type must be PNG or JPEG image"
		);
		
		final Image image = new Image();
		image.setType(Image.Type.valueOf(extension.toUpperCase(Locale.US)));
		
		try {
			image.setData(file.getBytes());
		} catch (final IOException e) {
			// throw RuntimeException for rolling back transaction
			throw Throwables.propagate(e);
		}
		
		final Image entity = imageDao.save(image);
		
		return Url.GET_IMAGE_PAGE.replace("{id}", String.valueOf(entity.getId()));
	}
	
	@Transactional(readOnly = true)
	public Image findById(final Integer id) {
		checkArgument(id != null, "Id should be non null");
		return imageDao.findOne(id);
	}
	
}
