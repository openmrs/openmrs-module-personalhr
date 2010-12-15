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
import org.openmrs.api.db.PatientDAO;
import org.openmrs.api.db.PersonDAO;
import org.openmrs.api.db.UserDAO;
import org.openmrs.module.personalhr.db.*;

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
	    
		if (dao == null)
			// fetch the dao from PhrSeucrity service, rather than from the spring application context
			// this bean name matches the name in /metadata/spring/applicationContext-service.xml
			dao = (PhrSharingTokenDAO) PersonalhrUtil.getService().getSharingTokenDao();
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
	@Verifies(value = "should return sharing tokens", method = "getSharingToken(Integer, Integer, Integer")
	public void testGetSharingToken() throws Exception {
	    PhrSharingToken token = (PhrSharingToken) dao.getSharingToken(patientDao.getPatient(4), personDao.getPerson(5), userDao.getUser(3)); //larmstrong2-4, msmith3-5, hxiao4-7
	    Assert.assertNotNull(token);
	    log.debug("Sharing token: " + token.getSharingToken()+"|"+token.getPatient()+"|"+token.getShareType()+"|"+token.getRelatedPersonName());	    
	    log.debug(token);
	    
	    List<PhrSharingToken> tokens = dao.getSharingTokenByPerson(patientDao.getPatient(4)); //larmstrong2-4, msmith3-5, hxiao4-7
        Assert.assertNotNull(tokens);	   
        Assert.assertTrue(tokens.size()>0); 
	}
	
	@Override
    public Boolean useInMemoryDatabase() {
        return false;
    }	
}
