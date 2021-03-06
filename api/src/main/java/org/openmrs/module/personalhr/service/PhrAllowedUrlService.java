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

package org.openmrs.module.personalhr.service;

import org.openmrs.api.OpenmrsService;
import org.openmrs.module.personalhr.model.PhrAllowedUrl;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface PhrAllowedUrlService extends OpenmrsService {

	@Transactional(readOnly = false)
	public PhrAllowedUrl savePhrAllowedUrl(PhrAllowedUrl url);

	@Transactional(readOnly = true)
	public List<PhrAllowedUrl> getAllPhrAllowedUrls();

	@Transactional(readOnly = false)
	public void deletePhrAllowedUrl(PhrAllowedUrl phrAllowedUrl);

	@Transactional(readOnly = true)
	public List<PhrAllowedUrl> getByUrl(String url);
}
