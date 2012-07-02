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

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.MessagingModuleActivator;
import org.openmrs.module.messaging.MessagingService;
import org.openmrs.module.messaging.PersonAttributeService;
import org.openmrs.util.OpenmrsConstants;


/**
 * Contains utility methods used internally or by other modules for convenience
 * 
 * @author hxiao
 */
public class PersonalhrUtil {
    
    /** Logger for this class and subclasses */
    private final static Log log = LogFactory.getLog(PersonalhrUtil.class);
    
    private final static String temporaryPrivileges[] = { OpenmrsConstants.PRIV_ADD_PATIENTS,
            OpenmrsConstants.PRIV_VIEW_CONCEPTS, OpenmrsConstants.PRIV_VIEW_FORMS,
            OpenmrsConstants.PRIV_VIEW_GLOBAL_PROPERTIES, OpenmrsConstants.PRIV_VIEW_ENCOUNTERS,
            OpenmrsConstants.PRIV_VIEW_LOCATIONS, OpenmrsConstants.PRIV_VIEW_IDENTIFIER_TYPES,
            OpenmrsConstants.PRIV_VIEW_OBS, OpenmrsConstants.PRIV_VIEW_ORDERS, OpenmrsConstants.PRIV_VIEW_PATIENTS,
            OpenmrsConstants.PRIV_VIEW_USERS, OpenmrsConstants.PRIV_ADD_ENCOUNTERS, OpenmrsConstants.PRIV_ADD_OBS,
            OpenmrsConstants.PRIV_EDIT_ENCOUNTERS, OpenmrsConstants.PRIV_EDIT_OBS, OpenmrsConstants.PRIV_DELETE_ENCOUNTERS,
            OpenmrsConstants.PRIV_DELETE_OBS, OpenmrsConstants.PRIV_VIEW_PERSONS, OpenmrsConstants.PRIV_ADD_PERSONS,
            OpenmrsConstants.PRIV_EDIT_PERSONS, OpenmrsConstants.PRIV_ADD_PATIENTS, OpenmrsConstants.PRIV_EDIT_PATIENTS,
            OpenmrsConstants.PRIV_VIEW_PATIENT_COHORTS,
			OpenmrsConstants.PRIV_VIEW_PROGRAMS, OpenmrsConstants.PRIV_VIEW_PATIENT_PROGRAMS, 
			OpenmrsConstants.PRIV_VIEW_ALLERGIES, OpenmrsConstants.PRIV_VIEW_PROBLEMS, OpenmrsConstants.PRIV_VIEW_RELATIONSHIPS,
			OpenmrsConstants.PRIV_ADD_PATIENT_PROGRAMS, OpenmrsConstants.PRIV_ADD_ALLERGIES,
			OpenmrsConstants.PRIV_EDIT_ALLERGIES, OpenmrsConstants.PRIV_DELETE_ALLERGIES,
			OpenmrsConstants.PRIV_ADD_PROBLEMS, OpenmrsConstants.PRIV_EDIT_PROBLEMS, OpenmrsConstants.PRIV_DELETE_PROBLEMS,
			OpenmrsConstants.PRIV_EDIT_ORDERS, OpenmrsConstants.PRIV_DELETE_ORDERS, "Manage Orders", "View Providers", "View Visits",
			OpenmrsConstants.PRIV_DELETE_RELATIONSHIPS, OpenmrsConstants.PRIV_ADD_RELATIONSHIPS, OpenmrsConstants.PRIV_EDIT_RELATIONSHIPS,
			"Manage Encounter Roles", "View Encounter Roles"
			};
 
    private final static String temporaryPrivilegesMin[] = {
        OpenmrsConstants.PRIV_VIEW_CONCEPTS,
        OpenmrsConstants.PRIV_VIEW_GLOBAL_PROPERTIES,
        OpenmrsConstants.PRIV_VIEW_PATIENTS,
        OpenmrsConstants.PRIV_VIEW_PERSONS,
        OpenmrsConstants.PRIV_VIEW_ENCOUNTERS,
		OpenmrsConstants.PRIV_VIEW_PROGRAMS,
		OpenmrsConstants.PRIV_VIEW_PATIENT_PROGRAMS,
		OpenmrsConstants.PRIV_VIEW_ALLERGIES,
		OpenmrsConstants.PRIV_VIEW_PROBLEMS,
		
   };   
    
    private static int privCount = 0;
    
    //Make the comparison case-insensitive.   
    static Pattern pattern = Pattern.compile(
        "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$", Pattern.CASE_INSENSITIVE);
    
    /**
     * Add temporary privileges needed for PHR users to access OpenMRS core 
     * 
     */
    public static void addTemporaryPrivileges() {
        //Add temporary privilege
        for (final String priv : temporaryPrivileges) {
            //log.debug("Adding tempary privilege: " + priv);
            Context.addProxyPrivilege(priv);
            privCount++;
        }
        log.debug("addTemporayPrivileges called: privCount=" + privCount);
    }
    
    /**
     * Remove temporary privileges
     * 
     */
    public static void removeTemporaryPrivileges() {
        //Remove temporary privilege
        for (final String priv : temporaryPrivileges) {
            //log.debug("Removing tempary privilege: " + priv);
            Context.removeProxyPrivilege(priv);
            privCount--;
        }
        log.debug("removeTemporayPrivileges called: privCount=" + privCount);
    }
    
    /**
     * Add minimum temporary privileges needed for PHR users to access OpenMRS core 
     * 
     */
    public static void addMinimumTemporaryPrivileges() {
        //Add temporary privilege
        log.debug("Adding minimum tempary privilege... ");
        for(String priv : temporaryPrivilegesMin) {
            //log.debug("Adding tempary privilege: " + priv);
            Context.addProxyPrivilege(priv);
            privCount++;
        }
        log.debug("addMinimumTemporayPrivileges called: privCount=" + privCount);
    }

    /**
     * Remove minimum temporary privileges
     * 
     */
    public static void removeMinimumTemporaryPrivileges() {
        //Remove temporary privilege
        for(String priv : temporaryPrivilegesMin) {
            //log.debug("Removing tempary privilege: " + priv);
            Context.removeProxyPrivilege(priv);
            privCount--;
        }
        log.debug("removeMinimumTemporayPrivileges called: privCount=" + privCount);
    }       
    
    /**
     * Generate a random text token of a predefined length
     * 
     * @return a random token string
     */
    public static String getRandomToken() {
        final int n = 30;
        return getRandomToken(n);
    }
    
    /**
     * Generate a random text token of a given length
     * 
     * @param n length of token
     * @return a random token string
     */
    public static String getRandomToken(final int n) {
        if (n <= 0) {
            return null;
        }
        
        final char[] token = new char[n];
        int c = 'A';
        int r1 = 0;
        for (int i = 0; i < n; i++) {
            r1 = (int) (Math.random() * 3);
            switch (r1) {
                case 0:
                    c = '0' + (int) (Math.random() * 10);
                    break;
                case 1:
                    c = 'a' + (int) (Math.random() * 26);
                    break;
                case 2:
                    c = 'A' + (int) (Math.random() * 26);
                    break;
            }
            token[i] = (char) c;
        }
        return new String(token);
    }
    
    /**
     * Convenient method to get PhrService instance
     * 
     * @return PhrService instance
     */
    public static PhrService getService() {
        return Context.getService(PhrService.class);
    }
    
    /**
     * Get a parameter contained in a url string as an Integer value
     * @param paramName parameter name
     * @param urlString URL string containing this parameter
     * 
     * @return an Integer value of the parameter
     */
    public static Integer getParamAsInteger(final String paramName, final String urlString) {
        // TODO Auto-generated method stub
        final int jj = urlString.indexOf(paramName + "=");
        
        String paramValue = null;
        if (jj >= 0) {
            int ii = 0;
            for (ii = jj + paramName.length() + 1; ii < urlString.length(); ii++) {
                if (!Character.isDigit(urlString.charAt(ii))) {
                    break;
                }
            }
            
            paramValue = urlString.substring(0, ii);
        }
        
        Integer retValue = null;
        
        try {
            retValue = Integer.valueOf(paramValue);
        } catch (final NumberFormatException e) {
            retValue = null;
        }
        
        return retValue;
    }
    
    /**
     * Convert a string value parameter to Integer value
     * 
     * @param paramValue given parameter as a string
     * @return a Integer value
     */
    public static Integer getParamAsInteger(final String paramValue) {
        Integer retValue = null;
        
        try {
            retValue = Integer.valueOf(paramValue);
        } catch (final NumberFormatException e) {
            retValue = null;
        }
        
        return retValue;
    }
    
    /**
     * Check if a string is null or empty
     * @param value a given string
     * 
     * @return true if the given string is null or empty
     */
    public static boolean isNullOrEmpty(final String value) {
        return ((value == null) || value.trim().isEmpty());
    }
    
    /**
     * Return the expire date of a generated token (default 14 days)
     * 
     * @param date date when the token is generated
     * @return expiration date
     */
    public static Date getExpireDate(final Date date) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, 14); //expire after 14 days
        
        return cal.getTime();
    }
    
    /**
     * Validate the format of a user entered email
     * 
     * @param email email entered by the user
     * @return true if the email is in good format
     */
    public static boolean isValidEmail(final String email) {
        final Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    
    /**
     * Test email validation function
     * 
     * @param args arguments passed in to the main method
     */
    public static void main(final String[] args) {
        final String email = "hxiao@regenstrief.org";
        System.out.print(email + " is valid? " + isValidEmail("hxiao@regenstrief.org"));
    }
    
    /**
     * Convert to a parameter value of any type to Integer
     * 
     * @param value any type of Object value
     * @return an Integer value
     */
    public static Integer getInteger(final Object value) {
        // TODO Auto-generated method stub
        if (value == null) {
            return null;
        } else if (value instanceof String) {
            return getParamAsInteger((String) value);
        } else if (value instanceof Integer) {
            return (Integer) value;
        } else {
            log.error("getInteger called for non integer value: " + value);
            return null;
        }
    }
    
    /**
     * Generate a random identifier for a person
     * 
     * @return an person identifier of default length (8)
     */
    public static String getRandomIdentifer() {
        final int n = 8;
        return getRandomToken(n);
    }
    
    
    /**
     * Set messaging alert options for a given user
     * 
     * @param per a given person
     * @param shouldAlert true if an alert will be sent when a message comes into OMail box
     * @param messagingAddressId ID of messaging address for the alert to be sent to
     */
    public static void setMessagingAlertSettings(Person per, Boolean shouldAlert, Integer messagingAddressId){
        log.info("Setting Omail Alert settings.");

        //we should return if they are trying to alert a null address
        if(shouldAlert && (messagingAddressId == null || messagingAddressId <= 0)) return;
        //void the old attributes
        PersonAttributeService personAttrService = Context.getService(PersonAttributeService.class);
        PersonAttributeType shouldAlertType = Context.getPersonService().getPersonAttributeTypeByName(MessagingModuleActivator.SEND_OMAIL_ALERTS_ATTR_NAME);
        PersonAttributeType alertAddressType = Context.getPersonService().getPersonAttributeTypeByName(MessagingModuleActivator.ALERT_ADDRESS_ATTR_NAME);
        List<PersonAttribute> attributes = personAttrService.getPersonAttributes(per, shouldAlertType, false);
        for(PersonAttribute attr: attributes){
            attr.voidAttribute("New data provided");
            personAttrService.savePersonAttribute(attr);
        }
        attributes = personAttrService.getPersonAttributes(per, alertAddressType, false);
        for(PersonAttribute attr: attributes){
            attr.voidAttribute("New data provided");
            personAttrService.savePersonAttribute(attr);
        }
        
        //create the new ones
        PersonAttribute shouldAlertAttr = new PersonAttribute(shouldAlertType,shouldAlert.toString());
        Set<PersonAttribute> attrSet = per.getAttributes();
        if(attrSet == null) {
            attrSet = new TreeSet<PersonAttribute>();
            per.setAttributes(attrSet);
        }
        attrSet.add(shouldAlertAttr);
        if(messagingAddressId != null && messagingAddressId != 0 && shouldAlert){
            PersonAttribute alertAddressAttr = new PersonAttribute(alertAddressType,messagingAddressId.toString());
            attrSet.add(alertAddressAttr);
        }
        //per.setAttributes(attrSet);
        
        //save the attributes 
        try{
        	Context.getPersonService().savePerson(per);
        } catch(Exception e) {
        	log.error("Failed to set Messaging Alert Settings for this person: " + per, e);
        }
    }    
    
    /**
     * Auto generated method comment
     * 
     * @param emailAddress
     * @param email
     */
    public static void sendEmail(final String emailAddress, final String email) {
        // TODO Auto-generated method stub
        try {
            Context.getService(MessagingService.class).sendMessage(email, emailAddress,
                org.openmrs.module.messaging.email.EmailProtocol.class);
        } catch (final Exception e) {
            log.debug("Unable to send message to " + emailAddress, e);
        } catch (final NoClassDefFoundError e) {
            log.debug("Messaging module is not found, unable to send message to " + emailAddress, e);           
        }
    }
    
}
