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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.annotation.PostConstruct;

import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

import ru.mystamps.web.entity.Image;
import ru.mystamps.web.service.dto.FsImageDto;
import ru.mystamps.web.service.dto.ImageDto;
import ru.mystamps.web.service.exception.ImagePersistenceException;

@Slf4j
public class FilesystemImagePersistenceStrategy implements ImagePersistenceStrategy {
	
	private final File storageDir;
	
	public FilesystemImagePersistenceStrategy(String directory) {
		this.storageDir = new File(directory);
	}
	
	@PostConstruct
	public void init() {
		LOG.info("Images will be saved into {} directory", storageDir);
		
		if (!storageDir.exists()) {
			LOG.warn("Directory '{}' doesn't exist! Image uploading won't work.", storageDir);
		}
		
		if (!storageDir.canWrite()) {
			LOG.warn(// NOPMD: GuardLogStatement
				"Directory '{}' exists but doesn't writable for current user! "
				+ "Image uploading won't work.",
				storageDir
			);
		}
	}
	
	@Override
	public void save(MultipartFile file, Image image) {
		try {
			Path dest = createFile(image);
			writeToFile(file, dest);
			
			LOG.info("Image's data was written into file {}", dest);
		
		} catch (IOException ex) {
			throw new ImagePersistenceException(ex);
		}
	}
	
	@Override
	public ImageDto get(Image image) {
		Path dest = createFile(image);
		if (!exists(dest)) {
			LOG.warn("Found image without content: #{} ({} doesn't exist)", image.getId(), dest);
			return null;
		}
		
		try {
			byte[] content = toByteArray(dest);
			return new FsImageDto(image, content);
		
		} catch (IOException ex) {
			throw new ImagePersistenceException(ex);
		}
	}
	
	// protected to allow spying
	protected Path createFile(Image image) {
		return new File(storageDir, generateFileName(image)).toPath();
	}
	
	// protected to allow spying
	protected void writeToFile(MultipartFile file, Path dest) throws IOException {
		// we can't use file.transferTo(dest) there because it creates file
		// relatively to directory from multipart.location in application.properties
		Files.copy(file.getInputStream(), dest);
	}

	// protected to allow spying
	protected boolean exists(Path path) {
		return Files.exists(path);
	}
	
	// protected to allow spying
	protected byte[] toByteArray(Path dest) throws IOException {
		return Files.readAllBytes(dest);
	}

	private static String generateFileName(Image image) {
		// TODO(performance): specify initial capacity explicitly
		return new StringBuilder()
			.append(image.getId())
			.append('.')
			.append(image.getType().toString().toLowerCase())
			.toString();
	}
	
}
