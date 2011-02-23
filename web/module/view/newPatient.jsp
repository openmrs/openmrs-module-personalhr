<%@ include file="/WEB-INF/view/module/personalhr/template/include.jsp" %>

<personalhr:require privilege="PHR Authenticated" otherwise="/phr/login.htm" redirect="/phr/findPatient.htm" />

<spring:message var="pageTitle" code="findPatient.title" scope="page"/>
<%@ include file="/WEB-INF/view/module/personalhr/template/header.jsp" %>

<personalhr:portlet id="createPatient" url="../module/personalhr/portlets/newPatientForm.portlet" parameters="size=full|postURL=patientDashboard.form|showIncludeVoided=false|viewType=shortEdit" />

<%@ include file="/WEB-INF/view/module/personalhr/template/footer.jsp" %>
