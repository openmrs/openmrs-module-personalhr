<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="template/localHeader.jsp"%>
<openmrs:htmlInclude file="/dwr/interface/DWRMyModuleService.js"/>
<script type="text/javascript">
$j(document).ready(function(){
	$j('#VitalSigns').data("counter", $j('#VitalSignsCounter').val());
	$j('#SocialHistory').data("counter", $j('#SocialHistoryCounter').val());
	$j('#LabResults').data("counter", $j('#LabResultsCounter').val());
	$j('#PlanOfCare').data("counter", $j('#PlanOfCareCounter').val());
	$j('#FamilyHistory').data("counter", $j('#FamilyHistoryCounter').val());
	
	
 $j('#save').click(function(){
    
               $j('#VitalSignsCounter').val($j('#VitalSigns').data("counter"));
               $j('#SocialHistoryCounter').val($j('#SocialHistory').data("counter"));
               $j('#LabResultsCounter').val($j('#LabResults').data("counter"));
               $j('#LabResultsCounter').val($j('#LabResults').data("counter"));
               $j('#PlanOfCareCounter').val($j('#PlanOfCare').data("counter"));
               $j('#FamilyHistoryCounter').val($j('#FamilyHistory').data("counter"));
          $j('form').submit();  
          
 });
 
 $j(':button.addButton').click(function(){
 
	 
  var parent=$j(this).parent();
  var count=$j(parent).data("counter");
  
  var parentId=$j(parent).attr('id');
  var generatedId=parentId+count;

  var mappingExists = false;
  
  DWRMyModuleService.getConceptMappings($j('#'+parentId+'_conceptId').val(),function (map){
 
  if(map > 0) 
  {
	count= parseInt(count);
	count = count+ 1;
  $j(parent).data("counter",count);
 
  var spanId=generatedId+"_span";
  var v='</br><span id="'+spanId+'">'+$j('#'+parentId+'_conceptId_selection').val()+'<input type="hidden" id="'+spanId+'_hid" name="'+spanId+'_hid" value="'+$j('#'+parentId+'_conceptId').val()+'"/><input id="'+spanId+'_remove" type="button" value="remove" onClick="$j(this).parent().remove();refresh(\''+parentId+'\')"/></span>';
  
  $j(parent).append(v);
  }else
  {
  	alert("Mapping for"+$j('#'+parentId+'_conceptId_selection').val()+"does not exists in OpenMRS");
  }
  
  });
 });
 
});

function refresh (superParentId) {
	var flag=true;
	$j('#'+superParentId+' span').each(function(index) {
		
		
		$j('#'+superParentId).data("counter",index+1);
		
		flag=false;
	    var spanId=this.id;
	    var newSpanId=superParentId+index+"_span";
	    this.id=newSpanId;
	    $j('#'+spanId+'_hid').attr('name',newSpanId+'_hid').attr('id',newSpanId+'_hid');
 		 $j('#'+spanId+'_remove').removeAttr('onclick',null).unbind('click').attr('id',newSpanId+'_remove').click(function() {
		 $j('#'+newSpanId).remove();
		
		 
		 refresh(superParentId);
			 });
	      });

	}
	

</script>


<form id='ccdSections' method="POST">

<div class="boxHeader"><spring:message code="ExportCCD.configure.vital.signs"/></div>
<div id="VitalSigns" class="box">
<spring:message code="Concept.find"/><openmrs_tag:conceptField formFieldName="VitalSigns_conceptId"  formFieldId="VitalSigns_conceptId" />
<input type="button" value="ADD" id="addButton" class="addButton"/>
 <input type="hidden" id="VitalSignsCounter" name="VitalSignsCounter" value="${fn:length(VitalSigns)}"/>
 <c:forEach var="concept" items="${VitalSigns}" varStatus="rIndex">
 
</br>
<span id="VitalSigns${rIndex.index}_span"> ${concept.getName().getName()} 
<input type="hidden" id="VitalSigns${rIndex.index}_span_hid" name="VitalSigns${rIndex.index}_span_hid" value="${concept.getId()}"/>
<input id="'VitalSigns'${rIndex.index}'_remove" type="button" value="remove" onClick="$j(this).parent().remove();refresh('VitalSigns')"/>

</span>
</c:forEach>

</div>
<br>
<div class="boxHeader"><spring:message code="ExportCCD.configure.social.history"/></div>
<div id="SocialHistory" class="box">
<spring:message code="Concept.find"/><openmrs_tag:conceptField formFieldName="SocialHistory_conceptId"  formFieldId="SocialHistory_conceptId" />
<input type="button" value="ADD" id="addButton" class="addButton"/>
 <input type="hidden" id="SocialHistoryCounter" name="SocialHistoryCounter" value="${fn:length(SocialHistory)}"/>
 <c:forEach var="concept" items="${SocialHistory}" varStatus="rIndex">
 
</br>
<span id="SocialHistory${rIndex.index}_span"> ${concept.getName().getName()} 
<input type="hidden" id="SocialHistory${rIndex.index}_span_hid" name="SocialHistory${rIndex.index}_span_hid" value="${concept.getId()}"/>
<input id="'SocialHistory'${rIndex.index}'_remove" type="button" value="remove" onClick="$j(this).parent().remove();refresh('SocialHistory')"/>

</span>
</c:forEach>
 
</div>
<br>
<div class="boxHeader"><spring:message code="ExportCCD.configure.lab.results"/></div>
<div id="LabResults" class="box">
<spring:message code="Concept.find"/><openmrs_tag:conceptField formFieldName="LabResults_conceptId"  formFieldId="LabResults_conceptId" />
<input type="button" value="ADD" id="addButton" class="addButton"/>
 <input type="hidden" id="LabResultsCounter" name="LabResultsCounter" value="${fn:length(LabResults)}"/>
 <c:forEach var="concept" items="${LabResults}" varStatus="rIndex">
 
</br>
<span id="LabResults${rIndex.index}_span"> ${concept.getName().getName()} 
<input type="hidden" id="LabResults${rIndex.index}_span_hid" name="LabResults${rIndex.index}_span_hid" value="${concept.getId()}"/>
<input id="'LabResults'${rIndex.index}'_remove" type="button" value="remove" onClick="$j(this).parent().remove();refresh('LabResults')"/>

</span>
</c:forEach>

</div>

<br>
<div class="boxHeader"><spring:message code="ExportCCD.configure.planofcare"/></div>
<div id="PlanOfCare" class="box">
<spring:message code="Concept.find"/><openmrs_tag:conceptField formFieldName="PlanOfCare_conceptId"  formFieldId="PlanOfCare_conceptId" />
<input type="button" value="ADD" id="addButton" class="addButton"/>
 <input type="hidden" id="PlanOfCareCounter" name="PlanOfCareCounter" value="${fn:length(PlanOfCare)}"/>
 <c:forEach var="concept" items="${PlanOfCare}" varStatus="rIndex">
 
</br>
<span id="PlanOfCare${rIndex.index}_span"> ${concept.getName().getName()} 
<input type="hidden" id="PlanOfCare${rIndex.index}_span_hid" name="PlanOfCare${rIndex.index}_span_hid" value="${concept.getId()}"/>
<input id="'PlanOfCare'${rIndex.index}'_remove" type="button" value="remove" onClick="$j(this).parent().remove();refresh('PlanOfCare')"/>

</span>
</c:forEach>

</div>
<br>

<div class="boxHeader"><spring:message code="ExportCCD.configure.familyhistory"/></div>
<div id="FamilyHistory" class="box">
<spring:message code="Concept.find"/><openmrs_tag:conceptField formFieldName="FamilyHistory_conceptId"  formFieldId="FamilyHistory_conceptId" />
<input type="button" value="ADD" id="addButton" class="addButton"/>
 <input type="hidden" id="FamilyHistoryCounter" name="FamilyHistoryCounter" value="${fn:length(FamilyHistory)}"/>
 <c:forEach var="concept" items="${FamilyHistory}" varStatus="rIndex">
 
</br>
<span id="FamilyHistory${rIndex.index}_span"> ${concept.getName().getName()} 
<input type="hidden" id="FamilyHistory${rIndex.index}_span_hid" name="FamilyHistory${rIndex.index}_span_hid" value="${concept.getId()}"/>
<input id="'FamilyHistory'${rIndex.index}'_remove" type="button" value="remove" onClick="$j(this).parent().remove();refresh('FamilyHistory')"/>

</span>
</c:forEach>

</div>
<br>
<input type="submit" id="save" value='<spring:message code="general.save" />' /></td>
<c:if test="${openmrs_error != ''}"><span class="error">${openmrs_error}</span></c:if>
</form>

 
<%@ include file="/WEB-INF/template/footer.jsp"%>