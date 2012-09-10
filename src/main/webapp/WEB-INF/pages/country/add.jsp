<%@ page pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8" />
		<title>MyStamps: <spring:message code="t_add_country" /></title>
		<link rel="shortcut icon" type="image/x-icon" href="${faviconUrl}" />
		<link rel="stylesheet" type="text/css" href="${mainCssUrl}" />
	</head>
	<body>
		<%@ include file="/WEB-INF/segments/header.jspf" %>
		<div id="content">
			<h3>
				<spring:message code="t_add_country_ucfirst" />
			</h3>
			<elem:legend />
			<div class="generic_form">
				<form:form method="post" modelAttribute="addCountryForm">
					<table>
						<tr>
							<td>
								<form:label path="country">
									<spring:message code="t_country" />
								</form:label>
							</td>
							<td>
								<span id="country.required" class="required_field">*</span>
							</td>
							<td>
								<form:input path="country" required="required" autofocus="autofocus" />
							</td>
							<td>
								<form:errors path="country" cssClass="error" />
							</td>
						</tr>
						<tr>
							<td></td>
							<td></td>
							<td>
								<input type="submit" value="<spring:message code="t_add" />" />
							</td>
							<td></td>
						</tr>
					</table>
				</form:form>
			</div>
		</div>
		<%@ include file="/WEB-INF/segments/footer.jspf" %>
	</body>
</html>
