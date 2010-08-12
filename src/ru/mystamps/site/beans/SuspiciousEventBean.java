package ru.mystamps.site.beans;

import java.sql.SQLException;

import javax.naming.NamingException;

import org.apache.log4j.Logger;

import ru.mystamps.db.SuspiciousActivities;

public class SuspiciousEventBean {
	
	private String type;
	private String page;
	private String ip;
	private String refererPage;
	private String userAgent;
	
	private final Logger log = Logger.getRootLogger();
	private final SuspiciousActivities act;
	
	/**
	 * @throws NamingException
	 **/
	public SuspiciousEventBean()
		throws NamingException {
		act = new SuspiciousActivities();
	}
	
	/**
	 * Log event about suspicious activity to database.
	 * @throws SQLException
	 **/
	public String getLogResult()
		throws SQLException {
		act.logEvent(getType(), getPage(), getIp(), getRefererPage(), getUserAgent());
		return "";
	}
	
	public void setType(final String type) {
		log.debug("type: " + type);
		this.type = type;
	}
	
	public String getType() {
		return this.type;
	}
	
	public void setPage(final String page) {
		log.debug("page: " + page);
		this.page = page;
	}
	
	public String getPage() {
		return this.page;
	}
	
	public void setIp(final String ip) {
		log.debug("ip: " + ip);
		this.ip = ip;
	}
	
	public String getIp() {
		return this.ip;
	}
	
	public void setRefererPage(final String refererPage) {
		log.debug("referer: " + refererPage);
		this.refererPage = refererPage;
	}
	
	public String getRefererPage() {
		return this.refererPage;
	}
	
	public void setUserAgent(final String userAgent) {
		log.debug("user-agent: " + userAgent);
		this.userAgent = userAgent;
	}
	
	public String getUserAgent() {
		return this.userAgent;
	}
	
}

