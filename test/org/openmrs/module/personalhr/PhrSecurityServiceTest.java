package org.openmrs.module.personalhr;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;
import org.openmrs.api.db.PatientDAO;
import org.openmrs.api.db.PersonDAO;
import org.openmrs.api.db.UserDAO;
import org.openmrs.module.personalhr.db.*;

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
	    
		if (service == null)
			// fetch the dao from PhrSeucrity service, rather than from the spring application context
			// this bean name matches the name in /metadata/spring/applicationContext-service.xml
			service = (PhrSecurityService) PersonalhrUtil.getService();
        if(patientDao == null) {
            patientDao = (PatientDAO) applicationContext.getBean("patientDAO");         
        }
        
        if(personDao == null) {
            personDao = (PersonDAO) applicationContext.getBean("personDAO");         
        }

        if(userDao == null) {
            userDao = (UserDAO) applicationContext.getBean("userDAO");         
        }
	}
	
	@Test
	@Verifies(value = "should escape sql wildcards in searchPhrase", method = "isUrlAllowed(String, Patient, Person, User)")
	public void testIsUrlAllowed() throws Exception {
	    //larmstrong:2-4-4, msmith:3-5-5, hxiao:4-7-7 (username: userId-patientId-personId)
	    boolean isAllowed = service.isUrlAllowed("/openmrs/module/htmlformentry/htmlFormEntry.form",patientDao.getPatient(4), personDao.getPerson(5), userDao.getUser(2));
	    Assert.assertTrue(isAllowed);
	    
        isAllowed = service.isUrlAllowed("/openmrs/module/htmlformentry/htmlFormEntry.form",patientDao.getPatient(4), personDao.getPerson(5), userDao.getUser(3));
        Assert.assertTrue(isAllowed);

        isAllowed = service.isUrlAllowed("/openmrs/module/htmlformentry/htmlFormEntry.form",patientDao.getPatient(4), personDao.getPerson(5), userDao.getUser(4));
        Assert.assertTrue(isAllowed);

	}
	
    @Test
    @Verifies(value = "should escape sql wildcards in searchPhrase", method = "isUrlAllowed(String, Patient, Person, User)")
    public void testHasPrivilege() throws Exception {
        //larmstrong:2-4-4, msmith:3-5-5, hxiao:4-7-7 (username: userId-patientId-personId)
        boolean isAllowed = service.hasPrivilege("View Treatment Summary",patientDao.getPatient(4), personDao.getPerson(5), userDao.getUser(2));
        Assert.assertTrue(isAllowed);
        
        isAllowed = service.hasPrivilege("View Treatment Summary",patientDao.getPatient(4), personDao.getPerson(5), userDao.getUser(3));
        Assert.assertTrue(isAllowed);
        
        isAllowed = service.hasPrivilege("View Treatment Summary",patientDao.getPatient(4), personDao.getPerson(5), userDao.getUser(4));
        Assert.assertTrue(isAllowed);
    }	
	
	@Override
    public Boolean useInMemoryDatabase() {
        return false;
    }	
}
