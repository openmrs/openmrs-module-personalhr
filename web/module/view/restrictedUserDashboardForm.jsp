<%@ include file="/WEB-INF/view/module/personalhr/template/include.jsp" %>
<%-- 
<openmrs:phrRequire privilege="PHR - View Patient" otherwise="/phr/login.htm" redirect="/module/personalhr/view/patientDashboard.form" />
extensionPoint
  org.openmrs.patientDashboard --> org.openmrs.module.personalhr.restrictedUserDashboard
patientRelationshipsTab
  patientRelationships --> restrictedUserRelationships
--%>

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

<div id="patientTabs">
	<ul>
		<%-- 
		<openmrs:hasPhrPrivilege privilege="PHR - View Overview Section">
		</openmrs:hasPhrPrivilege>
		--%>
		<li><a id="restrictedUserRelationshipsTab" href="#" onclick="return changeTab(this);" hidefocus="hidefocus"><spring:message code="personalhr.relationships"/></a></li>

		<openmrs:extensionPoint pointId="org.openmrs.module.personalhr.restrictedUserDashboardTab" type="html">
			<%-- %>openmrs:hasPrivilege privilege="${extension.requiredPrivilege}"--%>
				<li>
					<a id="${extension.tabId}Tab" href="#" onclick="return changeTab(this);" hidefocus="hidefocus"><spring:message code="${extension.tabName}"/></a>
				</li>
			<%--/openmrs:hasPrivilege--%>
		</openmrs:extensionPoint>		
	</ul>
</div>

<div id="patientSections">
	<%-- 
	<openmrs:hasPhrPrivilege privilege="PHR - View Overview Section">
	</openmrs:hasPhrPrivilege>
	--%>
		<div id="restrictedUserRelationships" style="display:none;">
			
			<personalhr:portlet url="../module/personalhr/portlets/restrictedUserRelationships.portlet" id="restrictedUserDashboardRelationships"/>
			
		</div>
		
		<openmrs:extensionPoint pointId="org.openmrs.module.personalhr.restrictedUserDashboardTab" type="html">
			<%--openmrs:hasPrivilege privilege="${extension.requiredPrivilege}"--%>
				<div id="${extension.tabId}" style="display:none;">
					<c:choose>
						<c:when test="${extension.portletUrl == '' || extension.portletUrl == null}">
							portletId is null: '${extension.extensionId}'
						</c:when>
						<c:otherwise>
						
							<openmrs:extensionPoint pointId="org.openmrs.module.personalhr.restrictedUserDashboard.${extension.tabId}TabHeader" type="html" parameters="patientId=${patient.patientId}" />
							<openmrs:portlet url="${extension.portletUrl}" id="${extension.tabId}" moduleId="${extension.moduleId}"/>
							
						</c:otherwise>
					</c:choose>
				</div>
			<%--/openmrs:hasPrivilege--%>
		</openmrs:extensionPoint>			
</div>

<%@ include file="/WEB-INF/template/footer.jsp" %>