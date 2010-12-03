package org.openmrs.module.personalhr;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;
import org.openmrs.module.personalhr.db.*;

public class PhrAllowedUrlDAOTest extends BaseModuleContextSensitiveTest {
	
	private PhrAllowedUrlDAO dao = null;
	
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
			dao = (PhrAllowedUrlDAO) PersonalhrUtil.getService().getAllowedUrlDao();
	}
	
	@Test
	@Verifies(value = "should escape sql wildcards in searchPhrase", method = "getUsers(String, List, Boolean)")
	public void testGetByUrl() throws Exception {
	    List<PhrAllowedUrl> urls = dao.getByUrl("module/htmlformentry/htmlFormEntry.form");
	    Assert.assertNotNull(urls);
	    Assert.assertEquals(1, urls.size());
	    Assert.assertEquals("module/htmlformentry/htmlFormEntry.form|View Treatment Summary",urls.get(0).getAllowedUrl()+"|"+urls.get(0).getPrivilege());		
	}
	
	@Override
    public Boolean useInMemoryDatabase() {
        return false;
    }	
}
