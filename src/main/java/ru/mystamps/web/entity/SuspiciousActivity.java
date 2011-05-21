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
