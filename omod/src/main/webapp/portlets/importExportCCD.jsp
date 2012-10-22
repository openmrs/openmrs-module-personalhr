<%@ include file="/WEB-INF/view/module/personalhr/template/include.jsp" %>
<personalhr:require privilege="PHR Authenticated" otherwise="/phr/login.htm" redirect="../module/exportccd/importExportCCD.htm" />

<div class="tooltipPhr">
<spring:message code="exportccd.tooltip.ccd"/>
</div>
<div id="importexportccd_div">
<br/>
<h3>Import/Export My CCD</h3>
<br/>
 
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
</br>
</div>
