<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/headerMinimal.jsp" %>
<openmrs:htmlInclude file="/scripts/easyAjax.js" />

<openmrs:htmlInclude file="/scripts/jquery/jquery.min.js" />
<openmrs:htmlInclude file="/scripts/jquery-ui/js/jquery-ui.custom.min.js" />
<openmrs:htmlInclude file="/scripts/jquery-ui/css/redmond/jquery-ui.custom.css" />
<openmrs:htmlInclude file="/scripts/jquery/dataTables/css/dataTables.css" />
<openmrs:htmlInclude file="/scripts/jquery/dataTables/js/jquery.dataTables.min.js" />
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
		$j('#relationDetailPopup').dialog({
			title: 'dynamic',
			autoOpen: false,
			draggable: false,
			resizable: false,
			width: '50%',
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

	function loadAddRelationshipPopup(title) {
		$j('#addNewRelationshipPopup')
			.dialog('option', 'title', title)
			.dialog('option', 'height', $j(window).height() - 50) 
			.dialog('open');
	}

	function loadRelationDetailPopup(title, personId, startDate, activateDate, expireDate) {
		$j('#relationDetailPopup')
			.dialog('option', 'title', title)
			.dialog('option', 'height', $j(window).height() - 50) 
			.dialog('open');
		$j('#startDate').text(startDate);
		$j('#activateDate').text(activateDate);
		$j('#expireDate').text(expireDate);		
	}	

	function onDelete(id, prompt) {
		if(confirm(prompt)) {		
		  $j('#deletedId').val("Delete "+id);
		  return true;
		} else {
			return false;
		}		
	}

	function onAdd() {
		$j('#commandClicked').val("Add");
	}

	function onChange() {
		$j('#saveChanges').attr("disabled", false);
	}	
		

	function confirmSubmit(formId,prompt) {
		if(confirm(prompt)) {
		  $j('#'+formId).submit();
		  return true;
		} else {
			return false;
		}
	}	
	
</script>

<%--
<openmrs:hasPhrPrivilege privilege="PHR - View Relationships Section">
</openmrs:hasPhrPrivilege>
<div id="patientRelationshipsBoxHeader" class="boxHeader${model.patientVariation}"><spring:message code="Relationship.relationships" /></div>
--%>

<div id="addNewRelationshipPopup">
  <form method="post" id="addRelationForm" onsubmit="return false;">
	<input type="submit" name="command" value="Add" onClick="onAdd(); return confirmSubmit('addRelationForm','An email notification will be sent to this person. Click OK to proceed, or Cancel to revise.');"/>
	<input type="submit" name="command" value="Cancel" onClick="$j('#addNewRelationshipPopup').dialog('close');"/>
	<input type="hidden" name="command" value="Unknown" id="commandClicked"/>
	
	<table cellspacing="0" cellpadding="2" id="patientRelationshipsTable">
	  <tbody>
	  <c:set var="token" value="${patient.newSharingToken}"/>
		  <tr>
		    <td>Person Name:</td>
		    <td>
			<spring:bind path="patient.newSharingToken.relatedPersonName">		    
				<input type="text" name="${status.expression}" value="${status.value}" size="35" />
				<c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>
		    </spring:bind>
		    <span style="color:red">*</span> (Required)
		    </td>
		  </tr>
		  <tr>
		    <td>Relation Type:</td>
			<td>
			<spring:bind path="patient.newSharingToken.relationType">
				<select name="${status.expression}"  >
					<c:forEach items="${patient.relationTypes}" var="relationType">
						<option value="${status.value}"
							<c:if test="${relationType.value == status.value}">selected="selected"</c:if>>${relationType.value}
						</option>
					</c:forEach>
		    	</select>
		    </spring:bind>
		    </td>
		  </tr>
		  <tr>
		    <td>Person Email:</td>
		    <td>
			<spring:bind path="patient.newSharingToken.relatedPersonEmail">		    
		    <input name="${status.expression}" type="text" value="${status.value}" />
		    </spring:bind>
		    <span style="color:red">*</span> (Required)
		    </td>
		  </tr>
		  <tr>
		    <td>Share:</td>
		    <td> 
			<spring:bind path="patient.newSharingToken.shareType">
				<select name="${status.expression}"  >
					<c:forEach items="${patient.sharingTypes}" var="sharingType">
						<option value="${status.value}"
							<c:if test="${sharingType.value == status.value}">selected="selected"</c:if>>${sharingType.value}
						</option>
					</c:forEach>
		    	</select>
		    </spring:bind>		    
		    <span style="color:red">*</span> (Required)
		    </td>
		  </tr>
	  </tbody>   
	</table>
  </form>
</div>

<div id="relationDetailPopup">
  <form method="post" id="relationDetailForm" onsubmit="return false;">
	<input type="submit" name="command" value="Cancel" onClick="$j('#relationDetailPopup').dialog('close');"/>
	
	<table cellspacing="0" cellpadding="2" id="patientRelationshipsTable">
	  <tbody>
		  <tr>
		    <td>Start date:</td>
		    <td>
		    <label id="startDate"></label>
		    </td>
		  </tr>
		  <tr>
		    <td>Activation date:</td>
		    <td>
		    <label id="activateDate"></label>
		    </td>
		  </tr>	  
		  <tr>
		    <td>Expiration date:</td>
		    <td>
		    <label id="expireDate"></label>
		    </td>
		  </tr>	  
	  </tbody>   
	</table>
  </form>
</div>


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

	<c:set var="addNewRelationshipUrl" value="${pageContext.request.contextPath}/phr/addNewRelationship.form?patientId=${patient.patientId}"/>
<form method="post" id="relationsForm">  
	<input type="button" name="command" value="Add New Relationship" onClick="loadAddRelationshipPopup('Add new relationship'); return false;" />
	<input type="submit" name="command" id="saveChanges" value="Save Changes" disabled="true"/>
	<input type="hidden" name="command" value="Unknown" id="deletedId"/>

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
		    <td><a href="#" onClick="loadRelationDetailPopup('View Detail','${token.relatedPerson.personId}','${token.startDate}','${token.activateDate}','${token.expireDate}'); return false;">${token.relatedPersonName}</a></td>
			<td>
			<spring:bind path="patient.sharingTokens[${status.index}].relationType">
				<select name="${status.expression}" onChange="onChange()">
					<c:forEach items="${patient.relationTypes}" var="relationType">
						<option value="${relationType.value}"
							<c:if test="${relationType.value == status.value}">selected="selected"</c:if>>${relationType.value}
						</option>
					</c:forEach>
		    	</select>
		    </spring:bind>
		    </td>
		    <td>
			<spring:bind path="patient.sharingTokens[${status.index}].relatedPersonEmail">		    
		    <input type="text" name="${status.expression}" value="${status.value}" id="token_email" onChange="onChange()"/>
		    </spring:bind>
		    </td>
		    <td> 
			<spring:bind path="patient.sharingTokens[${status.index}].shareType">
				<select name="${status.expression}" onChange="onChange()">
					<c:forEach items="${patient.sharingTypes}" var="sharingType">
						<option value="${sharingType.value}"
							<c:if test="${sharingType.value == status.value}">selected="selected"</c:if>>${sharingType.value}
						</option>
					</c:forEach>
		    	</select>
		    </spring:bind>		    
		    </td>
		    <td align="center">
 				<input type="image" src="${pageContext.request.contextPath}/images/delete.gif" name="command" value="Delete ${token.id}" onClick="onDelete('${token.id}', 'Do you really want to delete this relationship?');return true;"/>
		    </td>
		  </tr> 
 	  </c:forEach>  
	  </tbody>   
	</table>
 </form>	
</div>

<br/>
