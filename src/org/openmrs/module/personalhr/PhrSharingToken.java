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

import java.util.Date;

import org.openmrs.BaseOpenmrsMetadata;
import org.openmrs.Patient;
import org.openmrs.Person;

/**
 * The basic Sharing Token data object
 */

public class PhrSharingToken extends BaseOpenmrsMetadata implements Comparable<PhrSharingToken> {
    
    /** Unique identifying id */
    private Integer id;
    
    private String sharingToken;
    
    private Patient patient;
    
    private Person relatedPerson;
    
    private String relatedPersonName;
    
    private String relatedPersonEmail;
    
    private String relationType;
    
    private Date startDate;
    
    private Date activateDate;
    
    private Date expireDate;
    
    private String shareType;
    
    /**
     * @see org.openmrs.OpenmrsObject#getId()
     */
    @Override
    public Integer getId() {
        // TODO Auto-generated method stub
        return this.id;
    }
    
    /**
     * @see org.openmrs.OpenmrsObject#setId(java.lang.Integer)
     */
    @Override
    public void setId(final Integer id) {
        this.id = id;
        
    }
    
    public String getSharingToken() {
        return this.sharingToken;
    }
    
    public void setSharingToken(final String sharingToken) {
        this.sharingToken = sharingToken;
    }
    
    public Patient getPatient() {
        return this.patient;
    }
    
    public void setPatient(final Patient patient) {
        this.patient = patient;
    }
    
    public Person getRelatedPerson() {
        return this.relatedPerson;
    }
    
    public void setRelatedPerson(final Person relatedPerson) {
        this.relatedPerson = relatedPerson;
    }
    
    public String getRelatedPersonName() {
        return this.relatedPersonName;
    }
    
    public void setRelatedPersonName(final String relatedPersonName) {
        this.relatedPersonName = relatedPersonName;
    }
    
    public String getRelatedPersonEmail() {
        return this.relatedPersonEmail;
    }
    
    public void setRelatedPersonEmail(final String relatedPersonEmail) {
        this.relatedPersonEmail = relatedPersonEmail;
    }
    
    public String getRelationType() {
        return this.relationType;
    }
    
    public void setRelationType(final String relationType) {
        this.relationType = relationType;
    }
    
    public Date getStartDate() {
        return this.startDate;
    }
    
    public void setStartDate(final Date startDate) {
        this.startDate = startDate;
    }
    
    public Date getActivateDate() {
        return this.activateDate;
    }
    
    public void setActivateDate(final Date activateDate) {
        this.activateDate = activateDate;
    }
    
    public Date getExpireDate() {
        return this.expireDate;
    }
    
    public void setExpireDate(final Date expireDate) {
        this.expireDate = expireDate;
    }
    
    /**
     * Auto generated method comment
     * 
     * @return
     */
    public String getShareType() {
        // TODO Auto-generated method stub
        return this.shareType;
    }
    
    public void setShareType(final String shareType) {
        this.shareType = shareType;
    }
    
    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(final PhrSharingToken newToken) {
        // TODO Auto-generated method stub
        return newToken.getId().compareTo(this.id);
    }
    
}
