<%@ include file="/WEB-INF/view/module/personalhr/template/include.jsp" %>

<spring:message var="pageTitle" code="findPatient.title" scope="page"/>

<personalhr:require privilege="PHR Administrator" otherwise="/phr/login.htm" redirect="/phr/findPatient.htm" />

<%@ include file="/WEB-INF/view/module/personalhr/template/header.jsp" %>

<h2><spring:message code="Patient.search"/></h2>	

<br />

<personalhr:portlet id="findPatient" url="../module/personalhr/portlets/findPatient.portlet" parameters="size=full|postURL=patientDashboard.form|showIncludeVoided=false|viewType=shortEdit" />

<openmrs:extensionPoint pointId="org.openmrs.personalhr.findPatient" type="html" />

<%@ include file="/WEB-INF/view/module/personalhr/template/footer.jsp" %>
