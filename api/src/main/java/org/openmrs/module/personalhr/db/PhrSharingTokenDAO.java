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
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.User;
import org.openmrs.module.personalhr.PhrSharingToken;

/**
 * Data Access Object for phr_sharing_token table access
 * 
 * @author hxiao
 */
public interface PhrSharingTokenDAO {
    
    /**
     * Set Hibernate session factory
     * 
     * @param sessionFactory Hibernate session factory object
     */
    public void setSessionFactory(SessionFactory sessionFactory);
    
    /**
     * Get sharing token of a given id
     * 
     * @param id id of the sharing token
     * @return sharing token
     */
    public PhrSharingToken getPhrSharingToken(Integer id);
    
    
    /**
     * Save a sharing token
     * 
     * @param token sharing token to save
     * @return sharing token saved
     */
    public PhrSharingToken savePhrSharingToken(PhrSharingToken token);
    
    /**
     * Delete a sharing token
     * 
     * @param token sharing token to be dleted
     */
    public void deletePhrSharingToken(PhrSharingToken token);
    
    /**
     * Get all sharing tokens
     *  
     * @return all sharing tokens
     */
    public List<PhrSharingToken> getAllPhrSharingTokens();
    
    /**
     * Get sharing tokens belonging to a given patient
     * 
     * @param pat patient
     * @return sharing tokens belonged to a given patient
     */
    public List<PhrSharingToken> getSharingTokenByPatient(Patient pat);
    
    /**
     * Get sharing tokens belonging to a given person
     * 
     * @param per person
     * @return sharing tokens belonged to a given person
     */
    public List<PhrSharingToken> getSharingTokenByPerson(Person per);
    
    /**
     * Get sharing tokens owned by a given user on a given patient or person
     * 
     * @param requestedPatient given patient who grants the sharing token
     * @param requestedPerson given person who grants the sharing token
     * @param requestingUser given user who is granted with the sharing token
     * @return sharing token granted to the given user
     */
    public PhrSharingToken getSharingToken(Patient requestedPatient, Person requestedPerson, User requestingUser);
    
    /**
     * Delete sharing token of a given id
     * 
     * @param id sharing token id
     */
    public void deletePhrSharingToken(Integer id);
    
    /**
     * Get sharing token of a given id
     * 
     * @param tokenString token string
     * @return sharing token object
     */
    public PhrSharingToken getSharingToken(String tokenString);
    
    /**
     * Update a sharing token for a given user on a given person
     * @param user given user
     * @param person given person
     * @param sharingToken given sharing token to update
     */
    public void updateSharingToken(User user, Person person, String sharingToken);
}
