<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>

<%@ include file="template/localHeader.jsp"%>
<h2>
	<spring:message code="exportccd.export.title" />
</h2>

<form id='exportPatient' method="POST">
<openmrs_tag:personField formFieldName="patientId"  formFieldId="patientId" /> 

<input type="submit" value='Export Patient Summary' >
</form>
<%@ include file="/WEB-INF/template/footer.jsp"%>