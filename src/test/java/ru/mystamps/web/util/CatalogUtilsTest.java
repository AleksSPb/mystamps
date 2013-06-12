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

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.runners.MockitoJUnitRunner;

import ru.mystamps.web.entity.GibbonsCatalog;
import ru.mystamps.web.entity.MichelCatalog;
import ru.mystamps.web.entity.ScottCatalog;
import ru.mystamps.web.entity.StampsCatalog;
import ru.mystamps.web.entity.YvertCatalog;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class CatalogUtilsTest {
	
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
		Set<MichelCatalog> singleNumber = Collections.singleton(new MichelCatalog("1"));
		
		assertThat(CatalogUtils.toShortForm(singleNumber)).isEqualTo("1");
	}
	
	@Test
	public void toShortFormShouldReturnPairOfNumbersAsCommaSeparated() {
		Set<MichelCatalog> pairOfNumbers = new LinkedHashSet<MichelCatalog>();
		pairOfNumbers.add(new MichelCatalog("1"));
		pairOfNumbers.add(new MichelCatalog("2"));
		
		assertThat(CatalogUtils.toShortForm(pairOfNumbers)).isEqualTo("1, 2");
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
	public void fromStringShouldReturnEmptyCollectionIfCatalogNumbersIsNull() {
		assertThat(CatalogUtils.fromString(null, MichelCatalog.class)).isEmpty();
	}
	
	@Test
	public void fromStringShouldReturnEmptyCollectionIfCatalogNumbersIsEmpty() {
		assertThat(CatalogUtils.fromString("", MichelCatalog.class)).isEmpty();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void fromStringShouldThrowExceptionIfElementClassIsNull() {
		CatalogUtils.fromString("1", null);
	}
	
	@Test
	public void fromStringShouldReturnOneElementIfCatalogNumbersContainsOneNumber() {
		assertThat(CatalogUtils.fromString("1", MichelCatalog.class)).hasSize(1);
	}
	
	@Test
	public void fromStringShouldReturnOneElementIfCatalogNumbersContainsExtraComma() {
		assertThat(CatalogUtils.fromString("1,", MichelCatalog.class)).hasSize(1);
	}
	
	@Test
	public void fromStringShouldReturnTwoElementsIfCatalogNumbersContainsTwoNumbers() {
		assertThat(CatalogUtils.fromString("1,2", MichelCatalog.class)).hasSize(2);
	}
	
	@Test(expected = IllegalStateException.class)
	public void fromStringShouldThrowExceptionIfOneOfCatalogNumbersIsABlankString() {
		CatalogUtils.fromString("1, ", MichelCatalog.class);
	}
	
	// TODO: fromStringShouldConvertExceptionToRuntimeException()
	// TODO: fromStringShouldThrowRuntimeExceptionAsIs()
	
	@Test
	public void fromStringShouldReturnSetOfMichelNumbersForAppropriateElementClass() {
		for (StampsCatalog catalog : CatalogUtils.fromString("1,2", MichelCatalog.class)) {
			assertThat(catalog).isExactlyInstanceOf(MichelCatalog.class);
		}
	}
	
	@Test
	public void fromStringShouldReturnSetOfScottNumbersForAppropriateElementClass() {
		for (StampsCatalog catalog : CatalogUtils.fromString("1,2", ScottCatalog.class)) {
			assertThat(catalog).isExactlyInstanceOf(ScottCatalog.class);
		}
	}
	
	@Test
	public void fromStringShouldReturnSetOfYvertNumbersForAppropriateElementClass() {
		for (StampsCatalog catalog : CatalogUtils.fromString("1,2", YvertCatalog.class)) {
			assertThat(catalog).isExactlyInstanceOf(YvertCatalog.class);
		}
	}
	
	@Test
	public void fromStringShouldReturnSetOfGibbonsNumbersForAppropriateElementClass() {
		for (StampsCatalog catalog : CatalogUtils.fromString("1,2", GibbonsCatalog.class)) {
			assertThat(catalog).isExactlyInstanceOf(GibbonsCatalog.class);
		}
	}
	
}
