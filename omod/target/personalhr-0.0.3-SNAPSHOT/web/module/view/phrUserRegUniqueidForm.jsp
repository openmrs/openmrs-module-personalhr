<%@ include file="/WEB-INF/view/module/personalhr/template/include.jsp" %>
<%@ include file="/WEB-INF/view/module/personalhr/template/header.jsp" %>

<h2>First Time User</h2>

<form method="post" style="padding:15px;">

<%int imgno = (int) (Math.random() * 10); %>
<input type="hidden" name="ptr" value="<%=imgno%>" id="ptr">

	<c:if test="${secretQuestion == null}">
		Enter valid Unique-id<br/>
		<table>
			<tr>
				<td>Unique-id:</td>
				<td align="left"><input type="text" name="uid" value="" id="uniqueid" size="25" maxlength="50"/>
					<a href="${pageContext.request.contextPath}/phr/applyForID.form">
						Apply for unique-id
					</a>
				</td>
			</tr>

			<tr>
				<td>CAPTCHA:</td>
				<td><img name="imgid" id="imgid" src="${pageContext.request.contextPath}/moduleResources/personalhr/images/<%=imgno%>.jpg"/></td>
			</tr>
			<tr>	
				<td/>
				<td align="left"><input type="text" name="captcha" value="" id="captcha" size="25" maxlength="50"/></td>
			</tr>
			<tr>
				<td></td>
				<td align="left"><input type="submit" value='Show Secret Question' /></td>
			</tr>
		</table>
	</c:if>
	<c:if test="${secretQuestion != null}">
		<input type="hidden" name="uid" value="${uid}"/>
		<table>
			<tr>
				<td colspan="2">
					<i>Answer the following question to continue</i><br/>
					<b>${secretQuestion}</b>
				</td>
			</tr>
			<tr>
				<td align="left"><spring:message code="general.answer"/>:</td>
				<td align="left"><input type="password" name="secretAnswer" value="" id="secretAnswer" size="25" autocomplete="off"></td>
			</tr>
			<tr>
				<td></td>
				<td><input type="submit" value="<spring:message code="Set login details"/>" /></td>
			</tr>
		</table>
	</c:if>
	<br/>
		
</form>	

<script type="text/javascript">
 document.getElementById('uniqueid').focus();
</script>

<openmrs:extensionPoint pointId="org.openmrs.forgotPassword" type="html" />
		
<%@ include file="/WEB-INF/view/module/personalhr/template/footer.jsp" %>