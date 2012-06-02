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
package org.openmrs.module.medadherence.rest;

import java.util.List;

import org.openmrs.Patient;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.medadherence.MedBarriers;
import org.openmrs.module.medadherence.api.MedicationAdherenceBarriersService;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.resource.impl.DataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;


/**
 * {@link Resource} for Obs, supporting standard CRUD operations
 */
@Resource("medbarriers")
@Handler(supports = MedBarriers.class, order = 1)
public class MedBarriersResource extends DataDelegatingCrudResource<MedBarriers> {

	public MedBarriersResource() {
		log.debug("Constructor MedBarriersResource called");
	}
	
	/**
	 * @parsm uniqueId unique id of patient
	 */
	@Override
	public MedBarriers getByUniqueId(String uniqueId) {
		// TODO Auto-generated method stub
		Patient pat = Context.getPatientService().getPatientByUuid(uniqueId);
		if(pat == null) {			
			List<Patient> pats = Context.getPatientService().getPatients(uniqueId);
			if(pats!=null && !pats.isEmpty()) {
				if(pats.size() > 1) {
					log.warn("More than one patients are found for uniqueId=" + uniqueId + ". Only the first one found will be returned.");
				} 
				pat = pats.get(0);
			}
		}
		return Context.getService(MedicationAdherenceBarriersService.class).getTopFiveBarriers(pat);
	}

	@Override
	protected MedBarriers newDelegate() {
		// TODO Auto-generated method stub
		return new MedBarriers();
	}

	@Override
	protected MedBarriers save(
			MedBarriers delegate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void delete(MedBarriers delegate, String reason,
			RequestContext context) throws ResponseException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void purge(MedBarriers delegate,
			RequestContext context) throws ResponseException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public DelegatingResourceDescription getRepresentationDescription(
			Representation rep) {
		if (rep instanceof DefaultRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("patient_name", findMethod("getPatientName"));
			description.addProperty("patient_identifiers", findMethod("getPatientIdentiers"));
			description.addProperty("medication_barriers", findMethod("getMedicationBarriers"));
			return description;
		} 
		return null;	
	}

	/**
	 * @param patient
	 * @return identifier + name (for concise display purposes)
	 */
	public String getMedicationBarriers(MedBarriers barriers) {
		if (barriers == null)
			return "";
		
		return barriers.getMedicationBarriers();
	}
	
	public String getPatientName(MedBarriers barriers) {
		if (barriers == null)
			return "";
		
		return barriers.getPatientName();
	}	
	
	public String getPatientIdentiers(MedBarriers barriers) {
		if (barriers == null)
			return "";
		
		return barriers.getPatientIdentifiers();
	}		

}