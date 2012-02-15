<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
   "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib prefix="personalhr" uri="/WEB-INF/view/module/personalhr/taglibs/personalhr.tld" %>

<%@ page import="org.openmrs.web.WebConstants" %>
<%
	pageContext.setAttribute("msg", session.getAttribute(WebConstants.OPENMRS_MSG_ATTR));
	pageContext.setAttribute("msgArgs", session.getAttribute(WebConstants.OPENMRS_MSG_ARGS));
	pageContext.setAttribute("err", session.getAttribute(WebConstants.OPENMRS_ERROR_ATTR));
	pageContext.setAttribute("errArgs", session.getAttribute(WebConstants.OPENMRS_ERROR_ARGS));
	session.removeAttribute(WebConstants.OPENMRS_MSG_ATTR);
	session.removeAttribute(WebConstants.OPENMRS_MSG_ARGS);
	session.removeAttribute(WebConstants.OPENMRS_ERROR_ATTR);
	session.removeAttribute(WebConstants.OPENMRS_ERROR_ARGS);
%>

<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Pragma" content="no-cache"> 
		<meta http-equiv="Expires" content="0"> 		
		<openmrs:htmlInclude file="/openmrs.js" />
		<openmrs:htmlInclude file="/openmrs.css" />
		<link href="<openmrs:contextPath/><spring:theme code='stylesheet' />" type="text/css" rel="stylesheet" />
		<openmrs:htmlInclude file="/style.css" />
		<openmrs:htmlInclude file="/dwr/engine.js" />
		<openmrs:htmlInclude file="/dwr/interface/DWRAlertService.js" />
		<openmrs:htmlInclude file="/dwr/interface/DWRPersonalhrService.js" />
		<c:if test="${empty DO_NOT_INCLUDE_JQUERY}">
			<openmrs:htmlInclude file="/moduleResources/personalhr/jquery-1.4.4.min.js" />
			<openmrs:htmlInclude file="/moduleResources/personalhr/jquery-ui-1.8.9.custom.css" />
			<openmrs:htmlInclude file="/moduleResources/personalhr/jquery-ui-1.8.9.custom.min.js" />
			<openmrs:htmlInclude file="/scripts/jquery-ui/js/jquery-ui-datepicker-i18n.js" />
		</c:if>
		<openmrs:htmlInclude file="/scripts/calendar/calendar.js" />
		<link rel="icon" type="image/ico" href="<openmrs:contextPath/><spring:theme code='favicon' />">

		<c:choose>
			<c:when test="${!empty pageTitle}">
				<title>${pageTitle}</title>
			</c:when>
			<c:otherwise>
				<title><spring:message code="openmrs.title"/></title>
			</c:otherwise>
		</c:choose>


		<script type="text/javascript">
			<c:if test="${empty DO_NOT_INCLUDE_JQUERY}">
				var $j = jQuery.noConflict();
			</c:if>
			/* variable used in js to know the context path */
			var openmrsContextPath = '${pageContext.request.contextPath}';
			var dwrLoadingMessage = '<spring:message code="general.loading" />';
			var jsDateFormat = '<openmrs:datePattern localize="false"/>';
			var jsLocale = '<%= org.openmrs.api.context.Context.getLocale() %>';

			$j(document).ready(function() {
				$j('#contactUsPopup').dialog({
						title: 'dynamic',
						autoOpen: false,
						draggable: false,
						resizable: false,
						resize:    'auto',
						width: '700px',
						modal: true,
						open: function(a, b) {  }
				});
			});
				
			function onContactUs(title) {
				$j('#contactUsPopup')
					.dialog('option', 'title', title)
					.dialog('option', 'height', $j(window).height()/3) 
					.dialog('open');
			}
			
			function onSendMessage() {
				var messageContent = $j('#contactMessage').val();
      	  	  	$j('#contactUsPopup').dialog("close");
				DWRPersonalhrService.sendMessageToUs(messageContent);
			}
			
			function onCancelMessage() {
      	  	  	$j('#contactUsPopup').dialog("close");
			}								
			
		</script>

		<openmrs:extensionPoint pointId="org.openmrs.headerFullIncludeExt" type="html" requiredClass="org.openmrs.module.web.extension.HeaderIncludeExt">
			<c:forEach var="file" items="${extension.headerFiles}">
				<openmrs:htmlInclude file="${file}" />
			</c:forEach>
		</openmrs:extensionPoint>

	</head>

<body>
	<div id="pageBody">
        
		<div id="userBar">
			<openmrs:authentication>
				<c:if test="${authenticatedUser != null}">
					<span id="userLoggedInAs" class="firstChild">
						<spring:message code="header.logged.in"/> ${authenticatedUser.personName}
					</span>
					<span id="userLogout">
						<a style="color:navy;" href='${pageContext.request.contextPath}/moduleServlet/personalhr/phrLogoutServlet'><spring:message code="header.logout" /></a>
					</span>
					<span>
						<a style="color:navy;" href="${pageContext.request.contextPath}/phr/options.form"><spring:message code="Navigation.options"/></a>
					</span>
				</c:if>
				<c:if test="${authenticatedUser == null}">
					<span id="userLoggedOut" class="firstChild">
						<spring:message code="header.logged.out"/>
					</span>
					<span id="userLogIn">
						<a  style="color:navy;" href='${pageContext.request.contextPath}/phr/login.htm'><spring:message code="header.login"/></a>
					</span>
				</c:if>
			</openmrs:authentication>

			<span id="userHelp">
				<a style="color:navy;" href="${pageContext.request.contextPath}/phr/help.htm" ><spring:message code="header.help"/></a>
			</span>
			<span id="contactUs">
				<a style="color:navy;" href="#" onClick="onContactUs('Contact us with questions or feedback');" title="Contact us with questions or feedback"><spring:message code="personalhr.header.contact.us"/></a>
			</span>
		</div>

		<%@ include file="/WEB-INF/view/module/personalhr/template/banner.jsp" %>

		<%-- This is where the My Patients popup used to be. I'm leaving this placeholder here
			as a reminder of where to put back an extension point when I've figured out what it should
			look like. -DJ
		<div id="popupTray">
		</div>
		--%>

		<div id="content">

			<script type="text/javascript">
				// prevents users getting popup alerts when viewing pages
				var handler = function(msg, ex) {
					if(msg.indexOf('focus') != -1) {
						return;
					}
					var div = document.getElementById("openmrs_dwr_error");
					div.style.display = ""; // show the error div
					var msgDiv = document.getElementById("openmrs_dwr_error_msg");
					msgDiv.innerHTML = '<spring:message code="error.dwr"/>' + " <b>" + msg + "</b>";
					
				};
				dwr.engine.setErrorHandler(handler);
				dwr.engine.setWarningHandler(handler);
			</script>

			<openmrs:extensionPoint pointId="org.openmrs.module.personalhr.alertBar" type="html">
				<personalhr:hasPrivilege privilege="${extension.requiredPrivilege}">
					<div id="${extension.tabId}" >
						<c:choose>
							<c:when test="${extension.portletUrl == '' || extension.portletUrl == null}">
								portletId is null: '${extension.extensionId}'
							</c:when>
							<c:otherwise>
								<openmrs:portlet url="${extension.portletUrl}" id="${extension.tabId}" moduleId="${extension.moduleId}"/>							
							</c:otherwise>
						</c:choose>
					</div>
				</personalhr:hasPrivilege>
			</openmrs:extensionPoint>

			<c:if test="${msg != null}">
				<div id="openmrs_msg"><spring:message code="${msg}" text="${msg}" arguments="${msgArgs}" /></div>
			</c:if>
			<c:if test="${err != null}">
				<div id="openmrs_error"><spring:message code="${err}" text="${err}" arguments="${errArgs}"/></div>
			</c:if>
			<div id="openmrs_dwr_error" style="display:none" class="error">
				<div id="openmrs_dwr_error_msg"></div>
				<div id="openmrs_dwr_error_close" class="smallMessage">
					<i><spring:message code="error.dwr.stacktrace"/></i> 
					<a href="#" onclick="this.parentNode.parentNode.style.display='none'"><spring:message code="error.dwr.hide"/></a>
				</div>
			</div>
			<div id="contactUsPopup">
				<textarea id="contactMessage" rows="5" cols="100"></textarea>
				<br/><br/>
				<input type="button" name="sendMessage" id="sendMessage" value="Send" onClick="onSendMessage();"/>
				<input type="button" name="cancelMessage" id="cancelMessage" value="Cancel" onClick="onCancelMessage();"/>						
			</div>
			
			