<%@ include file="/WEB-INF/template/include.jsp" %>
<openmrs:htmlInclude file="/scripts/easyAjax.js" />

<openmrs:htmlInclude file="/scripts/jquery/dataTables/css/dataTables.css" />
<openmrs:htmlInclude file="/scripts/jquery/dataTables/js/jquery.dataTables.min.js" />

<openmrs:htmlInclude file="/scripts/jquery-ui/js/jquery-ui-1.7.2.custom.min.js" />
<openmrs:htmlInclude file="/scripts/jquery-ui/css/redmond/jquery-ui-1.7.2.custom.css" />

<openmrs:globalProperty key="dashboard.encounters.showViewLink" var="showViewLink" defaultValue="true"/>
<openmrs:globalProperty key="dashboard.encounters.showEditLink" var="showEditLink" defaultValue="true"/>


<div id="treatmentSummaryPortlet"">
		<c:forEach items='${openmrs:sort(model.patientEncounters, "encounterDatetime", true)}' var="enc" varStatus="encStatus">
			<c:if test="${enc.encounterType.name == 'CANCER TREATMENT SUMMARY'}">
				<iframe src ="${pageContext.request.contextPath}/module/htmlformentry/htmlFormEntry.form?encounterId=${enc.encounterId}&mode=EDIT&inTab=true" width="100%" height="1000"/>
	        </c:if>					
		</c:forEach>
</div>

