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

package ru.mystamps.web.controller;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;

import ru.mystamps.web.Url;
import ru.mystamps.web.entity.User;
import ru.mystamps.web.model.AuthAccountForm;
import ru.mystamps.web.model.AuthAccountForm.FormChecks;
import ru.mystamps.web.model.AuthAccountForm.LoginChecks;
import ru.mystamps.web.model.AuthAccountForm.PasswordChecks;
import ru.mystamps.web.service.SiteService;
import ru.mystamps.web.service.UserService;

@Controller
@RequestMapping(Url.AUTHENTICATION_PAGE)
public class AuthAccountController {
	
	private final UserService userService;
	private final SiteService siteService;
	
	@Inject
	AuthAccountController(final UserService userService, final SiteService siteService) {
		this.userService = userService;
		this.siteService = siteService;
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public AuthAccountForm showForm() {
		return new AuthAccountForm();
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String processInput(
			final HttpServletRequest request,
			final HttpSession session,
			@RequestHeader(value = "referer", required = false) final String referer,
			@RequestHeader(value = "user-agent", required = false) final String agent,
			@Validated({
				LoginChecks.class,
				PasswordChecks.class,
				FormChecks.class
			}) final AuthAccountForm form,
			final BindingResult result) {
		
		if (result.hasErrors()) {
			
			// When user provides wrong login/password pair than
			// validation mechanism add error to form. Check this to
			// handle situation with wrong credentials.
			if (result.hasGlobalErrors()
					&& result.getGlobalError() != null
					&& "ValidCredentials".equals(result.getGlobalError().getCode())) {
				
				// TODO: log more info (login for example) (#59)
				// TODO: sanitize all user's values (#60)
				final String page = request.getRequestURI();
				final String ip   = request.getRemoteAddr();
				
				final User user = (User)session.getAttribute("user");
				siteService.logAboutFailedAuthentication(page, user, ip, referer, agent);
			}
			
			return null;
		}
		
		final User user = userService.findByLogin(form.getLogin());
		
		// don't save password related fields in session
		user.setHash(null);
		user.setSalt(null);
		
		session.setAttribute("user", user);
		
		return "redirect:" + Url.INDEX_PAGE;
	}
	
}

