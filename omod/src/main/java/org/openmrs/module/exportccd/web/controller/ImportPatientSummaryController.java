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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.exportccd.api.PatientSummaryExportService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * The main controller.
 */
@Controller
@RequestMapping("/module/exportccd/importPatient*")
public class  ImportPatientSummaryController {
	protected final Log log = LogFactory.getLog(getClass());
	
	@RequestMapping(method = RequestMethod.GET)
	public void manage(ModelMap model, HttpServletRequest request) {
		model.addAttribute("inputDoc", "No CCD has been imported yet.");
	}
	
	//@RequestMapping(method = RequestMethod.POST)
	public void manage(HttpServletRequest request, ModelMap model) {
        String type = request.getContentType();

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));

            String boundary = extractBoundary(type);

            String aLine = null;
            int lineIndex = 0;
            StringBuffer sb = new StringBuffer();

            while ((aLine = br.readLine()) != null) {
                lineIndex++;

                if (aLine.indexOf("T_PATIENT_FILE_INPUT") > 0) {
                	break;
                } 
            }

            String pathSaveAs = "/data/CCDs/";
            String saveAs = System.currentTimeMillis() + ".xml";            
            saveAs = pathSaveAs + saveAs; 
            
            String line;
            FileWriter fw = new FileWriter(saveAs);
            BufferedWriter bw = new BufferedWriter(fw);

            while ((line = br.readLine()) != null) {
            	sb.append(line);
            	
                if ((boundary != null) && (line.indexOf(boundary) >= 0)) {
                    break;
                }

                bw.write(line);
                bw.newLine();
            }

            bw.flush();
            bw.close();
            fw.close();
            
    		model.addAttribute("inputDoc", sb.toString());
        } catch (Exception e) {
        	log.error(e.getMessage(), e);
        }
	}         

	@RequestMapping(method = RequestMethod.POST)
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    PrintWriter out = response.getWriter();
	    response.setContentType("text/plain");
	    out.println("<h1>Servlet File Upload Example using Commons File Upload</h1>");
	    out.println();
 
		DiskFileItemFactory  fileItemFactory = new DiskFileItemFactory ();
		/*
		 *Set the size threshold, above which content will be stored on disk.
		 */
		fileItemFactory.setSizeThreshold(1*1024*1024); //1 MB
		File tmpDir=new File("/data/tmp");
		/*
		 * Set the temporary directory to store the uploaded files of size above threshold.
		 */
		fileItemFactory.setRepository(tmpDir );
 
		ServletFileUpload uploadHandler = new ServletFileUpload(fileItemFactory);
		try {
			/*
			 * Parse the request
			 */
			List items = uploadHandler.parseRequest(request);
			Iterator itr = items.iterator();
			while(itr.hasNext()) {
				FileItem item = (FileItem) itr.next();
				/*
				 * Handle Form Fields.
				 */
				if(item.isFormField()) {
					out.println("File Name = "+item.getFieldName()+", Value = "+item.getString());
				} else {
					//Read the file into a String for display purpose
		            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
		            String line;
		            StringBuffer sb = new StringBuffer();

		            while ((line = br.readLine()) != null) {
		            	sb.append(line);		            	
		            }
		            
					
					//Save Uploaded files to local directory
					out.println("Field Name = "+item.getFieldName()+
						", File Name = "+item.getName()+
						", Content type = "+item.getContentType()+
						", File Size = "+item.getSize());
					String destinationDir="/data/CCDs";
					/*
					 * Write file to the ultimate location.
					 */
					File file = new File(destinationDir,item.getName());
					item.write(file);
				}
				out.close();
			}
		}catch(FileUploadException ex) {
			log.error("Error encountered while parsing the request",ex);
		} catch(Exception ex) {
			log.error("Error encountered while uploading file",ex);
		}
 
	}
 

    private String extractBoundary(String line) {
        int index = line.indexOf("boundary");

        if (index < 0) {
            return null;
        }

        String boundary = line.substring(index + 9);

        return boundary;
    }
	
}
