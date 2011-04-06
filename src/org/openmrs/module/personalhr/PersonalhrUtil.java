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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.util.OpenmrsConstants;

/**
 *
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
            OpenmrsConstants.PRIV_VIEW_PATIENT_COHORTS };
 
    private final static String temporaryPrivilegesMin[] = {
        OpenmrsConstants.PRIV_VIEW_CONCEPTS,
        OpenmrsConstants.PRIV_VIEW_GLOBAL_PROPERTIES,
        OpenmrsConstants.PRIV_VIEW_PATIENTS,
        OpenmrsConstants.PRIV_VIEW_PERSONS,
        OpenmrsConstants.PRIV_VIEW_ENCOUNTERS
   };   
    
    private static int privCount = 0;
    
    //Make the comparison case-insensitive.   
    static Pattern pattern = Pattern.compile(
        "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$", Pattern.CASE_INSENSITIVE);
    
    public static void addTemporaryPrivileges() {
        //Add temporary privilege
        for (final String priv : temporaryPrivileges) {
            //log.debug("Adding tempary privilege: " + priv);
            Context.addProxyPrivilege(priv);
            privCount++;
        }
        log.debug("addTemporayPrivileges called: privCount=" + privCount);
    }
    
    public static void removeTemporaryPrivileges() {
        //Remove temporary privilege
        for (final String priv : temporaryPrivileges) {
            //log.debug("Removing tempary privilege: " + priv);
            Context.removeProxyPrivilege(priv);
            privCount--;
        }
        log.debug("removeTemporayPrivileges called: privCount=" + privCount);
    }
    
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
     * Generate a random text token of a given length
     * 
     * @param n number of characters in this token
     * @return generated token
     */
    public static String getRandomToken() {
        final int n = 30;
        return getRandomToken(n);
    }
    
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
    
    public static PhrSecurityService getService() {
        return Context.getService(PhrSecurityService.class);
    }
    
    /**
     * Auto generated method comment
     * 
     * @param string
     * @param requestURI
     * @return
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
     * Auto generated method comment
     * 
     * @param relatedPersonEmail
     * @return
     */
    public static boolean isNullOrEmpty(final String value) {
        return ((value == null) || value.trim().isEmpty());
    }
    
    public static Date getExpireDate(final Date date) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, 14); //expire after 14 days
        
        return cal.getTime();
    }
    
    /**
     * Auto generated method comment
     * 
     * @param email
     * @return
     */
    public static boolean isValidEmail(final String email) {
        final Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    
    public static void main(final String[] args) {
        final String email = "hxiao@regenstrief.org";
        System.out.print(email + " is valid? " + isValidEmail("hxiao@regenstrief.org"));
    }
    
    /**
     * Auto generated method comment
     * 
     * @param attribute
     * @return
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
     * Auto generated method comment
     * 
     * @return
     */
    public static String getRandomIdentifer() {
        final int n = 8;
        return getRandomToken(n);
    }
}
