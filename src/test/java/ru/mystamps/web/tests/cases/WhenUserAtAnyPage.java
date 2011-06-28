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

package ru.mystamps.web.tests.cases;

import java.net.HttpURLConnection;

import org.openqa.selenium.support.PageFactory;

import ru.mystamps.web.tests.WebDriverFactory;
import ru.mystamps.web.tests.page.AbstractPage;

import static org.fest.assertions.Assertions.assertThat;

import static ru.mystamps.web.SiteMap.AUTHENTICATION_PAGE_URL;
import static ru.mystamps.web.SiteMap.REGISTRATION_PAGE_URL;
import static ru.mystamps.web.tests.TranslationUtils.tr;

abstract class WhenUserAtAnyPage<T extends AbstractPage> {
	
	/**
	 * Prefix of page's title  which will be prepend by hasTitle().
	 */
	private static final String TITLE_PREFIX = "MyStamps: ";
	
	protected final T page;
	
	/**
	 * @see hasResponseServerCode()
	 */
	private int serverCode = HttpURLConnection.HTTP_OK;
	
	/**
	 * @see hasTitle()
	 * @see hasTitleWithoutStandardPrefix()
	 */
	private String title;
	
	/**
	 * @see hasHeader()
	 */
	private String header;
	
	public WhenUserAtAnyPage(final Class<T> pageClass) {
		page = PageFactory.initElements(WebDriverFactory.getDriver(), pageClass);
	}
	
	protected void hasResponseServerCode(final int serverCode) {
		this.serverCode = serverCode;
	}
	
	protected void hasTitle(final String title) {
		this.title = TITLE_PREFIX + title;
	}
	
	protected void hasTitleWithoutStandardPrefix(final String title) {
		this.title = title;
	}
	
	protected void hasHeader(final String header) {
		this.header = header;
	}
	
	protected void checkStandardStructure() {
		checkServerResponseCode();
		shouldHaveTitle();
		shouldHaveLogo();
		shouldHaveUserBar();
		shouldHaveContentArea();
		mayHaveHeader();
		shouldHaveFooter();
	}
	
	private void checkServerResponseCode() {
		assertThat(page.getServerResponseCode())
			.overridingErrorMessage("Server response code")
			.isEqualTo(serverCode)
			;
	}
	
	private void shouldHaveTitle() {
		if (title == null) {
			throw new IllegalStateException(
				"Page title was not set!"
				+ " Did you call hasTitle() or hasTitleWithoutStandardPrefix() before?"
			);
		}
		
		assertThat(page.getTitle())
			.overridingErrorMessage("title should be '" + title + "'")
			.isEqualTo(title);
	}
	
	private void shouldHaveLogo() {
		assertThat(page.getTextAtLogo())
			.overridingErrorMessage("text at logo should be '" + tr("t_my_stamps") + "'")
			.isEqualTo(tr("t_my_stamps"));
	}
	
	private void shouldHaveUserBar() {
		assertThat(page.userBarExists())
			.overridingErrorMessage("user bar should exists")
			.isTrue();
		
		assertThat(page.linkHasLabelAndPointsTo(tr("t_enter"), AUTHENTICATION_PAGE_URL))
			.overridingErrorMessage("should exists link to authentication page")
			.isTrue();
		
		assertThat(page.linkHasLabelAndPointsTo(tr("t_register"), REGISTRATION_PAGE_URL))
			.overridingErrorMessage("should exists link to registration page")
			.isTrue();
	}
	
	private void shouldHaveContentArea() {
		assertThat(page.contentAreaExists())
			.overridingErrorMessage("should exists content area")
			.isTrue();
	}
	
	private void mayHaveHeader() {
		if (header != null) {
			assertThat(page.getHeader())
				.overridingErrorMessage("header should exists")
				.isEqualTo(header);
		}
	}
	
	private void shouldHaveFooter() {
		assertThat(page.footerExists())
			.overridingErrorMessage("footer should exists")
			.isTrue();
		
		assertThat(
				page.linkHasLabelWithTitleAndPointsTo(
					tr("t_site_author_name"),
					tr("t_write_email"),
					"mailto:" + tr("t_site_author_email")
				)
			)
			.overridingErrorMessage("should exists link with author's email")
			.isTrue();
	}
	
}
