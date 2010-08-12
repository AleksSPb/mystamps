package ru.mystamps.site.validators;

import java.sql.SQLException;

import javax.faces.application.FacesMessage;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import javax.naming.NamingException;

import org.apache.log4j.Logger;

import ru.mystamps.db.UsersActivation;
import ru.mystamps.site.utils.Messages;

public class ActivationKeyExistsValidator implements Validator {
	
	private final Logger log = Logger.getRootLogger();
	
	/**
	 * Check that activation key exists in database.
	 *
	 * @param FacesContext context
	 * @param UIComponent component
	 * @param Object value
	 * @throws ValidatorException
	 **/
	@Override
	public void validate(
			final FacesContext context,
			final UIComponent component,
			final Object value)
		throws ValidatorException {
		
		if (component instanceof EditableValueHolder) {
			if (! ((EditableValueHolder)component).isValid()) {
				return;
			}
		}
		
		try {
			final String actKey = (String)value;
			final UsersActivation activationRequests = new UsersActivation();
			
			if (! activationRequests.actKeyExists(actKey)) {
				final String msg =
					Messages.getTranslation(context, "tv_key_not_exists");
				final FacesMessage message =
					new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg);
				throw new ValidatorException(message);
			}
			
		// catch and convert exceptions from UsersActivation class to
		// ValidatorException
		} catch (NamingException ex) {
			log.error(ex);
			final String errMsg =
				Messages.getTranslation(context, "tv_internal_error");
			final FacesMessage facesMsg =
				new FacesMessage(FacesMessage.SEVERITY_ERROR, errMsg, errMsg);
			throw new ValidatorException(facesMsg, ex);
			
		} catch (SQLException ex) {
			log.error(ex);
			final String errMsg =
				Messages.getTranslation(context, "tv_internal_error");
			final FacesMessage facesMsg =
				new FacesMessage(FacesMessage.SEVERITY_ERROR, errMsg, errMsg);
			throw new ValidatorException(facesMsg, ex);
		}
	}
	
}
