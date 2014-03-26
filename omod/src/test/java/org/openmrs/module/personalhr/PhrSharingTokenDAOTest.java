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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.personalhr.model.PhrSharingToken;
import org.openmrs.module.personalhr.service.PhrSharingTokenService;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

import java.util.Date;
import java.util.List;

public class PhrSharingTokenDAOTest extends BaseModuleContextSensitiveTest {

	protected final Log log = LogFactory.getLog(getClass());

	private Patient patient;
	private User user;
	private PhrSharingTokenService svc;

	/**
	 * Run this before each unit test in this class. The "@Before" method in
	 * {@link BaseContextSensitiveTest} is run right before this method.
	 *
	 * @throws Exception
	 */
	@Before
	public void runBeforeEachTest() throws Exception {
		executeDataSet("datasets/sharing-token-dao-test.xml");

		patient = Context.getPatientService().getPatient(2);
		user = Context.getUserService().getUser(501);
		svc = Context.getService(PhrSharingTokenService.class);
	}

	@Test
	@Verifies(value = "should return sharing tokens", method = "getSharingToken(Integer, Integer, Integer")
	public void testGetSharingToken() throws Exception {
		PhrSharingToken token = svc.getSharingToken(patient, null, user);
		Assert.assertNotNull(token);
		this.log.debug("Sharing token: " + token.getSharingToken() + "|" + token.getPatient() + "|" + token.getShareType()
				+ "|" + token.getRelatedPersonName());
		this.log.debug(token);

		final List<PhrSharingToken> tokens = svc.getSharingTokenByPerson(patient);
		Assert.assertNotNull(tokens);
		Assert.assertTrue(tokens.size() > 0);
	}

	@Test
	public void testSaveSharingToken() throws Exception {
		PhrSharingToken token = svc.getSharingToken(patient, null, user);
		Assert.assertNotNull(token);

		this.log.debug("Sharing person old email: " + token.getRelatedPersonEmail());

		final String oldEmail = token.getRelatedPersonEmail();
		Assert.assertNotNull(oldEmail);

		if (!oldEmail.contains("-new")) {
			token.setRelatedPersonEmail(oldEmail + "-new");
		} else {
			token.setRelatedPersonEmail(oldEmail.replace("-new", ""));
		}

		svc.savePhrSharingToken(token);

		token = svc.getSharingToken(patient, null, user);

		this.log.debug("Sharing person new email: " + token.getRelatedPersonEmail());

		Assert.assertTrue(!token.getRelatedPersonEmail().equals(oldEmail));
	}

	@Test
	public void testAddDeleteSharingToken() throws Exception {
		final PhrSharingToken token = new PhrSharingToken();
		final String tokenString = PersonalhrUtil.getRandomToken();
		token.setSharingToken(tokenString);
		token.setPatient(patient);
		token.setRelatedPersonEmail("unit_test@test.test");
		token.setRelatedPersonName("unit_test, person");
		token.setShareType("Share Medical");
		token.setRelationType("Child");
		final Date startDate = new Date();
		token.setStartDate(startDate);
		token.setDateCreated(startDate);
		token.setExpireDate(PersonalhrUtil.getExpireDate(startDate));
		token.setCreator(user);

		//add this token
		svc.savePhrSharingToken(token);

		final PhrSharingToken tokenSaved = svc.getSharingToken(tokenString);
		Assert.assertNotNull(tokenSaved);

		this.log.debug("Sharing token saved: " + tokenSaved.getId() + "|" + tokenSaved.getSharingToken());

		Assert.assertTrue(tokenSaved.getRelatedPersonName().equals(token.getRelatedPersonName())
				&& tokenSaved.getRelatedPersonEmail().equals(token.getRelatedPersonEmail())
				&& tokenSaved.getShareType().equals(token.getShareType())
				&& tokenSaved.getRelationType().equals(token.getRelationType()));

		//delete this token
		svc.deletePhrSharingToken(tokenSaved.getId());
		final PhrSharingToken tokenDeleted = svc.getSharingToken(tokenString);
		Assert.assertNull(tokenDeleted);
	}
}
