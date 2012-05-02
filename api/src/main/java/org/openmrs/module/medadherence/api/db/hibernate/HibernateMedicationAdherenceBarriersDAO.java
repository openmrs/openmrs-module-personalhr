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
package org.openmrs.module.medadherence.api.db.hibernate;

import java.util.Date;
import java.util.Set;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.openmrs.*;
import org.openmrs.module.medadherence.api.db.MedicationAdherenceBarriersDAO;
import org.openmrs.api.context.Context;

/**
 * It is a default implementation of  {@link MedicationAdherenceBarriersDAO}.
 * No direct query to database though
 * Use OpenMRS utility to query patient encounter data instead
 */
public class HibernateMedicationAdherenceBarriersDAO implements MedicationAdherenceBarriersDAO {
	protected final Log log = LogFactory.getLog(this.getClass());
	
	private SessionFactory sessionFactory;
	
	static private String MEDICATION_BARRIERS_ENCOUNTER="Medication Barriers Encounter";
	
	/**
     * @param sessionFactory the sessionFactory to set
     */
    public void setSessionFactory(SessionFactory sessionFactory) {
	    this.sessionFactory = sessionFactory;
    }
    
	/**
     * @return the sessionFactory
     */
    public SessionFactory getSessionFactory() {
	    return sessionFactory;
    }

	@Override
	public Set<Obs> getLatestMedicationBarriers(Patient pat) {
		// TODO Auto-generated method stub
		Encounter enc = findLatestEncounter(pat, MEDICATION_BARRIERS_ENCOUNTER);
		Set<Obs> obsSet = enc.getAllObs(false);
		
		return obsSet;
	}
	
	@Override
	public Date getLatestFormEntryDate(Patient pat) {
		Encounter enc = findLatestEncounter(pat, MEDICATION_BARRIERS_ENCOUNTER);
		if(enc == null) {
			return null;
		}
		return enc.getDateCreated();
	}	
	
	private Encounter findLatestEncounter(Patient pat, String encounterType) {
	
		List<Encounter> encs = Context.getEncounterService().getEncountersByPatient(pat);   
		if(encs==null || encs.isEmpty()) {
			return null;
		}
		Integer encId = null;
		Date encDate = null;
		Encounter latestEncounter = null;
		for(Encounter enc : encs) {    		
			if(!enc.isVoided() && encounterType.equals(enc.getEncounterType().getName())) {
				if((encId == null || enc.getEncounterDatetime().after(encDate))) {
					encId = enc.getId();
					encDate = enc.getEncounterDatetime();
					enc.getObs();
					latestEncounter = enc;
				}   			
			}
		}
		
		return latestEncounter;
		
	}
	
}