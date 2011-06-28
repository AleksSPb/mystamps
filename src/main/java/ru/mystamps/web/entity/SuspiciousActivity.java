/*
 * Copyright (C) 2009-2011 Slava Semushin <slava.semushin@gmail.com>
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

package ru.mystamps.web.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "suspicious_activities")
public class SuspiciousActivity {
	
	public static final int PAGE_URL_LENGTH = 100;
	public static final int IP_LENGTH       = 15;
	
	@Getter
	@Setter
	@Id
	@GeneratedValue
	private Integer id;
	
	@Getter
	@Setter
	@ManyToOne(optional = false, cascade = CascadeType.ALL)
	private SuspiciousActivityType type;
	
	@Getter
	@Setter
	@Column(name = "occured_at", nullable = false)
	private Date occuredAt;
	
	@Getter
	@Setter
	@Column(length = PAGE_URL_LENGTH, nullable = false)
	private String page;
	
	@Getter
	@Setter
	@ManyToOne(cascade = CascadeType.ALL)
	private User user;
	
	@Getter
	@Setter
	@Column(length = IP_LENGTH, nullable = false)
	private String ip;
	
	@Getter
	@Setter
	@Column(name = "referer_page", nullable = false)
	private String refererPage;
	
	@Getter
	@Setter
	@Column(name = "user_agent", nullable = false)
	private String userAgent;
	
}
