<%@ include file="/WEB-INF/template/include.jsp" %>
<%-- 
<openmrs:phrRequire privilege="PHR - View Person" otherwise="/phr/login.htm" redirect="/module/personalhr/view/personDashboard.form" />
--%>
<c:set var="OPENMRS_VIEWING_PERSON_ID" scope="request" value="${person.personId}"/>

<spring:message var="pageTitle" code="personDashboard.title" scope="page"/>
<%@ include file="/WEB-INF/view/module/personalhr/template/header.jsp" %>
<openmrs:htmlInclude file="/scripts/calendar/calendar.js" />

<c:if test="${person.patient}">
	<a href="patientDashboard.form?patientId=${person.personId}">
		<spring:message code="patientDashboard.viewDashboard"/>
	</a>
	<br/>
</c:if>

<br/>

<openmrs:portlet url="personHeader" id="patientDashboardHeader" personId="${person.personId}"/>

<br/>

<div class="boxHeader"><spring:message code="Relationship.relationships" /></div>
<div class="box">
	<openmrs:portlet url="personRelationships" size="normal" personId="${person.personId}"/>
</div>

<%@ include file="/WEB-INF/template/footer.jsp" %>