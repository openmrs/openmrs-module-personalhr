<%@ taglib prefix="personalhr" uri="/WEB-INF/view/module/personalhr/taglibs/personalhr.tld" %>

<ul id="navList" class="navList">
		<personalhr:hasPrivilege role="PHR Administrator">
			<li id="findPatientNavLink">
				<a href="${pageContext.request.contextPath}/phr/findPatient.htm">
					<spring:message code="Navigation.findCreatePatient"/>
				</a>
			</li>
			<li id="manageUserLink">
				<a href="${pageContext.request.contextPath}/phr/user.list">
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
							
		<personalhr:hasPrivilege role="PHR Administrator">
		<li id="administrationNavLink">
			<a href="${pageContext.request.contextPath}/phr/adminindex.htm"><spring:message code="Navigation.administration"/></a>
		</li>
		</personalhr:hasPrivilege>	
</ul>