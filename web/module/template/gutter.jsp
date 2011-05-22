<%@ taglib prefix="personalhr" uri="/WEB-INF/view/module/personalhr/taglibs/personalhr.tld" %>

<ul id="navList" class="navList">
	<personalhr:hasPrivilege role="PHR Patient">
		<li id="patientDashboardLink">
			<a href="patientDashboard.form?"><spring:message code="personalhr.myPersonalRecord"/></a>
		</li>
	</personalhr:hasPrivilege>

		<personalhr:hasPrivilege role="PHR Restricted User">
			<li id="personDashboardLink">
				<a href="restrictedUserDashboard.form?"><spring:message code="personalhr.myPersonalRecord"/></a>
			</li>
		</personalhr:hasPrivilege>

		<personalhr:hasPrivilege role="PHR Administrator">
			<li id="findPatientNavLink">
				<a href="${pageContext.request.contextPath}/phr/findPatient.htm">
					<spring:message code="Navigation.findCreatePatient"/>
				</a>
			</li>
			<li id="manageUserLink">
				<a href="user.list">
					<spring:message code="User.manage"/>
				</a>
			</li>
		</personalhr:hasPrivilege>	
			
		<openmrs:extensionPoint pointId="org.openmrs.personalhr.gutter.tools" type="html">
			<personalhr:hasPrivilege privilege="${extension.requiredPrivilege}">
				<li>
				<a href="${pageContext.request.contextPath}/${extension.url}"><spring:message code="${extension.label}"/></a>
				</li>
			</personalhr:hasPrivilege>
		</openmrs:extensionPoint>				
								
</ul>