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
package ru.mystamps.web.support.togglz;

import java.util.Collections;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.togglz.console.TogglzConsoleServlet;
import org.togglz.core.manager.FeatureManager;
import org.togglz.core.manager.FeatureManagerBuilder;

@Configuration
public class TogglzConfig {
	
	@Inject
	private DataSource dataSource;
	
	@Bean
	public FeatureManager getFeatureManager() {
		return new FeatureManagerBuilder()
			.togglzConfig(new FeatureConfig(dataSource))
			.build();
	}
	
	/* Web console for managing Togglz.
	 *
	 * Access it via http://127.0.0.1:8081/togglz after authentication as "admin" user.
	 *
	 * @see http://www.togglz.org/documentation/admin-console.html
	 */
	@Bean
	public ServletRegistrationBean getTogglzConsole() {
		ServletRegistrationBean servlet = new ServletRegistrationBean();
		servlet.setName("TogglzConsole");
		servlet.setServlet(new TogglzConsoleServlet());
		// See also src/main/java/ru/mystamps/web/support/spring/security/SecurityConfig.java
		servlet.setUrlMappings(Collections.singletonList("/togglz/*"));
		return servlet;
	}
	
}
