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
import org.openmrs.module.personalhr.PhrAllowedUrl;

/**
 * Data Access Object for phr_allowed_url table access
 * 
 * @author hxiao
 */

public interface PhrAllowedUrlDAO {
    
    /**
     * Set Hibernate session factory 
     * 
     * @param sessionFactory hibernate session factory
     */
    public void setSessionFactory(SessionFactory sessionFactory);
    
    /**
     * Get allowed url of a given id
     * 
     * @param id id of the allowed URL entry
     * @return Allowed URL object
     */
    public PhrAllowedUrl getPhrAllowedUrl(Integer id);
    
    /**
     * Save allowed URL entry to database
     * 
     * @param url allowed URL object
     * @return allowed URL object saved
     */
    public PhrAllowedUrl savePhrAllowedUrl(PhrAllowedUrl url);
    
    /**
     * Delete the given allowed URL object
     * 
     * @param url a given allowed URL object to delete
     */
    public void deletePhrAllowedUrl(PhrAllowedUrl url);
    
    /**
     * Get all allowed URL objects
     * 
     * @return all allowed URL objects
     */
    public List<PhrAllowedUrl> getAllPhrAllowedUrls();
    
    /**
     * Get allowed URL configuration entries for a given URL
     * 
     * @param url a given URL checked for access allowance
     * @return list of configuration entries related to the given URL
     */
    public List<PhrAllowedUrl> getByUrl(String url);
    
    /**
     * Get allowed URL configuration entries for a given privilege
     * 
     * @param priv a given privilege checked for access allowance
     * @return list of configuration entries related to the given privilege
     */
    public List<PhrAllowedUrl> getByPrivilege(String priv);
}
