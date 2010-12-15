<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/headerMinimal.jsp" %>
<openmrs:htmlInclude file="/scripts/easyAjax.js" />

<openmrs:htmlInclude file="/scripts/jquery/jquery.min.js" />
<openmrs:htmlInclude file="/scripts/jquery-ui/js/jquery-ui.custom.min.js" />
<openmrs:htmlInclude file="/scripts/jquery-ui/css/redmond/jquery-ui.custom.css" />
<openmrs:htmlInclude file="/scripts/jquery/dataTables/css/dataTables.css" />
<openmrs:htmlInclude file="/scripts/jquery/dataTables/js/jquery.dataTables.min.js" />

<%--
<openmrs:hasPhrPrivilege privilege="PHR - View Relationships Section">
</openmrs:hasPhrPrivilege>
<div id="patientRelationshipsBoxHeader" class="boxHeader${model.patientVariation}"><spring:message code="Relationship.relationships" /></div>
--%>

<div id="addNewRelationshipPopup">
	<iframe id="addNewRelationshipPopupIframe" width="90%" height="100%" marginWidth="0" marginHeight="0" frameBorder="0" scrolling="auto"></iframe>
</div>

<script type="text/javascript">
	$j(document).ready(function() {
		$j('#addNewRelationshipPopup').dialog({
				title: 'dynamic',
				autoOpen: false,
				draggable: false,
				resizable: false,
				width: '95%',
				modal: true,
				open: function(a, b) {  }
		});
	});

	function loadUrlIntoRelationshipPopup(title, urlToLoad) {
		$j("#addNewRelationshipPopupIframe").attr("src", urlToLoad);
		$j('#addNewRelationshipPopup')
			.dialog('option', 'title', title)
			.dialog('option', 'height', $j(window).height() - 50) 
			.dialog('open');
	}
</script>

<div id="patientRelationshipsBox" class="box${model.patientVariation}">
<spring:hasBindErrors name="patient">
	<spring:message code="fix.error"/>
	<div class="error">
		<c:forEach items="${errors.allErrors}" var="error">
			<spring:message code="${error.code}" text="${error.code}"/><br/><!-- ${error} -->
		</c:forEach>
	</div>
	<br />
</spring:hasBindErrors>

  <form method="post">
	<c:set var="addNewRelationshipUrl" value="${pageContext.request.contextPath}/phr/addNewRelationship.form?patientId=${patient.patientId}"/>
  
	<input type="button" name="add" value="Add New Relationship" onClick="loadUrlIntoRelationshipPopup('Add new relationship', '${addNewRelationshipUrl}'); return false;" />
	<input type="submit" name="save" value="Save Changes"/>
	
	<table cellspacing="0" cellpadding="2" id="patientRelationshipsTable">
	  <thead>
	  <tr>
	    <th>Name</th>
	    <th>Relationship</th>
	    <th>Email</th>
	    <th>Share Type</th>
	    <th>Delete</th>
	  </tr>
	  </thead>
	  <tbody>
	  <c:forEach var="token" items="${patient.sharingTokens}" varStatus="status">
		  <tr>
		    <td><a href="personDashboard.form?personId=${token.relatedPerson.personId}">${token.relatedPersonName}</a></td>
			<td>
			<spring:bind path="patient.sharingTokens[${status.index}].relationType">
				<select name="relation_type" >
					<c:forEach items="${patient.relationTypes}" var="relationType">
						<option value="${relationType.value}"
							<c:if test="${relationType.value == token.relationType}">selected="selected"</c:if>>${relationType.value}
						</option>
					</c:forEach>
		    	</select>
		    </spring:bind>
		    </td>
		    <td>
			<spring:bind path="patient.sharingTokens[${status.index}].relationType">		    
		    <input type="text" value="${token.relatedPersonEmail}" id="token_email"/>
		    </spring:bind>
		    </td>
		    <td> 
			<spring:bind path="patient.sharingTokens[${status.index}].shareType">
				<select name="share_type" >
					<c:forEach items="${patient.sharingTypes}" var="sharingType">
						<option value="${sharingType.value}"
							<c:if test="${sharingType.value == token.shareType}">selected="selected"</c:if>>${sharingType.value}
						</option>
					</c:forEach>
		    	</select>
		    </spring:bind>		    
		    </td>
		    <td>
 				<input type="image" src="${pageContext.request.contextPath}/images/delete.gif" name="delete" value="Delete Relationship"/>
		    </td>
		  </tr> 
	  </c:forEach>  
	  </tbody>   
	</table>
  </form>
</div>

<br/>
