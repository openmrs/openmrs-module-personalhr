<%@ include file="/WEB-INF/template/include.jsp" %>

<spring:message var="pageTitle" code="login.title" scope="page"/>
<%@ include file="/WEB-INF/view/module/personalhr/template/header.jsp" %>

<openmrs:portlet url="../module/personalhr/portlets/register"/>
		
<%@ include file="/WEB-INF/template/footer.jsp" %>