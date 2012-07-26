<%@ include file="/WEB-INF/view/module/personalhr/template/include.jsp" %>
<%@ include file="/WEB-INF/view/module/personalhr/template/header.jsp" %>

<h2><spring:message code="EncounterType.manage.title"/></h2>	

<a href="phrEncounterType.form"><spring:message code="EncounterType.add"/></a> 

<openmrs:extensionPoint pointId="org.openmrs.admin.encounters.encounterTypeList.afterAdd" type="html" />

<br />
<br />

<b class="boxHeader"><spring:message code="EncounterType.list.title"/></b>
<form method="post" class="box">
	<table>
		<tr>
			<th> <spring:message code="general.name" /> </th>
			<th> <spring:message code="general.description" /> </th>
		</tr>
		<c:forEach var="encounterType" items="${encounterTypeList}">
			<tr>
				<td valign="top">
					<a href="phrEncounterType.form?encounterTypeId=${encounterType.encounterTypeId}">
						<c:choose>
							<c:when test="${encounterType.retired == true}">
								<del>${encounterType.name}</del>
							</c:when>
							<c:otherwise>
								${encounterType.name}
							</c:otherwise>
						</c:choose>
					</a>
				</td>
				<td valign="top">${encounterType.description}</td>
			</tr>
		</c:forEach>
	</table>
	<openmrs:extensionPoint pointId="org.openmrs.admin.encounters.encounterTypeList.inForm" type="html" />
	
</form>

<openmrs:extensionPoint pointId="org.openmrs.admin.encounters.encounterTypeList.footer" type="html" />

<%@ include file="/WEB-INF/view/module/personalhr/template/footer.jsp" %>