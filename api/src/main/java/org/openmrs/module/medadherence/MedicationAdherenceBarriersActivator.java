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
package org.openmrs.module.medadherence;


import java.util.UUID;

import org.apache.commons.logging.Log; 
import org.apache.commons.logging.LogFactory;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.ModuleActivator;
import org.openmrs.scheduler.SchedulerException;
import org.openmrs.scheduler.TaskDefinition;
import org.openmrs.util.OpenmrsConstants;

/**
 * This class contains the logic that is run every time this module is either started or stopped.
 */
public class MedicationAdherenceBarriersActivator implements ModuleActivator {
	
	protected Log log = LogFactory.getLog(getClass());
	private static String TASK_NAME="Patient Reminder";
	public static String EMAIL_ATTR_NAME = "Email";
	public static String DATE_OF_BIRTH_ATTR_NAME = "Date of Birth";
	
	/**
	 * A boolean used to protect against multiple started() calls
	 */
	private boolean startedCalled = false;
		
	/**
	 * @see ModuleActivator#willRefreshContext()
	 */
	public void willRefreshContext() {
		log.info("Refreshing Medication Adherence Barriers Module");
	}
	
	/**
	 * @see ModuleActivator#contextRefreshed()
	 */
	public void contextRefreshed() {
		log.info("Medication Adherence Barriers Module refreshed");
	}
	
	/**
	 * @see ModuleActivator#willStart()
	 */
	public void willStart() {
		log.info("Starting Medication Adherence Barriers Module");
	}
	
	/**
	 * @see ModuleActivator#started()
	 */
	public void started() {
		log.info("Medication Adherence Barriers Module started");
		if(!startedCalled){ 
			createPatientReminderTask();
			createPatientAttributes();
			startedCalled = true;
		}
	}
	
	/**
	 * @see ModuleActivator#willStop()
	 */
	public void willStop() {
		log.info("Stopping Medication Adherence Barriers Module");
	}
	
	/**
	 * @see ModuleActivator#stopped()
	 */
	public void stopped() {
		log.info("Medication Adherence Barriers Module stopped");
	}
		
	/**
	 * This method creates the task that polls the database and dispatches outgoing messages
	 */
	private void createPatientReminderTask(){
		//temporarily add the privilege to manage the scheduler
		Context.addProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_SCHEDULER);
		TaskDefinition dispatchMessagesTaskDef = Context.getSchedulerService().getTaskByName(TASK_NAME);

		if(dispatchMessagesTaskDef == null){
			dispatchMessagesTaskDef = new TaskDefinition();
			dispatchMessagesTaskDef.setUuid(UUID.randomUUID().toString());
			dispatchMessagesTaskDef.setName(TASK_NAME);
			dispatchMessagesTaskDef.setDescription("Send reminder to patient to fill out the medication adherence barriers form for the MedAdherence Module.");
			dispatchMessagesTaskDef.setStartOnStartup(true);
			dispatchMessagesTaskDef.setStartTime(null);
			dispatchMessagesTaskDef.setRepeatInterval(86400L); //once a day (24*3600=86400)
			dispatchMessagesTaskDef.setTaskClass("org.openmrs.module.medadherence.PatientReminderTask");
			try {
				Context.getSchedulerService().scheduleTask(dispatchMessagesTaskDef);
			} catch (SchedulerException e) {
				log.error("Error creating the patient reminder task in the scheduler", e);
			}
		}
		Context.removeProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_SCHEDULER);
	}	
	
	private void createPatientAttributes(){
		Context.addProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_PERSON_ATTRIBUTE_TYPES);
		Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PERSON_ATTRIBUTE_TYPES);
		PersonService personService = Context.getPersonService();
		if(personService.getPersonAttributeTypeByName(EMAIL_ATTR_NAME) == null){
			PersonAttributeType email = new PersonAttributeType();
			email.setName(EMAIL_ATTR_NAME);
			email.setFormat("java.lang.String");
			email.setDescription("A person's email address");
			email.setSearchable(true);
			personService.savePersonAttributeType(email);
		}
		if(personService.getPersonAttributeTypeByName(DATE_OF_BIRTH_ATTR_NAME) == null){
			PersonAttributeType dateOfBirth = new PersonAttributeType();
			dateOfBirth.setName(DATE_OF_BIRTH_ATTR_NAME);
			dateOfBirth.setFormat("org.openmrs.util.AttributableDate");
			dateOfBirth.setDescription("A person's date of birth");
			dateOfBirth.setSearchable(true);
			personService.savePersonAttributeType(dateOfBirth);
		}
		
		Context.removeProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_PERSON_ATTRIBUTE_TYPES);
		Context.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PERSON_ATTRIBUTE_TYPES);
	}	
}
