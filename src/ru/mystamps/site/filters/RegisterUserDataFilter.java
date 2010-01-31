package ru.mystamps.site.filters;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.ServletException;

import ru.mystamps.data.validators.Validator;
import ru.mystamps.data.validators.NotEmpty;
import ru.mystamps.data.validators.PasswordConfirm;
import ru.mystamps.data.validators.PasswordLoginMismatch;
import ru.mystamps.site.ContextObject;

public class RegisterUserDataFilter implements Filter {
	
	/**
	 * Map of fields and its validators.
	 **/
	private Map<String, Validator[]> validationRules =
		new HashMap<String, Validator[]>();
	
	/**
	 * Instance of NotEmpty validator.
	 **/
	private static final Validator notEmptyValidator =
		new NotEmpty("tv_empty_value");
	
	/**
	 * Instance of PasswordConfirm validator.
	 **/
	private static final PasswordConfirm passwordConfirm =
		new PasswordConfirm("tv_password_mismatch");
	
	/**
	 * Instance of PasswordLoginMismatch validator.
	 **/
	private static final PasswordLoginMismatch passwordLoginMismatch =
		new PasswordLoginMismatch("tv_password_login_match");
	
	public RegisterUserDataFilter() {
		validationRules.put("login", new Validator[]{notEmptyValidator});
		validationRules.put("pass1", new Validator[]{notEmptyValidator});
		validationRules.put("pass2", new Validator[]{notEmptyValidator});
		validationRules.put("email", new Validator[]{notEmptyValidator});
		validationRules.put("name",  new Validator[]{notEmptyValidator});
	}
	
	@Override
	public void init(FilterConfig config) {
	}
	
	@Override
	public void doFilter(ServletRequest request,
						ServletResponse response,
						FilterChain chain)
				throws IOException, ServletException {
		
		// if user sent data
		if (request.getParameterNames().hasMoreElements()) {
			ContextObject contextObject = new ContextObject();
			
			// check each field
			for (Map.Entry<String, Validator[]> field : validationRules.entrySet()) {
				final String fieldName = field.getKey();
				final Validator[] fieldValidators = field.getValue();
				final String fieldValue = request.getParameter(fieldName);
				
				// user not sent value for this field
				if (fieldValue == null) {
					continue;
				}
				
				// check value
				for (Validator validator : fieldValidators) {
					if (! validator.isValid(fieldValue, request)) {
						contextObject.addFailedElement(fieldName, validator.getMessage());
						// don't check by other validators
						break;
					}
				}
				
				// don't lost user's data
				contextObject.addElement(fieldName, fieldValue);
			}
			
			String field1 = null;
			String field2 = null;
			
			// check password and login for unicity only if:
			// - its presents in user input
			// - its passes all standart checks
			field1 = passwordLoginMismatch.getFirstFieldName();
			field2 = passwordLoginMismatch.getSecondFieldName();
			if (request.getParameter(field1) != null &&
				request.getParameter(field2) != null &&
				! contextObject.getFailedElements().containsKey(field1) &&
				! contextObject.getFailedElements().containsKey(field2) &&
				! passwordLoginMismatch.isValid("", request)) {
				contextObject.addFailedElement(field2, passwordLoginMismatch.getMessage());
			}
			
			// check password and its confirmation only if:
			// - its presents in user input
			// - its passes all standart checks
			field1 = passwordConfirm.getFirstFieldName();
			field2 = passwordConfirm.getSecondFieldName();
			if (request.getParameter(field1) != null &&
				request.getParameter(field2) != null &&
				! contextObject.getFailedElements().containsKey(field1) &&
				! contextObject.getFailedElements().containsKey(field2) &&
				! passwordConfirm.isValid("", request)) {
				contextObject.addFailedElement(field1, passwordConfirm.getMessage());
			}
			
			request.setAttribute("context", contextObject);
		}
		
		chain.doFilter(request, response);
	}
	
	@Override
	public void destroy() {
	}
}

