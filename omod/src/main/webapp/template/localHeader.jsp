<spring:htmlEscape defaultHtmlEscape="true" />
<ul id="menu">
	<li class="first"><a
		href="${pageContext.request.contextPath}/admin"><spring:message
				code="admin.title.short" /></a></li>

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
		href="${pageContext.request.contextPath}/module/exportccd/importPatient.form"><spring:message
				code="exportccd.import" /></a>


	</li>
	<!-- Add further links here -->
</ul>
