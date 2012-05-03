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

package ru.mystamps.web.tests.page;

import java.io.IOException;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;

import static com.google.common.base.Preconditions.checkArgument;

import ru.mystamps.web.tests.WebElementUtils;

import static ru.mystamps.web.SiteMap.SITE_URL;

import lombok.RequiredArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@RequiredArgsConstructor(access = PROTECTED)
public abstract class AbstractPage {
	
	private static final String A_HREF_LOCATOR = "//a[@href=\"%s\"]";
	
	private final WebDriver driver;
	private final String pageUrl;
	
	@FindBy(tagName = "body")
	@CacheLookup
	private WebElement body;
	
	public void open() {
		driver.get(getFullUrl());
	}
	
	public void open(final String pageUrl) {
		driver.get(SITE_URL + pageUrl);
	}
	
	/**
	 * Opens page and returns server response code.
	 * In case of error returns -1.
	 */
	public int getServerResponseCode() {
		/**
		 * XXX: currently we can't get access to server response from Selenium :(
		 * @see http://code.google.com/p/selenium/issues/detail?id=141
		 */
		
		try {
			// As workaround, for a while, use HttpUrlConnection
			// (but it leads to two connection to each page).
			final URL url = new URL(getFullUrl());
			final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			return conn.getResponseCode();
		
		} catch (final IOException ex) {
			// don't throw exception because it leads to error in tests
			// instead of failure
			return -1;
		}
	}
	
	private String getFullUrl() {
		return SITE_URL + pageUrl;
	}
	
	/**
	 * Get short URL (without host name) of page.
	 **/
	public String getUrl() {
		return pageUrl;
	}
	
	/**
	 * Get short URL (without host name) of current page.
	 **/
	public String getCurrentUrl() {
		return driver.getCurrentUrl().replace(SITE_URL, "");
	}
	
	public String getTitle() {
		return driver.getTitle();
	}
	
	public String getTextAtLogo() {
		return getTextOfElementById("logo");
	}
	
	public boolean userBarExists() {
		return elementWithIdExists("user_bar");
	}
	
	public List<String> getUserBarEntries() {
		final WebElement userBar       = getElementById("user_bar");
		final List<WebElement> entries = userBar.findElements(By.tagName("li"));
		return WebElementUtils.convertToListWithText(entries);
	}
	
	public boolean contentAreaExists() {
		return elementWithIdExists("content");
	}
	
	public String getHeader() {
		return getTextOfElementByTagName("h3");
	}
	
	public boolean footerExists() {
		return elementWithTagNameExists("footer");
	}
	
	public boolean linkWithLabelExists(final String label) {
		return getLinkByText(label) != null;
	}
	
	public boolean linkWithLabelAndTitleExists(final String label, final String title) {
		final WebElement link = getLinkByText(label);
		if (link == null) {
			return false;
		}
		
		return link.getAttribute("title").equals(title);
	}
	
	public boolean existsLinkTo(final String relativeUrl) {
		checkArgument(!"".equals(relativeUrl));
		return elementWithXPathExists(String.format(A_HREF_LOCATOR, relativeUrl));
	}
	
	public boolean textPresent(final String text) {
		return body.getText().contains(text);
	}
	
	
	//
	// Helpers for find elements
	//
	
	protected WebElement getElementById(final String elementId) {
		return driver.findElement(By.id(elementId));
	}
	
	protected WebElement getElementByName(final String name) {
		return driver.findElement(By.name(name));
	}
	
	protected WebElement getElementByXPath(final String xpath) {
		return driver.findElement(By.xpath(xpath));
	}
	
	protected WebElement getElementByTagName(final String tagName) {
		return driver.findElement(By.tagName(tagName));
	}
	
	protected List<WebElement> getElementsByClassName(final String className) {
		return driver.findElements(By.className(className));
	}
	
	//
	// Helpers for getting element's value
	//
	
	protected String getTextOfElementById(final String elementId) {
		return getElementById(elementId).getText();
	}
	
	protected String getTextOfElementByXPath(final String xpath) {
		return getElementByXPath(xpath).getText();
	}
	
	protected String getTextOfElementByTagName(final String tagName) {
		return getElementByTagName(tagName).getText();
	}
	
	
	//
	// Other helpers
	//
	
	protected boolean elementWithIdExists(final String elementId) {
		WebElement el = null;
		
		try {
			el = getElementById(elementId);
		} catch (final NoSuchElementException ex) {
			return false;
		}
		
		return el != null;
	}
	
	protected boolean elementWithXPathExists(final String xpath) {
		WebElement el = null;
		
		try {
			el = getElementByXPath(xpath);
		} catch (final NoSuchElementException ex) {
			return false;
		}
		
		return el != null;
	}
	
	protected boolean elementWithTagNameExists(final String tagName) {
		WebElement el = null;
		
		try {
			el = getElementByTagName(tagName);
		} catch (final NoSuchElementException ex) {
			return false;
		}
		
		return el != null;
	}
	
	protected WebElement getLinkByText(final String linkText) {
		try {
			return  driver.findElement(By.linkText(linkText));
		
		} catch (final NoSuchElementException ex) {
			return null;
		}
	}
	
	public void login(final String login, final String password) {
		checkArgument(!"".equals(login), "login must be not null and not empty");
		checkArgument(!"".equals(password), "password must be not null and not empty");
		
		// TODO: check than we already authenticated and do nothing
		final AuthAccountPage authPage = new AuthAccountPage(driver);
		authPage.open();
		authPage.authorizeUser(login, password);
		
		// return to current page
		open();
		// TODO: test for presence link with text "Sign out" to ensure than all right?
	}
	
	public void logout() {
		// TODO: check than we not authenticated and do nothing
		final LogoutAccountPage logoutPage = new LogoutAccountPage(driver);
		logoutPage.open();
		
		// return to current page
		open();
		// TODO: test for presence link with text "Sign in" to ensure than all right?
	}
	
}
