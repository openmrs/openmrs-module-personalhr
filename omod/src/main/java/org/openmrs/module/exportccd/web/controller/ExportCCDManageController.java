/**


 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.exportccd.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.Concept;
import org.openmrs.api.db.DAOException;
import org.hibernate.exception.ConstraintViolationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;
import org.openmrs.module.exportccd.api.*;
import org.openmrs.api.APIException;
import org.openmrs.web.WebConstants;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
/**
 * The main controller.
 */
@Controller
@RequestMapping("/module/exportccd/ccdConfiguration*")
public class  ExportCCDManageController {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	
	@RequestMapping(method = RequestMethod.GET)
	public void manage(ModelMap model, HttpServletRequest request) {
		
		PatientSummaryExportService patientSummaryService = Context.getService(PatientSummaryExportService.class);
		model.addAttribute("VitalSigns", patientSummaryService. getConceptByCategory("VitalSigns"));
		model.addAttribute("SocialHistory", patientSummaryService. getConceptByCategory("SocialHistory"));
		model.addAttribute("LabResults", patientSummaryService. getConceptByCategory("LabResults"));
		model.addAttribute("PlanOfCare", patientSummaryService. getConceptByCategory("PlanOfCare"));
		model.addAttribute("FamilyHistory", patientSummaryService. getConceptByCategory("FamilyHistory"));
	}
	
	
	@RequestMapping(method = RequestMethod.POST)
	public void getParameters(HttpServletRequest request , ModelMap model) {

		manageSections(request,"VitalSigns");
		PatientSummaryExportService patientSummaryService = Context.getService(PatientSummaryExportService.class);
		model.addAttribute("VitalSigns", patientSummaryService. getConceptByCategory("VitalSigns"));
		manageSections(request,"SocialHistory");
		model.addAttribute("SocialHistory", patientSummaryService. getConceptByCategory("SocialHistory"));
		manageSections(request,"LabResults");
		model.addAttribute("LabResults", patientSummaryService. getConceptByCategory("LabResults"));
		manageSections(request,"PlanOfCare");
		model.addAttribute("PlanOfCare", patientSummaryService. getConceptByCategory("PlanOfCare"));
		manageSections(request,"FamilyHistory");
		model.addAttribute("FamilyHistory", patientSummaryService. getConceptByCategory("FamilyHistory"));
	
}

private void manageSections(HttpServletRequest request , String section)
{
	
	PatientSummaryExportService patientSummaryService = Context.getService(PatientSummaryExportService.class);
	String s = request.getParameter(section+"Counter");
	List<Concept> conceptList = patientSummaryService.getConceptByCategory(section);
	int j=Integer.parseInt(request.getParameter(section+"Counter"));
	List conceptIds = new ArrayList();
	if(j>0)
	{
		
	 for(int i=0;i<j;i++)
	 {
	 
	
		 try{Integer conceptId = Integer.parseInt(request.getParameter(section+i+"_span_hid"));
	  conceptIds.add(conceptId);
		 }
		 catch(NumberFormatException n )
		 {
			 n.printStackTrace();
		 }
	 }
	}	 
	int endIndex = conceptList.size();
	int i = 0;
	while(i < endIndex)
	 {
		 
		 Integer dbConcept = conceptList.get(i).getConceptId();
		 if(conceptIds.contains(dbConcept))
		 {
			 
			 conceptIds.remove(dbConcept);
			 conceptList.remove(Context.getConceptService().getConcept(dbConcept));
			 endIndex--;
		 }
		 else
		 i++;
	 }
	 
	 try
	 {
		 if(conceptIds.size() > 0)
		 patientSummaryService.saveConceptAsCCDSections(conceptIds,section);
		 if(conceptList.size() > 0)
			 patientSummaryService.deleteConceptsByCategory(conceptList,section);
		 request.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "");
	 }catch(DAOException e )
	 {
			request.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, Context.getMessageSourceService().getMessage(
				    "exportccd.could.not.save"));
	 }catch(APIException e )
	 {
			request.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, e.getMessage());
	 }
	 catch(Exception e )
	 {
			request.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, e.getMessage());
	 }
	 

	
}
	



}