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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.personalhr.PersonalhrUtil;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.web.servlet.mvc.SimpleFormController;

/**
 *
 */
public class PersonDashboardController extends SimpleFormController {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	protected Object formBackingObject(HttpServletRequest request) throws ServletException {
		log.debug("Entering PersonDashboardController.formBackingObject");
		if (!Context.isAuthenticated()) {
			log.debug("Not authenticated");
			return new Person();
		}
		else {
            //Add temporary privilege
            PersonalhrUtil.addTemporayPrivileges();

			Person person = null;
			String personId = request.getParameter("personId");
			if(personId!= null && personId.trim().length()>0) {
			    person = Context.getPersonService().getPerson(Integer.valueOf(personId));			
			} else {
			    person = Context.getAuthenticatedUser().getPerson();
			}
			if(person!=null) {
			   log.debug("personId="+person.getPersonId());
			   request.setAttribute("personId", person.getPersonId());
			}
			return person;
		}
	}
}
