package ru.mystamps.web.tests.cases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.matchers.JUnitMatchers.hasItem;

import static ru.mystamps.web.SiteMap.AUTHENTICATION_PAGE_URL;
import static ru.mystamps.web.SiteMap.RESTORE_PASSWORD_PAGE_URL;
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
		assertThat(
			page.getFormHints(),
			hasItem(stripHtmlTags(tr("t_if_you_already_registered")))
		);
		
		assertTrue(
			"should exists link to authentication page",
			page.linkHasLabelAndPointsTo("authentication", AUTHENTICATION_PAGE_URL)
		);
	}
	
	@Test
	public void shouldExistsMessageWithLinkAboutPasswordRecovery() {
		assertThat(
			page.getFormHints(),
			hasItem(stripHtmlTags(tr("t_if_you_forget_password")))
		);
		
		assertTrue(
			"should exists link to password restoration page",
			page.linkHasLabelAndPointsTo("remind", RESTORE_PASSWORD_PAGE_URL)
		);
	}
	
	@Test
	public void emailShouldNotBeTooLong() {
		page.fillField("email", StringUtils.repeat("0", EMAIL_MAX_LENGTH) + "@mail.ru");
		page.submit();
		assertEquals(tr("value.too-long", EMAIL_MAX_LENGTH), page.getFieldError("email"));
	}
	
	@Test
	public void emailShouldBeValid() {
		final String[] emails = new String[] {
				"login",
				"login@domain"
		};
		
		for (final String invalidEmail : emails) {
			page.fillField("email", invalidEmail);
			page.submit();
			assertEquals(tr("email.invalid"), page.getFieldError("email"));
		}
	}
	
	@Test
	public void successfulMessageShouldBeShownAfterRegistration() {
		page.fillField("email", "coder@rock.home");
		page.submit();
		// TODO: check page url
		// TODO: delete email if it already exists
		assertTrue(page.textPresent(tr("t_activation_sent_message")));
	}
	
}
