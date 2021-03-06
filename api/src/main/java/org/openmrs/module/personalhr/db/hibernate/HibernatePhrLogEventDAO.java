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
package org.openmrs.module.personalhr.db.hibernate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.openmrs.module.personalhr.model.PhrLogEvent;
import org.openmrs.module.personalhr.db.PhrLogEventDAO;

/**
 * Hibernate implementation of the Data Access Object
 *
 * @author hxiao
 */
public class HibernatePhrLogEventDAO implements PhrLogEventDAO {

	protected final Log log = LogFactory.getLog(getClass());

	private SessionFactory sessionFactory;

	/**
	 * @see org.openmrs.module.personalhr.db.PhrLogEventDAO#setSessionFactory(org.hibernate.SessionFactory)
	 */
	@Override
	public void setSessionFactory(final SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/**
	 * @see org.openmrs.module.personalhr.db.PhrLogEventDAO#savePhrEventLog(org.openmrs.module.personalhr.model.PhrLogEvent)
	 */
	@Override
	public PhrLogEvent savePhrEventLog(PhrLogEvent event) {
		sessionFactory.getCurrentSession().saveOrUpdate(event);
		return event;
	}
}
