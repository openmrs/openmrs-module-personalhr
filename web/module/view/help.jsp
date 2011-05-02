<%@ include file="/WEB-INF/view/module/personalhr/template/include.jsp" %>
<personalhr:require privilege="PHR Authenticated" otherwise="/phr/login.htm" redirect="/phr/help.htm" />

<spring:message var="pageTitle" code="help.title" scope="page"/>
<%@ include file="/WEB-INF/view/module/personalhr/template/header.jsp" %>

<h2><spring:message code="personalhr.help.title"/></h2>

<br />
<spring:message code="personalhr.help.text"/>
<br />
<br />
<spring:message code="personalhr.help.text2"/>


<br/>

<openmrs:extensionPoint pointId="org.openmrs.module.personalhr.help" type="html" />


<%@ include file="/WEB-INF/view/module/personalhr/template/footer.jsp" %>