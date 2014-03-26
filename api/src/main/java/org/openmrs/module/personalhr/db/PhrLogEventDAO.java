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
package org.openmrs.module.personalhr.db;

import org.hibernate.SessionFactory;
import org.openmrs.module.personalhr.model.PhrLogEvent;


/**
 * Data Access Object for phr_log_event table access
 * 
 * @author hxiao
 */
public interface PhrLogEventDAO {
    /**
     * Set hibernate session factory
     * 
     * @param sessionFactory Hibernate session factory
     */
    public void setSessionFactory(SessionFactory sessionFactory);
       
    /**
     * Save PhrEventLog object to database
     * 
     * @param event PhrEventLog object
     * @return event log object saved
     */
    public PhrLogEvent savePhrEventLog(PhrLogEvent event);    
}
