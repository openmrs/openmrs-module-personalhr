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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.personalhr.service.PhrService;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

public class PhrSecurityServiceTest extends BaseModuleContextSensitiveTest {

	private PhrService service = null;
	private Person person;
	private Patient patient;
	private User user;

	/**
	 * Run this before each unit test in this class. The "@Before" method in
	 * {@link BaseContextSensitiveTest} is run right before this method.
	 *
	 * @throws Exception
	 */
	@Before
	public void runBeforeEachTest() throws Exception {
		person = Context.getPersonService().getPerson(9);
		patient = Context.getPatientService().getPatient(2);
		user = Context.getUserService().getUser(501);

		executeDataSet("datasets/allowed-url-dao-test.xml");
		executeDataSet("datasets/privilege-dao-test.xml");
		executeDataSet("datasets/sharing-token-dao-test.xml");

		if (this.service == null) {
			this.service = PersonalhrUtil.getService();
		}
	}

	@Test
	@Verifies(value = "should escape sql wildcards in searchPhrase", method = "isUrlAllowed(String, Patient, Person, User)")
	public void testIsUrlAllowed() throws Exception {
		Assert.assertTrue(this.service.isUrlAllowed("/openmrs/module/htmlformentry/htmlFormEntry.form", patient, person, user));
	}

	@Test
	@Verifies(value = "should escape sql wildcards in searchPhrase", method = "isUrlAllowed(String, Patient, Person, User)")
	public void testHasPrivilege() throws Exception {
		Assert.assertTrue(this.service.hasPrivilege("View Treatment Summary", patient, person, user));
	}
}
