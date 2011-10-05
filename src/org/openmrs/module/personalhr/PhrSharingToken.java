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
 * 
 * @author hxiao
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
        return this.id;
    }
    
    /**
     * @see org.openmrs.OpenmrsObject#setId(java.lang.Integer)
     */
    @Override
    public void setId(final Integer id) {
        this.id = id;
        
    }
    
    /**
     * Get sharing token
     * 
     * @return a sharing token
     */
    public String getSharingToken() {
        return this.sharingToken;
    }
    
    /**
     * Set sharing token
     * 
     * @param sharingToken a sharing token
     */
    public void setSharingToken(final String sharingToken) {
        this.sharingToken = sharingToken;
    }
    
    /**
     * Get Patient
     * 
     * @return a Patient
     */
    public Patient getPatient() {
        return this.patient;
    }
    
    /**
     * Set Patient
     * 
     * @param patient a Patient
     */
    public void setPatient(final Patient patient) {
        this.patient = patient;
    }
    
    /**
     * Get  a related Person
     * 
     * @return a related Person
     */
    public Person getRelatedPerson() {
        return this.relatedPerson;
    }
    
    /**
     * Set a related Person
     * 
     * @param relatedPerson a related person
     */
    public void setRelatedPerson(final Person relatedPerson) {
        this.relatedPerson = relatedPerson;
    }
    
    /**
     * Get related person name
     * 
     * @return related person name
     */
    public String getRelatedPersonName() {
        return this.relatedPersonName;
    }
    
    /**
     * Set related person's name
     * 
     * @param relatedPersonName name of the related person
     */
    public void setRelatedPersonName(final String relatedPersonName) {
        this.relatedPersonName = relatedPersonName;
    }
    
    /**
     * Get email of the related person
     * 
     * @return email of the related person
     */
    public String getRelatedPersonEmail() {
        return this.relatedPersonEmail;
    }
    
    /**
     * Set email of the related person
     * 
     * @param relatedPersonEmail email of the related person
     */
    public void setRelatedPersonEmail(final String relatedPersonEmail) {
        this.relatedPersonEmail = relatedPersonEmail;
    }
    
    /**
     * Get relation type
     * 
     * @return a relation type as a string
     */
    public String getRelationType() {
        return this.relationType;
    }
    
    /**
     * Set relation type
     * 
     * @param relationType a relation type
     */
    public void setRelationType(final String relationType) {
        this.relationType = relationType;
    }
    
    /**
     * Get start date of the sharing relation
     * 
     * @return a start date
     */
    public Date getStartDate() {
        return this.startDate;
    }
    
    /**
     * Set start date
     * 
     * @param startDate start date of the sharing relation
     */
    public void setStartDate(final Date startDate) {
        this.startDate = startDate;
    }
    
    /**
     * Get activation date
     * 
     * @return the activation date of the sharing relation
     */
    public Date getActivateDate() {
        return this.activateDate;
    }
    
    /**
     * Set activation date
     * 
     * @param activateDate activation date of a sharing relation 
     */
    public void setActivateDate(final Date activateDate) {
        this.activateDate = activateDate;
    }
    
    /**
     * Get expiration date of a sharing token
     * 
     * @return the expiration of a sharing token
     */
    public Date getExpireDate() {
        return this.expireDate;
    }
    
    /**
     * Set expiration of a sharing relation
     * 
     * @param expireDate expiration date of the relation
     */
    public void setExpireDate(final Date expireDate) {
        this.expireDate = expireDate;
    }
    
    /**
     * Get share type
     * 
     * @return a sharing type
     */
    public String getShareType() {
        return this.shareType;
    }
    
    /**
     * Set sharing type
     * 
     * @param shareType a sharing type
     */
    public void setShareType(final String shareType) {
        this.shareType = shareType;
    }
    
    /**
     * Used to sort the sharing tokens by their id
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(final PhrSharingToken newToken) {
        return newToken.getId().compareTo(this.id);
    }
    
}
