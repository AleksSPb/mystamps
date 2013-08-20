/*
 * Copyright (C) 2009-2013 Slava Semushin <slava.semushin@gmail.com>
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
	
	private UserService service;
	private ActivateAccountForm activationForm;
	private RegisterAccountForm registrationForm;
	
	@Before
	public void setUp() {
		User user = TestObjects.createUser();
		
		when(encoder.encodePassword(anyString(), anyString())).thenReturn(user.getHash());
		
		UsersActivation activation = TestObjects.createUsersActivation();
		when(usersActivationDao.findOne(anyString())).thenReturn(activation);
		
		registrationForm = new RegisterAccountForm();
		registrationForm.setEmail("john.dou@example.org");
		
		activationForm = new ActivateAccountForm();
		activationForm.setLogin(user.getLogin());
		activationForm.setPassword(TestObjects.TEST_PASSWORD);
		activationForm.setName(user.getName());
		activationForm.setActivationKey(activation.getActivationKey());
		
		service = new UserServiceImpl(userDao, usersActivationDao, encoder);
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
		
		String activationKey = activationCaptor.getValue().getActivationKey();
		assertThat(activationKey.length()).as("activation key length")
			.isEqualTo(UsersActivation.ACTIVATION_KEY_LENGTH);
		
		assertThat(activationKey).matches("^[\\p{Lower}\\p{Digit}]+$");
	}
	
	@Test
	public void addRegistrationRequestShouldGenerateUniqueActivationKey() {
		service.addRegistrationRequest(registrationForm);
		verify(usersActivationDao).save(activationCaptor.capture());
		String firstActivationKey = activationCaptor.getValue().getActivationKey();
		
		service.addRegistrationRequest(registrationForm);
		verify(usersActivationDao, atLeastOnce()).save(activationCaptor.capture());
		String secondActivationKey = activationCaptor.getValue().getActivationKey();
		
		assertThat(firstActivationKey).isNotEqualTo(secondActivationKey);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void addRegistrationRequestShouldThrowExceptionWhenEmailIsNull() {
		registrationForm.setEmail(null);
		
		service.addRegistrationRequest(registrationForm);
	}
	
	@Test
	public void addRegistrationRequestShouldPassEmailToDao() {
		String expectedEmail = "somename@example.org";
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
		UsersActivation expectedActivation = TestObjects.createUsersActivation();
		when(usersActivationDao.findOne(anyString())).thenReturn(expectedActivation);
		
		UsersActivation activation =
			service.findRegistrationRequestByActivationKey(expectedActivation.getActivationKey());
		
		assertThat(activation).isEqualTo(expectedActivation);
	}
	
	
	@Test
	public void findRegistrationRequestByActivationKeyShouldPassActivationKeyToDao() {
		service.findRegistrationRequestByActivationKey("0987654321");
		verify(usersActivationDao).findOne("0987654321");
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
		service.registerUser(activationForm);
		
		verify(userDao).save(any(User.class));
	}
	
	@Test
	public void registerUserShouldDeleteRegistrationRequest() {
		UsersActivation activation = TestObjects.createUsersActivation();
		when(usersActivationDao.findOne(anyString())).thenReturn(activation);
		
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
		when(usersActivationDao.findOne(anyString())).thenReturn(null);
		
		service.registerUser(activationForm);
		
		verify(userDao, never()).save(any(User.class));
		verify(usersActivationDao, never()).delete(any(UsersActivation.class));
	}
	
	@Test
	public void registerUserShouldPassNameToDao() {
		String expectedUserName = activationForm.getName();
		
		service.registerUser(activationForm);
		
		verify(userDao).save(userCaptor.capture());
		
		assertThat(userCaptor.getValue().getName()).isEqualTo(expectedUserName);
	}
	
	@Test
	public void registerUserShouldPassLoginInsteadOfNameWhenNameIsNull() {
		String expectedUserLogin = activationForm.getLogin();
		activationForm.setName(null);
		
		service.registerUser(activationForm);
		
		verify(userDao).save(userCaptor.capture());
		
		assertThat(userCaptor.getValue().getName()).isEqualTo(expectedUserLogin);
	}
	
	@Test
	public void registerUserShouldPassLoginInsteadOfNameWhenNameIsEmpty() {
		String expectedUserLogin = activationForm.getLogin();
		activationForm.setName("");
		
		service.registerUser(activationForm);
		
		verify(userDao).save(userCaptor.capture());
		
		assertThat(userCaptor.getValue().getName()).isEqualTo(expectedUserLogin);
	}
	
	@Test
	public void registerUserShouldUseEmailFromRegistrationRequest() {
		UsersActivation activation = TestObjects.createUsersActivation();
		when(usersActivationDao.findOne(anyString())).thenReturn(activation);
		
		service.registerUser(activationForm);
		
		verify(userDao).save(userCaptor.capture());
		
		assertThat(userCaptor.getValue().getEmail()).isEqualTo(activation.getEmail());
	}
	
	@Test
	public void registerUserShouldUseRegistrationDateFromRegistrationRequest() {
		UsersActivation activation = TestObjects.createUsersActivation();
		when(usersActivationDao.findOne(anyString())).thenReturn(activation);
		
		service.registerUser(activationForm);
		
		verify(userDao).save(userCaptor.capture());
		
		assertThat(userCaptor.getValue().getRegisteredAt()).isEqualTo(activation.getCreatedAt());
	}
	
	@Test
	public void registerUserShouldGenerateSalt() {
		service.registerUser(activationForm);
		
		verify(userDao).save(userCaptor.capture());
		
		String salt = userCaptor.getValue().getSalt();
		assertThat(salt.length()).as("salt length").isEqualTo(User.SALT_LENGTH);
		assertThat(salt).matches("^[\\p{Alnum}]+$");
	}
	
	@Test
	public void registerUserShouldGenerateUniqueSalt() {
		service.registerUser(activationForm);
		verify(userDao).save(userCaptor.capture());
		String firstSalt = userCaptor.getValue().getSalt();
		
		service.registerUser(activationForm);
		verify(userDao, atLeastOnce()).save(userCaptor.capture());
		String secondSalt = userCaptor.getValue().getSalt();
		
		assertThat(firstSalt).isNotEqualTo(secondSalt);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void registerUserShouldThrowExceptionWhenPasswordIsNull() {
		activationForm.setPassword(null);
		
		service.registerUser(activationForm);
	}
	
	@Test
	public void registerUserShouldGetsHashFromEncoder() {
		String expectedHash = TestObjects.createUser().getHash();
		
		when(encoder.encodePassword(anyString(), anyString())).thenReturn(expectedHash);
		
		service.registerUser(activationForm);
		
		verify(userDao).save(userCaptor.capture());
		verify(encoder).encodePassword(eq(TestObjects.TEST_PASSWORD), anyString());
		
		String actualHash = userCaptor.getValue().getHash();
		assertThat(actualHash).isEqualTo(expectedHash);
	}
	
	@Test(expected = IllegalStateException.class)
	public void registerUserShouldThrowExceptionWhenEncoderReturnsNull() {
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
		String expectedUserLogin = activationForm.getLogin();
		
		service.registerUser(activationForm);
		
		verify(userDao).save(userCaptor.capture());
		
		assertThat(userCaptor.getValue().getLogin()).isEqualTo(expectedUserLogin);
	}
	
	@Test
	public void registerUserShouldAssignActivatedAtToCurrentDate() {
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
		User expectedUser = TestObjects.createUser();
		when(userDao.findByLogin(anyString())).thenReturn(expectedUser);
		
		User user = service.findByLogin("any-login");
		
		assertThat(user).isEqualTo(expectedUser);
	}
	
	@Test
	public void findByLoginShouldPassLoginToDao() {
		service.findByLogin("john");
		
		verify(userDao).findByLogin("john");
	}
	
}
