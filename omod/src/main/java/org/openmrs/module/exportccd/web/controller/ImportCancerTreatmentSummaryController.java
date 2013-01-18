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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openhealthtools.mdht.uml.cda.ccd.ContinuityOfCareDocument;
import org.openhealthtools.mdht.uml.cda.util.CDAUtil;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.openmrs.module.exportccd.api.PatientSummaryExportService;
import org.openmrs.module.exportccd.api.PatientSummaryImportService;
import org.openmrs.web.WebConstants;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.*;

/**
 * The main controller.
 */
@Controller
@RequestMapping("/module/exportccd/importCancerTreatmentSummary*")
public class  ImportCancerTreatmentSummaryController {
	
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView post(HttpServletRequest request, HttpServletResponse response) throws Exception {
	      Patient pat = null;
	      Integer patientId = (Integer) request.getSession().getAttribute("patientId");
	            
	      pat = Context.getPatientService().getPatient(patientId);
		  	 
		  PatientSummaryImportService importService = Context.getService(PatientSummaryImportService.class);
		  String status = importService.importCancerTreatmentSummary(pat);
		  
		  ModelAndView mv = new ModelAndView(new RedirectView("/openmrs"),"status",status);
		  request.getSession().setAttribute(WebConstants.OPENMRS_MSG_ATTR, status);
			
		  //request.getSession().setAttribute(WebConstants.OPENMRS_HEADER_USE_MINIMAL, true);
		  return mv;	
	}	
}
