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
package ru.mystamps.web.config;

import javax.inject.Inject;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.data.repository.support.DomainClassConverter;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.Validator;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.ViewResolver;

import com.github.heneke.thymeleaf.togglz.TogglzDialect;
import org.thymeleaf.extras.springsecurity3.dialect.SpringSecurityDialect;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.view.ThymeleafView;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolver;

import ru.mystamps.web.support.spring.security.CustomUserDetailsArgumentResolver;
import ru.mystamps.web.Url;
import ru.mystamps.web.support.spring.security.UserArgumentResolver;

@Configuration
@EnableWebMvc
@EnableScheduling
@Import(ControllersConfig.class)
public class MvcConfig extends WebMvcConfigurerAdapter {
	
	@Inject
	private Environment env;
	
	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}
	
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController(Url.AUTHENTICATION_PAGE);
		registry.addViewController(Url.UNAUTHORIZED_PAGE);
	}
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/static/**")
			.addResourceLocations("/WEB-INF/static/");
		registry.addResourceHandler("/public/js/*.js")
			.addResourceLocations("/WEB-INF/classes/");
		
		// For WebJars:
		registry.addResourceHandler("/public/bootstrap/**")
			.addResourceLocations("classpath:/META-INF/resources/webjars/bootstrap/2.3.1/");
		registry.addResourceHandler("/public/jquery/**")
			.addResourceLocations("classpath:/META-INF/resources/webjars/jquery/1.9.1/");
	}
	
	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		CustomUserDetailsArgumentResolver userDetailsArgumentResolver =
			new CustomUserDetailsArgumentResolver();
		
		argumentResolvers.add(userDetailsArgumentResolver);
		argumentResolvers.add(new UserArgumentResolver(userDetailsArgumentResolver));
	}
	
	@Override
	public Validator getValidator() {
		ReloadableResourceBundleMessageSource messageSource =
			new ReloadableResourceBundleMessageSource();
		messageSource.setBasename("classpath:ru/mystamps/i18n/ValidationMessages");
		messageSource.setFallbackToSystemLocale(false);
		
		LocalValidatorFactoryBean factory = new LocalValidatorFactoryBean();
		factory.setValidationMessageSource(messageSource);
		
		return factory;
	}
	
	@Bean
	@SuppressWarnings("PMD.SignatureDeclareThrowsException")
	public ViewResolver getThymeleafViewResolver() throws Exception {
		TemplateResolver templateResolver = new ServletContextTemplateResolver();
		templateResolver.setTemplateMode("HTML5");
		templateResolver.setPrefix("/WEB-INF/views/");
		templateResolver.setSuffix(".html");
		templateResolver.setCharacterEncoding("UTF-8");
		templateResolver.setCacheable(env.acceptsProfiles("prod"));

		SpringTemplateEngine templateEngine = new SpringTemplateEngine();
		templateEngine.setTemplateResolver(templateResolver);
		templateEngine.setTemplateEngineMessageSource(getMessageSource());
		templateEngine.addDialect(new SpringSecurityDialect());
		templateEngine.addDialect(new TogglzDialect());
		templateEngine.afterPropertiesSet();
		
		ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
		viewResolver.setTemplateEngine(templateEngine);
		viewResolver.setContentType("text/html; charset=UTF-8");
		viewResolver.setStaticVariables(Url.asMap());
		viewResolver.setViewClass(ThymeleafView.class);
		
		return viewResolver;
	}
	
	@Bean(name = "messageSource")
	public MessageSource getMessageSource() {
		ReloadableResourceBundleMessageSource messageSource =
			new ReloadableResourceBundleMessageSource();
		
		messageSource.setBasename("classpath:ru/mystamps/i18n/Messages");
		messageSource.setDefaultEncoding("UTF-8");
		messageSource.setFallbackToSystemLocale(false);
		
		return messageSource;
	}
	
	@Bean(name = "multipartResolver")
	public MultipartResolver getMultipartResolver() {
		return new StandardServletMultipartResolver();
	}
	
	@Bean
	@Inject
	public DomainClassConverter<?> getDomainClassConverter(FormattingConversionService service) {
		return new DomainClassConverter<FormattingConversionService>(service);
	}
	
}
