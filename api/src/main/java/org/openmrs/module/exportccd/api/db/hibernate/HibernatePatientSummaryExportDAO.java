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
package org.openmrs.module.exportccd.api.db.hibernate;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.openmrs.Concept;
import org.openmrs.api.APIException;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.exportccd.CCDSectionEntity;
import org.openmrs.module.exportccd.api.db.PatientSummaryExportDAO;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;
/**
 * It is a default implementation of  {@link PatientSummaryExportDAO}.
 */
public class HibernatePatientSummaryExportDAO implements PatientSummaryExportDAO {
	protected final Log log = LogFactory.getLog(this.getClass());
	
	private SessionFactory sessionFactory;
	
	/**
     * @param sessionFactory the sessionFactory to set
     */
    public void setSessionFactory(SessionFactory sessionFactory) {
	    this.sessionFactory = sessionFactory;
    }
    
	/**
     * @return the sessionFactory
     */
    public SessionFactory getSessionFactory() {
	    return sessionFactory;
    }

	
	public CCDSectionEntity saveConceptByCategory(CCDSectionEntity e) throws DAOException, APIException {
		try
		{
				sessionFactory.getCurrentSession().save(e);
		}catch(ConstraintViolationException c)
		{
			throw new APIException("Concept Already Exists");
		}
		
		return e;
	}

	@Override
	public java.util.List<Concept> getConceptByCategory(
			String category) {
		
		
		Criteria c =  sessionFactory.getCurrentSession().createCriteria(CCDSectionEntity.class);
		ProjectionList projList =Projections.projectionList();

		projList.add(Projections.property("concept"));
		c.setProjection(projList);

		c.add(Restrictions.eq("category", category)).list();
		
		List<Concept> l = c.list();
		return l; 
		
		
	}

	@Override
	public boolean deleteConceptByCategory(CCDSectionEntity e) {
		// TODO Auto-generated method stub
		
		sessionFactory.getCurrentSession().delete(e);
		return true;
	}

	@Override
	public CCDSectionEntity getConceptByCcdSectionEntity(Integer conceptId , String category) {
		// TODO Aut1-generated method stub
		
		
		return (CCDSectionEntity)sessionFactory.getCurrentSession().createCriteria(CCDSectionEntity.class).add(Restrictions.eq("ccdSectionEntity",conceptId+category)).list().get(0);
	}

	
}