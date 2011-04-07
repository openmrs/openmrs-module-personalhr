<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp"%>
<openmrs:htmlInclude file="/moduleResources/personalhr/personalhr.css" />
<openmrs:htmlInclude file="/dwr/interface/DWRPersonalhrService.js" />
	<script type="text/javascript">
		$j = jQuery.noConflict();
	</script>
	<openmrs:htmlInclude file="/scripts/calendar/calendar.js" />
	<openmrs:htmlInclude file="/dwr/engine.js" />
	<openmrs:htmlInclude file="/dwr/util.js" />
	<openmrs:htmlInclude file="/moduleResources/personalhr/jquery-1.4.4.min.js" />
	<openmrs:htmlInclude file="/moduleResources/personalhr/jquery-ui-1.8.9.custom.css" />
	<openmrs:htmlInclude file="/moduleResources/personalhr/jquery-ui-1.8.9.custom.min.js" />

<script type="text/javascript">
	$j(document).ready(function(){
		//$j('input[type="button"]').attr('disabled','disabled'); 				
    });


	function onAddPrivilege(){
		if ($("#addPrivilegeDetailDiv").is(":hidden")) {
			$("#addPrivilegeDetailDiv").slideDown("fast", function(){
				$("#addPrivilege").toggle();
				$("#cancelAddPrivilege").toggle();
				$("#saveAddPrivilege").toggle();
			});
		}else{
			$("#addPrivilegeDetailDiv").slideUp("fast", function(){
				$("#addPrivilege").toggle();
				$("#cancelAddPrivilege").toggle();
				$("#saveAddPrivilege").toggle();
			});
		}
	}

	function saveAddedPrivilege(patientId){
		var role = $j('#newRole').val();
		var privilege = $j('#newPrivilege').val();
		var description = $j('#newDescription').val();

		DWRPersonalhrService.addPhrPrivilege(privilege, role, description);

		onAddPrivilege(); 

		$j('#privilegeForm').submit();		
	}	
	
	function onChange(privilegeId) {
		$j('#saveChanges'+privilegeId).removeAttr("disabled");
	}	
	
	function onUpdate(index, privilegeId) {
		$j('#privilegeIdField').val(index);
		//$j('#saveChanges'+privilegeId).attr("disabled", 'disabled');
		return true;
	}

	function onDelete(index, privilegeId) {
		if(confirm("Do you really want to delete this record?")) {
  		   $j('#privilegeIdField').val(index);
		   return true;
		} else {
	  	   $j('#privilegeIdField').val(-1);
		   return false;
		}
	}
			
</script>


<div id="allowed-privilege-div"  >
<div class="boxHeader">
	<spring:message code="personalhr.title.manage.phr.privilege"/>	
</div>
<spring:hasBindErrors name="patient">
	<spring:message code="fix.error"/>
	<div class="error">
		<c:forEach items="${errors.allErrors}" var="error">
			<spring:message code="${error.code}" text="${error.code}"/><br/><!-- ${error} -->
		</c:forEach>
	</div>
	<br />
</spring:hasBindErrors>

<form method="post" id="privilegeForm">  
	<input type="hidden" name="privilegeIdField" id="privilegeIdField" />
	<table border="1" width="99%">
		  <thead>
			  <tr>
			    <th align="center">PHR Privilege<privilege>
			    <th align="center">Required Role</th>
			    <th align="center">Description</th>
			    <th align="center">Action</th>
			  </tr>
		  </thead>
		  <tbody>
		  <c:forEach var="privilege" items="${phrSecurity.phrPrivilegeList}" varStatus="status">	
			  <tr>
				<td width="40%">
					<spring:bind path="phrSecurity.phrPrivilegeList[${status.index}].privilege">
						    <input type="text" style="width:99%" name="${status.expression}" value="${status.value}" id="privilege${privilege.id}" onChange="onChange(${privilege.id})"/>
					</spring:bind>
			    </td>
				<td width="35%">
					<spring:bind path="phrSecurity.phrPrivilegeList[${status.index}].requiredRole">
						    <input type="text" style="width:99%" name="${status.expression}" value="${status.value}" id="role${privilege.id}" onChange="onChange(${privilege.id})"/>
					</spring:bind>
			    </td>
				<td width="10%">
					<spring:bind path="phrSecurity.phrPrivilegeList[${status.index}].description">
						    <input type="text" style="width:99%" name="${status.expression}" value="${status.value}" id="description${privilege.id}" onChange="onChange(${privilege.id})"/>
					</spring:bind>
			    </td>
				<td width="15%" align="center">
					<input type="submit" value="<spring:message code="general.save" />" name="command" id="saveChanges${privilege.id}" disabled="disabled" onClick="onUpdate(${status.index}, ${privilege.id});return true;" />
					<input type="submit" value="<spring:message code="general.delete" />" name="command" id="deleteChanges${privilege.id}" onClick="onDelete(${status.index}, ${privilege.id});return true;"/>
				</td>
			  </tr> 
	 	  </c:forEach>  
		  </tbody>   
	</table>
</form>

<div id="addPrivilegeDetailDiv">
	<table border="1" width="99%">
	  <tbody>
		  <tr>
			<td width="40%">
				<input type="text" style="width:99%" name="${status.expression}" value="${status.value}" id="newPrivilege" />
		    </td>
			<td width="35%">
				<input type="text" style="width:99%" name="${status.expression}" value="${status.value}" id="newRole"/>
		    </td>
			<td width="10%">
				<input type="text" style="width:99%" name="${status.expression}" value="${status.value}" id="newDescription"/>
		    </td>
		    <td>
		    </td>
		  </tr> 
	  </tbody>   
	</table>
</div>
<div id="addPrivilegeDiv">
	<button id="addPrivilege" onClick="onAddPrivilege();return false;">Add PHR Privilege</button>
	<button id="saveAddPrivilege" onClick="saveAddedPrivilege();return false;">Save</button>
	<button id="cancelAddPrivilege" onClick="onAddPrivilege();return false;">Cancel</button>
</div>	
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>
