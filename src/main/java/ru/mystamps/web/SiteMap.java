package ru.mystamps.web;

/**
 * Site map (holds path to site and all URLs).
 *
 * Should be statically imported and used anywhere instead of hard-coded paths.
 *
 * @author Slava Semushin <slava.semushin@gmail.com>
 * @since 2010-10-24
 */
public class SiteMap {
	
	// defined at pom.xml (and used by functional tests only)
	public static final String SITE_URL                 = "http://127.0.0.1:8081";
	
	// defined at webapp/WEB-INF/spring-servlet.xml
	public static final String INDEX_PAGE_URL           = "/site/index.htm";
	public static final String MAINTENANCE_PAGE_URL     = "/site/maintenance.htm";
	
	public static final String REGISTRATION_PAGE_URL    = "/account/register.htm";
	public static final String AUTHENTICATION_PAGE_URL  = "/account/auth.htm";
	public static final String ACTIVATE_ACCOUNT_PAGE_URL= "/account/activate.htm";
	public static final String LOGOUT_PAGE_URL          = "/account/logout.htm";
	
	public static final String ADD_STAMPS_PAGE_URL      = "/stamps/add.htm";
	
	public static final String ADD_COUNTRY_PAGE_URL     = "/country/add.htm";
	
	// defined at webapp/WEB-INF/spring-servlet.xml
	public static final String RESTORE_PASSWORD_PAGE_URL= "/password/restore.htm";
	
	public static final String NOT_FOUND_PAGE_URL       = "/error/404.htm";
	
}
