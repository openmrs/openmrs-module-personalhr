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
package org.openmrs.module.personalhr;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.personalhr.db.PhrSharingTokenDAO;
import org.openmrs.module.personalhr.model.PhrSharingToken;
import org.openmrs.module.personalhr.service.PhrService;
import org.openmrs.module.personalhr.service.PhrSharingTokenService;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * The basic Sharing Token data object
 *
 * @author hxiao
 */

public class PhrPatient {

	protected final Log log = LogFactory.getLog(getClass());

	private List<PhrSharingToken> sharingTokens;

	private String personName;

	private PhrSharingToken newSharingToken;

	private Set<String> sharingTypes;

	private PhrService.PhrRelationType[] relationTypes;

	private Patient patient;

	private Integer patientId;

	private int numberChanged;

	private int numberAdded;

	private int numberDeleted;

	/**
	 * Construct a PhrPatient object with sharing tokens and other information pre-populated
	 *
	 * @param patId a given patient ID
	 */
	public PhrPatient(final Integer patId) {
		this.patientId = patId;

		this.patient = (patId == null ? null : Context.getPatientService().getPatient(patId));

		if (this.patient != null) {
			this.sharingTokens = Context.getService(PhrSharingTokenService.class).getSharingTokenByPatient(this.patient);
		}

		if (this.sharingTokens != null) {
			this.log.debug("Constructing PhrPatient: patId|patent|sharingTokens.szie=" + patId + "|" + this.patient + "|"
					+ this.sharingTokens.size());
		} else {
			this.log.debug("Constructing PhrPatient: patId|patent|sharingTokens=" + patId + "|" + this.patient + "|"
					+ this.sharingTokens);
		}

		this.sharingTypes = PersonalhrUtil.getService().getSharingTypes();

		this.relationTypes = PhrService.PhrRelationType.values();

		this.newSharingToken = new PhrSharingToken();
		this.newSharingToken.setPatient(this.patient);
		this.newSharingToken.setId(null);
		this.newSharingToken.setRelatedPersonName(null);
		this.newSharingToken.setRelatedPersonEmail(null);

		this.numberChanged = 0;
		this.numberAdded = 0;
		this.numberDeleted = 0;
		this.personName = null;

	}

	/**
	 * Get a list of sharing tokens owned by this patient
	 *
	 * @return a list of sharing tokens
	 */
	public List<PhrSharingToken> getSharingTokens() {
		return this.sharingTokens;
	}

	/**
	 * Set a list of sharing tokens for the patient
	 *
	 * @param sharingTokens a list of sharing tokens
	 */
	public void setSharingTokens(final List<PhrSharingToken> sharingTokens) {
		this.sharingTokens = sharingTokens;
	}

	/**
	 * Get the Patient object
	 *
	 * @return a Patient object
	 */
	public Patient getPatient() {
		return this.patient;
	}

	/**
	 * Set a Patient object
	 *
	 * @param patient patient object
	 */
	public void setPatient(final Patient patient) {
		this.patient = patient;
	}

	/**
	 * Get the ID of this patient
	 *
	 * @return patient ID
	 */
	public Integer getPatientId() {
		return this.patientId;
	}


	/**
	 * Set the id of this patient
	 *
	 * @param patientId patient ID
	 */
	public void setPatientId(final Integer patientId) {
		this.patientId = patientId;
	}

	/**
	 * Get all sharing types across all PHR users
	 *
	 * @return sharing types defined across PHR users
	 */
	public Set<String> getSharingTypes() {
		return this.sharingTypes;
	}

	/**
	 * Set sharing types
	 *
	 * @param sharingTypes list of sharing types
	 */
	public void setSharingTypes(final Set<String> sharingTypes) {
		this.sharingTypes = sharingTypes;
	}

	/**
	 * Get all relation types
	 *
	 * @return a list of relation types
	 */
	public PhrService.PhrRelationType[] getRelationTypes() {
		return this.relationTypes;
	}

	/**
	 * Set all relation types
	 *
	 * @param relationTypes a list of relation types
	 */
	public void setRelationTypes(final PhrService.PhrRelationType[] relationTypes) {
		this.relationTypes = relationTypes;
	}

	/**
	 * Get a newly added sharing token
	 *
	 * @return a newly added sharing token
	 */
	public PhrSharingToken getNewSharingToken() {
		return this.newSharingToken;
	}


	/**
	 * Set a newly added sharing token
	 *
	 * @param newSharingToken a newly added sharing token
	 */
	public void setNewSharingToken(final PhrSharingToken newSharingToken) {
		this.newSharingToken = newSharingToken;
	}

	/**
	 * Save relationship changes into database
	 */
	public void save() {

		PhrSharingTokenService svc = Context.getService(PhrSharingTokenService.class);

		final List<PhrSharingToken> oldTokens = svc.getSharingTokenByPatient(this.patient);

		//check non-deleted relationships
		if (this.sharingTokens != null) {
			for (final PhrSharingToken token : this.sharingTokens) {
				if (token.getId() > 0) { //check changed relationship
					boolean isChanged = false;
					final PhrSharingToken oldToken = svc.getPhrSharingToken(token.getId());
					if (!oldToken.getRelatedPersonEmail().equals(token.getRelatedPersonEmail())) {
						oldToken.setRelatedPersonEmail(token.getRelatedPersonEmail());
						isChanged = true;
					}
					if (!oldToken.getRelatedPersonName().equals(token.getRelatedPersonName())) {
						oldToken.setRelatedPersonName(token.getRelatedPersonName());
						isChanged = true;
					}
					if (!oldToken.getRelationType().equals(token.getRelationType())) {
						oldToken.setRelationType(token.getRelationType());
						isChanged = true;
					}
					if (!oldToken.getShareType().equals(token.getShareType())) {
						oldToken.setShareType(token.getShareType());
						isChanged = true;
					}
					if (isChanged) { //save changed relationship
						this.numberChanged++;
						svc.savePhrSharingToken(token);
						this.log.debug("Changed token id: " + token.getId());
					}
				} else { //save added relationship
					this.numberAdded++;
					final PhrSharingToken addedToken = svc.savePhrSharingToken(token);
					this.log.debug("Newly added token id: " + addedToken.getId());
				}
			}
			Collections.sort(this.sharingTokens);
		}

		//check deleted relationships
		if (oldTokens != null) {
			for (final PhrSharingToken token : oldTokens) {
				if (Collections.binarySearch(this.sharingTokens, token) < 0) {
					svc.deletePhrSharingToken(token);
					this.numberDeleted++;
					this.log.debug("Deleted token id: " + token.getId());
				}
			}
		}

		//check newly added relationship
		if ((this.newSharingToken != null) && (this.newSharingToken.getRelatedPersonName() != null)) {
			final PhrSharingToken token = this.newSharingToken;

			final String tokenString = PersonalhrUtil.getRandomToken();
			token.setSharingToken(tokenString);
			token.setRelatedPersonEmail(token.getRelatedPersonEmail());
			token.setRelatedPersonName(token.getRelatedPersonName());
			token.setShareType(token.getShareType());
			token.setRelationType(token.getRelationType());
			final Date startDate = new Date();
			token.setStartDate(startDate);
			token.setDateCreated(startDate);
			token.setExpireDate(PersonalhrUtil.getExpireDate(startDate));
			token.setCreator(Context.getAuthenticatedUser());

			svc.savePhrSharingToken(token);

			this.numberAdded++;

			if (this.log.isDebugEnabled()) {
				this.log.debug("Newly added token id: " + svc.getSharingToken(tokenString).getId());
			}
		}

	}

	/**
	 * Get the number of sharing token changed
	 *
	 * @return the number of sharing token changed
	 */
	public int getNumberChanged() {
		return this.numberChanged;
	}

	/**
	 * Set the number of sharing token changed
	 *
	 * @param numberChanged the number of sharing token changed
	 */
	public void setNumberChanged(final int numberChanged) {
		this.numberChanged = numberChanged;
	}

	/**
	 * Get the number of sharing token added
	 *
	 * @return the number of sharing token added
	 */
	public int getNumberAdded() {
		return this.numberAdded;
	}

	/**
	 * Set the number of sharing token added
	 *
	 * @param numberAdded the number of sharing token added
	 */
	public void setNumberAdded(final int numberAdded) {
		this.numberAdded = numberAdded;
	}

	/**
	 * Get the number of sharing token deleted
	 *
	 * @return the number of sharing token deleted
	 */
	public int getNumberDeleted() {
		return this.numberDeleted;
	}

	/**
	 * Set the number of sharing token deleted
	 *
	 * @param numberDeleted the number of sharing token deleted
	 */
	public void setNumberDeleted(final int numberDeleted) {
		this.numberDeleted = numberDeleted;
	}

	/**
	 * Get the person's name
	 *
	 * @return person's name
	 */
	public String getPersonName() {
		return this.personName;
	}

	/**
	 * Set the name of the patient/person
	 *
	 * @param personName the name of the patient/person
	 */
	public void setPersonName(final String personName) {
		this.personName = personName;
	}

	/**
	 * Delete a sharing token of a given id
	 *
	 * @param id id of a sharing token to be deleted
	 */
	public void delete(final Integer id) {
		Context.getService(PhrSharingTokenService.class).deletePhrSharingToken(id);
		this.numberDeleted++;
	}
}
