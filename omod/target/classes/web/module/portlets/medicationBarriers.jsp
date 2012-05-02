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
	<iframe id="medicationBarriersCreationFrame" src ="${pageContext.request.contextPath}/module/medadherence/htmlFormEntryForm.form?formId=1&personId=${model.personId}&patientId=${model.patientId}&mode=EDIT&inTab=true" width="100%" height="800">
	  Identify Medication Barriers
	</iframe>
</div>	

<div class="boxHeader"><spring:message code="medadherence.barriers.form.list"/></div>
<div class="box">

	<div>
		<table cellspacing="0" cellpadding="2" id="patientEncountersTable">
			<thead>
				<tr>
					<th class="encounterEdit" align="center"><c:if test="${showEditLink == 'true'}">
						<spring:message code="general.edit"/>
					</c:if></th>
					<th class="encounterView" align="center"><c:if test="${showViewLink == 'true'}">
					 	<spring:message code="general.view"/>
					</c:if></th>
					<th class="encounterDatetimeHeader"> <spring:message code="medadherence.barriers.form.datetime"/> </th>
				</tr>
			</thead>
			<tbody>
				<openmrs:forEachEncounter encounters="${model.patientEncounters}" sortBy="encounterDatetime" descending="true" var="enc" num="${model.num}">
					<c:if test="${enc.encounterType.name == 'Medication Barriers Encounter'}">
						<tr class='${status.index % 2 == 0 ? "evenRow" : "oddRow"}'>
							<td class="encounterEdit" align="center">
								<c:if test="${showEditLink == 'true'}">
									<openmrs:hasPrivilege privilege="Edit Encounters">
										<c:set var="editUrl" value="${pageContext.request.contextPath}/module/medadherence/htmlFormEntryForm.form?encounterId=${enc.encounterId}&mode=EDIT&inTab=false"/>
										<c:if test="${ model.formToEditUrlMap[enc.form] != null }">
											<c:url var="editUrl" value="${model.formToEditUrlMap[enc.form]}">
												<c:param name="encounterId" value="${enc.encounterId}"/>
											</c:url>
										</c:if>
										<a href="${editUrl}">
											<img src="${pageContext.request.contextPath}/images/edit.gif" title="<spring:message code="general.edit"/>" border="0" />
										</a>
									</openmrs:hasPrivilege>
								</c:if>
							</td>
							<td class="encounterView" align="center">
								<c:if test="${showViewLink}">
									<c:set var="viewEncounterUrl" value="${pageContext.request.contextPath}/module/medadherence/htmlFormEntryForm.form?encounterId=${enc.encounterId}&mode=VIEW&inTab=true"/>
									<c:if test="${ model.formToViewUrlMap[enc.form] != null }">
										<c:url var="viewEncounterUrl" value="${model.formToViewUrlMap[enc.form]}">
											<c:param name="encounterId" value="${enc.encounterId}"/>
											<c:param name="inPopup" value="true"/>
										</c:url>
									</c:if>
									<a href="javascript:void(0)" onClick="loadUrlIntoEncounterPopup('<openmrs:format encounter="${enc}" javaScriptEscape="true"/>', '${viewEncounterUrl}'); return false;">
										<img src="${pageContext.request.contextPath}/images/file.gif" title="<spring:message code="general.view"/>" border="0" />
									</a>
								</c:if>
							</td>
							<td class="encounterDatetime">
								<openmrs:formatDate date="${enc.encounterDatetime}" type="small" />
							</td>
						</tr>
					</c:if>					
				</openmrs:forEachEncounter>
			</tbody>
		</table>
	</div>
</div>

	

