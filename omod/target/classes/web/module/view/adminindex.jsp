<%@ include file="/WEB-INF/view/module/personalhr/template/include.jsp" %>


<%@ include file="/WEB-INF/view/module/personalhr/template/header.jsp" %>


<style>
	.adminMenuList #menu li {
		display: list-item;
		border-left-width: 0px;
		
	}
	.adminMenuList #menu li.first {
		display: none;
	}
	.adminMenuList #menu {
		list-style: none;
		margin-left: 10px;
		margin-top: 0;
	}
	h4 {
		margin-bottom: 0;
	}
</style>

<h2><spring:message code="admin.title"/></h2>

<table border="0" width="93%">
	<tbody>
	<tr>
	
		<td valign="top" width="30%">
		
		<openmrs:hasPrivilege privilege="View Orders,Manage Order Types">
				<div class="adminMenuList">
					<h4><spring:message code="Order.header"/></h4>
						<%@ include file="orders/orderslocalHeader.jsp" %>
				</div>
		</openmrs:hasPrivilege>
		
		<openmrs:hasPrivilege privilege="View Encounters,Manage Encounter Types">
				<div class="adminMenuList">
					<h4><spring:message code="Encounter.header"/></h4>
						<%@ include file="encounters/localHeader.jsp" %>
				</div>
			</openmrs:hasPrivilege>
		
		</td>
	</tr>
	</tbody>
	
</table>
		
<%@ include file="/WEB-INF/view/module/personalhr/template/footer.jsp" %> 