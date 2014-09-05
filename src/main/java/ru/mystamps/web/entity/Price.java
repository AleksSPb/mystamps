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
package ru.mystamps.web.entity;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Enumerated;
import javax.persistence.EnumType;

import org.apache.commons.lang3.Validate;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Price {
	public static final int MAX_CURRENCY_LEGNTH = 3;
	
	public static final String VALUE_FIELD_NAME = "value";
	public static final String CURRENCY_FIELD_NAME = "currency";
	
	@Column(name = "price")
	private Double value;
	
	@Enumerated(EnumType.STRING)
	@Column(length = MAX_CURRENCY_LEGNTH)
	private Currency currency;
	
	public static Price valueOf(Double price, Currency currency) {
		if (price == null) {
			return null;
		}
		
		Validate.isTrue(currency != null, "Currency must be non null when price is specified");
		
		return new Price(price, currency);
	}
	
	@Override
	public String toString() {
		// TODO(performance): specify initial capacity explicitly
		return new StringBuilder()
			.append(value)
			.append(' ')
			.append(currency)
			.toString();
	}

	// used to emulate <fmt:formatNumber pattern="###.##" />
	public String getFormattedValue() {
		NumberFormat formatter = NumberFormat.getInstance(Locale.ENGLISH);
		if (formatter instanceof DecimalFormat) {
			((DecimalFormat)formatter).applyPattern("###.##");
		}
		return formatter.format(value.doubleValue());
	}
	
}
