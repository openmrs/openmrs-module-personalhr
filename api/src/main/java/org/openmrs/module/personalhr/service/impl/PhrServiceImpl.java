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
package org.openmrs.module.personalhr.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.personalhr.model.PhrAllowedUrl;
import org.openmrs.module.personalhr.model.PhrLogEvent;
import org.openmrs.module.personalhr.model.PhrPrivilege;
import org.openmrs.module.personalhr.model.PhrSharingToken;
import org.openmrs.module.personalhr.service.PhrAllowedUrlService;
import org.openmrs.module.personalhr.service.PhrLogEventService;
import org.openmrs.module.personalhr.service.PhrPrivilegeService;
import org.openmrs.module.personalhr.service.PhrService;
import org.openmrs.module.personalhr.service.PhrSharingTokenService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Implementation of the services provided by PHR module
 * 
 * @author hxiao
 */
public class PhrServiceImpl extends BaseOpenmrsService implements PhrService {
    
    protected final Log log = LogFactory.getLog(getClass());
    
    /**
     * Check if a given PHR user is allowed to access a given URL   
     * This check is based on PHR allowed url list and corresponding privilege required
     * The PHR allowed url list may contain wild card '*'
     * 
     * @param requestingUser must not be null and must have a PHR role
     * 
     * @see org.openmrs.module.personalhr.service.PhrService#isUrlAllowed(java.lang.String,
     *      org.openmrs.Patient, org.openmrs.Person, org.openmrs.User)
     */
    @Override
    public boolean isUrlAllowed(final String requestedUrl, final Patient requestedPatient, final Person requestedPerson,
                                final User requestingUser) {
        this.log.debug("PhrServiceImpl:isUrlAllowed->" + requestedUrl + "|" + requestedPatient + "|"
                + requestedPerson + "|" + requestingUser);
                
        //Get all allowed URL list 
        final List<PhrAllowedUrl> urls = Context.getService(PhrAllowedUrlService.class).getAllPhrAllowedUrls();
        
        boolean hasPriv = false;        
        if (urls != null) {
            for (final PhrAllowedUrl url : urls) {
                if (url != null) {
                    String pattern = url.getAllowedUrl().trim().toLowerCase(); //Handling wild card '*'  
                    pattern = ".*"+pattern.replace(".", "\\.").replace("*", ".*");
                    if(!requestedUrl.toLowerCase().matches(pattern)) {
                       continue;
                    }
                    
                    hasPriv = hasPrivilege(url.getPrivilege(), requestedPatient, requestedPerson, requestingUser);                                        
                    
                    if(!hasPriv) {
                        this.log.debug("URL is allowed but not authorized: "
                            + requestedUrl
                            + "|"
                            + requestedPatient
                            + "|"
                            + requestedPerson
                            + "|"
                            + requestingUser);
                        
                        return hasPriv;
                    }                    
                }
            }
        }
        
        if(hasPriv) {
            this.log.debug("URL is allowed and authorized: "
                + requestedUrl
                + "|"
                + requestedPatient
                + "|"
                + requestedPerson
                + "|"
                + requestingUser);                        
        } else {                
            this.log.warn("URL is not allowed: " + requestedUrl
                    + "|" + requestedPatient + "|" + requestedPerson + "|" + requestingUser);
        }
        return hasPriv;
    }    
    
    /**
     * Get PHR specific role
     * 
     * @param user user object
     * @return PHR specific role
     */
    @Override
    public String getPhrRole(final User user) {
        this.log.debug("PhrServiceImpl:igetPhrRole->" + user);
        // TODO Auto-generated method stub
        if (user.hasRole(PhrBasicRole.PHR_ADMINISTRATOR.getValue(), true)) {
            return PhrBasicRole.PHR_ADMINISTRATOR.getValue();
        } else if (user.hasRole(PhrBasicRole.PHR_PATIENT.getValue(), true)) {
            return PhrBasicRole.PHR_PATIENT.getValue();
        } else if (user.hasRole(PhrBasicRole.PHR_RESTRICTED_USER.getValue(), true)) {
            return PhrBasicRole.PHR_RESTRICTED_USER.getValue();
        } else {
            return null;
        }
    }
    
    /**
     * Check if a given PHR user has a given privilege or not   
     * This check is based on PHR security rule table and user role and relationship to given patient
     * 
     * @param user must not be null and must have a PHR role
     * 
     */
    @Override
    public boolean hasPrivilege(final String privilege, final Patient requestedPatient, final Person requestedPerson,
                                final User user) {
        this.log.debug("PhrServiceImpl:hasPrivilege->" + privilege + "|" + requestedPatient + "|" + requestedPerson
                + "|" + user);
        
        //Handle "PHR Authenticated" and "PHR Administrator" specially
        String phrRole = this.getPhrRole(user);
        if((phrRole != null && "PHR Authenticated".equalsIgnoreCase(privilege)) ||
           (phrRole != null && "PHR Administrator".equalsIgnoreCase(privilege) && 
            "PHR Authenticated".equalsIgnoreCase(phrRole))) {
            this.log.debug("PhrServiceImpl:hasPrivilege returns true: phrRole=" + phrRole);
            return true;
        }
        
        //When url privilege is not specified, or requested patient or person is null for sharing pages,
        //always returns true
        if(privilege == null || privilege.trim().isEmpty()) 
        {
            this.log.debug("PhrServiceImpl:hasPrivilege returns true because no privilege is specified!");
            return true;
        }
                
        //When url privilege is specified, check the database for authorized roles
        final List<PhrPrivilege> rules = Context.getService(PhrPrivilegeService.class).getByPrivilege(privilege);
        final List<String> roles = getDynamicRoles(requestedPatient, requestedPerson, user);
        if (rules != null) {
            //for each required privilege/role
            for (final PhrPrivilege rule : rules) {
                if (rule != null) {
                    final String reqRole = rule.getRequiredRole().toUpperCase();
                    
                    if (roles != null) {
                        //for each role held
                        for (final String role : roles) {
                            if (reqRole.contains(role.toUpperCase()) ||
                                "Administrator".equalsIgnoreCase(role) ||
                                ("Share All".equalsIgnoreCase(role) 
                                 && !"PHR Administrator".equalsIgnoreCase(reqRole)
                                 && !"View Relationships".equalsIgnoreCase(privilege)
                                 //&& !"View Messages".equalsIgnoreCase(privilege)
                                 )) {
                                this.log.debug("hasPrivilege returns true ->" + privilege + "|" + requestedPatient + "|"
                                        + requestedPerson + "|" + user + "|reqRole=" + reqRole + "|role=" + role);
                                return true; //held at least one required role
                            }
                        }
                    }
                }
            }
        }
        
        this.log.debug("hasPrivilege returns false ->" + privilege + "|" + requestedPatient + "|" + requestedPerson + "|"
                + user);
        return false;
    }

    /**
     * Check if a given Person has a given basic role or not   
     * This check is based on PHR security rule table and user role and relationship to given patient
     * 
     * @param person person whose privilege is checked
     * @param role basic role to check
     * @return true if the person has the given basic PHR role
     * 
     */
    @Override
    public boolean hasBasicRole(final Person person, PhrBasicRole role) {
        this.log.debug("PhrServiceImpl:hasBasicRole->" + person);
        List<User> users = Context.getUserService().getUsersByRole(Context.getUserService().getRole(role.getValue()));
        if(users==null) {
            return false;
        }
        for(User u : users) {
            if(u.getPerson().equals(person)) {
                return true;
            }
        }
        return false;
    }

    
    /**
     * Get a list of dynamic roles assigned to a given user to access data of a given patient or person
     * 
     * @param requestedPatient patient whose data is contained in the request
     * @param requestedPerson person whose data is contained in the request
     * @param user user who made the request
     * @return true is this URL is allowed
     */
    @Override
    public List<String> getDynamicRoles(final Patient requestedPatient, final Person requestedPerson, final User user) {
        this.log.debug("PhrServiceImpl:getDynamicRoles->" + requestedPatient + "|" + requestedPerson + "|" + user);
        final List<String> roles = new ArrayList<String>();
        
        //check for administrator privilege
        if (user.hasRole(PhrBasicRole.PHR_ADMINISTRATOR.getValue(), true)) {
            roles.add("ADMINISTRATOR");
            this.log.debug("getDynamicRoles->ADMINISTRATOR");
        }
        
        //check for owner status
        if ((requestedPatient==null && requestedPerson == null) || isSamePerson(user, requestedPatient) || isSamePerson(user, requestedPerson)) {
            roles.add("OWNER");
            this.log.debug("getDynamicRoles->OWNER");
        } else {
            //check for sharing authorization
            final PhrSharingToken token = Context.getService(PhrSharingTokenService.class).getSharingToken(requestedPatient, requestedPerson, user);
            
            if (token != null) {
                final String shareType = token.getShareType();
                final String relationType = token.getRelationType();
                if ((shareType != null) && !shareType.trim().isEmpty()) {
                    this.log.debug("getDynamicRoles for shareType: " + shareType);
                    roles.add(shareType.toUpperCase());
                    roles.add(shareType.toUpperCase() + " & " + relationType);                    
                }
            }
        }
        
        if (roles.isEmpty()) {
            this.log.debug("getDynamicRoles returns null -> " + requestedPatient + "|" + requestedPerson + "|" + user);
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
    private boolean isSamePerson(final User user, final Person requestedPerson) {
        this.log.debug("PhrServiceImpl:isSamePerson->" + user + "|" + requestedPerson);
        if ((user == null) || (requestedPerson == null)) {
            this.log.debug("isSamePerson(Person)=false ->" + user + "|" + requestedPerson);
            return false;
        }
        
        return user.getPerson().getPersonId().equals(requestedPerson.getPersonId());
    }
    
    /**
     * Auto generated method comment
     * 
     * @param user
     * @param requestedPatient
     * @return
     */
    private boolean isSamePerson(final User user, final Patient requestedPatient) {
        this.log.debug("PhrServiceImpl:isSamePerson->" + user + "|" + requestedPatient);
        if ((user == null) || (requestedPatient == null)) {
            this.log.debug("isSamePerson(Patient)=false ->" + user + "|" + requestedPatient);
            return false;
        }
        
        return user.getPerson().getPersonId().equals(requestedPatient.getPersonId());
    }
    
    /*
     * @see org.openmrs.module.personalhr.service.PhrService#getRelatedPersons(org.openmrs.Person)
     */
    @Override
    public List<Person> getRelatedPersons(final Person person) {
        // TODO Auto-generated method stub
        final Patient pat = getPatient(person);
        User usr = Context.getAuthenticatedUser();
        boolean isAdmin = false;
        if(usr != null) {
            isAdmin = usr.hasRole(PhrBasicRole.PHR_ADMINISTRATOR.getValue(), false);
        }
        if(!isAdmin) {
            if (pat != null) {
                return getRelatedPersons(Context.getService(PhrSharingTokenService.class).getSharingTokenByPatient(pat));
            } else {
                return getRelatedPatients(Context.getService(PhrSharingTokenService.class).getSharingTokenByPerson(person));
            }
        } else{
            return getAllPhrUsers();
        }
    }
    
    /**
     * Get related persons from a given list of sharing tokens
     * 
     * @param tokens a list of sharing tokens
     * @return related persons
     */
    private List<Person> getRelatedPersons(final List<PhrSharingToken> tokens) {
        final List<Person> persons = new ArrayList<Person>();
        for (final PhrSharingToken token : tokens) {
            if(token.getRelatedPerson()!=null) {
                persons.add(token.getRelatedPerson());
            }
        }
        return persons;
    }
    
    /**
     * Get related patients from a given list of sharing tokens
     * 
     * @param tokens a list of sharing tokens
     * @return related patients
     */
    private List<Person> getRelatedPatients(final List<PhrSharingToken> tokens) {
        final List<Person> persons = new ArrayList<Person>();
        for (final PhrSharingToken token : tokens) {
            if(token.getPatient()!=null) {
                persons.add(token.getPatient());
            }
        }
        return persons;
    }    
    
    
    /**
     * Get all PHR Users
     * 
     * @return person objects of all PHR Users
     */
    private List<Person> getAllPhrUsers() {
        final List<Person> persons = new ArrayList<Person>();
        final List<User> users = new ArrayList<User>();
        
        users.addAll(Context.getUserService().getUsersByRole(Context.getUserService().getRole(PhrBasicRole.PHR_ADMINISTRATOR.getValue())));
        users.addAll(Context.getUserService().getUsersByRole(Context.getUserService().getRole(PhrBasicRole.PHR_PATIENT.getValue())));
        users.addAll(Context.getUserService().getUsersByRole(Context.getUserService().getRole(PhrBasicRole.PHR_RESTRICTED_USER.getValue())));
        
        for (final User user : users) {
            if(user != null && user.getPerson()!=null) {
                persons.add(user.getPerson());
            }
        }
        return persons;
    }
    
    /**
     * Get patient object of a given person
     * 
     * @param person given person obhect
     * @return patient object
     */
    private Patient getPatient(final Person person) {
        // TODO Auto-generated method stub
        if (person != null) {
            return Context.getPatientService().getPatient(person.getPersonId());
        } else {
            return null;
        }
    }

    /* 
     * @see org.openmrs.module.personalhr.service.PhrService#logEvent(java.lang.String, java.util.Date, int, java.lang.String, int, java.lang.String)
     */
    @Override
    public void logEvent(String eventType, Date eventDate, User user, String sessionId, Patient patient, String eventContent) {
        // TODO Auto-generated method stub
        User usr = user;
        if(usr==null) {
            usr = Context.getAuthenticatedUser();
        }
        
        PhrLogEvent eventLog = new PhrLogEvent(eventType, eventDate, (usr==null ? null:usr.getUserId()),
                                   sessionId, (patient==null?null:patient.getPatientId()), eventContent);
        Context.getService(PhrLogEventService.class).savePhrEventLog(eventLog);
    }

    /*
     * @see org.openmrs.module.personalhr.service.PhrService#getSharingTypes()
     */
    @Override
    public Set<String> getSharingTypes() {
        List<PhrPrivilege> privs = Context.getService(PhrPrivilegeService.class).getAllPhrPrivileges();
        
        TreeSet<String> types = new TreeSet<String>();
        
        for(PhrPrivilege priv : privs) {
            String[] roles = priv.getRequiredRole().toUpperCase().split(",");
            for(String role : roles) {
                if(role.trim().toUpperCase().startsWith("SHARE") && !role.trim().toUpperCase().endsWith("ALL") && !role.contains("&")) {
                    types.add(role.trim().toUpperCase());
                }
            }            
        }
        return types;
    }
    
}
