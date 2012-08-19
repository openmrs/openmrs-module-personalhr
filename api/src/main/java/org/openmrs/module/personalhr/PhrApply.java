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

import org.openmrs.BaseOpenmrsMetadata;

/**
 * The basic data object to represent PHR defined Privilege 
 * 
 * @author hxiao
 */

public class PhrApply extends BaseOpenmrsMetadata {
    
    /** Unique identifying id */
    private Integer id;
    
    private String privilege;
    
    private String requiredRole;
    
    /**
     * @see org.openmrs.OpenmrsObject#getId()
     */
     public Integer getId() {
        return this.id;
    }
    
    /**
     * @see org.openmrs.OpenmrsObject#setId(java.lang.Integer)
     */
    public void setId(final Integer id) {
        this.id = id;
        
    }
    
    /**
     * Get privilege
     * 
     * @return privilege
     */
    public String getPrivilege() {
        return this.privilege;
    }
    
    /**
     * Set a privilege
     * 
     * @param privilege a privilege
     */
    public void setPrivilege(final String privilege) {
        this.privilege = privilege;
    }
    
    /**
     * Get PHR role required to have this privilege 
     * 
     * @return PHR role required
     */
    public String getRequiredRole() {
        return this.requiredRole;
    }
    
    /**
     * Set PHR role required to have this privilege 
     * 
     * @param requiredRole PHR role required
     */
    public void setRequiredRole(final String requiredRole) {
        this.requiredRole = requiredRole;
    }
    
}
