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
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.personalhr.PersonalhrUtil;
import org.openmrs.module.personalhr.PhrAllowedUrl;
import org.openmrs.module.personalhr.PhrSecurityConfig;
import org.openmrs.module.personalhr.PhrApply;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

import org.openmrs.module.personalhr.PhrPrivilege;
/**
 * The controller for entering/viewing a form. This should always be set to sessionForm=false.
 * <p/>
 * Handles {@code htmlFormEntry.form} requests. Renders view {@code htmlFormEntry.jsp}.
 * <p/>
 * TODO: This has a bit too much logic in the onSubmit method. Move that into the FormEntrySession.
 */
public class ManagePhrApplyFormController extends SimpleFormController {
    
    protected final Log log = LogFactory.getLog(getClass());
        
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
    protected PhrSecurityConfig formBackingObject(final HttpServletRequest request) throws Exception {
        this.log.debug("Entering ManagePhrPrivilegeFormController:formBackingObject");
        return new PhrSecurityConfig();
    }
    
    @Override
    protected ModelAndView onSubmit(final HttpServletRequest request, final HttpServletResponse response,
                                    final Object commandObject, final BindException errors) throws Exception {
        final String command = request.getParameter("command");
        this.log.debug("Entering ManagePhrPrivilegeFormController:onSubmit, command=" + command);
        List<PhrApply> privilegeList = ((PhrSecurityConfig) commandObject).getPhrApplyList();
		
        try {
            Integer id = PersonalhrUtil.getParamAsInteger(request.getParameter("privilegeIdField"));
            if(command != null && command.startsWith("Save")) {
                 if(id >= 0) {
                    PersonalhrUtil.getService().getApplyDao().savePhrApply(privilegeList.get(id));                
                    log.debug("Allowed URL updated: " + privilegeList.get(id).getRequiredRole()+"/"+privilegeList.get(id).getPrivilege());
                } else {
                    log.debug("Nothing is updated. command=" + command);
                }
            } else if(command != null && command.startsWith("Delete")) {
                if( id != null && id >= 0) {
                    PersonalhrUtil.getService().getApplyDao().deletePhrApply(privilegeList.get(id));                
                    log.debug("Allowed URL deleted: " + privilegeList.get(id).getRequiredRole()+"/"+privilegeList.get(id).getPrivilege());
                } else {
                    log.debug("Nothing is deleted. command=" + command);
                }
            } 
            
            return new ModelAndView(new RedirectView(getSuccessView()));            
        } catch (Exception ex) {
            log.error("Exception trying to submit form", ex);
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            errors.reject("Exception! " + ex.getMessage() + "<br/>" + sw.toString());
            return showForm(request, response, errors);
        }                
    }
    
}
