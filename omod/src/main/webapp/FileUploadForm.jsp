<%@ include file="/WEB-INF/view/module/personalhr/template/include.jsp" %>
<personalhr:require privilege="PHR Authenticated" otherwise="/phr/login.htm" redirect="/module/exportccd/view/importExportCCD.htm" />
<%@ include file="/WEB-INF/view/module/personalhr/template/header.jsp" %>

<%@ include file="template/localHeader.jsp"%>

<style>
.error {
	color: #ff0000;
}
.errorblock{
	color: #000;
	background-color: #ffEEEE;
	border: 3px solid #ff0000;
	padding:8px;
	margin:16px;
}
</style>

<h2>
	<spring:message code="exportccd.import.title" />
</h2>

<form:form method="POST" commandName="fileUploadForm" enctype="multipart/form-data">

<form:errors path="*" cssClass="errorblock" element="div"/>

Please select a file to upload : <input type="file" name="file" /> <input type="submit" value="upload" />
<span><form:errors path="file" cssClass="error" /></span>

</form:form>


<%@ include file="/WEB-INF/template/footer.jsp"%>