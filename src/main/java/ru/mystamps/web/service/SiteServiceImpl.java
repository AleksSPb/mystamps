/*
 * Copyright (C) 2009-2015 Slava Semushin <slava.semushin@gmail.com>
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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.dao.SuspiciousActivityDao;
import ru.mystamps.web.entity.User;
import ru.mystamps.web.entity.SuspiciousActivity;
import ru.mystamps.web.entity.SuspiciousActivityType;

@RequiredArgsConstructor
public class SiteServiceImpl implements SiteService {
	
	private static final Logger LOG = LoggerFactory.getLogger(SiteServiceImpl.class);
	
	// see initiate-suspicious_activities_types-table changeset
	// in src/main/resources/liquibase/initial-state.xml
	private static final String PAGE_NOT_FOUND = "PageNotFound";
	private static final String AUTHENTICATION_FAILED = "AuthenticationFailed";
	
	private final SuspiciousActivityDao suspiciousActivities;
	
	@Override
	@SuppressWarnings("PMD.UseObjectForClearerAPI")
	@Async
	@Transactional
	public void logAboutAbsentPage(
			String page,
			String method,
			User user,
			String ip,
			String referer,
			String agent) {
		
		logEvent(PAGE_NOT_FOUND, page, method, user, ip, referer, agent, new Date());
	}
	
	@Override
	@SuppressWarnings("PMD.UseObjectForClearerAPI")
	@Transactional
	public void logAboutFailedAuthentication(
			String page,
			String method,
			User user,
			String ip,
			String referer,
			String agent,
			Date date) {
		
		logEvent(AUTHENTICATION_FAILED, page, method, user, ip, referer, agent, date);
	}
	
	@SuppressWarnings({"PMD.UseObjectForClearerAPI", "checkstyle:parameternumber"})
	private void logEvent(
			String type,
			String page,
			String method,
			User user,
			String ip,
			String referer,
			String agent,
			Date date) {
		
		Validate.isTrue(type != null, "Type of suspicious activity was not set");
		Validate.isTrue(page != null, "Page should be non null");
		
		SuspiciousActivity activity = new SuspiciousActivity();
		
		// TODO: replace entity with DTO and replace SuspiciousActivityType by String
		SuspiciousActivityType activityType = new SuspiciousActivityType();
		activityType.setName(type);
		activity.setType(activityType);
		
		activity.setOccurredAt(date == null ? new Date() : date);
		activity.setPage(abbreviatePage(page));
		activity.setMethod(method);
		
		activity.setUser(user);
		
		activity.setIp(StringUtils.defaultString(ip));
		activity.setRefererPage(abbreviateRefererPage(StringUtils.defaultString(referer)));
		activity.setUserAgent(abbreviateUserAgent(StringUtils.defaultString(agent)));
		
		suspiciousActivities.add(activity);
	}
	
	private static String abbreviatePage(String page) {
		return abbreviateIfLengthGreaterThan(page, SuspiciousActivity.PAGE_URL_LENGTH, "page");
	}
	
	private static String abbreviateRefererPage(String referer) {
		// CheckStyle: ignore LineLength for next 1 lines
		return abbreviateIfLengthGreaterThan(referer, SuspiciousActivity.REFERER_PAGE_LENGTH, "referer_page");
	}
	
	private static String abbreviateUserAgent(String agent) {
		// CheckStyle: ignore LineLength for next 1 lines
		return abbreviateIfLengthGreaterThan(agent, SuspiciousActivity.USER_AGENT_LENGTH, "user_agent");
	}
	
	// CheckStyle: ignore LineLength for next 1 lines
	private static String abbreviateIfLengthGreaterThan(String text, int maxLength, String fieldName) {
		if (text == null || text.length() <= maxLength) {
			return text;
		}
		
		// TODO(security): fix possible log injection
		LOG.warn(
				"Length of value for '{}' field ({}) exceeds max field size ({}): '{}'",
				fieldName,
				text.length(),
				maxLength,
				text
		);
		
		return StringUtils.abbreviate(text, maxLength);
	}
	
}
