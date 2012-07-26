<%@ include file="/WEB-INF/view/module/personalhr/template/include.jsp" %>
<%@ include file="/WEB-INF/view/module/personalhr/template/header.jsp" %>

<openmrs:htmlInclude file="/dwr/interface/DWREncounterService.js"/>
<openmrs:htmlInclude file="/scripts/jquery/dataTables/css/dataTables_jui.css"/>
<openmrs:htmlInclude file="/scripts/jquery/dataTables/js/jquery.dataTables.min.js"/>
<openmrs:htmlInclude file="/scripts/jquery-ui/js/openmrsSearch.js" />

<script type="text/javascript">
	var lastSearch;
	$j(document).ready(function() {
		new OpenmrsSearch("findEncounter", true, doEncounterSearch, doSelectionHandler, 
				[	{fieldName:"personName", header:omsgs.patientName},
					{fieldName:"encounterType", header:omsgs.encounterType},
					{fieldName:"formName", header:omsgs.encounterForm},
					{fieldName:"providerName", header:omsgs.encounterProvider},
					{fieldName:"location", header:omsgs.encounterLocation},
					{fieldName:"encounterDateString", header:omsgs.encounterDate}
				],
				{searchLabel: '<spring:message code="Encounter.search" javaScriptEscape="true"/>'});
	});
	
	function doSelectionHandler(index, data) {
		document.location = "phrEncounter.form?encounterId=" + data.encounterId + "&phrase=" + lastSearch;
	}
	
	//searchHandler for the Search widget
	function doEncounterSearch(text, resultHandler, getMatchCount, opts) {
		lastSearch = text;
		DWREncounterService.findCountAndEncounters(text, opts.includeVoided, opts.start, opts.length, getMatchCount, resultHandler);
	}
</script>

<h2><spring:message code="Encounter.title"/></h2>

<a href="phrEncounter.form"><spring:message code="Encounter.add"/></a>

<openmrs:extensionPoint pointId="org.openmrs.admin.encounters.index.afterAdd" type="html" />

<br/><br/>

<div>
	<b class="boxHeader"><spring:message code="Encounter.find"/></b>
	<div class="box">
		<div class="searchWidgetContainer" id="findEncounter"></div>
	</div>
</div>

<openmrs:extensionPoint pointId="org.openmrs.admin.encounters.index.footer" type="html" />

<%@ include file="/WEB-INF/view/module/personalhr/template/footer.jsp" %>