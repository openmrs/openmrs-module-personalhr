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
		System.out.println("Constructor MedBarriersResource called");
	}
	
	/**
	 * @parsm uniqueId unique id of patient
	 */
	@Override
	public MedBarriers getByUniqueId(String uniqueId) {
		// TODO Auto-generated method stub
		return Context.getService(MedicationAdherenceBarriersService.class).getTopFiveBarriers(Context.getPatientService().getPatientByUuid(uniqueId));
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
			description.addProperty("uuid");
			description.addProperty("display", findMethod("getDisplayString"));
			return description;
		} 
		return null;	
	}

	/**
	 * @param patient
	 * @return identifier + name (for concise display purposes)
	 */
	public String getDisplayString(MedBarriers barriers) {
		if (barriers == null)
			return "";
		
		return barriers.getDisplayString();
	}

}