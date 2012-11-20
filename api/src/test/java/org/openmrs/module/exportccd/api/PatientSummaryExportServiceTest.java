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
package org.openmrs.module.exportccd.api;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.InputStream;

import org.junit.Ignore;
import org.junit.Test;
import org.openhealthtools.mdht.uml.cda.ClinicalDocument;
import org.openhealthtools.mdht.uml.cda.ccd.CCDPackage;
import org.openhealthtools.mdht.uml.cda.ccd.ContinuityOfCareDocument;
import org.openhealthtools.mdht.uml.cda.util.CDAUtil;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.web.controller.patient.ShortPatientModel;

/**
 * Tests {@link ${PatientSummaryExportService}}.
 */
public class  PatientSummaryExportServiceTest extends BaseModuleContextSensitiveTest {
	
	@Ignore
	public void shouldSetupContext() {
		assertNotNull(Context.getService(PatientSummaryExportService.class));
	}
	
	@Test
	public void consumeCCD() throws Exception {
		PatientSummaryImportService importService = Context.getService(PatientSummaryImportService.class);
		assertNotNull(importService);
		InputStream is = new FileInputStream("G:\\COMMON\\everyone\\hxiao\\laf\\innovation\\CCD.xml");
		assertNotNull(importService.consumeCCD(is));		
	}	
}
