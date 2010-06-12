<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ page pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<f:view>
		<head>
			<title>MyStamps: <h:outputText value="#{m.t_add_country}" /></title>
			<meta http-equiv="Content-Type" content="text/html; charset=utf8" />
			<link rel="stylesheet" type="text/css" href="styles/main.css" />
		</head>
		<body>
			<%@ include file="/WEB-INF/segments/header.jspf" %>
			<div id="content">
				<h3>
					<h:outputText value="#{m.t_add_country_ucfirst}" />
				</h3>
				<div class="hint">
					<h:outputFormat value="#{m.t_required_fields_legend}" escape="false">
						<f:param value="<span class=\"required_field\">*</span>" />
					</h:outputFormat>
				</div>
				<div class="generic_form">
					<h:form id="add_country_form" prependId="false">
						<h:panelGrid columns="4">
								<h:outputLabel for="country" value="#{m.t_country}" />
								<h:outputText id="country_required" value="*" styleClass="required_field" />
								<h:inputText id="country" required="true" />
								<h:message id="country_error" for="country" styleClass="error" />
								
								<h:panelGroup />
								<h:panelGroup />
								<h:commandButton id="submit" type="submit" value="#{m.t_add}" />
								<h:panelGroup />
						</h:panelGrid>
					</h:form>
				</div>
			</div>
			<%@ include file="/WEB-INF/segments/footer.jspf" %>
		</body>
	</f:view>
</html>
