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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.MessagingService;
import org.openmrs.module.personalhr.PersonalhrUtil;
import org.openmrs.module.personalhr.PhrPatient;
import org.openmrs.module.personalhr.PhrService;
import org.openmrs.module.personalhr.PhrSharingToken;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

/**
 * The controller for entering/viewing a form. This should always be set to sessionForm=false.
 * <p/>
 * Handles {@code htmlFormEntry.form} requests. Renders view {@code htmlFormEntry.jsp}.
 * <p/>
 * TODO: This has a bit too much logic in the onSubmit method. Move that into the FormEntrySession.
 */
public class PatientRelationshipsFormController extends SimpleFormController {
    
    protected final Log log = LogFactory.getLog(getClass());
    
    private static final String EMAIL_TEMPLATE = "Dear OPENMRS_PHR_RELATED_PERSON,\n\nYou've been invited by OPENMRS_PHR_SHARING_PERSON to access OPENMRS_PHR_PATEINT_GENDER_S personal cancer toolkit. Specifically OPENMRS_PHR_PATEINT_GENDER_I has granted you permission to view certain information in order to assist OPENMRS_PHR_PATEINT_GENDER_M in OPENMRS_PHR_PATEINT_GENDER_S future treatment.\n\nOPENMRS_PHR_SHARING_LINK\n\nTo access OPENMRS_PHR_PATEINT_GENDER_S personal cancer toolkit, click the link above. Then sign in with your username and password. If you do not have an account yet, you can create one by clicking on the \"First Time User Registration\". You will need to use the e-mail address this sharing request e-mail was sent to. This invitation expires 30 days from the day it was sent.\n\nIf you have any questions or require further clarification, please contact the site administrator here:\n\ncancertoolkit-l@regenstrief.org\n\nThank You!\nSincerely,\nThe Personal Cancer Toolkit Development Team";
    
    /**
     * Allows for Integers to be used as values in input tags. Normally, only strings and lists are
     * expected
     * 
     * @see org.springframework.web.servlet.mvc.BaseCommandController#initBinder(javax.servlet.http.HttpServletRequest,
     *      org.springframework.web.bind.ServletRequestDataBinder)
     */
    @Override
    protected void initBinder(final HttpServletRequest request, final ServletRequestDataBinder binder) throws Exception {
        super.initBinder(request, binder);
        binder.registerCustomEditor(java.lang.Integer.class, new CustomNumberEditor(java.lang.Integer.class, true));
    }
    
    @Override
    protected PhrPatient formBackingObject(final HttpServletRequest request) throws Exception {
        this.log.debug("Entering PatientRelationshipsFormController:formBackingObject");
        Integer patientId = null;
        if ((request.getParameter("patientId") != null) && !"".equals(request.getParameter("patientId"))) {
            patientId = PersonalhrUtil.getParamAsInteger(request.getParameter("patientId"));
        }
        
        return new PhrPatient(patientId);
    }
    
    @Override
    protected void onBindAndValidate(final HttpServletRequest request, final Object commandObject, final BindException errors)
                                                                                                                              throws Exception {
        final String command = request.getParameter("command");
        this.log.debug("Entering PatientRelationshipsFormController:onBindAndValidate, command=" + command);
        final PhrPatient phrPatient = (PhrPatient) commandObject;
        final List<PhrSharingToken> tokens = phrPatient.getSharingTokens();
        final PhrSharingToken newToken = phrPatient.getNewSharingToken();
        
        try {
            if ("Save Changes".equals(command)) {
                for (final PhrSharingToken token : tokens) {
                    //validate email address
                    if (PersonalhrUtil.isNullOrEmpty(token.getRelatedPersonEmail())) {
                        errors.reject("Email can not be empty!");
                    } else if (!PersonalhrUtil.isValidEmail(token.getRelatedPersonEmail())) {
                        errors.reject("Invalid email address: " + token.getRelatedPersonEmail());
                    } else {
                        this.log.debug("token.getRelatedPersonEmail()=" + token.getRelatedPersonEmail());
                    }
                    //validate sharing type
                    if ("Select One".equalsIgnoreCase(token.getShareType())) {
                        errors.reject("Please select the type of information you want to share with the specified person: "
                                + token.getRelatedPersonName());
                    } else {
                        this.log.debug("token.getShareType()=" + token.getShareType());
                    }
                    
                }
            } else if ("Add".equals(command)) {
                final PhrSharingToken token = newToken;
                
                //validate person name
                if (PersonalhrUtil.isNullOrEmpty(token.getRelatedPersonName())) {
                    errors.reject("Person name can not be empty!");
                } else {
                    this.log.debug("token.getRelatedPersonName()=" + token.getRelatedPersonName());
                }
                
                //validate email address
                if (PersonalhrUtil.isNullOrEmpty(token.getRelatedPersonEmail())) {
                    errors.reject("Email can not be empty for added relationship!");
                } else if (!PersonalhrUtil.isValidEmail(token.getRelatedPersonEmail())) {
                    errors.reject("Invalid email address: " + token.getRelatedPersonEmail());
                } else {
                    this.log.debug("token.getRelatedPersonEmail()=" + token.getRelatedPersonEmail());
                }
                //validate sharing type
                if ("Select One".equalsIgnoreCase(token.getShareType())) {
                    errors.reject("Please select the type of information you want to share with the specified person: "
                            + token.getRelatedPersonName());
                } else {
                    this.log.debug("token.getShareType()=" + token.getShareType());
                }
            }
        } catch (final Exception ex) {
            this.log.error("Exception during form validation", ex);
            errors.reject("Exception during form validation, see log for more details: " + ex);
        }
    }
    
    @Override
    protected ModelAndView onSubmit(final HttpServletRequest request, final HttpServletResponse response,
                                    final Object commandObject, final BindException errors) throws Exception {
        final String command = request.getParameter("command");
        this.log.debug("Entering PatientRelationshipsFormController:onSubmit, command=" + command);
        
        final PhrPatient phrPat = (PhrPatient) commandObject;
        final List<PhrSharingToken> tokens = phrPat.getSharingTokens();
        final PhrSharingToken newToken = phrPat.getNewSharingToken();
        
        try {
            if ((command != null) && command.startsWith("Delete")) {
                final Integer id = PersonalhrUtil.getParamAsInteger(command.substring(7));
                if ((id != null) && (id > 0)) {
                    phrPat.delete(id);
                }
            } else if ((command != null) && (command.startsWith("Save") || command.startsWith("Add"))) {
                phrPat.save();
                
                if (command.startsWith("Add")) {
                    //send email notification to the specified person
                    String email = EMAIL_TEMPLATE;
                    final String emailAddress = phrPat.getNewSharingToken().getRelatedPersonEmail();
                    final String deployUrl= Context.getRuntimeProperties().getProperty("deployment.url");//"https://65.111.248.164:8443/"; //"172.30.201.24";
                    final String token = phrPat.getNewSharingToken().getSharingToken();
                    final String url = deployUrl + "/openmrs/phr/index.htm?sharingToken=" + token;
                    email = email.replaceAll("OPENMRS_PHR_SHARING_LINK", url);
                    
                    String hisOrHer = "his";
                    String himOrHer = "him";
                    String heOrShe = "he";
                    if("F".equalsIgnoreCase(phrPat.getPatient().getGender())) {
                        hisOrHer="her";
                        himOrHer="her";
                        heOrShe="she";
                    }
                    email = email.replaceAll("OPENMRS_PHR_PATEINT_GENDER_S", hisOrHer);
                    email = email.replaceAll("OPENMRS_PHR_PATEINT_GENDER_M", himOrHer);
                    email = email.replaceAll("OPENMRS_PHR_PATEINT_GENDER_I", heOrShe);
                        
                    String patientName = phrPat.getPatient().getPersonName().getFullName();
                    String relatedPersonName = newToken.getRelatedPersonName();
                    email = email.replaceAll("OPENMRS_PHR_SHARING_PERSON", patientName);
                    email = email.replaceAll("OPENMRS_PHR_RELATED_PERSON", relatedPersonName);

                    sendEmail(emailAddress, email);
                    
                    this.log.debug("\n\nThe following email has been sent to " + emailAddress + ":\n" + email + "\n\n");
                }
            }
            
            //String results = "Number of relationships changed: " + phrPat.getNumberChanged() + 
            //"; Number of relationships added: " + phrPat.getNumberAdded() +
            //"; Number of relationships deleted: " + phrPat.getNumberDeleted();
            //log.debug(results );
            //request.getSession().setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Saved " + phrPat.getPatient());
            final String successView = getSuccessView() + "?patientId=" + phrPat.getPatientId();
            return new ModelAndView(new RedirectView(successView));
            
        } catch (final Exception ex) {
            this.log.error("Exception trying to submit form", ex);
            final StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            errors.reject("Exception! " + ex.getMessage() + "<br/>" + sw.toString());
            return showForm(request, response, errors);
        }
    }
    
    /**
     * Auto generated method comment
     * 
     * @param emailAddress
     * @param email
     */
    private void sendEmail(final String emailAddress, final String email) {
        // TODO Auto-generated method stub
        try {
            Context.getService(MessagingService.class).sendMessage(email, emailAddress,
                org.openmrs.module.messaging.email.EmailProtocol.class);
        } catch (final Exception e) {
            this.log.debug("Unable to send message to " + emailAddress, e);
        } catch (final NoClassDefFoundError e) {
            this.log.debug("Messaging module is not found, unable to send message to " + emailAddress, e);           
        }
    }
    
}
