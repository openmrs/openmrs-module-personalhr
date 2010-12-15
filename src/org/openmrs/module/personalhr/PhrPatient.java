package org.openmrs.module.personalhr;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;

/**
 * The basic Sharing Token data object
 */

public class PhrPatient {
    protected final Log log = LogFactory.getLog(getClass());
   
    private List<PhrSharingToken> sharingTokens;
    
    private PhrSharingToken newSharingToken;
    
    private PhrSecurityService.PhrSharingType[] sharingTypes;
    
    private PhrSecurityService.PhrRelationType[] relationTypes;
    
    private Patient patient;
    
    private Integer patientId;

    public PhrPatient(Integer patId) {
        this.patientId = patId;
        
        this.patient = (patId==null ? null : Context.getPatientService().getPatient(patId));
        
        if(this.patient != null) {
            this.sharingTokens = PersonalhrUtil.getService().getSharingTokenDao().getSharingTokenByPatient(this.patient); 
        } 
        
        if(this.sharingTokens != null) {
            log.debug("Constructing PhrPatient: patId|patent|sharingTokens.szie="+patId+"|"+patient+"|" +sharingTokens.size());            
        } else {
            log.debug("Constructing PhrPatient: patId|patent|sharingTokens="+patId+"|"+patient+"|" +sharingTokens);
        }
        
        this.sharingTypes = PhrSecurityService.PhrSharingType.values();
        
        this.relationTypes = PhrSecurityService.PhrRelationType.values();
        
        this.newSharingToken = new PhrSharingToken();
        this.newSharingToken.setPatient(this.patient);
    }
    
    public List<PhrSharingToken> getSharingTokens() {
        return sharingTokens;
    }

    
    public void setSharingTokens(List<PhrSharingToken> sharingTokens) {
        this.sharingTokens = sharingTokens;
    }

    
    public Patient getPatient() {
        return patient;
    }

    
    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    
    public Integer getPatientId() {
        return patientId;
    }

    
    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    
    public PhrSecurityService.PhrSharingType[] getSharingTypes() {
        return sharingTypes;
    }

    
    public void setSharingTypes(PhrSecurityService.PhrSharingType[] sharingTypes) {
        this.sharingTypes = sharingTypes;
    }

    
    public PhrSecurityService.PhrRelationType[] getRelationTypes() {
        return relationTypes;
    }

    
    public void setRelationTypes(PhrSecurityService.PhrRelationType[] relationTypes) {
        this.relationTypes = relationTypes;
    }

    
    public PhrSharingToken getNewSharingToken() {
        return newSharingToken;
    }

    
    public void setNewSharingToken(PhrSharingToken newSharingToken) {
        this.newSharingToken = newSharingToken;
    }         
}
