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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.personalhr.db.PhrAllowedUrlDAO;
import org.openmrs.module.personalhr.db.PhrLogEventDAO;
import org.openmrs.module.personalhr.db.PhrPrivilegeDAO;
import org.openmrs.module.personalhr.db.PhrSharingTokenDAO;

/**
 *
 */
public class PhrServiceImpl extends BaseOpenmrsService implements PhrService {
    
    protected final Log log = LogFactory.getLog(getClass());
    
    private PhrPrivilegeDAO privilegeDao;
    
    private PhrAllowedUrlDAO allowedUrlDao;
    
    private PhrSharingTokenDAO sharingTokenDao;
    
    private PhrLogEventDAO logEventDao;
    
    /**
     * Check if a given PHR user is allowed to access a given URL   
     * This check is based on PHR allowed url list and corresponding privilege required
     * The PHR allowed url list may contain wild card '*'
     * 
     * @param requestingUser must not be null and must have a PHR role
     * 
     * @see org.openmrs.module.personalhr.PhrService#isUrlAllowed(java.lang.String,
     *      org.openmrs.Patient, org.openmrs.Person, org.openmrs.User)
     */
    @Override
    public boolean isUrlAllowed(final String requestedUrl, final Patient requestedPatient, final Person requestedPerson,
                                final User requestingUser) {
        this.log.debug("PhrServiceImpl:isUrlAllowed->" + requestedUrl + "|" + requestedPatient + "|"
                + requestedPerson + "|" + requestingUser);
                
        //Get all allowed URL list 
        final List<PhrAllowedUrl> urls = this.allowedUrlDao.getAllPhrAllowedUrls();
        
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
     * @param requestingUser user object
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
        final List<PhrPrivilege> rules = this.privilegeDao.getByPrivilege(privilege);
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
            final PhrSharingToken token = this.sharingTokenDao.getSharingToken(requestedPatient, requestedPerson, user);
            
            if (token != null) {
                final String shareType = token.getShareType();
                if ((shareType != null) && !shareType.trim().isEmpty()) {
                    this.log.debug("getDynamicRoles for shareType: " + shareType);
                    roles.add(shareType.toUpperCase());                  
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
        // TODO Auto-generated method stub
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
        // TODO Auto-generated method stub
        if ((user == null) || (requestedPatient == null)) {
            this.log.debug("isSamePerson(Patient)=false ->" + user + "|" + requestedPatient);
            return false;
        }
        
        return user.getPerson().getPersonId().equals(requestedPatient.getPersonId());
    }
    
    @Override
    public PhrPrivilegeDAO getPrivilegeDao() {
        return this.privilegeDao;
    }
    
    @Override
    public void setPrivilegeDao(final PhrPrivilegeDAO privilegeDao) {
        this.privilegeDao = privilegeDao;
    }
    
    @Override
    public PhrAllowedUrlDAO getAllowedUrlDao() {
        return this.allowedUrlDao;
    }
    
    @Override
    public void setAllowedUrlDao(final PhrAllowedUrlDAO allowedUrlDao) {
        this.allowedUrlDao = allowedUrlDao;
    }
    
    @Override
    public PhrSharingTokenDAO getSharingTokenDao() {
        return this.sharingTokenDao;
    }
    
    @Override
    public void setSharingTokenDao(final PhrSharingTokenDAO sharingTokenDao) {
        this.sharingTokenDao = sharingTokenDao;
    }
    
    /* (non-Jsdoc)
     * @see org.openmrs.module.personalhr.PhrService#getRelatedPersons(org.openmrs.Person)
     */
    @Override
    public List<Person> getRelatedPersons(final Person person) {
        // TODO Auto-generated method stub
        final Patient pat = getPatient(person);
        if (pat != null) {
            return getRelatedPersons(this.sharingTokenDao.getSharingTokenByPatient(pat));
        } else {
            return getRelatedPatients(this.sharingTokenDao.getSharingTokenByPerson(person));
        }
    }
    
    /**
     * Auto generated method comment
     * 
     * @param sharingTokenByPatient
     * @return
     */
    private List<Person> getRelatedPersons(final List<PhrSharingToken> tokens) {
        // TODO Auto-generated method stub
        final List<Person> persons = new ArrayList<Person>();
        for (final PhrSharingToken token : tokens) {
            if(token.getRelatedPerson()!=null) {
                persons.add(token.getRelatedPerson());
            }
        }
        return persons;
    }
    
    private List<Person> getRelatedPatients(final List<PhrSharingToken> tokens) {
        // TODO Auto-generated method stub
        final List<Person> persons = new ArrayList<Person>();
        for (final PhrSharingToken token : tokens) {
            if(token.getPatient()!=null) {
                persons.add(token.getPatient());
            }
        }
        return persons;
    }    
    
    /**
     * Auto generated method comment
     * 
     * @param person
     * @return
     */
    private Patient getPatient(final Person person) {
        // TODO Auto-generated method stub
        if (person != null) {
            return Context.getPatientService().getPatient(person.getPersonId());
        } else {
            return null;
        }
    }

    /* (non-Jsdoc)
     * @see org.openmrs.module.personalhr.PhrService#logEvent(java.lang.String, java.util.Date, int, java.lang.String, int, java.lang.String)
     */
    @Override
    public void logEvent(String eventType, Date eventDate, User user, String sessionId, Patient patient, String eventContent) {
        // TODO Auto-generated method stub
        PhrLogEvent eventLog = new PhrLogEvent(eventType, eventDate, (user==null ? null:user.getUserId()),
                                   sessionId, (patient==null?null:patient.getPatientId()), eventContent);
        logEventDao.savePhrEventLog(eventLog);
    }

    
    public PhrLogEventDAO getLogEventDao() {
        return logEventDao;
    }

    
    public void setLogEventDao(PhrLogEventDAO logEventDao) {
        this.logEventDao = logEventDao;
    }

    /* (non-Jsdoc)
     * @see org.openmrs.module.personalhr.PhrService#getSharingTypes()
     */
    @Override
    public Set<String> getSharingTypes() {
        // TODO Auto-generated method stub
        List<PhrPrivilege> privs = privilegeDao.getAllPhrPrivileges();
        
        TreeSet<String> types = new TreeSet<String>();
        
        for(PhrPrivilege priv : privs) {
            String[] roles = priv.getRequiredRole().toUpperCase().split(",");
            for(String role : roles) {
                if(role.trim().toUpperCase().startsWith("SHARE") && !role.trim().toUpperCase().endsWith("ALL")) {
                    types.add(role.trim().toUpperCase());
                }
            }            
        }
        return types;
    }
    
}
