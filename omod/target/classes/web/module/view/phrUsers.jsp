<%@ include file="/WEB-INF/view/module/personalhr/template/include.jsp" %>
<spring:message var="pageTitle" code="User.manage.titlebar" scope="page" />

<%@ include file="/WEB-INF/view/module/personalhr/template/header.jsp" %>

<form method="get">

<table>
	<tr>
		<td><spring:message code="User.find"/></td>
		<td><input type="text" name="name" value="<c:out value="${param.name}"/>" /></td>
	</tr>
	<tr>
		<td><spring:message code="Role.role"/></td>
		<td>
			<select name="role">
				<option></option>
				<openmrs:forEachRecord name="role">
					<c:if test="${record.role != 'Anonymous' && record.role != 'Authenticated'}">
						<option <c:if test="${param.role == record.role}">selected</c:if>><c:out value="${record.role}"/></option>
					</c:if>
				</openmrs:forEachRecord>
			</select>
		</td>
	</tr>
	<tr>
		<td><spring:message code="SearchResults.includeDisabled"/></td>
		<td>
			<input type="checkbox" name="includeDisabled" <c:if test="${param.includeDisabled == 'on'}">checked=checked</c:if>/>
		</td>
	</tr>
	<tr>
		<td></td>
		<td><input type="submit" name="action" value="<spring:message code="general.search"/>"/></td>
	</tr>
</table>

</form>

<br/>

<c:if test="${fn:length(users) == 0 && (param.name != None || param.role != None || param.includeDisabled != None)}">
	<spring:message code="User.noUsersFound"/>
</c:if>

<c:if test="${fn:length(users) > 0}">
<b class="boxHeader"><spring:message code="User.list.title" /></b>
<div class="box">
	<div dojoType="UserSearch" widgetId="uSearch" searchLabel='<spring:message code="User.find"/>' showIncludeRetired="true" showRoles="true"></div>
</div>
</c:if>

<br />
<br />

<script type="text/javascript">
  document.forms[0].elements[0].focus();
</script>

<%@ include file="/WEB-INF/view/module/personalhr/template/footer.jsp" %>