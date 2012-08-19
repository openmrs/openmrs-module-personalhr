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
package org.openmrs.module.personalhr.db;

import java.util.List;

import org.hibernate.SessionFactory;
import org.openmrs.module.personalhr.PhrApply;

/**
 * Data Access Object for phr_privilege table access
 * 
 * @author hxiao
 */

public interface PhrApplyDAO {
    
    /**
     * Set Hibernate session factory
     * 
     * @param sessionFactory Hibernate session factory
     */
    public void setSessionFactory(SessionFactory sessionFactory);
    
    /**
     * Get PhrPrivilge object of a given ID
     * 
     * @param id id of the privilge entry
     * @return PhrPrivilge object
     */
    public PhrApply getPhrApply(Integer id);
    
    /**
     * Save privilege
     * 
     * @param priv privilege to save
     * @return privilege saved
     */
    public PhrApply savePhrApply(PhrApply priv);
    
    /**
     * Delete privilege
     * 
     * @param priv privilege to be deleted
     */
    public void deletePhrApply(PhrApply priv);
    
    /**
     * Get all privileges
     * 
     * @return all privileges
     */
    public List<PhrApply> getAllPhrApplys();
    
    /**
     * Get PhrPrivilege list of a given privilege
     * 
     * @param priv given privilege
     * @return list of PhrPrivilege
     */
    public List<PhrApply> getByApply(String priv);
    
    /**
     * Get PhrPrivilege list of a given role
     * 
     * @param role given role
     * @return list of PhrPrivilege
     */
    public List<PhrApply> getByRole(String role);
}
