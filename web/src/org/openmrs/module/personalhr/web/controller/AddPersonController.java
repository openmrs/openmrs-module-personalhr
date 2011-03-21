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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Person;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.web.dwr.PersonListItem;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

public class AddPersonController extends SimpleFormController {
    
    /** Logger for this class and subclasses */
    protected final Log log = LogFactory.getLog(getClass());
    
    private final String PATIENT_SHORT_EDIT_URL = "/phr/newPatient.htm";
    
    private final String PATIENT_EDIT_URL = "/phr/patient.form";
    
    private final String PATIENT_VIEW_URL = "/phr/patientDashboard.form";
    
    private final String USER_EDIT_URL = "/phr/user.form";
    
    private final String FORM_ENTRY_ERROR_URL = "/phr/newPatient.htm";
    
    /** Parameters passed in view request object **/
    private String name = "";
    
    private String birthdate = "";
    
    private String age = "";
    
    private String gender = "";
    
    private String personType = "patient";
    
    private String personId = "";
    
    private String viewType = "view";
    
    private boolean invalidAgeFormat = false;
    
    /**
     * @see org.springframework.web.servlet.mvc.SimpleFormController#onSubmit(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse, java.lang.Object,
     *      org.springframework.validation.BindException)
     */
    @Override
    protected ModelAndView onSubmit(final HttpServletRequest request, final HttpServletResponse response,
                                    final Object command, final BindException errors) throws Exception {
        
        getParametersFromRequest(request);
        
        if ("".equals(this.personId)) {
            // if they didn't pick a person, continue on to the edit screen no matter what type of view was requsted)
            if ("view".equals(this.viewType) || "shortEdit".equals(this.viewType)) {
                this.viewType = "shortEdit";
            } else {
                this.viewType = "edit";
            }
            
            return new ModelAndView(new RedirectView(getPersonURL("", this.personType, this.viewType, request)));
        } else {
            // if they picked a person, go to the type of view that was requested
            
            // if they selected view, do a double check to make sure that type of person already exists
            if ("view".equals(this.viewType)) {
                // TODO Do we even want to ever redirect to a 'view'.  I'm torn between jumping the DAs right to the 
                // dashboard or jumping them to the short edit screen to make (potential) adjustments
                if ("patient".equals(this.personType)) {
                    try {
                        if (Context.getPatientService().getPatient(Integer.valueOf(this.personId)) == null) {
                            this.viewType = "shortEdit";
                        }
                    } catch (final Exception noPatientEx) {
                        // if there is no patient yet, they must go through those motions
                        this.viewType = "shortEdit";
                    }
                }
            }
            
            // redirect to the appropriate url
            return new ModelAndView(new RedirectView(getPersonURL(this.personId, this.personType, this.viewType, request)));
        }
    }
    
    /**
     * This is called prior to displaying a form for the first time. It tells Spring the
     * form/command object to load into the request
     * 
     * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected List<PersonListItem> formBackingObject(final HttpServletRequest request) throws ServletException {
        
        this.log.debug("Entering formBackingObject()");
        
        List<PersonListItem> personList = new Vector<PersonListItem>();
        
        if (Context.isAuthenticated()) {
            final PersonService ps = Context.getPersonService();
            
            final Integer userId = Context.getAuthenticatedUser().getUserId();
            
            this.invalidAgeFormat = false;
            getParametersFromRequest(request);
            
            this.log.debug("name: " + this.name + " birthdate: " + this.birthdate + " age: " + this.age + " gender: "
                    + this.gender);
            
            if (!this.name.equals("") || !this.birthdate.equals("") || !this.age.equals("") || !this.gender.equals("")) {
                
                this.log.info(userId + "|" + this.name + "|" + this.birthdate + "|" + this.age + "|" + this.gender);
                
                Integer d = null;
                this.birthdate = this.birthdate.trim();
                
                String birthyear = "";
                if (this.birthdate.length() > 6) {
                    birthyear = this.birthdate.substring(6); //parse out the year. assuming XX-XX-XXXX
                }
                
                this.age = this.age.trim();
                
                if (birthyear.length() > 3) {
                    d = Integer.valueOf(birthyear);
                } else if (this.age.length() > 0) {
                    final Calendar c = Calendar.getInstance();
                    c.setTime(new Date());
                    d = c.get(Calendar.YEAR);
                    try {
                        d = d - Integer.parseInt(this.age);
                    } catch (final NumberFormatException e) {
                        // In theory, this should never happen -- Javascript in the UI should prevent this... 
                        this.invalidAgeFormat = true;
                    }
                }
                
                if (this.gender.length() < 1) {
                    this.gender = null;
                }
                
                personList = new Vector<PersonListItem>();
                for (final Person p : ps.getSimilarPeople(this.name, d, this.gender)) {
                    personList.add(PersonListItem.createBestMatch(p));
                }
            }
            
        }
        
        this.log.debug("Returning personList of size: " + personList.size() + " from formBackingObject");
        
        return personList;
    }
    
    /**
     * Prepares the form view
     */
    @Override
    public ModelAndView showForm(final HttpServletRequest request, final HttpServletResponse response,
                                 final BindException errors) throws Exception {
        
        this.log.debug("In showForm method");
        
        ModelAndView mav = super.showForm(request, response, errors);
        
        // If a invalid age is submitted, give the user a useful error message.
        if (this.invalidAgeFormat) {
            mav = new ModelAndView(this.FORM_ENTRY_ERROR_URL);
            mav.addObject("errorTitle", "Person.age.error");
            mav.addObject("errorMessage", "Person.birthdate.required");
            return mav;
        }
        
        final Object o = mav.getModel().get(this.getCommandName());
        
        final List personList = (List) o;
        
        this.log.debug("Found list of size: " + personList.size());
        
        if ((personList.size() < 1) && Context.isAuthenticated()) {
            getParametersFromRequest(request);
            if (this.viewType == null) {
                this.viewType = "edit";
            }
            
            this.log.debug("name: " + this.name + " birthdate: " + this.birthdate + " age: " + this.age + " gender: "
                    + this.gender);
            
            if (!this.name.equals("") || !this.birthdate.equals("") || !this.age.equals("") || !this.gender.equals("")) {
                mav.clear();
                mav.setView(new RedirectView(getPersonURL("", this.personType, this.viewType, request)));
            }
        }
        
        return mav;
    }
    
    /**
     * Returns the url string for the given personType and viewType
     * 
     * @param personId
     * @param personType
     * @param viewType
     * @param request
     * @return url string
     * @throws ServletException
     * @throws UnsupportedEncodingException
     */
    private String getPersonURL(final String personId, final String personType, final String viewType,
                                final HttpServletRequest request) throws ServletException, UnsupportedEncodingException {
        if ("patient".equals(personType)) {
            if ("edit".equals(viewType)) {
                return request.getContextPath() + this.PATIENT_EDIT_URL + getParametersForURL(personId, personType);
            }
            if ("shortEdit".equals(viewType)) {
                return request.getContextPath() + this.PATIENT_SHORT_EDIT_URL + getParametersForURL(personId, personType);
            } else if ("view".equals(viewType)) {
                return request.getContextPath() + this.PATIENT_VIEW_URL + getParametersForURL(personId, personType);
            }
        } else if ("user".equals(personType)) {
            return request.getContextPath() + this.USER_EDIT_URL + getParametersForURL(personId, personType);
        }
        throw new ServletException("Undefined personType/viewType combo: " + personType + "/" + viewType);
    }
    
    /**
     * Returns the appropriate ?patientId/?userId/?name&age&birthyear etc
     * 
     * @param personId
     * @param personType
     * @return
     * @throws UnsupportedEncodingException
     */
    private String getParametersForURL(final String personId, final String personType) throws UnsupportedEncodingException {
        if ("".equals(personId)) {
            return "?addName=" + URLEncoder.encode(this.name, "UTF-8") + "&addBirthdate=" + this.birthdate + "&addAge="
                    + this.age + "&addGender=" + this.gender;
        } else {
            if ("patient".equals(personType)) {
                return "?patientId=" + personId;
            } else if ("user".equals(personType)) {
                return "?userId=" + personId;
            }
        }
        return "";
    }
    
    /**
     * @param request
     */
    private void getParametersFromRequest(final HttpServletRequest request) {
        this.name = ServletRequestUtils.getStringParameter(request, "addName", "");
        this.birthdate = ServletRequestUtils.getStringParameter(request, "addBirthdate", "");
        this.age = ServletRequestUtils.getStringParameter(request, "addAge", "");
        this.gender = ServletRequestUtils.getStringParameter(request, "addGender", "");
        
        this.personType = ServletRequestUtils.getStringParameter(request, "personType", "patient");
        this.personId = ServletRequestUtils.getStringParameter(request, "personId", "");
        this.viewType = ServletRequestUtils.getStringParameter(request, "viewType", "");
    }
}
