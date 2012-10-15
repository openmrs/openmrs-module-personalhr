<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>

<%@ include file="template/localHeader.jsp"%>
<h2>
	<spring:message code="exportccd.export.title" />
</h2>

<p>Hello ${user.systemId}!</p>

<%@ include file="/WEB-INF/template/footer.jsp"%>