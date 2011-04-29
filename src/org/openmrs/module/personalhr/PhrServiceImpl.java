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
     * Called only after user has been authenticated (i.e. requestingUser != null) PHR URL level
     * security check rules: 1. Check user type: unauthenticated (null) user, PHR user, non PHR user
     * 2. If unauthenticated user: all URL's are allowed (page level check needed) 3. Otherwise
     * check patient or person object in the request 4. If no patient or person object involved: all
     * URL's are allowed 5. Otherwise check access to /phr/ or /personalhr/ domain 7. If access to
     * /phr/ or /personalhr/ domain, allow authorized PHR user only; do not allow non PHR user 8.
     * Otherwise, allow all non PHR users, or 9. Allow only registered non /phr/ or /personalhr/
     * URL's to be accessed by only authorized PHR users
     * 
     * @see org.openmrs.module.personalhr.PhrService#isUrlAllowed(java.lang.String,
     *      org.openmrs.Patient, org.openmrs.Person, org.openmrs.User)
     */
    @Override
    public boolean isUrlAllowed(final String requestedUrl, final Patient requestedPatient, final Person requestedPerson,
                                final User requestingUser) {
        this.log.debug("PhrServiceImpl:isUrlAllowed->" + requestedUrl + "|" + requestedPatient + "|"
                + requestedPerson + "|" + requestingUser);
        if (requestingUser == null) {
            this.log.warn("Allowed -> User not authenticated yet: " + requestedUrl + "|" + requestedPatient + "|"
                    + requestedPerson + "|" + requestingUser);
            return true;
        }
        
        //always allowed if requestedPatient==null && requestedPerson==null
        if ((requestedPatient == null) && (requestedPerson == null)) {
            this.log.debug("Allowed -> accessing common URL: " + requestedUrl + "|" + requestedPatient + "|"
                    + requestedPerson + "|" + requestingUser);
            return true;
        }
        
        //Check access to /phr/ or /personalhr/ domain
        final String phrRole = getPhrRole(requestingUser);
        if (requestedUrl.contains("/phr") || requestedUrl.contains("/personalhr")) {
            if (phrRole != null) {
                this.log.debug("Allowed -> PHR User accessing /phr or /personalhr domain: " + requestedUrl + "|"
                        + requestedPatient + "|" + requestedPerson + "|" + requestingUser);
                return hasPrivilege("", requestedPatient, requestedPerson, requestingUser);
                
            } else {
                this.log.warn("Not allowed - > Non PHR User accessing /phr or /personalhr domain: " + requestedUrl + "|"
                        + requestedPatient + "|" + requestedPerson + "|" + requestingUser);
                return false;
            }
        } else {
            //Always allow non PHR user accessing non /phr/ domain
            if (phrRole == null) {
                this.log.debug("Allowed -> non PHR user accessing non /phr domain or /personalhr: " + requestedUrl + "|"
                        + requestedPatient + "|" + requestedPerson + "|" + requestingUser);
                return true;
            }
        }
        
        //Check access to non /phr/ domain
        final List<PhrAllowedUrl> urls = this.allowedUrlDao.getByUrl(requestedUrl);
        if (urls != null) {
            for (final PhrAllowedUrl url : urls) {
                if (url != null) {
                    this.log.debug("Allowed - > Accessing allowed non /phr or /personalhr domain -> Checking privileges ... "
                            + requestedUrl
                            + "|"
                            + requestedPatient
                            + "|"
                            + requestedPerson
                            + "|"
                            + requestingUser
                            + "|"
                            + url.getPrivilege());
                    return hasPrivilege(url.getPrivilege(), requestedPatient, requestedPerson, requestingUser);
                }
            }
        }
        
        this.log.warn("Not allowed - > Accessing non /phr or /personalhr domain by non-authorized PHR user: " + requestedUrl
                + "|" + requestedPatient + "|" + requestedPerson + "|" + requestingUser);
        return false;
    }
    
    /**
     * Auto generated method comment
     * 
     * @param requestingUser
     * @return
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
    
    @Override
    public boolean hasPrivilege(final String privilege, final Patient requestedPatient, final Person requestedPerson,
                                final User user) {
        this.log.debug("PhrServiceImpl:hasPrivilege->" + privilege + "|" + requestedPatient + "|" + requestedPerson
                + "|" + user);
        
        if ((user != null) && (requestedPatient == null) && (requestedPerson == null)) {
            return true;
        }
        
        if ((privilege == null) || privilege.trim().isEmpty()) {
            //When url privilege is not specified and requested patient/person is not null, allow only owner, admin, shareee and share all to access
            if ((requestedPatient != null) || (requestedPerson != null)) {
                final String reqRole = "Owner,Administrator,Share Medical,Share Journal,Share All".toUpperCase();
                final List<String> roles = getDynamicRoles(requestedPatient, requestedPerson, user);
                if (roles != null) {
                    for (final String role : roles) {
                        if (reqRole.contains(role.toUpperCase())) {
                            this.log.debug("hasPrivilege returns true ->" + privilege + "|" + requestedPatient + "|"
                                    + requestedPerson + "|" + user);
                            return true;
                        }
                    }
                }
                this.log.debug("PhrServiceImpl:hasPrivilege returns false ->" + privilege + "|" + requestedPatient
                        + "|" + requestedPerson + "|" + user);
                return false;
            } 
            //When url privilege is not specified and requested patient/person is null, allow every one to access            
            else {
                this.log.debug("PhrServiceImpl:hasPrivilege returns true ->" + privilege + "|" + requestedPatient
                        + "|" + requestedPerson + "|" + user);
                return true;
            }
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
                        //for each roles held
                        for (final String role : roles) {
                            if (reqRole.contains(role.toUpperCase()) ||
                                "Administrator".equalsIgnoreCase(role) ||
                                "Share All".equalsIgnoreCase(role)) {
                                this.log.debug("hasPrivilege returns true ->" + privilege + "|" + requestedPatient + "|"
                                        + requestedPerson + "|" + user);
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
        if (isSamePerson(user, requestedPatient) || isSamePerson(user, requestedPerson)) {
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
            return getRelatedPersons(this.sharingTokenDao.getSharingTokenByPerson(person));
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
