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

import static org.fest.assertions.Assertions.assertThat;

import static ru.mystamps.web.SiteMap.AUTHENTICATION_PAGE_URL;
import static ru.mystamps.web.SiteMap.RESTORE_PASSWORD_PAGE_URL;
import static ru.mystamps.web.SiteMap.SUCCESSFUL_REGISTRATION_PAGE_URL;
import static ru.mystamps.web.tests.TranslationUtils.tr;
import static ru.mystamps.web.tests.TranslationUtils.stripHtmlTags;
import static ru.mystamps.web.validation.ValidationRules.EMAIL_MAX_LENGTH;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import ru.mystamps.web.tests.page.RegisterAccountPage;

public class WhenUserRegisterAccount extends WhenUserAtAnyPageWithForm<RegisterAccountPage> {
	
	public WhenUserRegisterAccount() {
		super(RegisterAccountPage.class);
		hasTitle(tr("t_registration_title"));
		hasHeader(tr("t_registration_on_site"));
		
		page.open();
	}
	
	@Test
	public void shouldHaveStandardStructure() {
		checkStandardStructure();
	}
	
	@Test
	public void shouldExistsMessageWithLinkToAuthenticationPage() {
		assertThat(page.getFormHints()).contains(stripHtmlTags(tr("t_if_you_already_registered")));
		
		assertThat(page.linkHasLabelAndPointsTo("authentication", AUTHENTICATION_PAGE_URL))
			.overridingErrorMessage("should exists link to authentication page")
			.isTrue();
	}
	
	@Test
	public void shouldExistsMessageWithLinkAboutPasswordRecovery() {
		assertThat(page.getFormHints()).contains(stripHtmlTags(tr("t_if_you_forget_password")));
		
		assertThat(page.linkHasLabelAndPointsTo("remind", RESTORE_PASSWORD_PAGE_URL))
			.overridingErrorMessage("should exists link to password restoration page")
			.isTrue();
	}
	
	@Test
	public void emailShouldNotBeTooLong() {
		page.fillEmail(StringUtils.repeat("0", EMAIL_MAX_LENGTH) + "@mail.ru");
		page.submit();
		
		assertThat(page.getFieldError("email")).isEqualTo(tr("value.too-long", EMAIL_MAX_LENGTH));
	}
	
	@Test
	public void emailShouldBeValid() {
		final String[] emails = new String[] {
				"login",
				"login@domain"
		};
		
		for (final String invalidEmail : emails) {
			page.fillEmail(invalidEmail);
			page.submit();
			
			assertThat(page.getFieldError("email")).isEqualTo(tr("email.invalid"));
		}
	}
	
	@Test
	public void emailShouldBeStripedFromLeadingAndTrailingSpaces() {
		page.fillEmail(" test ");
		page.submit();
		
		assertThat(page.getFieldValue("email")).isEqualTo("test");
	}
	
	@Test
	public void successfulMessageShouldBeShownAfterRegistration() {
		page.fillEmail("coder@rock.home");
		page.submit();
		
		assertThat(page.getCurrentUrl()).isEqualTo(SUCCESSFUL_REGISTRATION_PAGE_URL);
		
		assertThat(page.textPresent(tr("t_activation_sent_message"))).isTrue();
	}
	
}
