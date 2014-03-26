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
import org.openmrs.api.context.Context;
import org.openmrs.module.personalhr.model.PhrPrivilege;
import org.openmrs.module.personalhr.service.PhrPrivilegeService;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

import java.util.List;

public class PhrPrivilegeServiceTest extends BaseModuleContextSensitiveTest {

	/**
	 * Run this before each unit test in this class. The "@Before" method in
	 * {@link BaseContextSensitiveTest} is run right before this method.
	 *
	 * @throws Exception
	 */
	@Before
	public void runBeforeEachTest() throws Exception {
		executeDataSet("datasets/privilege-dao-test.xml");
	}

	@Test
	@Verifies(value = "should return security rules", method = "getByPrivilege(String)")
	public void testGetByPrivilege() throws Exception {
		final List<PhrPrivilege> rules = Context.getService(PhrPrivilegeService.class).getByPrivilege("View Treatment Summary");
		Assert.assertNotNull(rules);
	}
}
