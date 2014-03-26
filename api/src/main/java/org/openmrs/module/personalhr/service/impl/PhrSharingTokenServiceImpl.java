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

package org.openmrs.module.personalhr.service.impl;

import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.User;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.personalhr.model.PhrSharingToken;
import org.openmrs.module.personalhr.db.PhrSharingTokenDAO;
import org.openmrs.module.personalhr.service.PhrSharingTokenService;

import java.util.List;

public class PhrSharingTokenServiceImpl extends BaseOpenmrsService implements PhrSharingTokenService {

	PhrSharingTokenDAO dao;

	public void setDao(PhrSharingTokenDAO dao) {
		this.dao = dao;
	}

	@Override
	public PhrSharingToken getSharingToken(Patient requestedPatient, Person requestedPerson, User user) {
		return dao.getSharingToken(requestedPatient, requestedPerson, user);
	}

	@Override
	public List<PhrSharingToken> getSharingTokenByPatient(Patient pat) {
		return dao.getSharingTokenByPatient(pat);
	}

	@Override
	public List<PhrSharingToken> getSharingTokenByPerson(Person person) {
		return dao.getSharingTokenByPerson(person);
	}

	@Override
	public PhrSharingToken getSharingToken(String sharingToken) {
		return dao.getSharingToken(sharingToken);
	}

	@Override
	public PhrSharingToken savePhrSharingToken(PhrSharingToken token) {
		return dao.savePhrSharingToken(token);
	}

	@Override
	public PhrSharingToken getPhrSharingToken(Integer id) {
		return dao.getPhrSharingToken(id);
	}

	@Override
	public void deletePhrSharingToken(PhrSharingToken token) {
		dao.deletePhrSharingToken(token);
	}

	@Override
	public void deletePhrSharingToken(Integer id) {
		dao.deletePhrSharingToken(id);
	}

	@Override
	public void updateSharingToken(User user, Person person, String sharingToken) {
		dao.updateSharingToken(user, person, sharingToken);
	}
}
