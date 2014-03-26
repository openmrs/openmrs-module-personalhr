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
package org.openmrs.module.personalhr.web;

import java.util.Date;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;

import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.MessagingService;
import org.openmrs.module.personalhr.PersonalhrUtil;
import org.openmrs.module.personalhr.model.PhrAllowedUrl;
import org.openmrs.module.personalhr.model.PhrLogEvent;
import org.openmrs.module.personalhr.model.PhrPrivilege;
import org.openmrs.module.personalhr.service.PhrAllowedUrlService;
import org.openmrs.module.personalhr.service.PhrPrivilegeService;

/**
 * DWR services called directly from jsp pages
 * 
 * @author hxiao
 */
public class DWRPersonalhrService {
    protected final Log log = LogFactory.getLog(getClass());
    private final String admin_email_address = "cancertoolkit-l@list.regenstrief.org"; 
    
	/**
	 * PHR security configuration: add allowed URL
	 * 
	 * @param allowedUrl allowed URL string
	 * @param privilege  privilege required
	 * @param description description of this rule
	 */
	public void addAllowedUrl(String allowedUrl, String privilege, String description) {
		log.debug("Calling DWRPersonalhrService.addAllowedUrl...allowedUrl=" + allowedUrl + ", privilege=" + privilege);
		PhrAllowedUrl url = new PhrAllowedUrl();
		url.setAllowedUrl(allowedUrl);
		url.setPrivilege(privilege);
		url.setDescription(description);
		url.setCreator(Context.getAuthenticatedUser());
		url.setDateCreated(new Date());

		Context.getService(PhrAllowedUrlService.class).savePhrAllowedUrl(url);
	}
	
    /**
     * PHR security configuration: add allowed URL
     * 
     * @param requiredRole PHR defined role
     * @param privilege  privilege required
     * @param description description of this rule
     */
    public void addPhrPrivilege(String privilege, String requiredRole, String description) {
        log.debug("Calling DWRPersonalhrService.addPhrPrivilege...requiredRole=" + requiredRole + ", privilege=" + privilege);

        PhrPrivilege rule = new PhrPrivilege();
        rule.setPrivilege(privilege);
        rule.setRequiredRole(requiredRole);
        rule.setDescription(description);
        rule.setCreator(Context.getAuthenticatedUser());
        rule.setDateCreated(new Date());

		Context.getService(PhrPrivilegeService.class).savePhrPrivilege(rule);
    }
    
    /**
     * Log the event when user switch tabs
     * 
     * @param eventContent describe the name of tab switched to
     */
    public void logChangeTabEvent(String eventContent) {
        WebContext webContext = WebContextFactory.get();
        HttpSession session = webContext.getSession();
        String sessionId = session.getId();
        Object o = session.getAttribute("org.openmrs.portlet.patientId");
        Patient pat = (o==null ? null:Context.getPatientService().getPatient((Integer) o));

        PersonalhrUtil.getService().logEvent(PhrLogEvent.CHANGE_TAB, new Date(), Context.getAuthenticatedUser(), 
            sessionId, pat, eventContent);
        
    }
    
    /**
     * Allow user to send questions or feedback to us (cancer toolkit administrators)
     * 
     * @param messageContent content of message entered by the user
     */
    public void sendMessageToUs(String messageContent) {
        try {
            WebContext webContext = WebContextFactory.get();
            HttpSession session = webContext.getSession();
            String sessionId = (session==null? "":session.getId());

            if(Context.isAuthenticated()) {
                messageContent = "(Sent from Personal Cancer Toolkit user " + Context.getAuthenticatedUser().getUsername() + ": )\n" + messageContent;
            } 
            PersonalhrUtil.getService().logEvent(PhrLogEvent.CONTACT_US, new Date(), Context.getAuthenticatedUser(), 
                sessionId, null, messageContent);
            
            Context.getService(MessagingService.class).sendMessage(messageContent, admin_email_address,
                org.openmrs.module.messaging.email.EmailProtocol.class);            
            log.debug("*****A message has been sent to " + admin_email_address + ":\n" + messageContent);
        } catch (final Exception e) {
            this.log.debug("Unable to send message to " + admin_email_address + ":\n" + messageContent, e);
        } catch (final NoClassDefFoundError e) {
            this.log.debug("Messaging module is not found, unable to send message to " + admin_email_address+ ":\n" + messageContent, e);           
        }        
    }    
    
}
