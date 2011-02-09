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
package org.openmrs.module.personalhr.web.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class SpringController implements Controller {
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException,
	                                                                                           IOException {
		log.debug("Entering org.openmrs.module.cancertoolkit.web.controller.SpringController");
		
		String path = request.getServletPath();
		if (path.endsWith("htm"))
			path = path.replace(".htm", "");
		else if (path.endsWith("jsp"))
			path = path.replace(".jsp", "");
		
		path = path.replace("/phr/", "");
		
		path = "module/personalhr/view/" + path;
		//int qmark = path.indexOf("?");
		log.debug("Exiting: path=" + path);
		
        Map<String, Object> model = new HashMap<String, Object>();
        String sharingToken = request.getParameter("sharingToken");
        model.put("sharingToken", sharingToken);
        
		return new ModelAndView(path, "model", model);
		
	}
}
