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
import org.openmrs.module.personalhr.model.PhrAllowedUrl;
import org.openmrs.module.personalhr.service.PhrAllowedUrlService;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

import java.util.List;

public class PhrAllowedUrlDAOTest extends BaseModuleContextSensitiveTest {

	/**
	 * Run this before each unit test in this class. The "@Before" method in
	 * {@link BaseContextSensitiveTest} is run right before this method.
	 *
	 * @throws Exception
	 */
	@Before
	public void runBeforeEachTest() throws Exception {
		executeDataSet("datasets/allowed-url-dao-test.xml");
	}

	@Test
	@Verifies(value = "should escape sql wildcards in searchPhrase", method = "getUsers(String, List, Boolean)")
	public void testGetByUrl() throws Exception {
		List<PhrAllowedUrl> urls = Context.getService(PhrAllowedUrlService.class).getByUrl("/openmrs/module/htmlformentry/htmlFormEntry.form");
		Assert.assertNotNull(urls);
		Assert.assertEquals(1, urls.size());
		Assert.assertEquals("/module/htmlformentry/htmlFormEntry.form|View Treatment Summary", urls.get(0).getAllowedUrl()
				+ "|" + urls.get(0).getPrivilege());
	}
}
