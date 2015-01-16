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
package ru.mystamps.web.config;

import javax.inject.Inject;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ru.mystamps.web.controller.*; // NOCHECKSTYLE: AvoidStarImportCheck, NOPMD: UnusedImports

@Configuration
public class ControllersConfig {
	
	@Inject
	private ServicesConfig servicesConfig;
	
	@Inject
	private MessageSource messageSource;
	
	@Bean
	public AccountController getAccountController() {
		return new AccountController(servicesConfig.getUserService());
	}
	
	@Bean
	public CategoryController getCategoryController() {
		return new CategoryController(
			servicesConfig.getCategoryService(),
			servicesConfig.getSeriesService()
		);
	}
	
	@Bean
	public CountryController getCountryController() {
		return new CountryController(
			servicesConfig.getCountryService(),
			servicesConfig.getSeriesService()
		);
	}
	
	@Bean
	public CollectionController getCollectionController() {
		return new CollectionController(
			servicesConfig.getCategoryService(),
			servicesConfig.getCountryService(),
			servicesConfig.getSeriesService(),
			messageSource
		);
	}
	
	@Bean
	public ImageController getImageController() {
		return new ImageController(servicesConfig.getImageService());
	}
	
	@Bean
	public ErrorController getErrorController() {
		return new ErrorController(servicesConfig.getSiteService());
	}
	
	@Bean
	public RobotsTxtController getRobotsTxtController() {
		return new RobotsTxtController();
	}
	
	@Bean
	public SeriesController getSeriesController() {
		return new SeriesController(
			servicesConfig.getCategoryService(),
			servicesConfig.getCollectionService(),
			servicesConfig.getCountryService(),
			servicesConfig.getSeriesService()
		);
	}
	
	@Bean
	public SiteController getSiteController() {
		return new SiteController(
			servicesConfig.getCategoryService(),
			servicesConfig.getCollectionService(),
			servicesConfig.getCountryService(),
			servicesConfig.getSeriesService()
		);
	}
	
	@Bean
	public SitemapController getSitemapController() {
		return new SitemapController(servicesConfig.getSeriesService());
	}
	
}
