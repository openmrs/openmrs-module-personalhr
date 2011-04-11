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
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.openmrs.module.personalhr.PhrLogEvent;
import org.openmrs.module.personalhr.db.PhrLogEventDAO;

/**
 * Hibernate implementation of the Data Access Object
 */
public class HibernatePhrLogEventDAO implements PhrLogEventDAO {
    
    protected final Log log = LogFactory.getLog(getClass());
    
    private SessionFactory sessionFactory;
    
    @Override
    public void setSessionFactory(final SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    /* (non-Jsdoc)
     * @see org.openmrs.module.personalhr.db.PhrLogEventDAO#savePhrEventLog(org.openmrs.module.personalhr.PhrLogEvent)
     */
    @Override
    public PhrLogEvent savePhrEventLog(PhrLogEvent event) {
        Session sess = sessionFactory.openSession();
        Transaction tx = sess.beginTransaction();
        sess.setFlushMode(FlushMode.COMMIT); // allow queries to return stale state
        sess.saveOrUpdate(event);
        tx.commit();
        //sess.flush();
        sess.close();
        return event;
    }    
}
