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

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    
    public enum PhrSharingType {
        SHARE_NOTHING("Select One"), SHARE_JOURNAL("Share Journal"), SHARE_MEDICAL("Share Medical"), SHARE_ALL("Share All");
        
        private final String value;
        
        PhrSharingType(final String type) {
            this.value = type;
        }
        
        public String getValue() {
            return value;
        }
    };
    
    public enum PhrRelationType {
        DOCTOR("Doctor"), CAREGIVER("Caregiver"), SIBLING("Sibling"), CHILD("Child"), OTHER("Other");
        
        private final String value;
        
        PhrRelationType(final String type) {
            this.value = type;
        }
        
        public String getValue() {
            return value;
        }
    };
    
    public enum PhrDynamicRole {
        ADMINISTRATOR("Administrator"), OWNER("Owner"), SHARE_JOURNAL("Share Journal"), SHARE_MEDICAL("Share Medical");
        
        private final String value;
        
        private static final Map<String, PhrDynamicRole> lookup = new HashMap<String, PhrDynamicRole>();
        
        static {
            for (final PhrDynamicRole s : EnumSet.allOf(PhrDynamicRole.class)) {
                lookup.put(s.getValue(), s);
            }
        }
        
        PhrDynamicRole(final String role) {
            this.value = role;
        }
        
        public String getValue() {
            return value;
        }
        
        public static PhrDynamicRole getRole(final String value) {
            return lookup.get(value);
        }
    };
    
    public enum PhrBasicRole {
        PHR_ADMINISTRATOR("PHR Administrator"), PHR_PATIENT("PHR Patient"), PHR_RESTRICTED_USER("PHR Restricted User");
        
        private final String value;
        
        PhrBasicRole(final String role) {
            this.value = role;
        }
        
        public String getValue() {
            return value;
        }
    };
    
    public enum PhrBasicPrivilege {
        PHR_ADMINISTRATOR_PRIV("PHR All Patients Access"), PHR_PATIENT_PRIV("PHR Single Patient Access"), PHR_RESTRICTED_USER_PRIV(
                "PHR Restricted Patient Access");
        
        private final String value;
        
        PhrBasicPrivilege(final String priv) {
            this.value = priv;
        }
        
        public String getValue() {
            return value;
        }
    };
    
    public boolean isUrlAllowed(String requestedUrl, Patient requestedPatient, Person requestedPerson, User requestingUser);
    
    public boolean hasPrivilege(String privilege, Patient requestedPatient, Person requestedPerson, User requestingUser);
    
    public List<PhrDynamicRole> getDynamicRoles(Patient requestedPatient, Person requestedPerson, User requestingUser);
    
    public List<Person> getRelatedPersons(Person person);
    
    public PhrSecurityRuleDAO getSecurityRuleDao();
    
    public void setSecurityRuleDao(PhrSecurityRuleDAO securityRuleDao);
    
    public PhrAllowedUrlDAO getAllowedUrlDao();
    
    public void setAllowedUrlDao(PhrAllowedUrlDAO allowedUrlDao);
    
    public PhrSharingTokenDAO getSharingTokenDao();
    
    public void setSharingTokenDao(PhrSharingTokenDAO sharingTokenDao);
    
    /**
     * Auto generated method comment
     * 
     * @param user
     * @return
     */
    public String getPhrRole(User user);
    
}
