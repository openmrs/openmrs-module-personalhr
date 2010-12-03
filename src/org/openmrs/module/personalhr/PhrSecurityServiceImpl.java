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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.User;

import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.personalhr.db.*;


/**
 *
 */
public class PhrSecurityServiceImpl extends BaseOpenmrsService implements PhrSecurityService {
    protected final Log log = LogFactory.getLog(getClass());
    
    private PhrSecurityRuleDAO securityRuleDao;
    private PhrAllowedUrlDAO allowedUrlDao;
    private PhrSharingTokenDAO sharingTokenDao;
      
    /**
     * Called only after user has been authenticated (i.e. requestingUser != null)
     * @see org.openmrs.module.personalhr.PhrSecurityService#isUrlAllowed(java.lang.String, org.openmrs.Patient, org.openmrs.Person, org.openmrs.User)
     */
    public boolean isUrlAllowed(String requestedUrl, 
                                Patient requestedPatient, 
                                Person requestedPerson, 
                                User requestingUser) {
        log.debug("PhrSecurityServiceImpl:isUrlAllowed->" + requestedUrl + "|"+requestedPatient+"|"+requestedPerson+"|"+requestingUser);
        if(requestingUser == null) {
            log.warn("Allowed -> User not authenticated yet: " + requestedUrl + "|"+requestedPatient+"|"+requestedPerson+"|"+requestingUser);
            return true;
        }
        
        //Check access to /phr/ domain
        if(requestedUrl.startsWith("/phr/")) {
            if(getPhrRole(requestingUser) != null) {
                log.debug("Allowed -> PHR User accessing /phr/ domain: " + requestedUrl + "|"+requestedPatient+"|"+requestedPerson+"|"+requestingUser);
                return true;
            } else {
                log.warn("Not allowed - > Non PHR User accessing /phr/ domain: " + requestedUrl + "|"+requestedPatient+"|"+requestedPerson+"|"+requestingUser);
                return false;
            }
        }
        
        //Check access to non /phr/ domain
        List<PhrAllowedUrl> urls  = allowedUrlDao.getByUrl(requestedUrl);
        for(PhrAllowedUrl url : urls) {
            if(url != null) {
                return hasPrivilege(url.getPrivilege(), requestedPatient, requestedPerson, requestingUser);
            }
        } 
        
        return false;
    }
    
    /**
     * Auto generated method comment
     * 
     * @param requestingUser
     * @return
     */
    private String getPhrRole(User user) {
        log.debug("PhrSecurityServiceImpl:igetPhrRole->" + user);
        // TODO Auto-generated method stub
        if(user.hasRole(PhrBasicRole.PHR_ADMINISTRATOR.getValue(), true)) {
            return PhrBasicRole.PHR_ADMINISTRATOR.getValue();
        } else if(user.hasRole(PhrBasicRole.PHR_PATIENT.getValue(), true)) {
            return PhrBasicRole.PHR_PATIENT.getValue();
        } else if(user.hasRole(PhrBasicRole.PHR_RESTRICTED_USER.getValue(), true)) {
            return PhrBasicRole.PHR_RESTRICTED_USER.getValue();
        } else {            
            return null;
        }
    }
    

    public boolean hasPrivilege(String privilege, 
                                Patient requestedPatient, 
                                Person requestedPerson, 
                                User user) {
        log.debug("PhrSecurityServiceImpl:hasPrivilege->" + privilege + "|"+requestedPatient+"|"+requestedPerson+"|"+user);
        
        List<PhrSecurityRule> rules  = securityRuleDao.getByPrivilege(privilege);
        List<PhrDynamicRole> roles = getDynamicRoles(requestedPatient, requestedPerson, user);
        
        for(PhrSecurityRule rule : rules) {
            if(rule != null) {
                String reqRole = rule.getRequiredRole();
                
                for(PhrDynamicRole role : roles) {
                  if(role.getValue().equalsIgnoreCase(reqRole)) {
                    log.debug("hasPrivilege returns true ->" + privilege + "|"+requestedPatient+"|"+requestedPerson+"|"+user);
                    return  true;                  
                  }
                }
            }
        } 
        
        log.debug("hasPrivilege returns false ->" + privilege + "|"+requestedPatient+"|"+requestedPerson+"|"+user);
        return false;
    }
    
    public List<PhrDynamicRole> getDynamicRoles(Patient requestedPatient, 
                                                Person requestedPerson, 
                                                User user) {
        log.debug("PhrSecurityServiceImpl:getDynamicRoles->" + requestedPatient+"|"+requestedPerson+"|"+user);
        List<PhrDynamicRole> roles = new ArrayList<PhrDynamicRole>();
        
        //check for administrator privilege
        if(user.hasRole(PhrBasicRole.PHR_ADMINISTRATOR.getValue(), true)) {
            roles.add(PhrSecurityService.PhrDynamicRole.ADMINISTRATOR);
            log.debug("getDynamicRoles->ADMINISTRATOR");
        } 
        
        //check for owner status
        if(isSamePerson(user, requestedPatient) || isSamePerson(user, requestedPerson)) {
            roles.add(PhrSecurityService.PhrDynamicRole.OWNER);
            log.debug("getDynamicRoles->OWNER");
        } 
                
        //check for sharing authorization
        PhrSharingToken token = sharingTokenDao.getSharingToken(requestedPatient, requestedPerson, user);
        
        if(token != null) {
            String shareType = token.getShareType();
            if(shareType != null && !shareType.trim().isEmpty()) {
               roles.add(PhrSecurityService.PhrDynamicRole.valueOf(shareType));
               log.debug("getDynamicRoles->shareType");
            }
        }
        
        if(roles.isEmpty()) {
            log.debug("getDynamicRoles returns null -> " + requestedPatient+"|"+requestedPerson+"|"+user);
            return null;
        }
        
        return roles;
        
    }

    
    /**
     * Auto generated method comment
     * 
     * @param user
     * @param requestedPerson
     * @return
     */
    private boolean isSamePerson(User user, Person requestedPerson) {
        log.debug("PhrSecurityServiceImpl:isSamePerson->" + user+"|"+requestedPerson);
        // TODO Auto-generated method stub
        if(user == null || requestedPerson==null) {
            log.debug("isSamePerson(Person)=false ->" + user + "|" + requestedPerson);
            return false;
        }
        
        return user.getPerson().getPersonId() == requestedPerson.getPersonId();
    }

    /**
     * Auto generated method comment
     * 
     * @param user
     * @param requestedPatient
     * @return
     */
    private boolean isSamePerson(User user, Patient requestedPatient) {
        log.debug("PhrSecurityServiceImpl:isSamePerson->" + user+"|"+requestedPatient);
       // TODO Auto-generated method stub
        if(user == null || requestedPatient==null) {
            log.debug("isSamePerson(Patient)=false ->" + user + "|" + requestedPatient);
            return false;
        }
        
        return user.getPerson().getPersonId() == requestedPatient.getPersonId();
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
