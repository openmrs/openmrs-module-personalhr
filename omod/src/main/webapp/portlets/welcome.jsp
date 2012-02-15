<%@ include file="/WEB-INF/template/include.jsp" %>

<c:choose>
	<c:when test="${model.authenticatedUser != null}">
		<c:choose>
			<c:when test="${model.showName != 'false'}">
				<spring:message code="welcomeUser" arguments="${model.authenticatedUser.personName.givenName},'Personal Health Toolkit'" />
			</c:when>
			<c:otherwise>
				<spring:message code="welcome" arguments="Personal Health Toolkit" />
			</c:otherwise>
		</c:choose>
		<c:if test="${model.customText != ''}">
			${model.customText}
		</c:if>
	</c:when>
	<c:otherwise>
		<spring:message code="welcome" arguments="Personal Health Toolkit" />
		<c:if test="${model.showLogin == 'true'}">
			<br/>
			<openmrs:portlet url="../module/personalhr/portlets/login" parameters="redirect=${model.redirect}|sharingToken=${model.sharingToken}" />
		</c:if>
	</c:otherwise>
</c:choose>