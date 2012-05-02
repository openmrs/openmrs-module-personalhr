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

import org.openmrs.BaseOpenmrsData;
import org.openmrs.BaseOpenmrsMetadata;
import org.openmrs.BaseOpenmrsObject;
import org.openmrs.Concept;

/**
 * It is a model class. It should extend either {@link BaseOpenmrsObject} or {@link BaseOpenmrsMetadata}.
 */
public class MedicationAdherenceBarrier extends BaseOpenmrsObject implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer id;
	
	private Concept barrierType;
	private Concept barrierAnswer;
	
	public MedicationAdherenceBarrier(Concept type, Concept answer) {
		barrierType = type;
		barrierAnswer = answer;
	}
	
	@Override
	public Integer getId() {
		return id;
	}
	
	@Override
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Concept getBarrierAnswer() {
		return barrierAnswer;
	}

	public void setBarrierAnswer(Concept barrierAnswer) {
		this.barrierAnswer = barrierAnswer;
	}


	public Concept getBarrierType() {
		return barrierType;
	}

	public void setBarrierType(Concept barrierType) {
		this.barrierType = barrierType;
	}
	
}