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
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.api.db.DAOException;
import org.hibernate.exception.ConstraintViolationException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;
import org.openmrs.module.exportccd.ImportedCCD;
import org.openmrs.module.exportccd.api.*;
import org.openmrs.api.APIException;
import org.openmrs.web.WebConstants;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
/**
 * The main controller.
 */
@Controller
@RequestMapping("/module/exportccd/CCDTab*")
public class  CCDTabController {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	
	@RequestMapping(method = RequestMethod.GET)
	public void manage(ModelMap model, HttpServletRequest request) throws Exception {
        Patient pat = null;
        Integer patientId = null;
        String patientIdStr = request.getParameter("patientId");            
        if(patientIdStr==null) {
          	patientId = (Integer) request.getAttribute("patientId");
        } else {
        	patientId = Integer.getInteger(patientIdStr);
        }
        log.debug("patientId=" + patientId);
            
        pat = Context.getPatientService().getPatient(patientId);
		
		PatientSummaryImportService importService = Context.getService(PatientSummaryImportService.class);
		ImportedCCD ccd = importService.getCCD(pat);
		if(ccd != null) {
			String renderedCCD = renderCCD (request, ccd.getCcdImported());
			model.addAttribute("ccdExists", true);			
			model.addAttribute("importedBy", ccd.getImportedBy());
			model.addAttribute("dateImported", ccd.getDateImported());
			model.addAttribute("fileContent", ccd.getCcdImported());
			model.addAttribute("displayContent", renderedCCD);
		} else {
			model.addAttribute("ccdExists", false);			
		}
	}


	private String renderCCD(HttpServletRequest request, String ccd) throws FileNotFoundException {
	     return transform(IOUtils.toInputStream(ccd), new FileInputStream(request.getRealPath("/")+"/WEB-INF/view/module/exportccd/template/CCD.xsl"));
	}	
	
	public String transform(InputStream xml, InputStream xsl) { 
		ByteArrayOutputStream result = new ByteArrayOutputStream();
	    try {   
		      TransformerFactory tFactory = TransformerFactory.newInstance(); 
		      Transformer transformer = tFactory.newTransformer(new StreamSource(xsl)); 
		      transformer.transform(new StreamSource(xml), new StreamResult(result)); 
		      System.out.println("************* The result is in output.out *************"); 
		} catch (Throwable t) { 
		          t.printStackTrace(); 
		}
		
		return result.toString();	    
	} 
	

}