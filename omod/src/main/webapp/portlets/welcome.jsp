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
		<br/>
		<spring:message code="medadherence.welcome" />
		<br/>
		<a href="medicationFeedback.htm"><button><spring:message code="medadherence.medication.feedback" /></button> </a>
		<table>
			<tr>
				<td>
				<a href="${pageContext.request.contextPath}/phr/options.form"><button><spring:message code="medadherence.my.profile" /></button> </a>
				</td>
				<td>
				<a href="help.htm"><button><spring:message code="medadherence.help" /></button> </a>
				</td>
				<td>
				<a href="contactUs.htm"><button><spring:message code="medadherence.contact" /></button> </a>
				</td>
				<td>
				<a href="${pageContext.request.contextPath}/moduleServlet/personalhr/phrLogoutServlet"><button><spring:message code="medadherence.logout" /></button> </a>
				</td>
			</tr>
		</table>
		
	</c:when>
	<c:otherwise>
		<spring:message code="welcome" arguments="Personal Health Toolkit" />
		<c:if test="${model.showLogin == 'true'}">
			<br/>
			<openmrs:portlet url="../module/personalhr/portlets/login" parameters="redirect=${model.redirect}|sharingToken=${model.sharingToken}" />
		</c:if>
	</c:otherwise>
</c:choose>