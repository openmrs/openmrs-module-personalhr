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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Form;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PersonAddress;
import org.openmrs.PersonName;
import org.openmrs.User;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.personalhr.PersonalhrUtil;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.mvc.SimpleFormController;

public class PatientDashboardController extends SimpleFormController {
    
    /** Logger for this class and subclasses */
    protected final Log log = LogFactory.getLog(getClass());
    
    /**
     * This is called prior to displaying a form for the first time. It tells Spring the
     * form/command object to load into the request
     * 
     * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected Object formBackingObject(final HttpServletRequest request) throws ServletException {
        this.log.debug("Entering PatientDashboardController.formBackingObject");
        
        if (!Context.isAuthenticated()) {
            return new Patient();
        }
        
        Patient patient = null;
        try {
            String patientId = request.getParameter("patientId");
            this.log.debug("patientId: " + patientId);
            if (patientId == null) {
                patientId = getPatientId(Context.getAuthenticatedUser());
                if (patientId == null) {
                    //throw new ServletException("Integer 'patientId' is a required parameter");
                    this.log.error("Integer 'patientId' is a required parameter");
                    return new Patient();
                }
            }
            request.setAttribute("patientId", patientId);
            
            //Add temporary privilege
            //PersonalhrUtil.addTemporayPrivileges();
            
            final PatientService ps = Context.getPatientService();
            Integer id = null;
            
            try {
                id = Integer.valueOf(patientId);
                patient = ps.getPatient(id);
            } catch (final NumberFormatException numberError) {
                this.log.warn("Invalid patientId supplied: '" + patientId + "'", numberError);
            } catch (final ObjectRetrievalFailureException noPatientEx) {
                this.log.warn("There is no patient with id: '" + patientId + "'", noPatientEx);
            }
            
            if (patient == null) {
                throw new ServletException("There is no patient with id: '" + patientId + "'");
            }
        } finally {
            //PersonalhrUtil.removeTemporayPrivileges();
        }
        
        return patient;
    }
    
    /**
     * Get a person's patient ID
     * 
     * @param person a given person object
     * @return null if no patient is assoicated with this person
     */
    private String getPatientId(final User user) {
        this.log.debug("Finding a matching patient for user:" + user);
        return user == null ? null : user.getPerson().getId().toString();
    }
    
    /**
     * Called prior to form display. Allows for data to be put in the request to be used in the view
     * 
     * @see org.springframework.web.servlet.mvc.SimpleFormController#referenceData(javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected Map<String, Object> referenceData(final HttpServletRequest request, final Object obj, final Errors err)
                                                                                                                     throws Exception {
        
        this.log.debug("Entering PatientDashboardController.referenceData");
        
        final Patient patient = (Patient) obj;
        
        this.log.debug("patient: '" + patient + "'");
        
        final List<Form> forms = new Vector<Form>();
        final Map<String, Object> map = new HashMap<String, Object>();
        final List<Encounter> encounters = new Vector<Encounter>();
        String causeOfDeathOther = "";
        
        try {
            //Add temporary privilege
            //PersonalhrUtil.addTemporayPrivileges();
            
            if (Context.isAuthenticated()) {
                if (Context.hasPrivilege(OpenmrsConstants.PRIV_VIEW_UNPUBLISHED_FORMS)) {
                    forms.addAll(Context.getFormService().getAllForms());
                } else {
                    forms.addAll(Context.getFormService().getPublishedForms());
                }
                
                final List<Encounter> encs = Context.getEncounterService().getEncountersByPatient(patient);
                if ((encs != null) && (encs.size() > 0)) {
                    encounters.addAll(encs);
                }
                
                final String propCause = Context.getAdministrationService().getGlobalProperty("concept.causeOfDeath");
                final Concept conceptCause = Context.getConceptService().getConcept(propCause);
                
                if (conceptCause != null) {
                    final List<Obs> obssDeath = Context.getObsService().getObservationsByPersonAndConcept(patient,
                        conceptCause);
                    if (obssDeath.size() == 1) {
                        final Obs obsDeath = obssDeath.iterator().next();
                        causeOfDeathOther = obsDeath.getValueText();
                        if (causeOfDeathOther == null) {
                            this.log.debug("cod is null, so setting to empty string");
                            causeOfDeathOther = "";
                        } else {
                            this.log.debug("cod is valid: " + causeOfDeathOther);
                        }
                    } else {
                        this.log.debug("obssDeath is wrong size: " + obssDeath.size());
                    }
                } else {
                    this.log.debug("No concept cause found");
                }
            }
            
            String patientVariation = "";
            
            final Concept reasonForExitConcept = Context.getConceptService().getConcept(
                Context.getAdministrationService().getGlobalProperty("concept.reasonExitedCare"));
            if (reasonForExitConcept != null) {
                final List<Obs> patientExitObs = Context.getObsService().getObservationsByPersonAndConcept(patient,
                    reasonForExitConcept);
                if (patientExitObs != null) {
                    this.log.debug("Exit obs is size " + patientExitObs.size());
                    if (patientExitObs.size() == 1) {
                        final Obs exitObs = patientExitObs.iterator().next();
                        final Concept exitReason = exitObs.getValueCoded();
                        final Date exitDate = exitObs.getObsDatetime();
                        if ((exitReason != null) && (exitDate != null)) {
                            patientVariation = "Exited";
                        }
                    } else if (patientExitObs.size() > 1) {
                        this.log.error("Too many reasons for exit - not putting data into model");
                    }
                }
            }
            
            map.put("patientVariation", patientVariation);
        } finally {
            //PersonalhrUtil.removeTemporayPrivileges();
        }
        
        map.put("forms", forms);
        
        // empty objects used to create blank template in the view
        map.put("emptyIdentifier", new PatientIdentifier());
        map.put("emptyName", new PersonName());
        map.put("emptyAddress", new PersonAddress());
        map.put("encounters", encounters);
        map.put("causeOfDeathOther", causeOfDeathOther);
        
        return map;
    }
    
}
