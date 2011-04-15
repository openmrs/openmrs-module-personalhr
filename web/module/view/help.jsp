<%@ include file="/WEB-INF/template/include.jsp" %>

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