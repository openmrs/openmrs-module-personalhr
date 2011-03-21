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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.personalhr.db.PhrSecurityRuleDAO;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

public class PhrSecurityRuleDAOTest extends BaseModuleContextSensitiveTest {
    
    protected final Log log = LogFactory.getLog(getClass());
    
    private PhrSecurityRuleDAO dao = null;
    
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
            this.dao = PersonalhrUtil.getService().getSecurityRuleDao();
        }
    }
    
    @Test
    @Verifies(value = "should return security rules", method = "getByPrivilege(String)")
    public void testGetByPrivilege() throws Exception {
        final List<PhrSecurityRule> rules = this.dao.getByPrivilege("View Treatment Summary");
        Assert.assertNotNull(rules);
        this.log.debug("Number of rules = " + rules.size());
        this.log.debug(rules);
    }
    
    @Override
    public Boolean useInMemoryDatabase() {
        return false;
    }
}
