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
import org.openmrs.module.personalhr.model.PhrPrivilege;
import org.openmrs.module.personalhr.db.PhrPrivilegeDAO;
import org.openmrs.module.personalhr.service.PhrPrivilegeService;

import java.util.List;

public class PhrPrivilegeServiceImpl extends BaseOpenmrsService implements PhrPrivilegeService {

	private PhrPrivilegeDAO dao;

	public void setDao(PhrPrivilegeDAO dao) {
		this.dao = dao;
	}

	@Override
	public PhrPrivilege savePhrPrivilege(PhrPrivilege phrPrivilege) {
		return dao.savePhrPrivilege(phrPrivilege);
	}

	@Override
	public List<PhrPrivilege> getByPrivilege(String privilege) {
		return dao.getByPrivilege(privilege);
	}

	@Override
	public List<PhrPrivilege> getAllPhrPrivileges() {
		return dao.getAllPhrPrivileges();
	}

	@Override
	public void deletePhrPrivilege(PhrPrivilege phrPrivilege) {
		dao.deletePhrPrivilege(phrPrivilege);
	}
}
