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

import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.personalhr.model.PhrLogEvent;
import org.openmrs.module.personalhr.db.PhrLogEventDAO;
import org.openmrs.module.personalhr.service.PhrLogEventService;

public class PhrLogEventServiceImpl extends BaseOpenmrsService implements PhrLogEventService {

	PhrLogEventDAO dao;

	public void setDao(PhrLogEventDAO dao) {
		this.dao = dao;
	}

	@Override
	public PhrLogEvent savePhrEventLog(PhrLogEvent eventLog) {
		return dao.savePhrEventLog(eventLog);
	}
}
