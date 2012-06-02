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
package org.openmrs.module.medadherence.api.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.medadherence.MedBarriers;
import org.openmrs.module.medadherence.MedicationAdherenceBarrier;
import org.openmrs.module.medadherence.api.MedicationAdherenceBarriersService;
import org.openmrs.module.medadherence.api.db.MedicationAdherenceBarriersDAO;

/**
 * It is a default implementation of {@link MedicationAdherenceBarriersService}.
 */
public class MedicationAdherenceBarriersServiceImpl extends BaseOpenmrsService implements MedicationAdherenceBarriersService {
	
	protected final Log log = LogFactory.getLog(this.getClass());
	
	private MedicationAdherenceBarriersDAO dao;

	protected final static HashMap<String, Integer> scoreMap = new HashMap<String, Integer>();
	static {
		scoreMap.put("Never", 1);
		scoreMap.put("Rarely", 2); 
		scoreMap.put("Sometimes", 3); 
		scoreMap.put("Often", 41); 
		scoreMap.put("Very Often", 5); 		
	}
	
	public class ObsComparable implements Comparator<Obs>{
		@Override
		public int compare(Obs o1, Obs o2) {
			String score1 = o1.getValueCoded().getName().getName();
			String score2 = o1.getValueCoded().getName().getName();
			
			return (scoreMap.get(score1)>scoreMap.get(score2) ? -1 : (scoreMap.get(score1)==scoreMap.get(score2) ? 0 : 1));
		}
	}
	
	/**
     * @param dao the dao to set
     */
    public void setDao(MedicationAdherenceBarriersDAO dao) {
	    this.dao = dao;
    }
    
    /**
     * @return the dao
     */
    public MedicationAdherenceBarriersDAO getDao() {
	    return dao;
    }

	@Override
	public String getTopFiveScores(Patient pat) {
		Set<Obs> obsSet = dao.getLatestMedicationBarriers(pat);
		List<Obs> obsList = new ArrayList();
		obsList.addAll(obsSet);
		Collections.sort(obsList, new ObsComparable());
		
		String topFive = null;
		for(int ii = 0; ii<5; ii++) {
			Obs obs = obsList.get(ii);
			String barrier = obs.getConcept().getName().getName();
			String score = obs.getValueCoded().getName().getName();
			if(topFive == null) {
				topFive = barrier + "^" + score;
			} else {
				topFive += "~" + barrier + "^" + score;				
			}
		}
		return topFive;
	}

	@Override
	public Date getLatestFormEntryDate(Patient pat) {
		return dao.getLatestFormEntryDate(pat);
	}

	@Override
	public MedBarriers getTopFiveBarriers(Patient pat) {
		// TODO Auto-generated method stub
		Set<Obs> obsSet = dao.getLatestMedicationBarriers(pat);
		List<Obs> obsList = new ArrayList();
		obsList.addAll(obsSet);
		Collections.sort(obsList, new ObsComparable());
		
		MedBarriers topFive = new MedBarriers(pat);
		for(int ii = 0; ii<5; ii++) {
			Obs obs = obsList.get(ii);
			Concept barrier = obs.getConcept();
			Concept score = obs.getValueCoded();
			topFive.addBarrier(barrier, score);
		}
		return topFive;
	}
}