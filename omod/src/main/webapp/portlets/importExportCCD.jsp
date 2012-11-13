<%@ include file="/WEB-INF/view/module/personalhr/template/include.jsp" %>
<personalhr:require privilege="PHR Authenticated" otherwise="/phr/login.htm" redirect="../module/exportccd/importExportCCD.htm" />

<div class="tooltipPhr">
<spring:message code="exportccd.tooltip.ccd"/>
</div>
<div id="importexportccd_div">
<table>
	<tbody>
		<tr>
			<td>				
				<a href="../module/exportccd/fileupload.htm">Import CCD</a>
			</td>
			<td>
			<td>				
				<a href="../module/exportccd/exportPatient.form">Export CCD</a>
			</td>
		</tr>
	</tbody>
</table>
<br/>
</div>

<div id="importedCCD_div">
<h3>
	<spring:message code="exportccd.importedccd.title" />
</h3>
<c:choose>
	<c:when test="${ccdExists}">
			<spring:message code="exportccd.imported.on" /> ${dateImported}.
		
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
		<spring:message code="exportccd.not.imported" />	
	</c:otherwise>
</c:choose>
</div>