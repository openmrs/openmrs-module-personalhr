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
package org.openmrs.module.personalhr.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.layout.web.LayoutSupport;
import org.openmrs.layout.web.name.NameSupport;

public class PhrNameLayoutPortletController extends PhrLayoutPortletController {
	
	private static Log log = LogFactory.getLog(PhrNameLayoutPortletController.class);
	
	protected String getDefaultsPropertyName() {
		return "layout.name.defaults";
	}
	
	protected String getDefaultDivId() {
		return "phrNameLayoutPortlet";
	}
	
	protected LayoutSupport getLayoutSupportInstance() {
		log.debug("Getting PHR name layout instance");
		return NameSupport.getInstance();
	}
}
