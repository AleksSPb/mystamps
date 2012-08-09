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
package ru.mystamps.web.support.spring.security;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static com.google.common.base.Preconditions.checkState;

public class CustomUserDetailsArgumentResolver implements HandlerMethodArgumentResolver {
	
	@Override
	public boolean supportsParameter(final MethodParameter parameter) {
		return isSupportedType(parameter.getParameterType());
	}
	
	@Override
	public Object resolveArgument(
		final MethodParameter parameter,
		final ModelAndViewContainer mavContainer,
		final NativeWebRequest webRequest,
		final WebDataBinderFactory binderFactory) {
		
		final SecurityContext ctx = SecurityContextHolder.getContext();
		checkState(ctx != null, "Security context must be non null");
		
		final Authentication auth = ctx.getAuthentication();
		if (auth == null) {
			return null;
		}
		
		final Object principal = auth.getPrincipal();
		if (principal == null) {
			return null;
		}
		
		if (isSupportedType(principal.getClass())) {
			return principal;
		}
		
		return null;
	}
	
	private static boolean isSupportedType(final Class<?> clazz) {
		return CustomUserDetails.class.isAssignableFrom(clazz);
	}
	
}

