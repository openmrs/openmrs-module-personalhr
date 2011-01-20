<%@ include file="/WEB-INF/view/module/personalhr/template/include.jsp" %>

<%-- 
<openmrs:phrRequire privilege="PHR - View Patient" otherwise="/phr/login.htm" redirect="/module/personalhr/view/patientDashboard.form" />
--%>

	<%-- Header showing preferred name, id, and treatment status --%>
	<c:if test="${empty model.patientReasonForExit}">
		<div id="patientHeader" class="boxHeader">
	</c:if>
	<c:if test="${not empty model.patientReasonForExit}">
		<div id="patientHeader" class="boxHeaderRed">
	</c:if>
		<div id="patientHeaderPatientName">${model.patient.personName}</div>
		<table id="patientHeaderGeneralInfo">
			<tr class="patientHeaderGeneralInfoRow">
				<td id="patientHeaderPatientGender">
					<c:if test="${model.patient.gender == 'M'}"><img src="${pageContext.request.contextPath}/images/male.gif" alt='<spring:message code="Person.gender.male"/>' id="maleGenderIcon"/></c:if>
					<c:if test="${model.patient.gender == 'F'}"><img src="${pageContext.request.contextPath}/images/female.gif" alt='<spring:message code="Person.gender.female"/>' id="femaleGenderIcon"/></c:if>
				</td>
			</tr>
		</table>
	</div>
	
	<script type="text/javascript">
		function showMoreIdentifiers() {
			if (identifierElement.style.display == '') {
				linkElement.innerHTML = '<spring:message code="general.nMore" arguments="${fn:length(model.patient.activeIdentifiers) - 2}"/>';
				identifierElement.style.display = "none";
			}
			else {
				linkElement.innerHTML = '<spring:message code="general.nLess" arguments="${fn:length(model.patient.activeIdentifiers) - 2}"/>';
				identifierElement.style.display = "";
			}
		}
		
		var identifierElement = document.getElementById("patientHeaderMoreIdentifiers");
		var linkElement = document.getElementById("patientHeaderShowMoreIdentifiers");
		if (identifierElement)
			identifierElement.style.display = "none";
		
	</script>
	