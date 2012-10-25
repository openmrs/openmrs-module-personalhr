<%@ include file="/WEB-INF/view/module/personalhr/template/include.jsp" %>
<personalhr:require privilege="PHR Authenticated" otherwise="/phr/login.htm" redirect="/module/medadherence/portlets/medicationBarriers.htm" />

<openmrs:htmlInclude file="/scripts/easyAjax.js" />

<openmrs:htmlInclude file="/scripts/jquery/dataTables/css/dataTables.css" />
<openmrs:htmlInclude file="/scripts/jquery/dataTables/js/jquery.dataTables.min.js" />

<openmrs:globalProperty key="dashboard.encounters.showViewLink" var="showViewLink" defaultValue="true"/>
<openmrs:globalProperty key="dashboard.encounters.showEditLink" var="showEditLink" defaultValue="true"/>

<div id="displayEncounterPopup">
	<div id="displayEncounterPopupLoading"><spring:message code="general.loading"/></div>
	<iframe id="displayEncounterPopupIframe" width="100%" height="100%" marginWidth="0" marginHeight="0" frameBorder="0" scrolling="auto"></iframe>
</div>

<script type="text/javascript">
	$j(document).ready(function() {
		$j('#displayEncounterPopup').dialog({
				title: 'dynamic',
				autoOpen: false,
				draggable: false,
				resizable: false,
				width: '95%',
				modal: true,
				open: function(a, b) { $j('#displayEncounterPopupLoading').show(); }
		});
	});

	function loadUrlIntoEncounterPopup(title, urlToLoad) {
		$j("#displayEncounterPopupIframe").attr("src", urlToLoad);
		$j('#displayEncounterPopup')
			.dialog('option', 'title', title)
			.dialog('option', 'height', $j(window).height() - 50) 
			.dialog('open');
	}
</script>

<div class="tooltipPhr">
<spring:message code="medadherence.tooltip.barriers"/>
</div>

<div class="boxHeader"><spring:message code="medadherence.barriers.form.add"/></div>
<div class="box">
	<iframe id="medicationBarriersCreationFrame" src ="${pageContext.request.contextPath}/module/medadherence/htmlFormEntryForm.form?formId=1&personId=${model.personId}&patientId=${model.patientId}&mode=EDIT&inTab=false" width="100%" height="800">
	  Identify Medication Barriers
	</iframe>
</div>	


	

