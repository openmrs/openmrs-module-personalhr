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
package org.openmrs.module.personalhr.web.filter;

import java.io.IOException;
import java.util.Date;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.personalhr.PersonalhrUtil;
import org.openmrs.module.personalhr.PhrLogEvent;
import org.openmrs.module.personalhr.PhrService.PhrBasicRole;

/**
 * This filter checks if an authenticated user has been flagged by the admin to change his password
 * on first/subsequent login. It will intercept any requests made to a *.html or a *.form to force
 * the user to change his password.
 */
public class PhrSecurityFilter implements Filter {
    
    protected final Log log = LogFactory.getLog(getClass());
    
    private String excludeURL;
    
    private String loginForm;
    
    private FilterConfig config;
    
    private String[] excludedURLs;
    
    /**
     * @see javax.servlet.Filter#destroy()
     */
    @Override
    public void destroy() {
    }
    
    /**
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
     *      javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
                                                                                                               throws IOException,
                                                                                                               ServletException {
        final String requestURI = ((HttpServletRequest) request).getRequestURI();
        final String patientId = ((HttpServletRequest) request).getParameter("patientId");
        final String personId = ((HttpServletRequest) request).getParameter("personId");
        final String encounterId = ((HttpServletRequest) request).getParameter("encounterId");
        String phrRole = null;
        
        this.log.debug("Entering PhrSecurityFilter.doFilter: " + requestURI + "|" + patientId + "|" + personId + "|"
                + encounterId);
        
        if (Context.isAuthenticated()) {
            final User user = Context.getAuthenticatedUser();
            phrRole = PersonalhrUtil.getService().getPhrRole(user);
            
            if(shouldCheckAccessToUrl(requestURI)) {
                try{
                    PersonalhrUtil.addMinimumTemporaryPrivileges();  
             
                    final Integer patId = PersonalhrUtil.getParamAsInteger(patientId);
                    
                    Patient pat = patId == null ? null : Context.getPatientService().getPatient(patId);
                    
                    final Integer perId = PersonalhrUtil.getParamAsInteger(personId);
                    final Person per = perId == null ? null : Context.getPersonService().getPerson(perId);
                    
                    final Integer encId = PersonalhrUtil.getParamAsInteger(encounterId);
                    final Encounter enc = encId == null ? null : Context.getEncounterService().getEncounter(encId);
                    if (enc != null) {
                        pat = enc.getPatient();
                    }
                                
                    if (!PersonalhrUtil.getService().isUrlAllowed(requestURI, pat, per, Context.getAuthenticatedUser())) {
                        this.log.debug("***URL access not allowed!!! " + requestURI + "|" + pat + "|" + per + "|"
                                + user);
                        PersonalhrUtil.getService().logEvent(PhrLogEvent.ACCESS_NOT_ALLOWED, new Date(), user, 
                            ((HttpServletRequest) request).getSession().getId(), pat, 
                            "requestURI="+requestURI+"; client_ip=" + request.getLocalAddr());
                        this.config.getServletContext().getRequestDispatcher(this.loginForm).forward(request, response);
                    } else {
                        if (requestURI.toLowerCase().contains("index.htm") && !requestURI.toLowerCase().contains("/phr/")) {
                            String redirect = requestURI;
                            if (phrRole != null) {
                                final Integer userPersonId = (user == null ? null : user.getPerson().getId());
                                if (PhrBasicRole.PHR_PATIENT.getValue().equals(phrRole)) {
                                    if (userPersonId != null) {
                                        redirect = "/phr/patientDashboard.form?patientId=" + userPersonId;
                                    } else {
                                        this.log.error("Error: PHR Patient's person id is null!");
                                    }
                                    //PersonalhrUtil.addTemporayPrivileges();
                                } else if (PhrBasicRole.PHR_RESTRICTED_USER.getValue().equals(phrRole)) {
                                    if (userPersonId != null) {
                                        redirect = "/phr/restrictedUserDashboard.form?personId=" + userPersonId;
                                    } else {
                                        this.log.error("Error: PHR Restricted user's person id is null!");
                                    }
                                    //PersonalhrUtil.addTemporayPrivileges();
                                } else if (PhrBasicRole.PHR_ADMINISTRATOR.getValue().equals(phrRole)) {
                                    redirect = "/phr/findPatient.htm";
                                    //PersonalhrUtil.addTemporayPrivileges();
                                }
                                
                                this.log.debug("***URL access is redirected to " + redirect + " for user " + user + "|" + requestURI
                                        + "|" + pat + "|" + per);
                                
                                PersonalhrUtil.getService().logEvent(PhrLogEvent.ACCESS_REDIRECT, new Date(), user, 
                                    ((HttpServletRequest) request).getSession().getId(), pat, 
                                    "redirect="+redirect+"; client_ip=" + request.getLocalAddr());
                                
                                ((HttpServletResponse) response).sendRedirect(((HttpServletRequest) request).getContextPath()
                                        + redirect);
                                return;
                            }
                        }
                    }
                    
                    this.log.debug("***URL access is checked and allowed for this authenticated user!!! " + user + "|" + requestURI + "|"
                            + pat + "|" + per);
                    
                    if("POST".equalsIgnoreCase(((HttpServletRequest) request).getMethod())) {
                        String command = request.getParameter("command");
                        if(command != null) {
                            PersonalhrUtil.getService().logEvent(PhrLogEvent.SUBMIT_CHANGES, new Date(), user, 
                                ((HttpServletRequest) request).getSession().getId(), pat, 
                                "requestURI="+requestURI+"; command=" + command +"; client_ip=" + request.getLocalAddr());
                        }
                        else if(!(requestURI.toLowerCase().contains("/admin")||
                               requestURI.toLowerCase().contains("get")
                                ||requestURI.toLowerCase().contains("find")
                                ||requestURI.toLowerCase().contains("list")
                                ||requestURI.toLowerCase().contains("check")
                                ||requestURI.toLowerCase().contains("validate"))){
                            PersonalhrUtil.getService().logEvent(PhrLogEvent.SUBMIT_CHANGES, new Date(), user, 
                            ((HttpServletRequest) request).getSession().getId(), pat, 
                            "requestURI="+requestURI+"; client_ip=" + request.getLocalAddr());
                        }
                    }
                } finally {
                    PersonalhrUtil.removeMinimumTemporaryPrivileges();
                }
            }
            else {
                this.log.debug("***URL access is unchecked and allowed for this authenticated user!!! " + user + "|" + requestURI + "|"
                    + patientId + "|" + personId + "|" + encounterId);
            }                            
        } else {
            this.log.debug("***URL access is allowed for all unauthenticated users!!! " + requestURI + "|" + patientId + "|"
                    + personId + "|" + encounterId);
        }
        
        try {
            //Add temporary privilege
            if (phrRole != null) {
                PersonalhrUtil.addTemporaryPrivileges();  
            }
            
            chain.doFilter(request, response);
        } finally {
            if (phrRole != null) {
                PersonalhrUtil.removeTemporaryPrivileges();
            }
        }
    }
    
    /**
     * Method to check if the request url is an excluded url.
     * 
     * @param requestURI
     * @param excludeURL
     * @return
     */
    private boolean shouldCheckAccessToUrl(final String requestURI) {
        for (final String url : this.excludedURLs) {
            if (requestURI.contains(url)) {
                this.log.debug("shouldCheckAccessToUrl: " + false + " for " + requestURI + " due to " + url);
                return false;
            }
        }
        
        this.log.debug("shouldCheckAccessToUrl: " + true + " for " + requestURI);
        return true;
    }
    
    /**
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    @Override
    public void init(final FilterConfig config) throws ServletException {
        this.config = config;
        this.excludeURL = config.getInitParameter("excludeURL");
        this.excludedURLs = this.excludeURL.split(",");
        this.loginForm = config.getInitParameter("loginForm");
    }
    
}
