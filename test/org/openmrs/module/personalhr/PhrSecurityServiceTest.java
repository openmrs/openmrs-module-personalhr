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
package org.openmrs.module.personalhr;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.db.PatientDAO;
import org.openmrs.api.db.PersonDAO;
import org.openmrs.api.db.UserDAO;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

public class PhrSecurityServiceTest extends BaseModuleContextSensitiveTest {
    
    private PhrSecurityService service = null;
    
    private UserDAO userDao = null;
    
    private PersonDAO personDao = null;
    
    private PatientDAO patientDao = null;
    
    /**
     * Run this before each unit test in this class. The "@Before" method in
     * {@link BaseContextSensitiveTest} is run right before this method.
     * 
     * @throws Exception
     */
    @Before
    public void runBeforeEachTest() throws Exception {
        //org.hibernate.impl.SessionFactoryImpl sessionFactory = (org.hibernate.impl.SessionFactoryImpl) applicationContext.getBean("sessionFactory");
        //String[] mappingRes = new String[1];
        //mappingRes[0]="personalhr.hbm.xml";
        //sessionFactory.setMappingResources(mappingRes);
        
        if (this.service == null) {
            // fetch the dao from PhrSeucrity service, rather than from the spring application context
            // this bean name matches the name in /metadata/spring/applicationContext-service.xml
            this.service = PersonalhrUtil.getService();
        }
        if (this.patientDao == null) {
            this.patientDao = (PatientDAO) this.applicationContext.getBean("patientDAO");
        }
        
        if (this.personDao == null) {
            this.personDao = (PersonDAO) this.applicationContext.getBean("personDAO");
        }
        
        if (this.userDao == null) {
            this.userDao = (UserDAO) this.applicationContext.getBean("userDAO");
        }
    }
    
    @Test
    @Verifies(value = "should escape sql wildcards in searchPhrase", method = "isUrlAllowed(String, Patient, Person, User)")
    public void testIsUrlAllowed() throws Exception {
        //larmstrong:2-4-4, msmith:3-5-5, hxiao:4-7-7 (username: userId-patientId-personId)
        boolean isAllowed = this.service.isUrlAllowed("/openmrs/module/htmlformentry/htmlFormEntry.form",
            this.patientDao.getPatient(4), this.personDao.getPerson(5), this.userDao.getUser(2));
        Assert.assertTrue(isAllowed);
        
        isAllowed = this.service.isUrlAllowed("/openmrs/module/htmlformentry/htmlFormEntry.form",
            this.patientDao.getPatient(4), this.personDao.getPerson(5), this.userDao.getUser(3));
        Assert.assertTrue(isAllowed);
        
        isAllowed = this.service.isUrlAllowed("/openmrs/module/htmlformentry/htmlFormEntry.form",
            this.patientDao.getPatient(4), this.personDao.getPerson(5), this.userDao.getUser(4));
        Assert.assertTrue(isAllowed);
        
    }
    
    @Test
    @Verifies(value = "should escape sql wildcards in searchPhrase", method = "isUrlAllowed(String, Patient, Person, User)")
    public void testHasPrivilege() throws Exception {
        //larmstrong:2-4-4, msmith:3-5-5, hxiao:4-7-7 (username: userId-patientId-personId)
        boolean isAllowed = this.service.hasPrivilege("View Treatment Summary", this.patientDao.getPatient(4),
            this.personDao.getPerson(5), this.userDao.getUser(2));
        Assert.assertTrue(isAllowed);
        
        isAllowed = this.service.hasPrivilege("View Treatment Summary", this.patientDao.getPatient(4),
            this.personDao.getPerson(5), this.userDao.getUser(3));
        Assert.assertTrue(isAllowed);
        
        isAllowed = this.service.hasPrivilege("View Treatment Summary", this.patientDao.getPatient(4),
            this.personDao.getPerson(5), this.userDao.getUser(4));
        Assert.assertTrue(isAllowed);
    }
    
    @Override
    public Boolean useInMemoryDatabase() {
        return false;
    }
}
