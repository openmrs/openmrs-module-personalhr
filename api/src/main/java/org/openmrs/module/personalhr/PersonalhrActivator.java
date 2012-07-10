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
package org.openmrs.module.personalhr;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.BaseModuleActivator;
import org.openmrs.util.OpenmrsConstants;

/**
 * This class contains the logic that is run every time this module is either started or shutdown
 */
public class PersonalhrActivator extends BaseModuleActivator {
    
    private final Log log = LogFactory.getLog(this.getClass());
	/**
	 * A boolean used to protect against multiple started() calls
	 */
	private boolean startedCalled = true; //create patient attributes manually to prevent security issues
	private static String EMAIL_ATTR_NAME = "Email";
    
    /**
     * @see org.openmrs.module.Activator#startup()
     */
    @Override
	public void started() {
		log.info("Started Personal Health Records Module");
		if(!startedCalled){ 
			createPatientAttributes();
			startedCalled = true;
		}
	}
	
	private void createPatientAttributes(){
		Context.addProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_PERSON_ATTRIBUTE_TYPES);
		Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PERSON_ATTRIBUTE_TYPES);
		Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_USERS);
		PersonService personService = Context.getPersonService();
		if(personService.getPersonAttributeTypeByName(EMAIL_ATTR_NAME) == null){
			PersonAttributeType emailAttr = new PersonAttributeType();
			emailAttr.setName(EMAIL_ATTR_NAME);
			emailAttr.setFormat("java.lang.String");
			emailAttr.setDescription("A person's email address");
			emailAttr.setSearchable(true);
			emailAttr.setCreator(Context.getUserService().getUserByUsername("admin"));
			personService.savePersonAttributeType(emailAttr);
		}
		Context.removeProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_PERSON_ATTRIBUTE_TYPES);
		Context.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PERSON_ATTRIBUTE_TYPES);		
		Context.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_USERS);
	}    
    /**
     * @see org.openmrs.module.Activator#shutdown()
     */
    @Override
    public void stopped() {
        this.log.info("Shutting down Personal Health Records Module");
    }
    
}
