<%@ include file="/WEB-INF/view/module/personalhr/template/include.jsp" %>
<personalhr:require privilege="View Relationships" otherwise="/phr/login.htm" redirect="../module/personalhr/portlets/newPatientForm.portlet"/>

<openmrs:htmlInclude file="/scripts/calendar/calendar.js" />

<script type="text/javascript">	
	function updateAge() {
		var birthdateBox = document.getElementById('birthdate');
		var ageBox = document.getElementById('age');
		try {
			var birthdate = parseSimpleDate(birthdateBox.value, '<openmrs:datePattern />');
			var age = getAge(birthdate);
			if (age > 0)
				ageBox.innerHTML = "(" + age + ' <spring:message code="Person.age.years"/>)';
			else if (age == 1)
				ageBox.innerHTML = '(1 <spring:message code="Person.age.year"/>)';
			else if (age == 0)
				ageBox.innerHTML = '( < 1 <spring:message code="Person.age.year"/>)';
			else
				ageBox.innerHTML = '( ? )';
			ageBox.style.display = "";
		} catch (err) {
			ageBox.innerHTML = "";
			ageBox.style.display = "none";
		}
	}

	function updateNewAge() {
		var birthdateBox = document.getElementById('birthdate');
		var ageBox = document.getElementById('age');
		try {
			var birthdate = parseSimpleDate(birthdateBox.value, '<openmrs:datePattern />');
			var age = getAge(birthdate);
            ageBox.value = age;
		} catch (err) {
			ageBox.innerHTML = "";
			ageBox.style.display = "none";
		}
	}	
	
	function updateEstimated() {
		var input = document.getElementById("birthdateEstimatedInput");
		if (input) {
			input.checked = false;
			input.parentNode.className = "";
		}
		else
			input.parentNode.className = "listItemChecked";
	}
	
	// age function borrowed from http://anotherdan.com/2006/02/simple-javascript-age-function/
	function getAge(d, now) {
		var age = -1;
		if (typeof(now) == 'undefined') now = new Date();
		while (now >= d) {
			age++;
			d.setFullYear(d.getFullYear() + 1);
		}
		return age;
	}
	
	function removeRow(btn) {
		var parent = btn.parentNode;
		while (parent.tagName.toLowerCase() != "tr")
			parent = parent.parentNode;
		
		parent.style.display = "none";
	}
	
	function removeHiddenRows() {
		var rows = document.getElementsByTagName("TR");
		var i = 0;
		while (i < rows.length) {
			if (rows[i].style.display == "none") {
				rows[i].parentNode.removeChild(rows[i]);
			}
			else {
				i = i + 1;
			}
		}
	}
	
	function identifierOrTypeChanged(input) {
		var parent = input.parentNode;
		while (parent.tagName.toLowerCase() != "tr")
			parent = parent.parentNode;
		
		var inputs = parent.getElementsByTagName("input");
		var prefInput;
		var idInput;
		var typeInput;
		for (var i in inputs) {
			if (inputs[i] && inputs[i].name == "preferred")
				prefInput = inputs[i];
			else if (inputs[i] && inputs[i].name == "identifier")
				idInput = inputs[i];
		}
		inputs = parent.getElementsByTagName("select");
		for (var i in inputs)
			if (inputs[i] && inputs[i].name == "identifierType")
				typeInput = inputs[i];
		
		if (idInput && typeInput)
			prefInput.value = idInput.value + typeInput.value;
	}
	
</script>

<style>
	th { text-align: left } 
	th.headerCell {
		border-top: 1px lightgray solid; 
		xborder-right: 1px lightgray solid
	}
	td.inputCell {
		border-top: 1px lightgray solid;
		}
		td.inputCell th {
			font-weight: normal;
		}
	.lastCell {
		border-bottom: 1px lightgray solid;
	}
</style>

<openmrs:globalProperty key="use_patient_attribute.mothersName" defaultValue="false" var="showMothersName"/>

<spring:hasBindErrors name="patient">
	<spring:message code="fix.error"/>
	<div class="error">
		<c:forEach items="${errors.allErrors}" var="error">
			<spring:message code="${error.code}" text="${error.code}" arguments="${error.arguments}"/><br/><!-- ${fn:replace(error, '--', '\\-\\-')} -->
		</c:forEach>
	</div>
</spring:hasBindErrors>

<form method="post" action="newPatientForm.form" onSubmit="removeHiddenRows()">
	<c:set var="isPhrAdministrator" value="false" />
	<openmrs:hasPrivilege privilege="PHR All Patients Access">
		<c:if test="${patient.patientId == null}"><h2><spring:message code="Patient.create"/></h2></c:if>
		<c:if test="${patient.patientId != null}"><h2><spring:message code="Patient.edit"/></h2></c:if>
		<c:set var="isPhrAdministrator" value="true" />
	</openmrs:hasPrivilege>
    
	<table cellspacing="0" cellpadding="7">
	<tr>
		<th class="headerCell"><spring:message code="Person.name"/></th>
		<td class="inputCell">
			<table cellspacing="2">
				<thead>
					<openmrs:portlet url="nameLayout" id="namePortlet" size="columnHeaders" parameters="layoutShowTable=false|layoutShowExtended=false" />
				</thead>
				<spring:nestedPath path="patient.name">
					<openmrs:portlet url="nameLayout" id="namePortlet" size="inOneRow" parameters="layoutMode=edit|layoutShowTable=false|layoutShowExtended=false" />
				</spring:nestedPath>
			</table>
		</td>
	</tr>
	
	<tr>
		<th class="headerCell"><spring:message code="patientDashboard.demographics"/></th>
		<td class="inputCell">
			<table>
				<tr>
					<td><spring:message code="Person.gender"/></td>
					<td><spring:message code="Person.age"/></td>
				</tr>
				<tr>
					<td style="padding-right: 3em">
						<spring:bind path="patient.gender">
								<openmrs:forEachRecord name="gender">
									<input type="radio" name="gender" id="${record.key}" value="${record.key}" <c:if test="${record.key == status.value}">checked</c:if> />
										<label for="${record.key}"> <spring:message code="Person.gender.${record.value}"/> </label>
								</openmrs:forEachRecord>
							<c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>
						</spring:bind>
					</td>
					<td style="padding-right: 3em">
						<script type="text/javascript">
							function updateBirthdate(txtbox) {
								var input = document.getElementById("birthdate");
								var currentTime = new Date();
								var year = currentTime.getFullYear();
								var birthyear=year - txtbox.value;
								input.value = "01/01/"+birthyear;								 								
							}

						</script>
						<input type="text" 
							name="age" size="5" id="age"
							onChange="updateBirthdate(this)"
							value="${2011 - (1900 + patient.birthdate.year)}"/> years
					</td>
					<td style="padding-right: 3em">
						<spring:bind path="patient.birthdate">			
							<input type="hidden" 
									name="birthdate" size="10" id="birthdate"
									value="${status.value}" />
							<c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if> 
						</spring:bind>												
					</td>
					<td style="padding-right: 3em">
						<spring:bind path="patient.patientId">			
							<input type="hidden" 
									name="patientId" size="10" id="patientId"
									value="${status.value}" />
						</spring:bind>												
					</td>
				</tr>
			</table>
		</td>
	</tr>
	
	<openmrs:forEachDisplayAttributeType personType="patient" displayType="viewing" var="attrType">
		<c:set var="authorized" value="false" />
		<c:choose>
			<c:when test="${not empty attrType.editPrivilege}">
				<openmrs:hasPrivilege privilege="${attrType.editPrivilege.privilege}">
					<c:set var="authorized" value="true" />
				</openmrs:hasPrivilege>
			</c:when>
			<c:otherwise>
				<c:set var="authorized" value="true" />
			</c:otherwise>
		</c:choose>
	
		<tr>
			<th class="headerCell"><spring:message code="PersonAttributeType.${fn:replace(attrType.name, ' ', '')}" text="${attrType.name}"/></th>
			<td class="inputCell">
				<c:choose>
					<c:when test="${authorized == true}">
				
						<openmrs:fieldGen 
							type="${attrType.format}" 
							formFieldName="${attrType.personAttributeTypeId}" 
							val="${patient.attributeMap[attrType.name].hydratedObject}" 
							parameters="optionHeader=[blank]|showAnswers=${attrType.foreignKey}|isNullable=false" /> <%-- isNullable=false so booleans don't have 'unknown' radiobox --%>
					</c:when>
					<c:otherwise>
						${patient.attributeMap[attrType.name]}
					</c:otherwise>
				</c:choose>
			</td>
		</tr>
	</openmrs:forEachDisplayAttributeType>
	
	</table>
	
	<!-- input type="hidden" name="patientId" value="${param.patientId}" /-->
	
	<br />
	<input type="submit" value="<spring:message code="general.save" />" name="action" id="addButton"> &nbsp; &nbsp; 
	<input type="button" value="<spring:message code="general.back"/>" onclick="history.go(-1);" id="backButton">
</form>

<script type="text/javascript">
	//document.forms[0].elements[0].focus();
	//document.getElementById("identifierRow").style.display = "none";
	//updateAge();
</script>
