<%@ include file="/WEB-INF/template/include.jsp" %>

<%--
<openmrs:hasPhrPrivilege privilege="PHR - View Relationships Section">
</openmrs:hasPhrPrivilege>
<div id="patientRelationshipsBoxHeader" class="boxHeader${model.patientVariation}"><spring:message code="Relationship.relationships" /></div>
--%>
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
	<input type="submit" name="add" value="Add" onClick="confirm("An email notification will be sent to this person. Click OK to proceed, or Cancel to revise.");"/>
	<input type="submit" name="cancel" value="Cancel"/>
	
	<table cellspacing="0" cellpadding="2" id="patientRelationshipsTable">
	  <tbody>
	  <c:set var="token" value="${patient.newSharingToken}"/>
		  <tr>
		    <td>Person Name:</td>
		    <td>
			<spring:bind path="patient.newSharingToken.relatedPersonName">		    
		    <input type="text" value="${token.relatedPersonName}"/>
		    </spring:bind>
		    </td>
		  </tr>
		  <tr>
		    <td>Relation Type:</td>
			<td>
			<spring:bind path="patient.newSharingToken.relationType">
				<select name="relation_type" >
					<c:forEach items="${patient.relationTypes}" var="relationType">
						<option value="${relationType.value}"
							<c:if test="${relationType.value == token.relationType}">selected="selected"</c:if>>${relationType.value}
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
		    <input type="text" value="${token.relatedPersonEmail}" />
		    </spring:bind>
		    </td>
		  </tr>
		  <tr>
		    <td>Share:</td>
		    <td> 
			<spring:bind path="patient.newSharingToken.shareType">
				<select name="share_type" >
					<c:forEach items="${patient.sharingTypes}" var="sharingType">
						<option value="${sharingType.value}"
							<c:if test="${sharingType.value == token.shareType}">selected="selected"</c:if>>${sharingType.value}
						</option>
					</c:forEach>
		    	</select>
		    </spring:bind>		    
		    </td>
		  </tr>
	  </tbody>   
	</table>
  </form>

<br/>
