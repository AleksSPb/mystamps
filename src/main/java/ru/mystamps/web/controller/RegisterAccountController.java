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

package ru.mystamps.web.controller;

import javax.inject.Inject;
import javax.validation.Valid;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.validation.BindingResult;

import ru.mystamps.web.Url;
import ru.mystamps.web.model.RegisterAccountForm;
import ru.mystamps.web.service.UserService;

@Controller
@RequestMapping(Url.REGISTRATION_PAGE)
public class RegisterAccountController {
	
	private final UserService userService;
	
	@Inject
	RegisterAccountController(final UserService userService) {
		this.userService = userService;
	}
	
	@InitBinder
	protected void initBinder(final WebDataBinder binder) {
		binder.registerCustomEditor(String.class, "email", new StringTrimmerEditor(false));
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public RegisterAccountForm showForm() {
		return new RegisterAccountForm();
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String processInput(
			@Valid final RegisterAccountForm form,
			final BindingResult result) {
		
		if (result.hasErrors()) {
			return null;
		}
		
		userService.addRegistrationRequest(form.getEmail());
		
		return "redirect:" + Url.SUCCESSFUL_REGISTRATION_PAGE;
	}
	
}

