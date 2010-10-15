<%@ include file="/WEB-INF/view/module/personalhr/template/include.jsp" %>
<%-- 
<openmrs:phrRequire privilege="PHR - View Patient" otherwise="/phr/login.htm" redirect="/module/personalhr/view/patientDashboard.form" />
--%>

<c:set var="OPENMRS_VIEWING_PATIENT_ID" scope="request" value="${patient.patientId}"/>
<openmrs:globalProperty var="enablePatientName" key="dashboard.showPatientName" defaultValue="false"/>

<c:if test="${enablePatientName}">
	<c:set var="patientName" value="${patient.personName.fullName} (${patient.patientIdentifier})"/>
	<spring:message var="pageTitle" text="${patientName}" scope="page"/>
</c:if>
<c:if test="${!enablePatientName}">
	<spring:message var="pageTitle" code="patientDashboard.title" scope="page"/>
</c:if>
<%@ include file="/WEB-INF/view/module/personalhr/template/header.jsp" %>

<script type="text/javascript">
	var timeOut = null;
	addEvent(window, 'load', initTabs);

	<openmrs:authentication>var userId = "${authenticatedUser.userId}";</openmrs:authentication>

	function initTabs() {
		var c = getTabCookie();
		if (c == null) {
			var tabs = document.getElementById("patientTabs").getElementsByTagName("a");
			if (tabs.length && tabs[0].id)
				c = tabs[0].id;
		}
		changeTab(c);
	}
	
	function setTabCookie(tabType) {
		document.cookie = "dashboardTab-" + userId + "="+escape(tabType);
	}
	
	function getTabCookie() {
		var cookies = document.cookie.match('dashboardTab-' + userId + '=(.*?)(;|$)');
		if (cookies) {
			return unescape(cookies[1]);
		}
		return null;
	}
	
	function changeTab(tabObj) {
		if (!document.getElementById || !document.createTextNode) {return;}
		if (typeof tabObj == "string")
			tabObj = document.getElementById(tabObj);
		
		if (tabObj) {
			var tabs = tabObj.parentNode.parentNode.getElementsByTagName('a');
			for (var i=0; i<tabs.length; i++) {
				if (tabs[i].className.indexOf('current') != -1) {
					manipulateClass('remove', tabs[i], 'current');
				}
				var divId = tabs[i].id.substring(0, tabs[i].id.lastIndexOf("Tab"));
				var divObj = document.getElementById(divId);
				if (divObj) {
					if (tabs[i].id == tabObj.id)
						divObj.style.display = "";
					else
						divObj.style.display = "none";
				}
			}
			addClass(tabObj, 'current');
			
			setTabCookie(tabObj.id);
		}
		return false;
    }
</script>

<c:if test="${patient.voided}">
	<div id="patientDashboardVoided" class="retiredMessage">
		<div><spring:message code="Patient.voidedMessage"/></div>
	</div>
</c:if>

<c:if test="${patient.dead}">
	<div id="patientDashboardDeceased" class="retiredMessage">
		<div>
			<spring:message code="Patient.patientDeceased"/>
			<c:if test="${not empty patient.deathDate}">
				&nbsp;&nbsp;&nbsp;&nbsp;
				<spring:message code="Person.deathDate"/>: <openmrs:formatDate date="${patient.deathDate}"/>
			</c:if>
			<c:if test="${not empty patient.causeOfDeath}">
				&nbsp;&nbsp;&nbsp;&nbsp;
				<spring:message code="Person.causeOfDeath"/>: <openmrs:format concept="${patient.causeOfDeath}"/>
				<c:if test="${not empty causeOfDeathOther}"> 
					  &nbsp;:&nbsp;<c:out value="${causeOfDeathOther}"></c:out>
				</c:if>
			</c:if>
		</div>
	</div>
</c:if>

<personalhr:portlet url="../module/personalhr/portlets/patientHeader" id="patientDashboardHeader" patientId="${patient.patientId}"/>

<openmrs:globalProperty var="enableFormEntryTab" key="FormEntry.enableDashboardTab" defaultValue="true"/>

<div id="patientTabs${patientVariation}">
	<ul>
		<%-- 
		<openmrs:hasPhrPrivilege privilege="PHR - View Overview Section">
		</openmrs:hasPhrPrivilege>
		--%>
		<li><a id="patientRelationshipsTab" href="#" onclick="return changeTab(this);" hidefocus="hidefocus"><spring:message code="personalhr.relationships"/></a></li>
		<li><a id="patientDemographicsTab" href="#" onclick="return changeTab(this);" hidefocus="hidefocus"><spring:message code="personalhr.demographics"/></a></li>
		<li><a id="patientTreatmentSummaryTab" href="#" onclick="return changeTab(this);" hidefocus="hidefocus"><spring:message code="personalhr.treatmentSummary"/></a></li>
		<li><a id="patientToxicitiesTab" href="#" onclick="return changeTab(this);" hidefocus="hidefocus"><spring:message code="personalhr.toxicities"/></a></li>
		<li><a id="patientFollowTestsTab" href="#" onclick="return changeTab(this);" hidefocus="hidefocus"><spring:message code="personalhr.followTests"/></a></li>
		<li><a id="patientJournalTab" href="#" onclick="return changeTab(this);" hidefocus="hidefocus"><spring:message code="personalhr.journal"/></a></li>
		<li><a id="patientMessagingTab" href="#" onclick="return changeTab(this);" hidefocus="hidefocus"><spring:message code="personalhr.messaging"/></a></li>
		<li><a id="patientCommunitiesTab" href="#" onclick="return changeTab(this);" hidefocus="hidefocus"><spring:message code="personalhr.communities"/></a></li>
	</ul>
</div>

<div id="patientSections">
	<%-- 
	<openmrs:hasPhrPrivilege privilege="PHR - View Overview Section">
	</openmrs:hasPhrPrivilege>
	--%>
		<div id="patientRelationships" style="display:none;">
			
			<personalhr:portlet url="../module/personalhr/portlets/patientRelationships" id="patientDashboardRelationships" patientId="${patient.patientId}"/>
			
		</div>
		<div id="patientFollowTests" style="display:none;">
			
			<personalhr:portlet url="../module/personalhr/portlets/patientFollowTests" id="patientDashboardFollowTests" patientId="${patient.patientId}"/>
			
		</div>
		<div id="patientTreatmentSummary" style="display:none;">
			
			<personalhr:portlet url="../module/personalhr/portlets/patientTreatmentSummary" id="patientDashboardTreatmentSummary" patientId="${patient.patientId}"/>
			
		</div>
		<div id="patientJournal" style="display:none;">
			
			<personalhr:portlet url="../module/personalhr/portlets/patientJournal" id="patientDashboardJournal" patientId="${patient.patientId}"/>
			
		</div>
		<div id="patientToxicities" style="display:none;">
			
			<personalhr:portlet url="../module/personalhr/portlets/patientToxicities" id="patientDashboardToxicities" patientId="${patient.patientId}"/>
			
		</div>
		<div id="patientMessaging" style="display:none;">
			
			<personalhr:portlet url="../module/personalhr/portlets/patientMessaging" id="patientDashboardMessaging" patientId="${patient.patientId}"/>
			
		</div>		
		<div id="patientDemographics" style="display:none;">
			<personalhr:portlet url="../module/personalhr/portlets/patientDemographics" id="patientDashboardDemographics" patientId="${patient.patientId}"/>
		</div>
		<div id="patientCommunities" style="display:none;">
			<personalhr:portlet url="../module/personalhr/portlets/patientCommunities" id="patientDashboardCommunities" patientId="${patient.patientId}"/>
		</div>
	
</div>

<%@ include file="/WEB-INF/template/footer.jsp" %>