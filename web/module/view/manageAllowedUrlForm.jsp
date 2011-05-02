<%@ include file="/WEB-INF/template/include.jsp" %>
<openmrs:require privilege="Manage Modules" otherwise="/login.htm" redirect="/manageAllowedUrl.form" />

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


	function onAddUrl(){
		if ($("#addUrlDetailDiv").is(":hidden")) {
			$("#addUrlDetailDiv").slideDown("fast", function(){
				$("#addUrl").toggle();
				$("#cancelAddUrl").toggle();
				$("#saveAddUrl").toggle();
			});
		}else{
			$("#addUrlDetailDiv").slideUp("fast", function(){
				$("#addUrl").toggle();
				$("#cancelAddUrl").toggle();
				$("#saveAddUrl").toggle();
			});
		}
	}

	function saveAddedUrl(patientId){
		var url = $j('#newAllowedUrl').val();
		var privilege = $j('#newPrivilege').val();
		var description = $j('#newDescription').val();

		DWRPersonalhrService.addAllowedUrl(url, privilege, description);

		onAddUrl(); 

		$j('#urlForm').submit();		
	}	
	
	function onChange(urlId) {
		$j('#saveChanges'+urlId).removeAttr("disabled");
	}	
	
	function onUpdate(index, urlId) {
		$j('#urlIdField').val(index);
		//$j('#saveChanges'+urlId).attr("disabled", 'disabled');
		return true;
	}

	function onDelete(index, urlId) {
		if(confirm("Do you really want to delete this record?")) {
  		   $j('#urlIdField').val(index);
		   return true;
		} else {
	  	   $j('#urlIdField').val(-1);
		   return false;
		}
	}
			
</script>


<div id="allowed-url-div"  >
<div class="boxHeader">
	<spring:message code="personalhr.title.manage.allowed.url"/>	
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

<form method="post" id="urlForm">  
	<input type="hidden" name="urlIdField" id="urlIdField" />
	<table border="1" width="99%">
		  <thead>
			  <tr>
			    <th align="center">Allowed URL</th>
			    <th align="center">Required Privilege</th>
			    <th align="center">Description</th>
			    <th align="center">Action</th>
			  </tr>
		  </thead>
		  <tbody>
		  <c:forEach var="url" items="${phrSecurity.allowedUrlList}" varStatus="status">	
			  <tr>
				<td width="40%">
					<spring:bind path="phrSecurity.allowedUrlList[${status.index}].allowedUrl">
						    <input type="text" style="width:99%" name="${status.expression}" value="${status.value}" id="allowedUrl${url.id}" onChange="onChange(${url.id})"/>
					</spring:bind>
			    </td>
				<td width="35%">
					<spring:bind path="phrSecurity.allowedUrlList[${status.index}].privilege">
						    <input type="text" style="width:99%" name="${status.expression}" value="${status.value}" id="privilege${url.id}" onChange="onChange(${url.id})"/>
					</spring:bind>
			    </td>
				<td width="10%">
					<spring:bind path="phrSecurity.allowedUrlList[${status.index}].description">
						    <input type="text" style="width:99%" name="${status.expression}" value="${status.value}" id="description${url.id}" onChange="onChange(${url.id})"/>
					</spring:bind>
			    </td>
				<td width="15%" align="center">
					<input type="submit" value="<spring:message code="general.save" />" name="command" id="saveChanges${url.id}" disabled="disabled" onClick="onUpdate(${status.index}, ${url.id});return true;" />
					<input type="submit" value="<spring:message code="general.delete" />" name="command" id="deleteChanges${url.id}" onClick="onDelete(${status.index}, ${url.id});return true;"/>
				</td>
			  </tr> 
	 	  </c:forEach>  
		  </tbody>   
	</table>
</form>

<div id="addUrlDetailDiv">
	<table border="1" width="99%">
	  <tbody>
		  <tr>
			<td width="40%">
				<input type="text" style="width:99%" name="${status.expression}" value="${status.value}" id="newAllowedUrl" />
		    </td>
			<td width="35%">
				<input type="text" style="width:99%" name="${status.expression}" value="${status.value}" id="newPrivilege"/>
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
<div id="addUrlDiv">
	<button id="addUrl" onClick="onAddUrl();return false;">Add Allowed URL</button>
	<button id="saveAddUrl" onClick="saveAddedUrl();return false;">Save</button>
	<button id="cancelAddUrl" onClick="onAddUrl();return false;">Cancel</button>
</div>	
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>
