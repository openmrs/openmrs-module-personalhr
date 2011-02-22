<%@ include file="/WEB-INF/view/module/personalhr/template/include.jsp" %>

<personalhr:require privilege="PHR Authenticated" otherwise="/phr/login.htm" redirect="/phr/findPatient.htm" />

<spring:message var="pageTitle" code="findPatient.title" scope="page"/>
<%@ include file="/WEB-INF/view/module/personalhr/template/header.jsp" %>

<h2><spring:message code="Patient.search"/></h2>	

<br />

<openmrs:portlet id="findPatient" url="findPatient" parameters="size=full|postURL=patientDashboard.form|showIncludeVoided=false|viewType=shortEdit" />

<openmrs:extensionPoint pointId="org.openmrs.personalhr.findPatient" type="html" />

<%@ include file="/WEB-INF/view/module/personalhr/template/footer.jsp" %>
