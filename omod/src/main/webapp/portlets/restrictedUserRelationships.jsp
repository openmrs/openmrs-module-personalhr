<%@ include file="/WEB-INF/template/include.jsp" %>

<openmrs:htmlInclude file="/scripts/easyAjax.js" />
<openmrs:htmlInclude file="/dwr/interface/DWRRelationshipService.js" />
<openmrs:htmlInclude file="/dwr/util.js" />
<openmrs:htmlInclude file="/scripts/dojoConfig.js" />
<openmrs:htmlInclude file="/scripts/dojo/dojo.js" />

<script type="text/javascript">
</script>

<div id="followup-div"  >
<table style="margin: 0px 0px 1em 2em;">
  <tr>
    <th>Patient Name</th>
    <th>My Role</th>
    <th>My Email</th>
    <th>Share Type</th>
  </tr>
  <c:forEach var="token" items="${model.phrSharingTokens}">
	  <tr>
		<td><a href="patientDashboard.form?patientId=${token.patient.patientId}">${token.patient.personName.fullName}</a></td>
	    <td>${token.relationType}</td>
	    <td>${token.relatedPersonEmail}</td>
	    <td>${token.shareType}</td>
	  </tr> 
  </c:forEach>     
</table>
</div>

