package org.openmrs.module.medadherence;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.medadherence.api.MedicationAdherenceBarriersService;
import org.openmrs.notification.MessageException;
import org.openmrs.notification.MessageService;
import org.openmrs.scheduler.Task;
import org.openmrs.scheduler.TaskDefinition;
import org.openmrs.util.OpenmrsConstants;

/**
 * Send email reminder to patient to fill out the medication adherence form every three months.
 * The reminder will be sent on the due date, as well as one week before (about-to-due) and one week after the due date (past-due).
 *  
 * @author hxiao
 *
 */
public class PatientReminderTask implements Task {

    private static Log log = LogFactory.getLog(PatientReminderTask.class);
    
    // Using an int nesting counter for this is almost certainly overkill
    private int executeNesting = 0;
    
    /** The OpenMRS definition of this task */
    TaskDefinition config = null;
    
    /* Send alert every three months (90 days) */
    private final static int ALERT_DAYS = 90; 
    private final static int BEFORE_AFTER_DAYS = 7; 
	private final static String EMAIL_SENDER="noreply.regenstrief.org";
	private final static String EMAIL_SUBJECT="It is time to update your Medication Reconciliation Barriers form";
	private final static String EMAIL_TEMPLATE="PATIENT_NAME:\n\nYou have been enrolled in the Medication Adherence study. It's time for you to update your Medication Reconciliation Barriers form through the following link: \n\nOPENMRS_URL\n\nSincerely,\nMedication Adherence Study Team\n";

    public void initialize(TaskDefinition config) {

        log.debug("initialize: config=" + config);
        this.config = config;
    }

    public TaskDefinition getTaskDefinition() {
        return config;
    }

    public boolean isExecuting() {
        
        // Test if this task is currently executing
        synchronized (this) {
            return executeNesting != 0;
        }
    }

    public void execute() {
        synchronized (this) {
            executeNesting++;
        }
       
		long startTime = System.currentTimeMillis();
		
		sendEmailNotification();
        
		synchronized (this) {
            executeNesting--;
        }

    }

    public void shutdown() {
        
        // If the task is executing, stop it as soon as possible.
        log.debug("shutdown: enter");
        log.debug("shutdown: exit");
    }
  
    private void sendEmailNotification() {
    	//login as a temporary user
        boolean isTemporary = false;
		try {
	        if (!Context.isAuthenticated()) {
	            Context.authenticate("temporary", "Temporary8");
	            Context.addProxyPrivilege(OpenmrsConstants.PRIV_ADD_USERS);
	            Context.addProxyPrivilege(OpenmrsConstants.PRIV_EDIT_USERS);
	            Context.addProxyPrivilege(OpenmrsConstants.PRIV_EDIT_PERSONS);
	            Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_USERS);
	            Context.addProxyPrivilege(OpenmrsConstants.PRIV_EDIT_USER_PASSWORDS);
	            Context.addProxyPrivilege("PHR Restricted Patient Access");
	            isTemporary = true;
	            log.debug("Added proxy privileges!");
	        }    	
	        
	    	//find patients who meet the criteria of email notification
	    	List<Person> allPatients = getAllPhrPatients();
	    	
	    	MedicationAdherenceBarriersService mabService = Context.getService(org.openmrs.module.medadherence.api.MedicationAdherenceBarriersService.class);
	    	Integer emailAttrId = Context.getPersonService().getPersonAttributeTypeByName("Email").getId();
            final String deployUrl= Context.getRuntimeProperties().getProperty("deployment.url");//"https://65.111.248.164:8443/"; //"172.30.201.24";
            final String url = deployUrl + "/openmrs/phr/index.htm";            
	    	for(Person per : allPatients) {
	    		String patName = per.getPersonName().getFullName();
	    		Patient pat = getPatient(per);
	    		Date latestEntry = mabService.getLatestFormEntryDate(pat);
	    		if(shouldNotify(latestEntry)) {
			    	//send email to patients found above; crate a log entry for every email sent
		    	
					String sender=EMAIL_SENDER;
					String recipients=Context.getPersonService().getPersonAttribute(emailAttrId).getValue();
					String subject=EMAIL_SUBJECT;
					String content=EMAIL_TEMPLATE.replaceAll("OPENMRS_URL", url).replaceAll("PATIENT_NAME", patName);
		
					if (recipients != null && recipients.trim().length() > 0) {					
							// Use the OpenMRS message service to create and send the email
							MessageService messageService = Context.getMessageService();
							messageService.sendMessage(messageService.createMessage(recipients, sender, subject, content));
							
							//PersonalhrUtil.getService().logEvent("EMAIL_SENT", new Date(), Context.getAuthenticatedUser(), null, pat, recipients);
					}	    			
	    		}
	    	}
	    	
		}
		catch (MessageException me) {
            log.error("Error sending email alert: " + me.getMessage(), me);
		} finally{
            //remove temporary privileges
            if (isTemporary) {
                Context.removeProxyPrivilege(OpenmrsConstants.PRIV_ADD_USERS);
                Context.removeProxyPrivilege(OpenmrsConstants.PRIV_EDIT_USERS);
                Context.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_USERS);
                Context.removeProxyPrivilege("PHR Restricted Patient Access");
                Context.removeProxyPrivilege(OpenmrsConstants.PRIV_EDIT_PERSONS);
                Context.removeProxyPrivilege(OpenmrsConstants.PRIV_EDIT_USER_PASSWORDS);
                Context.logout();
                log.debug("Removed proxy privileges for self registration!");
            }			
		}
    }

	private boolean shouldNotify(Date latestEntryDate) {
		Calendar today = Calendar.getInstance();
		Calendar latestEntry = Calendar.getInstance();
		latestEntry.setTime(latestEntryDate);

		Calendar nextEntry = Calendar.getInstance();
		nextEntry.setTime(latestEntryDate);
		nextEntry.add(Calendar.DATE, ALERT_DAYS);

		Calendar beforeNextEntry = Calendar.getInstance();
		beforeNextEntry.setTime(latestEntryDate);
		beforeNextEntry.add(Calendar.DATE, ALERT_DAYS-BEFORE_AFTER_DAYS);

		Calendar afterNextEntry = Calendar.getInstance();
		afterNextEntry.setTime(latestEntryDate);
		afterNextEntry.add(Calendar.DATE, ALERT_DAYS+BEFORE_AFTER_DAYS);

        if(matchDate(today, nextEntry) || matchDate(today, beforeNextEntry) || matchDate(today, afterNextEntry) ) {
        	return true;
        }
		return false;
	}

	private boolean matchDate(Calendar today, Calendar nextEntry) {
		Calendar oneDayAfter = Calendar.getInstance();
		oneDayAfter.setTime(nextEntry.getTime());
		oneDayAfter.add(Calendar.DATE, 1);
		
		if(today.after(nextEntry) && today.before(oneDayAfter)) {
			return true;
		}
		return false;
	}
	
    /**
     * Get all PHR Patient Users
     * 
     * @return person objects of all PHR Patient Users
     */
    public List<Person> getAllPhrPatients() {
        final List<Person> persons = new ArrayList<Person>();
        final List<User> users = new ArrayList<User>();
        
        users.addAll(Context.getUserService().getUsersByRole(Context.getUserService().getRole("PHR Patient")));
        
        for (final User user : users) {
            if(user != null && user.getPerson()!=null) {
                persons.add(user.getPerson());
            }
        }
        return persons;
    }	
    
    /**
     * Get patient object of a given person
     * 
     * @param person given person object
     * @return patient object
     */
    public Patient getPatient(final Person person) {
        // TODO Auto-generated method stub
        if (person != null) {
            return Context.getPatientService().getPatient(person.getPersonId());
        } else {
            return null;
        }
    }    
}
