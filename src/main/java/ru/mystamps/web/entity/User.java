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
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
public class User {
	
	public static final int LOGIN_LENGTH = 15;
	public static final int NAME_LENGTH  = 100;
	public static final int HASH_LENGTH  = 40;
	public static final int SALT_LENGTH  = 10;
	
	@Getter
	@Setter
	@Id
	@GeneratedValue
	private Integer id;
	
	@Getter
	@Setter
	@Column(length = LOGIN_LENGTH, unique = true, nullable = false)
	private String login;
	
	@Getter
	@Setter
	@Column(length = NAME_LENGTH, nullable = false)
	private String name;
	
	@Getter
	@Setter
	@Column(nullable = false)
	private String email;
	
	@Getter
	@Setter
	@Column(name = "registered_at", nullable = false)
	private Date registeredAt;
	
	@Getter
	@Setter
	@Column(name = "activated_at", nullable = false)
	private Date activatedAt;
	
	@Getter
	@Setter
	@Column(length = HASH_LENGTH, nullable = false)
	private String hash;
	
	@Getter
	@Setter
	@Column(length = SALT_LENGTH, nullable = false)
	private String salt;
	
}
