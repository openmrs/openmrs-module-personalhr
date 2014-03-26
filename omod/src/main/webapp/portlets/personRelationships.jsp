<%@ include file="/WEB-INF/template/include.jsp" %>

<openmrs:htmlInclude file="/scripts/easyAjax.js" />
<openmrs:htmlInclude file="/dwr/interface/DWRRelationshipService.js" />
<openmrs:htmlInclude file="/dwr/util.js" />
<openmrs:htmlInclude file="/scripts/dojoConfig.js" />
<openmrs:htmlInclude file="/scripts/dojo/dojo.js" />

<div id="personRelationship-div"  >

  <form method="post">
	<a id="addRelationshipLink" href="javascript:showDiv('addRelationship'); hideDiv('addRelationshipLink');"><spring:message code="Relationship.add"/></a>
	
	<table style="margin: 0px 0px 1em 2em;">
	  <tr>
	    <th>Name</th>
	    <th>Relationship</th>
	    <th>Email</th>
	    <th>Share Type</th>
	    <th></th>
	  </tr>
	  <c:forEach var="token" items="${model.phrSharingTokens}">
		  <tr>
		    <td><a href="patientDashboard.form?patientId=${token.patient.patientId}">${token.patient.personName.fullName}</a></td>
		    <td>${token.relationType}</td>
		    <td><input type="text" value="${token.relatedPersonEmail}" id="token_email"/></td>
		    <td> 
				<select name="share_type" size="3" multiple="true">
                  <c:forEach items="${model.phrSharingTypes}" var="sharingType">
			        <option value="$sharingType" <c:if test="${token.shareType==sharingType}">selected="true"</c:if>>
			        $sharingType
			        </option>
			      </c:forEach>
		    	</select>		    
		    </td>
		    <td><a href="patientDashboard.form?patientId=${token.patient.patientId}">Delete</a></td>
		  </tr> 
	  </c:forEach>     
	</table>
  </form>

</div>

