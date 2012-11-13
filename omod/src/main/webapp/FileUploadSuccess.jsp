<%@ include file="/WEB-INF/view/module/personalhr/template/include.jsp" %>
<personalhr:require privilege="PHR Authenticated" otherwise="/phr/login.htm" redirect="/module/exportccd/FileUploadSuccess.htm" />
<%@ include file="/WEB-INF/view/module/personalhr/template/header.jsp" %>

<%@ include file="template/localHeader.jsp"%>

<h3>
	<spring:message code="exportccd.import.title" />
</h3>

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