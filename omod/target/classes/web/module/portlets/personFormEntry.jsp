<%@ include file="/WEB-INF/template/include.jsp" %>
<%--
Parameters:
	showDecoration (boolean): whether or not to put this in a box
	showLastThreeEncounters (boolean): whether or not to show a snippet of encounters
	returnUrl (String): where to go back to when a form has been cancelled or successfully filled out
--%>

<style type="text/css">
	.EncounterTypeClass {
		color: lightgrey;
	}
</style>
		
<%-- hack because calling a portlet clears parameters --%>
<c:set var="showDecorationProp" value="false" />

<c:if test="${showDecorationProp}">
	<div class="boxHeader${model.patientVariation}"><spring:message code="FormEntry.fillOutForm"/></div>
	<div class="box${model.patientVariation}">
</c:if>

<c:if test="${!model.anyUpdatedFormEntryModules}">
	<span class="error"><spring:message code="FormEntry.noModulesInstalled"/></span>
	<br/><br/>
</c:if>

<c:if test="${model.anyUpdatedFormEntryModules}">
	
	<%--
		goBackOnEntry == 'true' means have the browser go back to the find patient page after starting to enter a form
	--%>
	<openmrs:globalProperty key="FormEntry.patientForms.goBackOnEntry" var="goBackOnEntry" defaultValue="false"/>
	
	<script type="text/javascript">	
		<%-- global var and datatable filter for showRetired --%>
		var showRetiredFormsForEntryorg.openmrs.module:personalhr-omod:jar:0.0.3-SNAPSHOT = false;
		$j.fn.dataTableExt.afnFiltering.push(
			function( oSettings, aData, iDataIndex ) {
				if (oSettings.sTableId != 'formEntryTableorg.openmrs.module:personalhr-omod:jar:0.0.3-SNAPSHOT')
					return true;
				else
					return showRetiredFormsForEntryorg.openmrs.module:personalhr-omod:jar:0.0.3-SNAPSHOT || aData[4] == 'false';
			}
		);

		$j(document).ready(function() {
		});
	
		function startDownloading() {
			<c:if test="${goBackOnEntry}">
				timeOut = setTimeout("goBackToPatientSearch()", 30000);
			</c:if>
		}
		
		function goBackToPatientSearch() {
			document.location='findPatient.htm';
		}
	</script>
	<div id="formEntryTableParentorg.openmrs.module:personalhr-omod:jar:0.0.3-SNAPSHOT">
	<table id="formEntryTableorg.openmrs.module:personalhr-omod:jar:0.0.3-SNAPSHOT" cellspacing="0" cellpadding="3">
		<thead>
			<tr>
				<th></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="entry" items="${model.formToEntryUrlMap}" varStatus="rowCounter">
				<openmrs:hasPrivilege privilege="${entry.value.requiredPrivilege}">
					<c:url var="formUrl" value="${entry.value.formEntryUrl}">
						<c:param name="personId" value="${model.personId}"/>
						<c:param name="patientId" value="${model.patientId}"/>
						<c:param name="returnUrl" value="${model.returnUrl}"/>
						<c:param name="formId" value="${entry.key.formId}"/>
					</c:url>
					<tr<c:if test="${entry.key.retired}"> class="retired"</c:if>>
						<td>
							<a href="${formUrl}" onclick="startDownloading();"> Create ${entry.key.name}</a>
						</td>
					</tr>
				</openmrs:hasPrivilege>
			</c:forEach>
		</tbody>
	</table>
	</div>
</c:if>

<c:if test="${showDecorationProp}">
	</div>
</c:if>