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
package org.openmrs.module.exportccd.api.db;

import java.util.List;

import org.openmrs.Concept;
import org.openmrs.module.exportccd.CCDSectionEntity;
import org.openmrs.module.exportccd.api.PatientSummaryExportService;
import org.openmrs.api.APIException;
import org.openmrs.api.db.DAOException;
/**
 *  Database methods for {@link PatientSummaryExportService}.
 */
public interface PatientSummaryExportDAO {
	


	public List<Concept> getConceptByCategory(String category);
	public boolean deleteConceptByCategory(CCDSectionEntity e);
	public CCDSectionEntity getConceptByCcdSectionEntity(Integer conceptId , String category);
	public CCDSectionEntity saveConceptByCategory(CCDSectionEntity e) throws DAOException, APIException;
}