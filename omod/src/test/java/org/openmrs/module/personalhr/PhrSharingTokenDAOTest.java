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

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.db.PatientDAO;
import org.openmrs.api.db.PersonDAO;
import org.openmrs.api.db.UserDAO;
import org.openmrs.module.personalhr.db.PhrSharingTokenDAO;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

public class PhrSharingTokenDAOTest extends BaseModuleContextSensitiveTest {
    
    protected final Log log = LogFactory.getLog(getClass());
    
    private PhrSharingTokenDAO dao = null;
    
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
        
        if (this.dao == null) {
            // fetch the dao from PhrSeucrity service, rather than from the spring application context
            // this bean name matches the name in /metadata/spring/applicationContext-service.xml
            this.dao = PersonalhrUtil.getService().getSharingTokenDao();
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
    @Verifies(value = "should return sharing tokens", method = "getSharingToken(Integer, Integer, Integer")
    public void testGetSharingToken() throws Exception {
        final PhrSharingToken token = this.dao.getSharingToken(this.patientDao.getPatient(4), this.personDao.getPerson(5),
            this.userDao.getUser(3)); //larmstrong2-4, msmith3-5, hxiao4-7
        Assert.assertNotNull(token);
        this.log.debug("Sharing token: " + token.getSharingToken() + "|" + token.getPatient() + "|" + token.getShareType()
                + "|" + token.getRelatedPersonName());
        this.log.debug(token);
        
        final List<PhrSharingToken> tokens = this.dao.getSharingTokenByPerson(this.patientDao.getPatient(4)); //larmstrong2-4, msmith3-5, hxiao4-7
        Assert.assertNotNull(tokens);
        Assert.assertTrue(tokens.size() > 0);
    }
    
    @Test
    public void testSaveSharingToken() throws Exception {
        PhrSharingToken token = this.dao.getSharingToken(this.patientDao.getPatient(4), this.personDao.getPerson(5),
            this.userDao.getUser(3)); //larmstrong2-4, msmith3-5, hxiao4-7
        Assert.assertNotNull(token);
        this.log.debug("Sharing person old email: " + token.getRelatedPersonEmail());
        final String oldEmail = token.getRelatedPersonEmail();
        if (!oldEmail.contains("-new")) {
            token.setRelatedPersonEmail(oldEmail + "-new");
        } else {
            token.setRelatedPersonEmail(oldEmail.replace("-new", ""));
        }
        
        this.dao.savePhrSharingToken(token);
        
        token = this.dao
                .getSharingToken(this.patientDao.getPatient(4), this.personDao.getPerson(5), this.userDao.getUser(3)); //larmstrong2-4, msmith3-5, hxiao4-7
        
        this.log.debug("Sharing person new email: " + token.getRelatedPersonEmail());
        
        Assert.assertTrue(!token.getRelatedPersonEmail().equals(oldEmail));
    }
    
    @Test
    public void testAddDeleteSharingToken() throws Exception {
        final PhrSharingToken token = new PhrSharingToken();
        final String tokenString = PersonalhrUtil.getRandomToken();
        token.setSharingToken(tokenString);
        token.setPatient(this.patientDao.getPatient(4));
        token.setRelatedPersonEmail("unit_test@test.test");
        token.setRelatedPersonName("unit_test, person");
        token.setShareType("Share Medical");
        token.setRelationType("Child");
        final Date startDate = new Date();
        token.setStartDate(startDate);
        token.setDateCreated(startDate);
        token.setExpireDate(PersonalhrUtil.getExpireDate(startDate));
        token.setCreator(this.userDao.getUser(4));
        
        //add this token
        this.dao.savePhrSharingToken(token);
        
        final PhrSharingToken tokenSaved = this.dao.getSharingToken(tokenString); //larmstrong2-4, msmith3-5, hxiao4-7
        Assert.assertNotNull(tokenSaved);
        
        this.log.debug("Sharing token saved: " + tokenSaved.getId() + "|" + tokenSaved.getSharingToken());
        
        Assert.assertTrue(tokenSaved.getRelatedPersonName().equals(token.getRelatedPersonName())
                && tokenSaved.getRelatedPersonEmail().equals(token.getRelatedPersonEmail())
                && tokenSaved.getShareType().equals(token.getShareType())
                && tokenSaved.getRelationType().equals(token.getRelationType()));
        
        //delete this token
        this.dao.deletePhrSharingToken(tokenSaved.getId());
        final PhrSharingToken tokenDeleted = this.dao.getSharingToken(tokenString); //larmstrong2-4, msmith3-5, hxiao4-7
        Assert.assertNull(tokenDeleted);
    }
    
    @Override
    public Boolean useInMemoryDatabase() {
        return false;
    }
}
