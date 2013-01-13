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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package ru.mystamps.web.service;

import java.util.Date;

import org.springframework.security.authentication.encoding.PasswordEncoder;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.fest.assertions.api.Assertions.assertThat;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

import ru.mystamps.web.dao.UserDao;
import ru.mystamps.web.dao.UsersActivationDao;
import ru.mystamps.web.entity.User;
import ru.mystamps.web.entity.UsersActivation;
import ru.mystamps.web.model.ActivateAccountForm;
import ru.mystamps.web.model.RegisterAccountForm;
import ru.mystamps.web.tests.fest.DateAssert;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {
	
	private static final String TEST_NAME           = "Test Name";
	private static final String TEST_LOGIN          = "test";
	private static final String TEST_PASSWORD       = "secret";
	private static final String TEST_SALT           = "salt";
	
	// sha1(TEST_SALT + "{" + TEST_PASSWORD + "}")
	private static final String TEST_HASH           = "b0dd94c84e784ddb1e9a83c8a2e8f403846647b9";
	
	private static final String TEST_EMAIL          = "test@example.org";
	private static final String TEST_ACTIVATION_KEY = "1234567890";
	
	@Mock
	private UserDao userDao;
	
	@Mock
	private UsersActivationDao usersActivationDao;
	
	@Mock
	private PasswordEncoder encoder;
	
	@Captor
	private ArgumentCaptor<UsersActivation> activationCaptor;
	
	@Captor
	private ArgumentCaptor<User> userCaptor;
	
	@InjectMocks
	private UserService service = new UserService();
	
	private ActivateAccountForm activationForm;
	private RegisterAccountForm registrationForm;
	
	@Before
	public void setUp() {
		when(encoder.encodePassword(anyString(), anyString())).thenReturn(TEST_HASH);
		
		registrationForm = new RegisterAccountForm();
		registrationForm.setEmail(TEST_EMAIL);
		
		activationForm = new ActivateAccountForm();
		activationForm.setLogin(TEST_LOGIN);
		activationForm.setPassword(TEST_PASSWORD);
		activationForm.setName(TEST_NAME);
		activationForm.setActivationKey(TEST_ACTIVATION_KEY);
	}
	
	//
	// Tests for addRegistrationRequest()
	//
	
	@Test(expected = IllegalArgumentException.class)
	public void addRegistrationRequestShouldThrowExceptionWhenDtoIsNull() {
		service.addRegistrationRequest(null);
	}
	
	@Test
	public void addRegistrationRequestShouldCallDao() {
		service.addRegistrationRequest(registrationForm);
		verify(usersActivationDao).save(any(UsersActivation.class));
	}
	
	@Test
	public void addRegistrationRequestShouldGenerateActivationKey() {
		service.addRegistrationRequest(registrationForm);
		
		verify(usersActivationDao).save(activationCaptor.capture());
		
		final String activationKey = activationCaptor.getValue().getActivationKey();
		assertThat(activationKey.length()).as("activation key length")
			.isEqualTo(UsersActivation.ACTIVATION_KEY_LENGTH);
		
		assertThat(activationKey).matches("^[\\p{Lower}\\p{Digit}]+$");
	}
	
	@Test
	public void addRegistrationRequestShouldGenerateUniqueActivationKey() {
		service.addRegistrationRequest(registrationForm);
		verify(usersActivationDao).save(activationCaptor.capture());
		final String firstActivationKey = activationCaptor.getValue().getActivationKey();
		
		service.addRegistrationRequest(registrationForm);
		verify(usersActivationDao, atLeastOnce()).save(activationCaptor.capture());
		final String secondActivationKey = activationCaptor.getValue().getActivationKey();
		
		assertThat(firstActivationKey).isNotEqualTo(secondActivationKey);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void addRegistrationRequestShouldThrowExceptionWhenEmailIsNull() {
		registrationForm.setEmail(null);
		
		service.addRegistrationRequest(registrationForm);
	}
	
	@Test
	public void addRegistrationRequestShouldPassEmailToDao() {
		final String expectedEmail = "somename@example.org";
		registrationForm.setEmail(expectedEmail);
		
		service.addRegistrationRequest(registrationForm);
		
		verify(usersActivationDao).save(activationCaptor.capture());
		
		assertThat(activationCaptor.getValue().getEmail()).isEqualTo(expectedEmail);
	}
	
	@Test
	public void addRegistrationRequestShouldAssignCreatedAtToCurrentDate() {
		service.addRegistrationRequest(registrationForm);
		
		verify(usersActivationDao).save(activationCaptor.capture());
		
		DateAssert.assertThat(activationCaptor.getValue().getCreatedAt()).isCurrentDate();
	}
	
	//
	// Tests for findRegistrationRequestByActivationKey()
	//
	
	@Test(expected = IllegalArgumentException.class)
	public void findRegistrationRequestByActivationKeyShouldThrowExceptionWhenKeyIsNull() {
		service.findRegistrationRequestByActivationKey(null);
	}
	
	@Test
	public void findRegistrationRequestByActivationKeyShouldCallDao() {
		final UsersActivation expectedActivation = getUsersActivation();
		when(usersActivationDao.findByActivationKey(anyString())).thenReturn(expectedActivation);
		
		final UsersActivation activation =
			service.findRegistrationRequestByActivationKey(TEST_ACTIVATION_KEY);
		
		assertThat(activation).isEqualTo(expectedActivation);
	}
	
	
	@Test
	public void findRegistrationRequestByActivationKeyShouldPassActivationKeyToDao() {
		service.findRegistrationRequestByActivationKey(TEST_ACTIVATION_KEY);
		verify(usersActivationDao).findByActivationKey(TEST_ACTIVATION_KEY);
	}
	
	//
	// Tests for registerUser()
	//
	
	@Test(expected = IllegalArgumentException.class)
	public void registerUserShouldThrowExceptionWhenDtoIsNull() {
		service.registerUser(null);
	}
	
	@Test
	public void registerUserShouldCreateUser() {
		when(usersActivationDao.findByActivationKey(anyString())).thenReturn(getUsersActivation());
		
		service.registerUser(activationForm);
		
		verify(userDao).save(any(User.class));
	}
	
	@Test
	public void registerUserShouldDeleteRegistrationRequest() {
		final UsersActivation activation = getUsersActivation();
		when(usersActivationDao.findByActivationKey(anyString())).thenReturn(activation);
		
		service.registerUser(activationForm);
		
		verify(usersActivationDao).delete(activation);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void registerUserShouldThrowExceptionWhenActivationKeyIsNull() {
		activationForm.setActivationKey(null);
		
		service.registerUser(activationForm);
	}
	
	@Test
	public void registerUserShouldDoNothingWhenRegistrationRequestNotFound() {
		when(usersActivationDao.findByActivationKey(anyString())).thenReturn(null);
		
		service.registerUser(activationForm);
		
		verify(userDao, never()).save(any(User.class));
		verify(usersActivationDao, never()).delete(any(UsersActivation.class));
	}
	
	@Test
	public void registerUserShouldPassNameToDao() {
		when(usersActivationDao.findByActivationKey(anyString())).thenReturn(getUsersActivation());
		
		service.registerUser(activationForm);
		
		verify(userDao).save(userCaptor.capture());
		
		assertThat(userCaptor.getValue().getName()).isEqualTo(TEST_NAME);
	}
	
	@Test
	public void registerUserShouldPassLoginInsteadOfNameWhenNameIsNull() {
		when(usersActivationDao.findByActivationKey(anyString())).thenReturn(getUsersActivation());
		activationForm.setName(null);
		
		service.registerUser(activationForm);
		
		verify(userDao).save(userCaptor.capture());
		
		assertThat(userCaptor.getValue().getName()).isEqualTo(TEST_LOGIN);
	}
	
	@Test
	public void registerUserShouldPassLoginInsteadOfNameWhenNameIsEmpty() {
		when(usersActivationDao.findByActivationKey(anyString())).thenReturn(getUsersActivation());
		activationForm.setName("");
		
		service.registerUser(activationForm);
		
		verify(userDao).save(userCaptor.capture());
		
		assertThat(userCaptor.getValue().getName()).isEqualTo(TEST_LOGIN);
	}
	
	@Test
	public void registerUserShouldUseEmailFromRegistrationRequest() {
		final UsersActivation activation = getUsersActivation();
		when(usersActivationDao.findByActivationKey(anyString())).thenReturn(activation);
		
		service.registerUser(activationForm);
		
		verify(userDao).save(userCaptor.capture());
		
		assertThat(userCaptor.getValue().getEmail()).isEqualTo(activation.getEmail());
	}
	
	@Test
	public void registerUserShouldUseRegistrationDateFromRegistrationRequest() {
		final UsersActivation activation = getUsersActivation();
		when(usersActivationDao.findByActivationKey(anyString())).thenReturn(activation);
		
		service.registerUser(activationForm);
		
		verify(userDao).save(userCaptor.capture());
		
		assertThat(userCaptor.getValue().getRegisteredAt()).isEqualTo(activation.getCreatedAt());
	}
	
	@Test
	public void registerUserShouldGenerateSalt() {
		when(usersActivationDao.findByActivationKey(anyString())).thenReturn(getUsersActivation());
		
		service.registerUser(activationForm);
		
		verify(userDao).save(userCaptor.capture());
		
		final String salt = userCaptor.getValue().getSalt();
		assertThat(salt.length()).as("salt length").isEqualTo(User.SALT_LENGTH);
		assertThat(salt).matches("^[\\p{Alnum}]+$");
	}
	
	@Test
	public void registerUserShouldGenerateUniqueSalt() {
		when(usersActivationDao.findByActivationKey(anyString())).thenReturn(getUsersActivation());
		
		service.registerUser(activationForm);
		verify(userDao).save(userCaptor.capture());
		final String firstSalt = userCaptor.getValue().getSalt();
		
		service.registerUser(activationForm);
		verify(userDao, atLeastOnce()).save(userCaptor.capture());
		final String secondSalt = userCaptor.getValue().getSalt();
		
		assertThat(firstSalt).isNotEqualTo(secondSalt);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void registerUserShouldThrowExceptionWhenPasswordIsNull() {
		activationForm.setPassword(null);
		
		service.registerUser(activationForm);
	}
	
	@Test
	public void registerUserShouldGetsHashFromEncoder() {
		final String expectedHash = TEST_HASH;
		
		when(usersActivationDao.findByActivationKey(anyString())).thenReturn(getUsersActivation());
		when(encoder.encodePassword(anyString(), anyString())).thenReturn(expectedHash);
		
		service.registerUser(activationForm);
		
		verify(userDao).save(userCaptor.capture());
		verify(encoder).encodePassword(eq(TEST_PASSWORD), anyString());
		
		final String actualHash = userCaptor.getValue().getHash();
		assertThat(actualHash).isEqualTo(expectedHash);
	}
	
	@Test(expected = IllegalStateException.class)
	public void registerUserShouldThrowExceptionWhenEncoderReturnsNull() {
		when(usersActivationDao.findByActivationKey(anyString())).thenReturn(getUsersActivation());
		when(encoder.encodePassword(anyString(), anyString())).thenReturn(null);
		
		service.registerUser(activationForm);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void registerUserShouldThrowExceptionWhenLoginIsNull() {
		activationForm.setLogin(null);
		
		service.registerUser(activationForm);
	}
	
	@Test
	public void registerUserShouldPassLoginToDao() {
		when(usersActivationDao.findByActivationKey(anyString())).thenReturn(getUsersActivation());
		
		service.registerUser(activationForm);
		
		verify(userDao).save(userCaptor.capture());
		
		assertThat(userCaptor.getValue().getLogin()).isEqualTo(TEST_LOGIN);
	}
	
	@Test
	public void registerUserShouldAssignActivatedAtToCurrentDate() {
		when(usersActivationDao.findByActivationKey(anyString())).thenReturn(getUsersActivation());
		
		service.registerUser(activationForm);
		
		verify(userDao).save(userCaptor.capture());
		
		DateAssert.assertThat(userCaptor.getValue().getActivatedAt()).isCurrentDate();
	}
	
	//
	// Tests for findByLogin()
	//
	
	@Test(expected = IllegalArgumentException.class)
	public void findByLoginShouldThrowExceptionWhenLoginIsNull() {
		service.findByLogin(null);
	}
	
	@Test
	public void findByLoginShouldCallDao() {
		final User expectedUser = getValidUser();
		when(userDao.findByLogin(anyString())).thenReturn(expectedUser);
		
		final User user = service.findByLogin(TEST_LOGIN);
		
		assertThat(user).isEqualTo(expectedUser);
	}
	
	@Test
	public void findByLoginShouldPassLoginToDao() {
		service.findByLogin(TEST_LOGIN);
		verify(userDao).findByLogin(TEST_LOGIN);
	}
	
	static User getValidUser() {
		final Integer anyId = 777;
		final User user = new User();
		user.setId(anyId);
		user.setLogin(TEST_LOGIN);
		user.setName(TEST_NAME);
		user.setEmail(TEST_EMAIL);
		user.setRegisteredAt(new Date());
		user.setActivatedAt(new Date());
		user.setHash(TEST_HASH);
		user.setSalt(TEST_SALT);
		
		return user;
	}
	
	static UsersActivation getUsersActivation() {
		final UsersActivation activation = new UsersActivation();
		activation.setActivationKey(TEST_ACTIVATION_KEY);
		activation.setEmail(TEST_EMAIL);
		activation.setCreatedAt(new Date());
		return activation;
	}
	
}
