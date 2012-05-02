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
package org.openmrs.module.medadherence.api.db;

import java.util.Date;
import java.util.Set;

import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.module.medadherence.api.MedicationAdherenceBarriersService;

/**
 *  Database methods for {@link MedicationAdherenceBarriersService}.
 */
public interface MedicationAdherenceBarriersDAO {
	
	/*
	 * Get all medication barriers
	 */
	public Set<Obs> getLatestMedicationBarriers(Patient pat);

	/*
	 * Get latest form entry date for medication barriers questionaire
	 * @return null if no form entries have been found, otherwise the creation date of the latest form entry
	 */
	public Date getLatestFormEntryDate(Patient pat); 
}