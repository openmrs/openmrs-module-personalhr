<ul id="menu">
	
		<li <c:if test='<%= request.getRequestURI().contains("encounters/encounterIndex") %>'>class="active"</c:if>>
			<a href="${pageContext.request.contextPath}/phr/encounterIndex.htm">
				<spring:message code="Encounter.manage"/>
			</a>
		</li>
	
		<li <c:if test='<%= request.getRequestURI().contains("encounterType") %>'>class="active"</c:if>>
			<a href="${pageContext.request.contextPath}/phr/phrEncounterType.list">
				<spring:message code="EncounterType.manage"/>
			</a>
		</li>
	
</ul>