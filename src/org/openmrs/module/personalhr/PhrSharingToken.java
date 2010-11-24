package org.openmrs.module.personalhr;
import java.util.Date;

import org.openmrs.BaseOpenmrsMetadata;
import org.openmrs.Form;
import org.openmrs.Patient;
import org.openmrs.Person;

/**
 * The basic Sharing Token data object
 */

public class PhrSharingToken extends BaseOpenmrsMetadata {
    
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
    public void setId(Integer id) {
        this.id=id;
        
    }

    
    public String getSharingToken() {
        return sharingToken;
    }

    
    public void setSharingToken(String sharingToken) {
        this.sharingToken = sharingToken;
    }

    
    public Patient getPatient() {
        return patient;
    }

    
    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    
    public Person getRelatedPerson() {
        return relatedPerson;
    }

    
    public void setRelatedPerson(Person relatedPerson) {
        this.relatedPerson = relatedPerson;
    }

    
    public String getRelatedPersonName() {
        return relatedPersonName;
    }

    
    public void setRelatedPersonName(String relatedPersonName) {
        this.relatedPersonName = relatedPersonName;
    }

    
    public String getRelatedPersonEmail() {
        return relatedPersonEmail;
    }

    
    public void setRelatedPersonEmail(String relatedPersonEmail) {
        this.relatedPersonEmail = relatedPersonEmail;
    }

    
    public String getRelationType() {
        return relationType;
    }

    
    public void setRelationType(String relationType) {
        this.relationType = relationType;
    }

    
    public Date getStartDate() {
        return startDate;
    }

    
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    
    public Date getActivateDate() {
        return activateDate;
    }

    
    public void setActivateDate(Date activateDate) {
        this.activateDate = activateDate;
    }

    
    public Date getExpireDate() {
        return expireDate;
    }

    
    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    /**
     * Auto generated method comment
     * 
     * @return
     */
    public String getShareType() {
        // TODO Auto-generated method stub
        return null;
    }

    
    public void setShareType(String shareType) {
        this.shareType = shareType;
    }
    
 
}
