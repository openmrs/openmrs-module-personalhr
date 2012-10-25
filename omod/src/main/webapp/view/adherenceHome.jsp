<%@ include file="/WEB-INF/view/module/personalhr/template/include.jsp" %>

<spring:message var="pageTitle" code="index.title" scope="page"/>
<%@ include file="/WEB-INF/view/module/personalhr/template/header.jsp" %>
<openmrs:htmlInclude file="/moduleResources/medadherence/adherence.css" />

<c:set var="anyExtension" value="false" />
<openmrs:authentication>
	<c:if test="${authenticatedUser != null}">
		<openmrs:extensionPoint pointId="org.openmrs.navigation.homepage" type="html" varStatus="status">
			<c:set var="anyExtension" value="true" />
			<openmrs:portlet url="${extension.portletUrl}" parameters="${extension.portletParameters}" moduleId="${extension.moduleId}" />
		</openmrs:extensionPoint>
	</c:if>
</openmrs:authentication>

<c:if test="${not anyExtension}">
<center>
<div class="bigger_font">

<img src="${pageContext.request.contextPath}<spring:theme code="image.logo.large"/>" alt='<spring:message code="medadherence.title"/>' title='<spring:message code="medadherence.title"/>'/>

		<br/><br/><br/>
		<spring:message code="welcomeUser" arguments="${user.personName.givenName},'Personal Health Toolkit'" />
		<br/><br/>

<table width="1000">
  <tr>
  <td align="left">
		<spring:message code="medadherence.welcome" />
  </td>
  </tr>
</table>

<br/>

<a href="view/medicationFeedback.htm"><button class="big_button"><spring:message code="medadherence.medication.feedback" /></button> </a>

<br/> <br/>

<table >
  <tr align="center">
    <td>				
    	<a href="${pageContext.request.contextPath}/phr/options.form"><button class="medium_button"><spring:message code="medadherence.my.profile" /></button> </a>
    </td>
    <td>
    	<a href="view/help.htm"><button class="medium_button"><spring:message code="medadherence.help" /></button> </a>
	</td>
    <td>
		<a href="view/contactUs.htm"><button class="medium_button"><spring:message code="medadherence.contact" /></button> </a>
    </td>
    <td>
		<a href="${pageContext.request.contextPath}/moduleServlet/personalhr/phrLogoutServlet"><button class="medium_button"><spring:message code="medadherence.logout" /></button> </a>
    </td>
   </tr>
</table>
</div>
</center>
</c:if>

<br />

<%@ include file="/WEB-INF/view/module/personalhr/template/footer.jsp" %> 