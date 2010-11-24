/**
 * The contents of this file are subject to the Regenstrief Public License
 * Version 1.0 (the "License"); you may not use this file except in compliance with the License.
 * Please contact Regenstrief Institute if you would like to obtain a copy of the license.
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) Regenstrief Institute.  All Rights Reserved.
 */
package org.openmrs.module.personalhr;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.User;

import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.personalhr.db.*;


/**
 *
 */
public class PhrSecurityServiceImpl extends BaseOpenmrsService implements PhrSecurityService {
    private PhrSecurityRuleDAO securityRuleDao;
    private PhrAllowedUrlDAO allowedUrlDao;
    private PhrSharingTokenDAO sharingTokenDao;
    
    public boolean isUrlAllowed(String requestedUrl, 
                                Patient requestedPatient, 
                                Person requestedPerson, 
                                User requestingUser) {
        List<PhrAllowedUrl> urls  = allowedUrlDao.getByUrl(requestedUrl);
        for(PhrAllowedUrl url : urls) {
            if(url != null) {
                return hasPrivilege(url.getPrivilege(), requestedPatient, requestedPerson, requestingUser);
            }
        } 
        
        return false;
    }
    
    public boolean hasPrivilege(String privilege, 
                                Patient requestedPatient, 
                                Person requestedPerson, 
                                User requestingUser) {
        
        List<PhrSecurityRule> rules  = securityRuleDao.getByPrivilege(privilege);
        List<PhrDynamicRole> roles = getDynamicRoles(requestedPatient, requestedPerson, requestingUser);
        
        for(PhrSecurityRule rule : rules) {
            if(rule != null) {
                String reqRole = rule.getRequiredRole();
                
                for(PhrDynamicRole role : roles) {
                  if(role.getValue().equalsIgnoreCase(reqRole)) {
                    return  true;                  
                  }
                }
            }
        } 
        
        return false;
    }
    
    public List<PhrDynamicRole> getDynamicRoles(Patient requestedPatient, 
                                                Person requestedPerson, 
                                                User requestingUser) {
        List<PhrDynamicRole> roles = null;
        
        PhrSharingToken token = sharingTokenDao.getSharingToken(requestedPatient, requestedPerson, requestingUser);
        
        if(token != null) {
            String shareType = token.getShareType();
            if(shareType != null && !shareType.trim().isEmpty()) {
               if(roles == null) {
                   roles = new ArrayList<PhrDynamicRole>(); 
               }
               roles.add(PhrSecurityService.PhrDynamicRole.valueOf(shareType));
            }
        }
        return roles;
        
    }

    
    public PhrSecurityRuleDAO getSecurityRuleDao() {
        return securityRuleDao;
    }

    
    public void setSecurityRuleDao(PhrSecurityRuleDAO securityRuleDao) {
        this.securityRuleDao = securityRuleDao;
    }

    
    public PhrAllowedUrlDAO getAllowedUrlDao() {
        return allowedUrlDao;
    }

    
    public void setAllowedUrlDao(PhrAllowedUrlDAO allowedUrlDao) {
        this.allowedUrlDao = allowedUrlDao;
    }

    
    public PhrSharingTokenDAO getSharingTokenDao() {
        return sharingTokenDao;
    }

    
    public void setSharingTokenDao(PhrSharingTokenDAO sharingTokenDao) {
        this.sharingTokenDao = sharingTokenDao;
    }

}
