package ru.mystamps.web.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "suspicious_activities_types")
public class SuspiciousActivityType {
	
	public static final int NAME_LENGTH = 100;
	
	@Getter
	@Setter
	@Id
	@GeneratedValue
	private Integer id;
	
	@Getter
	@Setter
	@Column(length = NAME_LENGTH, unique = true, nullable = false)
	private String name;
	
}
