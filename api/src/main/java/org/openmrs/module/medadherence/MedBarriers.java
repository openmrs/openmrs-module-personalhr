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
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;

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
	
	Patient patient = null;

	public MedBarriers () {
	}
	
	public MedBarriers (Patient pat) {
		barriers = new HashSet<MedicationAdherenceBarrier>();
		patient = pat;
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
	
	public String getMedicationBarriers() {
		if(barriers==null) { 
			return null;
		}
		
		String displayString = null;
		for(MedicationAdherenceBarrier barrier : barriers) {
			String barrierType = barrier.getBarrierType().getName().getName();
			String score = barrier.getBarrierAnswer().getName().getName();
			String solution = barrier.getBarrierType().getDescription().getDescription();
			if(displayString == null) {
				displayString = barrierType + "=" + score + "=" + solution;
			} else {
				displayString += "~" + barrierType + "=" + score + "=" + solution;				
			}
		}
		return displayString;		
	}
	
	public String getPatientName() {
		String info = null;
		if(patient!=null) {
			info = patient.getPersonName().getFullName();			
		}
		return info;
	}
	
	public String getPatientIdentifiers() {
		String info = null;
		if(patient!=null) {
			for(PatientIdentifier id : patient.getIdentifiers()) {
				if(info != null) {
					info += ("~" + id.getIdentifierType().getName() + "=" + id.getIdentifier()); 
				} else {
					info = id.getIdentifierType().getName() + "=" + id.getIdentifier();
				}
			}
		}
		return info;
	}	
}