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
import org.openmrs.module.exportccd.api.PatientSummaryExportService;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.*;

/**
 * The main controller.
 */
@Controller
@RequestMapping("/module/exportccd/exportPatient*")
public class  ExportPatientSummaryController {
	
	@RequestMapping(method = RequestMethod.POST)
	public void manage(@RequestParam(value="patientId",required=true)Patient patient, HttpServletResponse response) {
	if(patient != null)
		{
		System.out.println(patient.getId());
		PatientSummaryExportService yservice = (PatientSummaryExportService)Context.getService(PatientSummaryExportService.class);
	    ContinuityOfCareDocument ccd =  yservice.produceCCD(patient.getId());
	   
	   response.setHeader( "Content-Disposition", "attachment;filename="+patient.getGivenName()+".xml");	
	   try {
		  
		 StringWriter r = new StringWriter();
		 
		  CDAUtil.save(ccd, r);
		  String ccdDoc = r.toString();
		  ccdDoc = ccdDoc.replaceAll("&lt;", "<");
		  ccdDoc = ccdDoc.replaceAll("&quot;", "\"");
		  byte[] res = ccdDoc.getBytes(Charset.forName("UTF-8"));
		  response.setCharacterEncoding("UTF-8");
		  response.getOutputStream().write(res);
		   response.flushBuffer();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		}
	}
	
	
	
	
	
	
	@RequestMapping( method = RequestMethod.GET)
	public void manage(@RequestParam(value="patientId",required=false)Patient patient) {

	  System.out.println("Hellow World");
			
	}
}
