package ru.mystamps.web.service;

import java.util.Date;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.mystamps.web.entity.User;
import ru.mystamps.web.entity.UsersActivation;
import ru.mystamps.web.dao.UserDao;
import ru.mystamps.web.dao.UsersActivationDao;

@Service
public class UserService {
	
	private final Logger log = LoggerFactory.getLogger(UserService.class);
	
	@Autowired
	private UserDao users;
	
	@Autowired
	private UsersActivationDao usersActivation;
	
	@Transactional
	public void addRegistrationRequest(final String email) {
		final UsersActivation activation = new UsersActivation();
		
		/// @todo: get rid of hardcoded act key (#98)
		activation.setActivationKey(
			email.equals("coder@rock.home")
			? "7777744444"
			: generateActivationKey()
		);
		
		activation.setEmail(email);
		activation.setCreatedAt(new Date());
		usersActivation.add(activation);
	}
	
	@Transactional(readOnly = true)
	public UsersActivation findRegistrationRequestByActivationKey(
			final String activationKey) {
		return usersActivation.findByActivationKey(activationKey);
	}
	
	@Transactional
	public void registerUser(final String login, final String password,
			final String name, final String activationKey) {
		
		// use login as name if name is not provided
		final String finalName;
		if ("".equals(name)) {
			finalName = login;
		} else {
			finalName = name;
		}
		
		final UsersActivation activation =
			usersActivation.findByActivationKey(activationKey);
		
		final String email = activation.getEmail();
		final Date registrationDate = activation.getCreatedAt();
		
		final String salt = generateSalt();
		final String hash = computeSha1Sum(salt + password);
		final Date currentDate = new Date();
		
		final User user = new User();
		user.setLogin(login);
		user.setName(finalName);
		user.setEmail(email);
		user.setRegisteredAt(registrationDate);
		user.setActivatedAt(currentDate);
		user.setHash(hash);
		user.setSalt(salt);
		
		users.add(user);
		usersActivation.delete(activation);
		
		log.debug(
			"Added user (login='{}', name='{}', activation key='{}')",
			new Object[]{login, finalName, activationKey}
		);
	}
	
	@Transactional(readOnly = true)
	public User findByLogin(final String login) {
		return users.findByLogin(login);
	}
	
	@Transactional(readOnly = true)
	public User findByLoginAndPassword(final String login, final String password) {
		final User user = users.findByLogin(login);
		if (user == null) {
			log.debug("Wrong login '{}'", login);
			return null;
		}
		
		if (!user.getHash().equals(computeSha1Sum(user.getSalt() + password))) {
			log.debug("Wrong password for login '{}'", login);
			return null;
		}
		
		log.debug("Valid credentials for login '{}'. User has id = {}", login, user.getId());
		
		return user;
	}
	
	/**
	 * Generates activation key.
	 * @return string which contains numbers and letters in lower case
	 *         in 10 characters length
	 **/
	private static String generateActivationKey() {
		final int actKeyLength = UsersActivation.ACTIVATION_KEY_LENGTH;
		return RandomStringUtils.randomAlphanumeric(actKeyLength).toLowerCase();
	}
	
	/**
	 * Generate password salt.
	 * @return string which contains letters and numbers in 10 characters length
	 **/
	private String generateSalt() {
		return RandomStringUtils.randomAlphanumeric(User.SALT_LENGTH);
	}
	
	private String computeSha1Sum(final String string) {
		return Hex.encodeHexString(DigestUtils.sha(string));
	}
	
}
