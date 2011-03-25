<%@ include file="/WEB-INF/view/module/personalhr/template/include.jsp" %>
<%-- 
<openmrs:phrRequire privilege="PHR - View Patient" otherwise="/phr/login.htm" redirect="/module/personalhr/view/patientDashboard.form" />
--%>
<personalhr:require privilege="PHR Authenticated" otherwise="/phr/login.htm" redirect="/module/personalhr/view/patientDashboard.form" />

<c:set var="enableFormEntry" value="false"/>
<%--
<openmrs:hasPrivilege privilege="PHR All Patients Access">
	<c:set var="enableFormEntry" value="true"/>
</openmrs:hasPrivilege>
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
	var summaryTabLoaded=false;
	addEvent(window, 'load', initTabs);

	<openmrs:authentication>var userId = "${authenticatedUser.userId}";</openmrs:authentication>

	function initTabs() {
		var c = getTabCookie();
		if (c == null) {
			var tabs = document.getElementById("patientTabs").getElementsByTagName("a");
			if (tabs.length && tabs[0].id)
				c = tabs[0].id;
		}
		//changeTab("patientTreatmentSummaryTab");
		//if(!summaryTabLoaded){
        //	c="patientTreatmentSummaryTab";
		//}
		if(c=="patientTreatmentSummaryTab") {
			summaryTabLoaded = true;
		}
		changeTab(c);

	}
	
	function setTabCookie(tabType) {
		document.cookie = "patientDashboardTab-" + userId + "="+escape(tabType);
	}
	
	function getTabCookie() {
		var cookies = document.cookie.match('patientDashboardTab-' + userId + '=(.*?)(;|$)');
		if (cookies) {
			return unescape(cookies[1]);
		}
		return null;
	}
	
	function changeTab(tabObj) {
		var c = "";
		if (!document.getElementById || !document.createTextNode) {return;}
		if (typeof tabObj == "string") {
			tabObj = document.getElementById(tabObj);
			c = tabObj;
		} else {			
			c = tabObj.id;
		}
		
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
			if(c=="patientTreatmentSummaryTab" && !summaryTabLoaded) {
				summaryTabLoaded = true;
				location.reload(true);
			}
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

<personalhr:portlet url="../module/personalhr/portlets/patientHeader.portlet" id="patientDashboardHeader" patientId="${patient.patientId}"/>

<openmrs:globalProperty var="enableFormEntryTab" key="FormEntry.enableDashboardTab" defaultValue="true"/>

<div id="patientTabs${patientVariation}">
	<ul>
		<%-- 
		<openmrs:hasPhrPrivilege privilege="PHR - View Overview Section">
		</openmrs:hasPhrPrivilege>
		--%>
		
	<personalhr:hasPrivilege privilege="View Relationships">	
		<li><a id="patientRelationshipsTab" href="#" onclick="return changeTab(this);" hidefocus="hidefocus"><spring:message code="personalhr.relationships"/></a></li>
		<li><a id="patientDemographicsTab" href="#" onclick="return changeTab(this);" hidefocus="hidefocus"><spring:message code="personalhr.demographics"/></a></li>
		<c:if test="${enableFormEntry}">
			<li><a id="formEntryTab" href="#" onclick="return changeTab(this);" hidefocus="hidefocus"><spring:message code="patientDashboard.formEntry"/></a></li>
		</c:if>
	</personalhr:hasPrivilege>

		<openmrs:extensionPoint pointId="org.openmrs.module.personalhr.patientDashboardTab" type="html">
			<personalhr:hasPrivilege privilege="${extension.requiredPrivilege}">
				<li>
					<a id="${extension.tabId}Tab" href="#" onclick="return changeTab(this);" hidefocus="hidefocus"><spring:message code="${extension.tabName}"/></a>
				</li>
		    </personalhr:hasPrivilege>
		</openmrs:extensionPoint>		
	</ul>
</div>

<div id="patientSections">
	<%-- 
	<openmrs:hasPhrPrivilege privilege="PHR - View Overview Section">
	</openmrs:hasPhrPrivilege>
	--%>
	<personalhr:hasPrivilege privilege="View Relationships">	
		<div id="patientRelationships" style="display:none;">
				<div class="tooltip">
				<spring:message code="personalhr.tooltip.patient.relationships"/>		
				</div>
				<iframe src ="${pageContext.request.contextPath}/phr/patientRelationshipsForm.form?patientId=${patient.patientId}" width="100%" height="500">
				Loading relationships ...
				</iframe>
			</div>						
		</div>
		
		<div id="patientDemographics" style="display:none;">
				<div class="tooltip">
				<spring:message code="personalhr.tooltip.patient.demographics"/>
				</div>
				<openmrs:portlet url="../module/personalhr/portlets/newPatientForm" patientId="${patient.patientId}" />
		</div>

	</personalhr:hasPrivilege>
		
	    <c:if test="${enableFormEntry}">
			<div id="formEntry" style="display:none;">		
				<openmrs:extensionPoint pointId="org.openmrs.patientDashboard.FormEntryTabHeader" type="html" parameters="patientId=${patient.patientId}" />
				<personalhr:portlet url="../module/personalhr/portlets/personFormEntry.portlet" id="formEntryPortlet" personId="${patient.personId}" parameters="showDecoration=true|showLastThreeEncounters=true|returnUrl=${pageContext.request.contextPath}/phr/patientDashboard.form"/>			
			</div>
		</c:if>
		
		<openmrs:extensionPoint pointId="org.openmrs.module.personalhr.patientDashboardTab" type="html">
			<%--openmrs:hasPrivilege privilege="${extension.requiredPrivilege}"--%>
				<div id="${extension.tabId}" style="display:none;">
					<c:choose>
						<c:when test="${extension.portletUrl == '' || extension.portletUrl == null}">
							portletId is null: '${extension.extensionId}'
						</c:when>
						<c:otherwise>
						
							<openmrs:extensionPoint pointId="org.openmrs.patientDashboard.${extension.tabId}TabHeader" type="html" parameters="patientId=${patient.patientId}" />
							<openmrs:portlet url="${extension.portletUrl}" id="${extension.tabId}" moduleId="${extension.moduleId}"/>
							
						</c:otherwise>
					</c:choose>
				</div>
			<%--/openmrs:hasPrivilege--%>
		</openmrs:extensionPoint>			
</div>

<%@ include file="/WEB-INF/view/module/personalhr/template/footer.jsp" %>
