<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>

<%@ include file="template/localHeader.jsp"%>
<h2>
	<spring:message code="exportccd.import.title" />
</h2>

<form id='importPatient' method="POST" enctype="multipart/form-data" >
	<table>
		<tr>
			<td>Patient CCCD: </td>
			<td><input type="file" name="T_PATIENT_FILE_INPUT" /></td>
		</tr>
	</table>
	<table>
		<input type="submit" value='Import Patient Summary' >
	</table>
</form>
<br/>
<h2>
	<spring:message code="exportccd.import.display" />
</h2>

<div>
<c:out value="${inputDoc}"></c:out>

</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>