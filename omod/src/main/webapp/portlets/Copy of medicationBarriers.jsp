<%@ include file="/WEB-INF/view/module/personalhr/template/include.jsp" %>
<personalhr:require privilege="PHR Authenticated" otherwise="/phr/login.htm" redirect="/module/medadherence/portlets/medicationBarriers.htm" />

<openmrs:htmlInclude file="/scripts/easyAjax.js" />

<openmrs:htmlInclude file="/scripts/jquery/dataTables/css/dataTables.css" />
<openmrs:htmlInclude file="/scripts/jquery/dataTables/js/jquery.dataTables.min.js" />

<openmrs:globalProperty key="dashboard.encounters.showViewLink" var="showViewLink" defaultValue="true"/>
<openmrs:globalProperty key="dashboard.encounters.showEditLink" var="showEditLink" defaultValue="true"/>

<c:set var="foundSummary" value="false"/>
<div class="tooltipPhr">
<spring:message code="medadherence.tooltip.barriers"/>
</div>

<div class="boxHeader"><spring:message code="medadherence.barriers.form.list"/></div>
<div class="box">

				<div>
					<table cellspacing="0" cellpadding="2" id="patientEncountersTable">
						<thead>
							<tr>
								<th class="hidden"> hidden Encounter id </th>
								<th class="encounterEdit" align="center"><c:if test="${showEditLink == 'true'}">
									<spring:message code="general.edit"/>
								</c:if></th>
								<th class="encounterView" align="center"><c:if test="${showViewLink == 'true'}">
								 	<spring:message code="general.view"/>
								</c:if></th>
								<th class="encounterDatetimeHeader"> <spring:message code="Encounter.datetime"/> </th>
								<th class="hidden"> hidden Encounter.datetime </th>
								<th class="encounterTypeHeader"> <spring:message code="Encounter.type"/>     </th>
								<th class="encounterProviderHeader"> <spring:message code="Encounter.provider"/> </th>
								<th class="encounterFormHeader"> <spring:message code="Encounter.form"/>     </th>
								<th class="encounterLocationHeader"> <spring:message code="Encounter.location"/> </th>
								<th class="encounterEntererHeader"> <spring:message code="Encounter.enterer"/>  </th>
							</tr>
						</thead>
						<tbody>
							<openmrs:forEachEncounter encounters="${model.patientEncounters}" sortBy="encounterDatetime" descending="true" var="enc" num="${model.num}">
								<tr class='${status.index % 2 == 0 ? "evenRow" : "oddRow"}'>
									<td class="hidden">
										<%--  this column contains the encounter id and will be used for sorting in the dataTable's encounter edit column --%>
										${enc.encounterId}
									</td>
									<td class="encounterEdit" align="center">
										<c:if test="${showEditLink == 'true'}">
											<openmrs:hasPrivilege privilege="Edit Encounters">
												<c:set var="editUrl" value="${pageContext.request.contextPath}/admin/encounters/encounter.form?encounterId=${enc.encounterId}"/>
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
											<c:set var="viewEncounterUrl" value="${pageContext.request.contextPath}/admin/encounters/encounterDisplay.list?encounterId=${enc.encounterId}"/>
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
									<td class="hidden">
									<%--  this column contains milliseconds and will be used for sorting in the dataTable's encounterDatetime column --%>
										<openmrs:formatDate date="${enc.encounterDatetime}" type="milliseconds" />
									</td>
					 				<td class="encounterType"><openmrs:format encounterType="${enc.encounterType}"/></td>
					 				<td class="encounterProvider"><openmrs:format person="${enc.provider}"/></td>
					 				<td class="encounterForm">${enc.form.name}</td>
					 				<td class="encounterLocation"><openmrs:format location="${enc.location}"/></td>
					 				<td class="encounterEnterer">${enc.creator.personName}</td>
								</tr>
							</openmrs:forEachEncounter>
						</tbody>
					</table>
				</div>

	<div id="medicationBarriersPortlet">
			<c:forEach items='${openmrs:sort(model.patientEncounters, "encounterDatetime", true)}' var="enc" varStatus="encStatus">
				<c:if test="${enc.encounterType.name == 'Medication Barriers Encounter' && foundSummary=='false'}">
					<c:set var="foundSummary" value="true"/>			
					<iframe id="medicationBarriersFrame" src ="${pageContext.request.contextPath}/module/medadherence/htmlFormEntryForm.form?encounterId=${enc.encounterId}&mode=EDIT&inTab=true" width="100%" height="1500">
					  Loading medication barriers ...
					</iframe>
					<%--
					<personalhr:portlet url="../module/medadherence/portlets/htmlFormEntryForm.portlet?encounterId=${enc.encounterId}&mode=EDIT&inTab=true" parameters="encounterId=${enc.encounterId}&mode=EDIT&inTab=true" />
					--%>
		        </c:if>					
			</c:forEach>
	</div>
</div>

<c:if test="${foundSummary=='false'}">
	<iframe id="medicationBarriersCreationFrame" src ="${pageContext.request.contextPath}/module/medadherence/htmlFormEntryForm.form?formId=1&personId=${model.personId}&patientId=${model.patientId}&mode=EDIT&inTab=true" width="100%" height="1500">
	  Identify Medication Barriers
	</iframe>
</c:if>			

