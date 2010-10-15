<%@ include file="/WEB-INF/template/include.jsp" %>
<c:set var="anyExtension" value="false" />
<openmrs:authentication>
	<c:if test="${authenticatedUser != null}">
		<openmrs:extensionPoint pointId="personalhr.portlets.toxicities" type="html" varStatus="status">
			<c:set var="anyExtension" value="true" />
			<openmrs:portlet url="${extension.portletUrl}" parameters="${extension.portletParameters}" moduleId="${extension.moduleId}" />
		</openmrs:extensionPoint>
	</c:if>
</openmrs:authentication>

<c:if test="${not anyExtension}">
<center>
	<openmrs:portlet url="../module/personalhr/portlets/blank"/>
</center>
</c:if>

<br />
