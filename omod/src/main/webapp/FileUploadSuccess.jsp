<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>

<%@ include file="template/localHeader.jsp"%>

<h2>
	<spring:message code="exportccd.import.title" />
</h2>

FileName : "<strong> ${fileName} </strong>" - Uploaded Successful.

<br/><br/>
<h3>Formatted display: </h3>
<br/>

${displayContent}
 
<br/><br/>
<h3>FileContent: </h3>
<br/>
<c:out value="${fileContent}"></c:out>


<%@ include file="/WEB-INF/template/footer.jsp"%>