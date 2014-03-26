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
import org.openmrs.module.personalhr.model.PhrAllowedUrl;
import org.openmrs.module.personalhr.db.PhrAllowedUrlDAO;
import org.openmrs.module.personalhr.service.PhrAllowedUrlService;

import java.util.List;

public class PhrAllowedUrlServiceImpl extends BaseOpenmrsService implements PhrAllowedUrlService {

	PhrAllowedUrlDAO dao;

	public void setDao(PhrAllowedUrlDAO dao) {
		this.dao = dao;
	}

	@Override
	public PhrAllowedUrl savePhrAllowedUrl(PhrAllowedUrl url) {
		return dao.savePhrAllowedUrl(url);
	}

	@Override
	public List<PhrAllowedUrl> getAllPhrAllowedUrls() {
		return dao.getAllPhrAllowedUrls();
	}

	@Override
	public void deletePhrAllowedUrl(PhrAllowedUrl phrAllowedUrl) {
		dao.deletePhrAllowedUrl(phrAllowedUrl);
	}

	@Override
	public List<PhrAllowedUrl> getByUrl(String url) {
		return dao.getByUrl(url);
	}
}
