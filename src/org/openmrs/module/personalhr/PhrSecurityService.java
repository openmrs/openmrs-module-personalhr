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

import java.util.List;

import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.User;
import org.openmrs.module.personalhr.db.PhrAllowedUrlDAO;
import org.openmrs.module.personalhr.db.PhrSecurityRuleDAO;
import org.openmrs.module.personalhr.db.PhrSharingTokenDAO;


/**
 *
 */
public interface PhrSecurityService {
    public enum PhrDynamicRole {
        ADMINISTRATOR ("Administrator"),
        OWNER ("Owner"),
        SHARE_JOURNAL ("Share Journal"),
        SHARE_MEDICAL ("Share Medical");
        
        private String value;
        
        PhrDynamicRole(String role) {
            this.value = role;
        }
        
        public String getValue() {
            return value;
        }
    };
    
    public enum PhrBasicRole {
        PHR_ADMINISTRATOR ("PHR Administrator"),
        PHR_PATIENT ("PHR Patient"),
        PHR_RESTRICTED_USER ("PHR Restricted User");
         
        private String value;
        
        PhrBasicRole(String role) {
            this.value = role;
        }
        
        public String getValue() {
            return value;
        }
    };    
    public boolean isUrlAllowed(String requestedUrl, 
                                Patient requestedPatient, 
                                Person requestedPerson, 
                                User requestingUser);
    
    public boolean hasPrivilege(String privilege, 
                                Patient requestedPatient, 
                                Person requestedPerson, 
                                User requestingUser);
    
    public List<PhrDynamicRole> getDynamicRoles(Patient requestedPatient, 
                                                Person requestedPerson, 
                                                User requestingUser);
    
    public PhrSecurityRuleDAO getSecurityRuleDao();

    
    public void setSecurityRuleDao(PhrSecurityRuleDAO securityRuleDao);
    
    public PhrAllowedUrlDAO getAllowedUrlDao();

    
    public void setAllowedUrlDao(PhrAllowedUrlDAO allowedUrlDao);

    
    public PhrSharingTokenDAO getSharingTokenDao();

    
    public void setSharingTokenDao(PhrSharingTokenDAO sharingTokenDao);

}
