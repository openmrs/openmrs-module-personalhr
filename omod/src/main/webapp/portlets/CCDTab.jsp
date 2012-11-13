<%@ include file="/WEB-INF/view/module/personalhr/template/include.jsp" %>
<personalhr:require privilege="PHR Authenticated" otherwise="/phr/login.htm" redirect="/module/exportccd/FileUploadSuccess.htm" />

<h3>
	<spring:message code="exportccd.ccdtab.title" />
</h3>
<c:choose>
	<c:when test="${ccdExists}">
		CCD was imported on ${dateImported}.
		
		<br/><br/>
		<h3>Formatted display: </h3>
		<br/>
		
		${displayContent}
		 
		<br/><br/>
		<h3>FileContent: </h3>
		<br/>
		<c:out value="${fileContent}"></c:out>
	</c:when>
	<c:otherwise>
		CCD was not found.	
	</c:otherwise>
</c:choose>

