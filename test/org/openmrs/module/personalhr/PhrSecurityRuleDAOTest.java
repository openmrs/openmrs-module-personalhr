package org.openmrs.module.personalhr;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;
import org.openmrs.module.personalhr.db.*;

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
	    
		if (dao == null)
			// fetch the dao from PhrSeucrity service, rather than from the spring application context
			// this bean name matches the name in /metadata/spring/applicationContext-service.xml
			dao = (PhrSecurityRuleDAO) PersonalhrUtil.getService().getSecurityRuleDao();
	}
	
	@Test
	@Verifies(value = "should return security rules", method = "getByPrivilege(String)")
	public void testGetByPrivilege() throws Exception {
	    List<PhrSecurityRule> rules = dao.getByPrivilege("View Treatment Summary");
	    Assert.assertNotNull(rules);
	    log.debug("Number of rules = " + rules.size());	    
	    log.debug(rules);		
	}
	
	@Override
    public Boolean useInMemoryDatabase() {
        return false;
    }	
}
