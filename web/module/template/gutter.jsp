<ul id="navList" class="navList">
	<li id="homeNavLink" class="firstChild">
		<a href="${pageContext.request.contextPath}/phr/index.htm"><spring:message code="Navigation.home"/></a>
	</li>

	<openmrs:hasPrivilege privilege="Single Patient Access">
		<li id="patientDashboardLink">
			<a href="patientDashboard.form?"><spring:message code="personalhr.myPersonalRecord"/></a>
		</li>
	</openmrs:hasPrivilege>
	
	<openmrs:hasPrivilege privilege="Single Patient Access" inverse="true">
		<openmrs:hasPrivilege privilege="Restricted Patient Access">
			<li id="personDashboardLink">
				<a href="personDashboard.form?personId=5"><spring:message code="personalhr.myPersonalRecord"/></a>
			</li>
		</openmrs:hasPrivilege>
	</openmrs:hasPrivilege>
			
</ul>