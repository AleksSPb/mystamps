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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */

package ru.mystamps.web.tests.cases;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ArrayList;

import org.junit.Test;

import ru.mystamps.web.tests.page.AddStampsPage;

import static ru.mystamps.web.tests.TranslationUtils.tr;

public class WhenUserAddStamps extends WhenUserAtAnyPageWithForm<AddStampsPage> {
	
	private static final int DAYS_IN_MONTH  = 31;
	private static final int MONTHS_IN_YEAR = 12;
	private static final int SINCE_YEAR     = 1840;
	private static final int CURRENT_YEAR   = new GregorianCalendar().get(Calendar.YEAR);
	
	private static final List<String> EXPECTED_DAYS =
		new ArrayList<String>(DAYS_IN_MONTH + 1);
	
	private static final List<String> EXPECTED_MONTHS =
		new ArrayList<String>(MONTHS_IN_YEAR + 1);
	
	private static final List<String> EXPECTED_YEARS =
		new ArrayList<String>(CURRENT_YEAR - SINCE_YEAR + 1);
	
	static {
		EXPECTED_DAYS.add("");
		for (int i = 1; i <= DAYS_IN_MONTH; i++) {
			EXPECTED_DAYS.add(String.valueOf(i));
		}
		
		EXPECTED_MONTHS.add("");
		for (int i = 1; i <= MONTHS_IN_YEAR; i++) {
			EXPECTED_MONTHS.add(String.valueOf(i));
		}
		
		EXPECTED_YEARS.add("");
		// years in reverse order
		for (int i = CURRENT_YEAR; i >= SINCE_YEAR; i--) {
			EXPECTED_YEARS.add(String.valueOf(i));
		}
	}
	
	public WhenUserAddStamps() {
		super(AddStampsPage.class);
		hasTitle(tr("t_add_series"));
		hasHeader(tr("t_add_series_ucfirst"));
		
		page.open();
	}
	
	@Test
	public void shouldHaveStandardStructure() {
		checkStandardStructure();
	}
	
	@Test
	public void issueDayFieldShouldHaveOptionsFor31Days() {
		assertThat(page.getDayFieldValues()).isEqualTo(EXPECTED_DAYS);
	}
	
	@Test
	public void issueMonthFieldShouldHaveOptionsFor12Months() {
		assertThat(page.getMonthFieldValues()).isEqualTo(EXPECTED_MONTHS);
	}
	
	@Test
	public void issueYearFieldShouldHaveOptionsForRangeFrom1840ToCurrentYear() {
		assertThat(page.getYearFieldValues()).isEqualTo(EXPECTED_YEARS);
	}
	
}
