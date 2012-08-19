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

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.User;
import org.openmrs.module.personalhr.db.PhrAllowedUrlDAO;
import org.openmrs.module.personalhr.db.PhrPrivilegeDAO;
import org.openmrs.module.personalhr.db.PhrSharingTokenDAO;

import org.openmrs.module.personalhr.db.PhrApplyDAO;
/**
 * Interface of the services provided by PHR module
 * 
 * @author hxiao
 */
public interface PhrService {
    
    
    /**
     * Defines all sorts of relation type
     * 
     */
    public enum PhrRelationType {
        /**
         * Relation Type
         */
        DOCTOR("Doctor"), 
        /**
         * Relation Type
         */
        CAREGIVER("Caregiver"),
        /**
         * Relation Type
         */
        SPOUSE("Spouse"),
        /**
         * Relation Type
         */
        SIBLING("Sibling"),
        /**
         * Relation Type
         */
        CHILD("Child"),
        /**
         * Relation Type
         */
        OTHER("Other");
        
        private final String value;
        
        PhrRelationType(final String type) {
            this.value = type;
        }
       
        
        /**
         * Get a string value of the relation type
         * 
         * @return string value of this relation type
         */
        public String getValue() {
            return value;
        }
    };
    
    
    /**
     * Defines all sorts of PHR basic roles
     * 
     */
    public enum PhrBasicRole {
        /**
         * Basic Role
         */
        PHR_ADMINISTRATOR("PHR Administrator"), 
        /**
         * Basic Role
         */
        PHR_PATIENT("PHR Patient"), 
        /**
         * Basic Role
         */
       PHR_RESTRICTED_USER("PHR Restricted User");
        
        private final String value;
        
        PhrBasicRole(final String role) {
            this.value = role;
        }
        
        /**
         * Get the string representation of the basic role
         * 
         * @return string value of the basic role
         */
        public String getValue() {
            return value;
        }
    };
    
    /**
     * Define all sorts of PHR basic privileges
     * 
     */
    public enum PhrBasicPrivilege {        
        /**
         * Basic Privilege
         */
        PHR_ADMINISTRATOR_PRIV("PHR All Patients Access"), 
        /**
         * Basic Privilege
         */
        PHR_PATIENT_PRIV("PHR Single Patient Access"), 
        /**
         * Basic Privilege
         */
        PHR_RESTRICTED_USER_PRIV("PHR Restricted Patient Access");
        
        private final String value;
        
        PhrBasicPrivilege(final String priv) {
            this.value = priv;
        }
        
        /**
         * String value of basic privilege
         * 
         * @return a string value of basic privilege
         */
        public String getValue() {
            return value;
        }
    };
    
    /**
     * Check if a given URL is allowed for a given user to access data of a given patient or person
     * 
     * @param requestedUrl URL requested for access
     * @param requestedPatient patient whose data is contained in the request
     * @param requestedPerson person whose data is contained in the request
     * @param requestingUser user who made the request
     * @return true is this URL is allowed
     */
    public boolean isUrlAllowed(String requestedUrl, Patient requestedPatient, Person requestedPerson, User requestingUser);
    
    /**
     * Check if a given privilege is granted to a given user to access data of a given patient or person
     * 
     * @param privilege a given privilege checked for
     * @param requestedPatient patient whose data is contained in the request
     * @param requestedPerson person whose data is contained in the request
     * @param requestingUser user who made the request
     * @return true is this URL is allowed
     */
    public boolean hasPrivilege(String privilege, Patient requestedPatient, Person requestedPerson, User requestingUser);
    
    /**
     * Get a list of dynamic roles assigned to a given user to access data of a given patient or person
     * 
     * @param requestedPatient patient whose data is contained in the request
     * @param requestedPerson person whose data is contained in the request
     * @param requestingUser user who made the request
     * @return true is this URL is allowed
     */
    public List<String> getDynamicRoles(Patient requestedPatient, Person requestedPerson, User requestingUser);
    
    /**
     * Get the list persons who are related to a given person
     * 
     * @param person a given person object
     * @return a list of persons who have relationship with the given person
     */
    public List<Person> getRelatedPersons(Person person);
    
    /**
     * Get all PHR Users
     * 
     * @return person objects of all PHR Users
     */
    public List<Person> getAllPhrUsers();

    /**
     * Get all PHR Patient Users
     * 
     * @return person objects of all PHR Patient Users
     */
    public List<Person> getAllPhrPatients();

    /**
     * Get patient object of a given person
     * 
     * @param person given person obhect
     * @return patient object
     */
    public Patient getPatient(final Person person);
    
    /**
     * Log a specific event
     * 
     * @param eventType event type
     * @param eventDate event date
     * @param user user who generated this event
     * @param sessionId user session id
     * @param patient patient whose data is associated with this event
     * @param eventContent additional information of this event 
     */
    public void logEvent(String eventType, Date eventDate, User user, 
                         String sessionId, Patient patient, String eventContent);
   
    //DAO access
    
    /**
     * Get PrivilegeDAO object
     * 
     * @return PrivilegeDAO object
     */
    public PhrPrivilegeDAO getPrivilegeDao();
	
	/**
     * Get ApplyDAO object
     * 
     * @return ApplyDAO object
     */
    public PhrApplyDAO getApplyDao();
    
    /**
     * Set PhrAllowedUrlDAO object
     * 
     * @param privilegeDao PhrPrivilegeDAO object
     */
    public void setPrivilegeDao(PhrPrivilegeDAO privilegeDao);
    

    public void setApplyDao(PhrApplyDAO privilegeDao);
	
    /**
     * Get PhrAllowedUrlDAO object
     * 
     * @return PhrAllowedUrlDAO object
     */
    public PhrAllowedUrlDAO getAllowedUrlDao();
    
    /**
     * Set PhrAllowedUrlDAO object
     * 
     * @param allowedUrlDao PhrAllowedUrlDAO object
     */
    public void setAllowedUrlDao(PhrAllowedUrlDAO allowedUrlDao);
    
    /**
     * Get PhrSharingTokenDAO object
     * 
     * @return PhrSharingTokenDAO object
     */
    public PhrSharingTokenDAO getSharingTokenDao();
    
    /**
     * Set PhrAllowedUrlDAO object
     * 
     * @param sharingTokenDao PhrAllowedUrlDAO object
     */
    public void setSharingTokenDao(PhrSharingTokenDAO sharingTokenDao);
    
    /**
     * Get PHR role of a given user
     * 
     * @param user a given user
     * @return PHR role as a string value
     */
    public String getPhrRole(User user);
    
    /**
     * Get all sharing types
     * 
     * @return all sharing types
     */
    public Set<String> getSharingTypes();

    /**
     * Check if a given Person has a given basic role or not   
     * This check is based on PHR security rule table and user role and relationship to given patient
     * 
     * @param person person whose privilege is checked
     * @param role basic role to check
     * @return true if the person has the given basic PHR role
     */
    boolean hasBasicRole(Person person, PhrBasicRole role);
    
}
