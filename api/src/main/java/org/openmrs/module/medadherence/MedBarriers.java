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
package org.openmrs.module.medadherence;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.BaseOpenmrsMetadata;
import org.openmrs.Concept;
import org.openmrs.Obs;

/**
 * It is a model class. It should extend either {@link BaseOpenmrsData} or {@link BaseOpenmrsMetadata}.
 */
public class MedBarriers extends BaseOpenmrsData implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer id;
	
	Set<MedicationAdherenceBarrier> barriers = null;

	public MedBarriers () {
		barriers = new HashSet<MedicationAdherenceBarrier>();
	}
	
	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	public Set<MedicationAdherenceBarrier> getBarriers() {
		return barriers;
	}

	public void setBarriers(Set<MedicationAdherenceBarrier> barriers) {
		this.barriers = barriers;
	}
	
	public void addBarrier(Concept barrierType, Concept barrierScore) {
		MedicationAdherenceBarrier barrier = new MedicationAdherenceBarrier(barrierType, barrierScore);
		barriers.add(barrier);
	}
	
	public String getDisplayString() {
		if(barriers==null) { 
			return null;
		}
		
		String displayString = null;
		for(MedicationAdherenceBarrier barrier : barriers) {
			String barrierType = barrier.getBarrierType().getName().getName();
			String score = barrier.getBarrierAnswer().getName().getName();
			if(displayString == null) {
				displayString = barrierType + "^" + score;
			} else {
				displayString += "~" + barrierType + "^" + score;				
			}
		}
		return displayString;		
	}
}