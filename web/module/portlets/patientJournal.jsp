<%@ include file="/WEB-INF/template/include.jsp" %>
<openmrs:htmlInclude file="/scripts/easyAjax.js" />
<openmrs:htmlInclude file="/dwr/interface/DWRRelationshipService.js" />
<openmrs:htmlInclude file="/dwr/interface/DWRPatientService.js" />
<openmrs:htmlInclude file="/dwr/interface/DWRObsService.js" />
<openmrs:htmlInclude file="/dwr/engine.js" />
<openmrs:htmlInclude file="/dwr/util.js" />

<%--
<openmrs:hasPhrPrivilege privilege="PHR - View Relationships Section">
</openmrs:hasPhrPrivilege>
--%>
<div id="patientRelationshipsBoxHeader" class="boxHeader${model.patientVariation}"><spring:message code="Relationship.relationships" /></div>
<div id="patientRelationshipsBox" class="box${model.patientVariation}">
	<openmrs:portlet url="../module/personalhr/portlets/personRelationships" size="normal" patientId="${patient.patientId}" />
</div>

<br/>
