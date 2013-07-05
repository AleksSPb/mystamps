/*
 * Copyright (C) 2009-2013 Slava Semushin <slava.semushin@gmail.com>
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
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.fest.assertions.api.Assertions.assertThat;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ru.mystamps.web.dao.CountryDao;
import ru.mystamps.web.entity.Country;
import ru.mystamps.web.entity.MetaInfo;
import ru.mystamps.web.entity.User;
import ru.mystamps.web.model.AddCountryForm;
import ru.mystamps.web.tests.fest.DateAssert;

@RunWith(MockitoJUnitRunner.class)
public class CountryServiceTest {
	
	private static final Integer TEST_COUNTRY_ID = 1;
	private static final String TEST_COUNTRY_NAME = "Somewhere";
	
	@Mock
	private CountryDao countryDao;
	
	@Captor
	private ArgumentCaptor<Country> countryCaptor;
	
	private CountryService service;
	private AddCountryForm form;
	private User user;
	
	@Before
	public void setUp() {
		form = new AddCountryForm();
		form.setName(TEST_COUNTRY_NAME);
		
		user = UserServiceTest.getValidUser();
		
		service = new CountryServiceImpl(countryDao);
	}
	
	//
	// Tests for add()
	//
	
	@Test(expected = IllegalArgumentException.class)
	public void addShouldThrowExceptionWhenDtoIsNull() {
		service.add(null, user);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void addShouldThrowExceptionWhenCountryNameIsNull() {
		form.setName(null);
		
		service.add(form, user);
	}

	@Test(expected = IllegalArgumentException.class)
	public void addShouldThrowExceptionWhenUserIsNull() {
		service.add(form, null);
	}
	
	@Test
	public void addShouldCallDao() {
		Country expected = getCountry();
		when(countryDao.save(any(Country.class))).thenReturn(expected);
		
		Country actual = service.add(form, user);
		
		assertThat(actual).isEqualTo(expected);
	}
	
	@Test
	public void addShouldPassCountryNameToDao() {
		String expectedCountryName = "Italy";
		form.setName(expectedCountryName);
		
		service.add(form, user);
		
		verify(countryDao).save(countryCaptor.capture());
		
		assertThat(countryCaptor.getValue().getName()).isEqualTo(expectedCountryName);
	}
	
	@Test
	public void addShouldAssignCreatedAtToCurrentDate() {
		service.add(form, user);
		
		verify(countryDao).save(countryCaptor.capture());
		
		MetaInfo metaInfo = countryCaptor.getValue().getMetaInfo();
		assertThat(metaInfo).isNotNull();
		DateAssert.assertThat(metaInfo.getCreatedAt()).isCurrentDate();
	}
	
	@Test
	public void addShouldAssignUpdatedAtToCurrentDate() {
		service.add(form, user);
		
		verify(countryDao).save(countryCaptor.capture());
		
		MetaInfo metaInfo = countryCaptor.getValue().getMetaInfo();
		assertThat(metaInfo).isNotNull();
		DateAssert.assertThat(metaInfo.getUpdatedAt()).isCurrentDate();
	}
	
	@Test
	public void addShouldAssignCreatedByToUser() {
		service.add(form, user);
		
		verify(countryDao).save(countryCaptor.capture());
		final MetaInfo metaInfo = countryCaptor.getValue().getMetaInfo();
		assertThat(metaInfo).isNotNull();
		assertThat(metaInfo.getCreatedBy()).isEqualTo(user);
	}
	
	@Test
	public void addShouldAssignUpdatedByToUser() {
		service.add(form, user);
		
		verify(countryDao).save(countryCaptor.capture());
		final MetaInfo metaInfo = countryCaptor.getValue().getMetaInfo();
		assertThat(metaInfo).isNotNull();
		assertThat(metaInfo.getUpdatedBy()).isEqualTo(user);
	}
	
	//
	// Tests for findAll()
	//
	
	@Test
	public void findAllShouldCallDao() {
		List<Country> expectedCountries = new ArrayList<Country>();
		
		Country country1 = getCountry();
		country1.setName("First Country");
		expectedCountries.add(country1);
		
		Country country2 = getCountry();
		country1.setName("Second Country");
		expectedCountries.add(country2);
		
		when(countryDao.findAll()).thenReturn(expectedCountries);
		
		Iterable<Country> resultCountries = service.findAll();
		
		assertThat(resultCountries).isEqualTo(expectedCountries);
	}
	
	//
	// Tests for findByName()
	//
	
	@Test(expected = IllegalArgumentException.class)
	public void findByNameShouldThrowExceptionWhenNameIsNull() {
		service.findByName(null);
	}
	
	@Test
	public void findByNameShouldCallDao() {
		Country expectedCountry = getCountry();
		when(countryDao.findByName(anyString())).thenReturn(expectedCountry);
		
		Country country = service.findByName(TEST_COUNTRY_NAME);
		
		assertThat(country).isEqualTo(expectedCountry);
	}
	
	@Test
	public void findByNameShouldPassCountryNameToDao() {
		service.findByName(TEST_COUNTRY_NAME);
		
		verify(countryDao).findByName(eq(TEST_COUNTRY_NAME));
	}
	
	static Country getCountry() {
		Country country = new Country();
		country.setId(TEST_COUNTRY_ID);
		country.setName(TEST_COUNTRY_NAME);
		Date now = new Date();
		country.getMetaInfo().setCreatedAt(now);
		country.getMetaInfo().setUpdatedAt(now);
		return country;
	}
	
}
