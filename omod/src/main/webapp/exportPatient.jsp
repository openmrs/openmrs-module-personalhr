<%@ include file="/WEB-INF/view/module/personalhr/template/include.jsp" %>
<personalhr:require privilege="PHR Authenticated" otherwise="/phr/login.htm" redirect="../module/exportccd/exportPatient.htm" />
<%@ include file="/WEB-INF/view/module/personalhr/template/header.jsp" %>

<%@ include file="template/localHeader.jsp"%>
<h2>
	<spring:message code="exportccd.export.title" />
</h2>

<form id='exportPatient' method="POST">
<openmrs_tag:personField formFieldName="patientId"  formFieldId="patientId" /> 

<input type="submit" value='Export Patient Summary' >
</form>
<%@ include file="/WEB-INF/template/footer.jsp"%>