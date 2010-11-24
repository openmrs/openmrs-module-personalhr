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


/**
 *
 */
public interface PhrSecurityService {
    public enum PhrDynamicRole {
        ADMINISTRATOR ("ADMINISTRATOR"),
        OWNER ("OWNER"),
        SHARE_JOURNAL ("SHARE_JOURNAL"),
        SHARE_MEDICAL ("SHARE_MEDICAL");
        
        private String value;
        
        PhrDynamicRole(String role) {
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

}
