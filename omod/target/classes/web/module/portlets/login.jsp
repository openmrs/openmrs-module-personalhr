<%@ include file="/WEB-INF/template/include.jsp" %>

<%@ page import="org.openmrs.web.WebConstants" %>
<%
	pageContext.setAttribute("redirect", session.getAttribute(WebConstants.OPENMRS_LOGIN_REDIRECT_HTTPSESSION_ATTR));
	session.removeAttribute(WebConstants.OPENMRS_LOGIN_REDIRECT_HTTPSESSION_ATTR); 
%>

<br/>

<form method="post" action="/openmrs/moduleServlet/personalhr/phrLoginServlet" style="padding:15px; width: 300px;" autocomplete="off">
	<table>
		<tr>
			<td><spring:message code="User.username"/>:</td>
			<td><input type="text" name="uname" value="<request:parameter name="username" />" id="username" size="25" maxlength="50" /></td>
		</tr>
		<tr>
			<td><spring:message code="User.password"/>:</td>
			<td><input type="password" name="pw" value="" id="password" size="25" /></td>
		</tr>
		<tr>
			<td></td>
			<td><input type="submit" value="<spring:message code="auth.login"/>" /></td>
		</tr>
		<tr>
			<td></td>
			<td><a style="font-size: 90%;" href="${pageContext.request.contextPath}/phr/phrForgotPassword.form"><spring:message code="User.password.forgot"/></a></td>
		</tr>
		<tr>
			<td><input type="hidden" name="sharingToken" id="sharingToken" value="${model.sharingToken}"/></td>
			<td><a style="font-size: 90%;" href="${pageContext.request.contextPath}/phr/user.form?sharingToken=${model.sharingToken}"></a></td>
		</tr>
		<tr>
			<td>
				<td><a style="font-size: 90%;" href="${pageContext.request.contextPath}/phr/userRegUniqueid.form"><spring:message code="personalhr.signon"/></a></td>
			</td>
		</tr>
	</table>
	<br/>
	
	<c:if test="${not param.noredirect}">
		<c:choose>
			<c:when test="${not empty model.redirect}">
				<input type="hidden" name="redirect" value="${model.redirect}" />
			</c:when>
			<c:when test="${redirect != ''}">
				<input type="hidden" name="redirect" value="${redirect}" />
			</c:when>
			<c:otherwise>
				<input type="hidden" name="redirect" value="" />
			</c:otherwise>
		</c:choose>
		
		<input type="hidden" name="refererURL" value='<request:header name="referer" />' />
	</c:if>
	
</form>

<openmrs:extensionPoint pointId="org.openmrs.login" type="html" />

<script type="text/javascript">
 document.getElementById('username').focus();
</script>