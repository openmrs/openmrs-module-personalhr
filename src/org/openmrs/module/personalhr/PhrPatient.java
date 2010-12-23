package org.openmrs.module.personalhr;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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
    
    private PhrSecurityService.PhrSharingType[] sharingTypes;
    
    private PhrSecurityService.PhrRelationType[] relationTypes;
    
    private Patient patient;
    
    private Integer patientId;
    
    private int numberChanged;
    
    private int numberAdded;
    
    private int numberDeleted;
    

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
        this.newSharingToken.setId(null);
        this.newSharingToken.setRelatedPersonName(null);
        this.newSharingToken.setRelatedPersonEmail(null);
        
        this.numberChanged = 0;
        this.numberAdded = 0;
        this.numberDeleted = 0;
        this.personName = null;
       
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

    /**
     * Save relationship changes into database
     * 
     */
    public void save() {
        List<PhrSharingToken> oldTokens =PersonalhrUtil.getService().getSharingTokenDao().getSharingTokenByPatient(this.patient);
        PhrSharingTokenDAO dao = PersonalhrUtil.getService().getSharingTokenDao();
        
        //check non-deleted relationships
        for(PhrSharingToken token : this.sharingTokens) {            
           if(token.getId() > 0) {  //check changed relationship
               boolean isChanged = false;
               PhrSharingToken oldToken = dao.getPhrSharingToken(token.getId());
               if(!oldToken.getRelatedPersonEmail().equals(token.getRelatedPersonEmail())) {
                   oldToken.setRelatedPersonEmail(token.getRelatedPersonEmail());
                   isChanged = true;
               }
               if(!oldToken.getRelatedPersonName().equals(token.getRelatedPersonName())) {
                   oldToken.setRelatedPersonName(token.getRelatedPersonName());
                   isChanged = true;
               }
               if(!oldToken.getRelationType().equals(token.getRelationType())) {
                   oldToken.setRelationType(token.getRelationType());
                   isChanged = true;
               }  
               if(!oldToken.getShareType().equals(token.getShareType())) {
                   oldToken.setShareType(token.getShareType());
                   isChanged = true;
               }   
               if(isChanged) {  //save changed relationship
                   this.numberChanged ++;
                   dao.savePhrSharingToken(token);
                   log.debug("Changed token id: " + token.getId());
               }
           } else { //save added relationship
               this.numberAdded ++;
               PhrSharingToken addedToken = dao.savePhrSharingToken(token);
               log.debug("Newly added token id: " + addedToken.getId());               
           }
        }
        
        //check deleted relationships
        Collections.sort(this.sharingTokens);
        for(PhrSharingToken token : oldTokens) {
            if(Collections.binarySearch(this.sharingTokens, token)<0) {
                dao.deletePhrSharingToken(token); 
                this.numberDeleted ++;
                log.debug("Deleted token id: " + token.getId());                       
            }
        } 
        
        //check newly added relationship
        if(this.newSharingToken != null && this.newSharingToken.getRelatedPersonName() != null) {
            PhrSharingToken token = this.newSharingToken;

            String tokenString = PersonalhrUtil.getRandomToken();
            token.setSharingToken(tokenString);
            token.setRelatedPersonEmail(token.getRelatedPersonEmail());
            token.setRelatedPersonName(token.getRelatedPersonName());
            token.setShareType(token.getShareType());
            token.setRelationType(token.getRelationType());
            Date startDate = new Date();
            token.setStartDate(startDate);
            token.setDateCreated(startDate);
            token.setExpireDate(PersonalhrUtil.getExpireDate(startDate));
            token.setCreator(Context.getAuthenticatedUser());
            
            dao.savePhrSharingToken(token);
            
            this.numberAdded ++;
            
            if(log.isDebugEnabled()) {
                log.debug("Newly added token id: " + dao.getSharingToken(tokenString).getId());
            }
        }
        
    }

    
    public int getNumberChanged() {
        return numberChanged;
    }

    
    public void setNumberChanged(int numberChanged) {
        this.numberChanged = numberChanged;
    }

    
    public int getNumberAdded() {
        return numberAdded;
    }

    
    public void setNumberAdded(int numberAdded) {
        this.numberAdded = numberAdded;
    }

    
    public int getNumberDeleted() {
        return numberDeleted;
    }

    
    public void setNumberDeleted(int numberDeleted) {
        this.numberDeleted = numberDeleted;
    }

    
    public String getPersonName() {
        return personName;
    }

    
    public void setPersonName(String personName) {
        this.personName = personName;
    }

    /**
     * Auto generated method comment
     * 
     * @param id
     */
    public void delete(Integer id) {
        PhrSharingTokenDAO dao = PersonalhrUtil.getService().getSharingTokenDao();
        dao.deletePhrSharingToken(id);
        this.numberDeleted++;
    }         
}
