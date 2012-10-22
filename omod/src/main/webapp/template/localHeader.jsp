<spring:htmlEscape defaultHtmlEscape="true" />
<ul id="menu">
	<li class="first">
		<personalhr:hasPrivilege role="PHR Patient">
			<a style="color:navy;" href="patientDashboard.form?"><spring:message code="personalhr.myPersonalRecord"/></a>
		</personalhr:hasPrivilege>

		<openmrs:hasPrivilege privilege="Administrator">
			<a 	href="${pageContext.request.contextPath}/admin"><spring:message	code="admin.title.short" /></a>
		</openmrs:hasPrivilege>
	</li>

	<li
		<c:if test='<%=request.getRequestURI().contains("/manage")%>'>class="active"</c:if>>
		<a
		href="${pageContext.request.contextPath}/module/exportccd/ccdConfiguration.form"><spring:message
				code="exportccd.manage.ccd.configuration" /></a>


	</li>
	<li
		<c:if test='<%=request.getRequestURI().contains("/manage")%>'>class="active"</c:if>>
		<a
		href="${pageContext.request.contextPath}/module/exportccd/exportPatient.form"><spring:message
				code="exportccd.export" /></a>


	</li>
	<li
		<c:if test='<%=request.getRequestURI().contains("/manage")%>'>class="active"</c:if>>
		<a
		href="${pageContext.request.contextPath}/module/exportccd/fileupload.htm"><spring:message
				code="exportccd.import" /></a>


	</li>
	<!-- Add further links here -->
</ul>
