<%@ include file="/WEB-INF/template/include.jsp" %>
<openmrs:htmlInclude file="/scripts/easyAjax.js" />
<openmrs:htmlInclude file="/dwr/interface/DWRRelationshipService.js" />
<openmrs:htmlInclude file="/dwr/interface/DWRPatientService.js" />
<openmrs:htmlInclude file="/dwr/interface/DWRObsService.js" />
<openmrs:htmlInclude file="/dwr/engine.js" />
<openmrs:htmlInclude file="/dwr/util.js" />

<c:set var="anyExtension" value="false" />
<openmrs:authentication>
	<c:if test="${authenticatedUser != null}">
		<openmrs:extensionPoint pointId="personalhr.portlets.journal" type="html" varStatus="status">
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

<br/>
