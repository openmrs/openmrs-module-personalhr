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

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Patient;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.exportccd.ImportedCCD;
import org.openmrs.module.exportccd.api.db.ImportedCCDDAO;
/**
 *  Implementation of database methods for imported_ccd table.
 *  @author hxiao
 */
public class HibernateImportedCCDDAO implements ImportedCCDDAO {
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
	
	public ImportedCCD getImportedCCD(Patient pat) throws HibernateException {
		List ccdList = sessionFactory.getCurrentSession().createCriteria(ImportedCCD.class).add(Restrictions.eq("importedFor",pat)).list();
		return (ccdList == null || ccdList.isEmpty()) ? null : (ImportedCCD) (ccdList.get(0));
	}
	
	public void saveImportedCCD(ImportedCCD ccd) throws HibernateException {
		//sessionFactory.getCurrentSession().saveOrUpdate(ccd);
		ImportedCCD existingCCD = getImportedCCD (ccd.getImportedFor());
		if(existingCCD != null) {
			existingCCD.setCcdImported(ccd.getCcdImported());
			existingCCD.setDateImported(new Date());
			existingCCD.setImportedBy(Context.getAuthenticatedUser());
			sessionFactory.getCurrentSession().update(existingCCD);
		} else {
			sessionFactory.getCurrentSession().saveOrUpdate(ccd);
			/*
	        Session sess = sessionFactory.openSession();
	        Transaction tx = sess.beginTransaction();
	        sess.setFlushMode(FlushMode.COMMIT); // allow queries to return stale state
	        sess.saveOrUpdate(ccd);
	        tx.commit();
	        //sess.flush();
	        sess.close();
	        */
		}		
	} 	
}