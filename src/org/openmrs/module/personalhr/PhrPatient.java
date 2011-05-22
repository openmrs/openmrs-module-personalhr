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
package org.openmrs.module.personalhr;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.personalhr.db.PhrSharingTokenDAO;

/**
 * The basic Sharing Token data object
 */

public class PhrPatient {
    
    protected final Log log = LogFactory.getLog(getClass());
    
    private List<PhrSharingToken> sharingTokens;
    
    private String personName;
    
    private PhrSharingToken newSharingToken;
    
    private Set<String> sharingTypes;
    
    private PhrService.PhrRelationType[] relationTypes;
    
    private Patient patient;
    
    private Integer patientId;
    
    private int numberChanged;
    
    private int numberAdded;
    
    private int numberDeleted;
    
    public PhrPatient(final Integer patId) {
        this.patientId = patId;
        
        this.patient = (patId == null ? null : Context.getPatientService().getPatient(patId));
        
        if (this.patient != null) {
            this.sharingTokens = PersonalhrUtil.getService().getSharingTokenDao().getSharingTokenByPatient(this.patient);
        }
        
        if (this.sharingTokens != null) {
            this.log.debug("Constructing PhrPatient: patId|patent|sharingTokens.szie=" + patId + "|" + this.patient + "|"
                    + this.sharingTokens.size());
        } else {
            this.log.debug("Constructing PhrPatient: patId|patent|sharingTokens=" + patId + "|" + this.patient + "|"
                    + this.sharingTokens);
        }
        
        this.sharingTypes = PersonalhrUtil.getService().getSharingTypes();
        
        this.relationTypes = PhrService.PhrRelationType.values();
        
        this.newSharingToken = new PhrSharingToken();
        this.newSharingToken.setPatient(this.patient);
        this.newSharingToken.setId(null);
        this.newSharingToken.setRelatedPersonName(null);
        this.newSharingToken.setRelatedPersonEmail(null);
        
        this.numberChanged = 0;
        this.numberAdded = 0;
        this.numberDeleted = 0;
        this.personName = null;
        
    }
    
    public List<PhrSharingToken> getSharingTokens() {
        return this.sharingTokens;
    }
    
    public void setSharingTokens(final List<PhrSharingToken> sharingTokens) {
        this.sharingTokens = sharingTokens;
    }
    
    public Patient getPatient() {
        return this.patient;
    }
    
    public void setPatient(final Patient patient) {
        this.patient = patient;
    }
    
    public Integer getPatientId() {
        return this.patientId;
    }
    
    public void setPatientId(final Integer patientId) {
        this.patientId = patientId;
    }
    
    public Set<String> getSharingTypes() {
        return this.sharingTypes;
    }
    
    public void setSharingTypes(final Set<String> sharingTypes) {
        this.sharingTypes = sharingTypes;
    }
    
    public PhrService.PhrRelationType[] getRelationTypes() {
        return this.relationTypes;
    }
    
    public void setRelationTypes(final PhrService.PhrRelationType[] relationTypes) {
        this.relationTypes = relationTypes;
    }
    
    public PhrSharingToken getNewSharingToken() {
        return this.newSharingToken;
    }
    
    public void setNewSharingToken(final PhrSharingToken newSharingToken) {
        this.newSharingToken = newSharingToken;
    }
    
    /**
     * Save relationship changes into database
     */
    public void save() {
        final List<PhrSharingToken> oldTokens = PersonalhrUtil.getService().getSharingTokenDao()
                .getSharingTokenByPatient(this.patient);
        final PhrSharingTokenDAO dao = PersonalhrUtil.getService().getSharingTokenDao();
        
        //check non-deleted relationships
        if(this.sharingTokens != null) {
            for (final PhrSharingToken token : this.sharingTokens) {
                if (token.getId() > 0) { //check changed relationship
                    boolean isChanged = false;
                    final PhrSharingToken oldToken = dao.getPhrSharingToken(token.getId());
                    if (!oldToken.getRelatedPersonEmail().equals(token.getRelatedPersonEmail())) {
                        oldToken.setRelatedPersonEmail(token.getRelatedPersonEmail());
                        isChanged = true;
                    }
                    if (!oldToken.getRelatedPersonName().equals(token.getRelatedPersonName())) {
                        oldToken.setRelatedPersonName(token.getRelatedPersonName());
                        isChanged = true;
                    }
                    if (!oldToken.getRelationType().equals(token.getRelationType())) {
                        oldToken.setRelationType(token.getRelationType());
                        isChanged = true;
                    }
                    if (!oldToken.getShareType().equals(token.getShareType())) {
                        oldToken.setShareType(token.getShareType());
                        isChanged = true;
                    }
                    if (isChanged) { //save changed relationship
                        this.numberChanged++;
                        dao.savePhrSharingToken(token);
                        this.log.debug("Changed token id: " + token.getId());
                    }
                } else { //save added relationship
                    this.numberAdded++;
                    final PhrSharingToken addedToken = dao.savePhrSharingToken(token);
                    this.log.debug("Newly added token id: " + addedToken.getId());
                }
            }
            Collections.sort(this.sharingTokens);
        }
        
        //check deleted relationships
        if(oldTokens != null) {
            for (final PhrSharingToken token : oldTokens) {
                if (Collections.binarySearch(this.sharingTokens, token) < 0) {
                    dao.deletePhrSharingToken(token);
                    this.numberDeleted++;
                    this.log.debug("Deleted token id: " + token.getId());
                }
            }
        }
        
        //check newly added relationship
        if ((this.newSharingToken != null) && (this.newSharingToken.getRelatedPersonName() != null)) {
            final PhrSharingToken token = this.newSharingToken;
            
            final String tokenString = PersonalhrUtil.getRandomToken();
            token.setSharingToken(tokenString);
            token.setRelatedPersonEmail(token.getRelatedPersonEmail());
            token.setRelatedPersonName(token.getRelatedPersonName());
            token.setShareType(token.getShareType());
            token.setRelationType(token.getRelationType());
            final Date startDate = new Date();
            token.setStartDate(startDate);
            token.setDateCreated(startDate);
            token.setExpireDate(PersonalhrUtil.getExpireDate(startDate));
            token.setCreator(Context.getAuthenticatedUser());
            
            dao.savePhrSharingToken(token);
            
            this.numberAdded++;
            
            if (this.log.isDebugEnabled()) {
                this.log.debug("Newly added token id: " + dao.getSharingToken(tokenString).getId());
            }
        }
        
    }
    
    public int getNumberChanged() {
        return this.numberChanged;
    }
    
    public void setNumberChanged(final int numberChanged) {
        this.numberChanged = numberChanged;
    }
    
    public int getNumberAdded() {
        return this.numberAdded;
    }
    
    public void setNumberAdded(final int numberAdded) {
        this.numberAdded = numberAdded;
    }
    
    public int getNumberDeleted() {
        return this.numberDeleted;
    }
    
    public void setNumberDeleted(final int numberDeleted) {
        this.numberDeleted = numberDeleted;
    }
    
    public String getPersonName() {
        return this.personName;
    }
    
    public void setPersonName(final String personName) {
        this.personName = personName;
    }
    
    /**
     * Auto generated method comment
     * 
     * @param id
     */
    public void delete(final Integer id) {
        final PhrSharingTokenDAO dao = PersonalhrUtil.getService().getSharingTokenDao();
        dao.deletePhrSharingToken(id);
        this.numberDeleted++;
    }
}
