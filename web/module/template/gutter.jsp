<ul id="navList" class="navList">
	<openmrs:hasPrivilege privilege="PHR Single Patient Access">
		<li id="patientDashboardLink">
			<a href="patientDashboard.form?"><spring:message code="personalhr.myPersonalRecord"/></a>
		</li>
	</openmrs:hasPrivilege>
	
	<openmrs:hasPrivilege privilege="PHR Single Patient Access" inverse="true">
		<openmrs:hasPrivilege privilege="PHR Restricted Patient Access">
			<li id="personDashboardLink">
				<a href="restrictedUserDashboard.form?"><spring:message code="personalhr.myPersonalRecord"/></a>
			</li>
		</openmrs:hasPrivilege>
		
		<openmrs:hasPrivilege privilege="PHR Restricted Patient Access" inverse="true">
			<openmrs:hasPrivilege privilege="PHR All Patients Access">
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
				<openmrs:extensionPoint pointId="org.openmrs.personalhr.gutter.tools" type="html">
					<openmrs:hasPrivilege privilege="${extension.requiredPrivilege}">
						<li>
						<a href="${pageContext.request.contextPath}/${extension.url}"><spring:message code="${extension.label}"/></a>
						</li>
					</openmrs:hasPrivilege>
				</openmrs:extensionPoint>				
			</openmrs:hasPrivilege>

			<openmrs:hasPrivilege privilege="PHR All Patients Access" inverse="true">
				<li id="homeNavLink" class="firstChild">
					<a href="${pageContext.request.contextPath}/phr/index.htm"><spring:message code="Navigation.home"/></a>
				</li>
			</openmrs:hasPrivilege>
							
		</openmrs:hasPrivilege>
		
	</openmrs:hasPrivilege>
			
</ul>