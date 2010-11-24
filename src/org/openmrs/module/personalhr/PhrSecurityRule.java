package org.openmrs.module.personalhr;
import java.util.Date;

import org.openmrs.BaseOpenmrsMetadata;
import org.openmrs.Form;
import org.openmrs.Patient;
import org.openmrs.Person;

/**
 * The basic Sharing Token data object
 */

public class PhrSecurityRule extends BaseOpenmrsMetadata {
    
    /** Unique identifying id */
    private Integer id;
          
    private String privilege;
    
    private String requiredRole;
    
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

    
    public String getPrivilege() {
        return privilege;
    }

    
    public void setPrivilege(String privilege) {
        this.privilege = privilege;
    }

        
    public String getRequiredRole() {
        return requiredRole;
    }

    
    public void setRequiredRole(String requiredRole) {
        this.requiredRole = requiredRole;
    }
 
}
