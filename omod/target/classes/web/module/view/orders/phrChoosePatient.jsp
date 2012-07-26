<%@ include file="/WEB-INF/view/module/personalhr/template/include.jsp" %>
<%@ include file="/WEB-INF/view/module/personalhr/template/header.jsp" %>

<h2><spring:message code="Order.list.patient.choose" /></h2>

<br />

<openmrs:portlet id="choosePatient" url="findPatient" parameters="size=full|postURL=phrOrderDrug.list|hideAddNewPatient=true" />

<br />

<center>
	<form>
		<input type="hidden" name="showAll" value="true" />
		<input type="submit" value="<spring:message code="DrugOrder.list.showAll" />" />
	</form>
</center>

<%@ include file="/WEB-INF/view/module/personalhr/template/footer.jsp" %>