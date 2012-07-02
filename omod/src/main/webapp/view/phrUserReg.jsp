<%@ include file="/WEB-INF/view/module/personalhr/template/include.jsp" %>
<%@ include file="/WEB-INF/view/module/personalhr/template/header.jsp" %>


<form method="post" style="padding:15px;">
	<c:if test="${secretQuestion == null}">
		<spring:message code="forgotPassword.help"/><br/>
		<table>
			<tr>
				<td>Enter unique-id:</td>
				<td align="left"><input type="text" name="user_id" value="" id="user_id" size="25" maxlength="50"/></td>
			</tr>
			<tr>
				<td></td>
				<td align="left"><input type="submit" value='Show Secret Question' /></td>
			</tr>
		</table>
	</c:if>
	<c:if test="${secretQuestion != null}">
		<input type="hidden" name="uname" value="${uname}"/>
		<table>
			<tr>
				<td colspan="2">
					<i><spring:message code="User.secretQuestion.prompt"/></i><br/>
					<b>${secretQuestion}</b>
				</td>
			</tr>
			<tr>
				<td align="left"><spring:message code="general.answer"/>:</td>
				<td align="left"><input type="password" name="secretAnswer" value="" id="secretAnswer" size="25" autocomplete="off"></td>
			</tr>
			<tr>
				<td></td>
				<td><input type="submit" value="Set Username and Password" /></td>
			</tr>
		</table>
	</c:if>
	<br/>
		
</form>	

<script type="text/javascript">
 document.getElementById('user_id').focus();
</script>

<openmrs:extensionPoint pointId="org.openmrs.forgotPassword" type="html" />
		
<%@ include file="/WEB-INF/view/module/personalhr/template/footer.jsp" %>