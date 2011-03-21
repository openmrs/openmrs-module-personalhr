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
package org.openmrs.module.personalhr.web.taglib;

import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.UserContext;
import org.openmrs.module.personalhr.PersonalhrUtil;
import org.openmrs.module.personalhr.PhrSecurityService;

public class PrivilegeTag extends TagSupport {
    
    public static final long serialVersionUID = 11233L;
    
    private final Log log = LogFactory.getLog(getClass());
    
    private String privilege;
    
    private String inverse;
    
    @Override
    public int doStartTag() {
        this.log.debug("PHR PrivilegeTag started...");
        
        final UserContext userContext = Context.getUserContext();
        
        if (!userContext.isAuthenticated()) {
            return SKIP_BODY;
        }
        
        final User user = userContext.getAuthenticatedUser();
        //log.debug("Checking user " + user + " for privs " + privilege);           
        
        Integer patientId = PersonalhrUtil.getInteger(this.pageContext.getAttribute("patientId"));
        //log.debug("1Checking user " + user + " for privs " + privilege + " on patient " + patientId);            
        if (patientId == null) {
            patientId = PersonalhrUtil.getInteger(this.pageContext.getRequest().getParameter("patientId"));
        }
        //log.debug("2Checking user " + user + " for privs " + privilege + " on patient " + patientId);            
        if (patientId == null) {
            patientId = PersonalhrUtil.getInteger(this.pageContext.getAttribute("patientId"));
        }
        //log.debug("3Checking user " + user + " for privs " + privilege + " on patient " + patientId);            
        if (patientId == null) {
            patientId = PersonalhrUtil.getInteger(this.pageContext.getRequest()
                    .getAttribute("org.openmrs.portlet.patientId"));
            //log.debug("4Checking user " + user + " for privs " + privilege + " on patient " + patientId);            
        }
        
        final Patient pat = patientId == null ? null : Context.getPatientService().getPatient(patientId);
        if (pat != null) {
            this.log.debug("Checking user " + user + " for privs " + this.privilege + " on patient " + pat);
        }
        
        Integer personId = PersonalhrUtil.getInteger(this.pageContext.getAttribute("personId"));
        //log.debug("1Checking user " + user + " for privs " + privilege + " on person " + personId);            
        if (personId == null) {
            personId = PersonalhrUtil.getInteger(this.pageContext.getRequest().getParameter("personId"));
        }
        //log.debug("2Checking user " + user + " for privs " + privilege + " on person " + personId);            
        if (personId == null) {
            personId = PersonalhrUtil.getInteger(this.pageContext.getRequest().getAttribute("personId"));
        }
        //log.debug("3Checking user " + user + " for privs " + privilege + " on person " + personId);            
        if (personId == null) {
            personId = PersonalhrUtil.getInteger(this.pageContext.getRequest().getAttribute("org.openmrs.portlet.personId"));
            //log.debug("4Checking user " + user + " for privs " + privilege + " on person " + personId);            
        }
        
        final Person per = personId == null ? null : Context.getPersonService().getPerson(personId);
        if (per != null) {
            this.log.debug("Checking user " + user + " for privs " + this.privilege + " on person " + per);
        }
        
        if ((per == null) && (pat == null)) {
            this.log.debug("Checking user " + user + " for privs " + this.privilege);
        }
        
        boolean hasPrivilege = false;
        final PhrSecurityService serv = PersonalhrUtil.getService();
        if (this.privilege.contains(",")) {
            final String[] privs = this.privilege.split(",");
            for (final String p : privs) {
                if (serv.hasPrivilege(p, pat, per, user)) {
                    hasPrivilege = true;
                    break;
                }
            }
        } else {
            hasPrivilege = serv.hasPrivilege(this.privilege, pat, per, user);
        }
        
        // allow inversing
        boolean isInverted = false;
        if (this.inverse != null) {
            isInverted = "true".equals(this.inverse.toLowerCase());
        }
        
        if ((hasPrivilege && !isInverted) || (!hasPrivilege && isInverted)) {
            this.pageContext.setAttribute("authenticatedUser", userContext.getAuthenticatedUser());
            return EVAL_BODY_INCLUDE;
        } else {
            return SKIP_BODY;
        }
    }
    
    /**
     * @return Returns the privilege.
     */
    public String getPrivilege() {
        return this.privilege;
    }
    
    /**
     * @param converse The privilege to set.
     */
    public void setPrivilege(final String privilege) {
        this.privilege = privilege;
    }
    
    /**
     * @return Returns the inverse.
     */
    public String getInverse() {
        return this.inverse;
    }
    
    /**
     * @param inverse The inverse to set.
     */
    public void setInverse(final String inverse) {
        this.inverse = inverse;
    }
}
