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
package ru.mystamps.web.util;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.lang3.reflect.ConstructorUtils;

import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.Test;

import org.mockito.runners.MockitoJUnitRunner;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;

import static org.fest.assertions.api.Assertions.assertThat;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyVararg;
import static org.mockito.Mockito.when;

import ru.mystamps.web.entity.GibbonsCatalog;
import ru.mystamps.web.entity.MichelCatalog;
import ru.mystamps.web.entity.ScottCatalog;
import ru.mystamps.web.entity.StampsCatalog;
import ru.mystamps.web.entity.YvertCatalog;

@PrepareForTest(ConstructorUtils.class)
@RunWith(MockitoJUnitRunner.class)
public class CatalogUtilsTest {
	
	@Rule
	public PowerMockRule powerMockRule = new PowerMockRule(); // NOCHECKSTYLE
	
	//
	// Tests for toShortForm()
	//
	
	@Test(expected = IllegalArgumentException.class)
	public void toShortFormShouldThrowExceptionIfNumbersIsNull() {
		CatalogUtils.toShortForm(null);
	}
	
	@Test
	public void toShortFormShouldReturnEmptyStringForEmptyNumbers() {
		assertThat(CatalogUtils.toShortForm(Collections.<MichelCatalog>emptySet())).isEqualTo("");
	}
	
	@Test
	public void toShortFormShouldReturnSingleNumberAsIs() {
		Set<MichelCatalog> setOfSingleNumber = Collections.singleton(new MichelCatalog("1"));
		
		assertThat(CatalogUtils.toShortForm(setOfSingleNumber)).isEqualTo("1");
	}
	
	@Test
	public void toShortFormShouldReturnPairOfNumbersAsCommaSeparated() {
		Set<MichelCatalog> setOfNumbers = new LinkedHashSet<MichelCatalog>();
		setOfNumbers.add(new MichelCatalog("1"));
		setOfNumbers.add(new MichelCatalog("2"));
		
		assertThat(CatalogUtils.toShortForm(setOfNumbers)).isEqualTo("1, 2");
	}
	
	@Test
	public void toShortFormShouldProduceRangeForSequence() {
		Set<MichelCatalog> setOfNumbers = new LinkedHashSet<MichelCatalog>();
		setOfNumbers.add(new MichelCatalog("1"));
		setOfNumbers.add(new MichelCatalog("2"));
		setOfNumbers.add(new MichelCatalog("3"));
		
		assertThat(CatalogUtils.toShortForm(setOfNumbers)).isEqualTo("1-3");
	}
	
	@Test
	public void toShortFormShouldReturnCommaSeparatedNumbersIfTheyAreNotASequence() {
		Set<MichelCatalog> setOfNumbers = new LinkedHashSet<MichelCatalog>();
		setOfNumbers.add(new MichelCatalog("1"));
		setOfNumbers.add(new MichelCatalog("2"));
		setOfNumbers.add(new MichelCatalog("4"));
		setOfNumbers.add(new MichelCatalog("5"));
		
		assertThat(CatalogUtils.toShortForm(setOfNumbers)).isEqualTo("1, 2, 4, 5");
	}
	
	@Test
	public void toShortFormShouldProduceTwoRangesForTwoSequences() {
		Set<MichelCatalog> setOfNumbers = new LinkedHashSet<MichelCatalog>();
		setOfNumbers.add(new MichelCatalog("1"));
		setOfNumbers.add(new MichelCatalog("2"));
		setOfNumbers.add(new MichelCatalog("3"));
		setOfNumbers.add(new MichelCatalog("10"));
		setOfNumbers.add(new MichelCatalog("19"));
		setOfNumbers.add(new MichelCatalog("20"));
		setOfNumbers.add(new MichelCatalog("21"));
		
		assertThat(CatalogUtils.toShortForm(setOfNumbers)).isEqualTo("1-3, 10, 19-21");
	}
	
	//
	// Tests for fromString()
	//
	
	@Test
	public void fromStringShouldReturnsEmptyCollectionIfCatalogNumbersIsNull() {
		assertThat(CatalogUtils.fromString(null, MichelCatalog.class)).isEmpty();
	}
	
	@Test
	public void fromStringShouldReturnsEmptyCollectionIfCatalogNumbersIsEmpty() {
		assertThat(CatalogUtils.fromString("", MichelCatalog.class)).isEmpty();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void fromStringShouldThrowExceptionIfElementClassIsNull() {
		CatalogUtils.fromString("1", null);
	}
	
	@Test
	public void fromStringShouldReturnsOneElementIfCatalogNumbersDoesNotContainsComma() {
		assertThat(CatalogUtils.fromString("1", MichelCatalog.class)).hasSize(1);
	}
	
	@Test
	public void fromStringShouldReturnsOneElementIfCatalogNumbersContainsExtraComma() {
		assertThat(CatalogUtils.fromString("1,", MichelCatalog.class)).hasSize(1);
	}
	
	@Test
	public void fromStringShouldReturnsTwoElementIfCatalogNumbersHasTwoNumbers() {
		assertThat(CatalogUtils.fromString("1,2", MichelCatalog.class)).hasSize(2);
	}
	
	@Test(expected = IllegalStateException.class)
	public void fromStringShouldThrowExceptionIfOneOfCatalogNumbersIsABlankString() {
		CatalogUtils.fromString("1, ", MichelCatalog.class);
	}
	
	@Test(expected = RuntimeException.class)
	public void fromStringShouldConvertExceptionToRuntimeException() throws Exception {
		PowerMockito.mockStatic(ConstructorUtils.class);
		when(ConstructorUtils.invokeConstructor(any(Class.class), anyVararg()))
			.thenThrow(new InstantiationException("Can't initiate object"));
		
		CatalogUtils.fromString("1", NopCatalog.class);
	}
	
	@Test(expected = RuntimeException.class)
	public void fromStringShouldThrowRuntimeExceptionAsIs() throws Exception {
		PowerMockito.mockStatic(ConstructorUtils.class);
		when(ConstructorUtils.invokeConstructor(any(Class.class), anyVararg()))
			.thenThrow(new RuntimeException("Error occurs"));
		
		CatalogUtils.fromString("1", MichelCatalog.class);
	}
	
	@Test
	public void fromStringShouldReturnsSetOfMichelNumbersForAppropriateElementClass() {
		for (StampsCatalog catalog : CatalogUtils.fromString("1,2", MichelCatalog.class)) {
			assertThat(catalog).isExactlyInstanceOf(MichelCatalog.class);
		}
	}
	
	@Test
	public void fromStringShouldReturnsSetOfScottNumbersForAppropriateElementClass() {
		for (StampsCatalog catalog : CatalogUtils.fromString("1,2", ScottCatalog.class)) {
			assertThat(catalog).isExactlyInstanceOf(ScottCatalog.class);
		}
	}
	
	@Test
	public void fromStringShouldReturnsSetOfYvertNumbersForAppropriateElementClass() {
		for (StampsCatalog catalog : CatalogUtils.fromString("1,2", YvertCatalog.class)) {
			assertThat(catalog).isExactlyInstanceOf(YvertCatalog.class);
		}
	}
	
	@Test
	public void fromStringShouldReturnsSetOfGibbonsNumbersForAppropriateElementClass() {
		for (StampsCatalog catalog : CatalogUtils.fromString("1,2", GibbonsCatalog.class)) {
			assertThat(catalog).isExactlyInstanceOf(GibbonsCatalog.class);
		}
	}
	
	class NopCatalog implements StampsCatalog {
		@Override
		public String getCode() {
			return null;
		}
	}
	
}
