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
	
	private final static String temporaryPrivileges[] = {
		OpenmrsConstants.PRIV_ADD_PATIENTS,
	    OpenmrsConstants.PRIV_VIEW_CONCEPTS,
	    OpenmrsConstants.PRIV_VIEW_FORMS,
	    OpenmrsConstants.PRIV_VIEW_GLOBAL_PROPERTIES,
	    OpenmrsConstants.PRIV_VIEW_ENCOUNTERS,
	    OpenmrsConstants.PRIV_VIEW_LOCATIONS,
	    OpenmrsConstants.PRIV_VIEW_IDENTIFIER_TYPES,
	    OpenmrsConstants.PRIV_VIEW_OBS,
	    OpenmrsConstants.PRIV_VIEW_ORDERS,
	    OpenmrsConstants.PRIV_VIEW_PATIENTS,
	    OpenmrsConstants.PRIV_VIEW_USERS,
	    OpenmrsConstants.PRIV_ADD_ENCOUNTERS,
	    OpenmrsConstants.PRIV_EDIT_ENCOUNTERS
	};
	
	public static void addTemporayPrivileges() {
		//Add temporary privilege
		for(String priv : temporaryPrivileges) {
			//log.debug("Adding tempary privilege: " + priv);
			Context.addProxyPrivilege(priv);
		}
	}

	public static void removeTemporayPrivileges() {
		//Remove temporary privilege
		for(String priv : temporaryPrivileges) {
			//log.debug("Removing tempary privilege: " + priv);
			//Context.removeProxyPrivilege(priv);
		}
	}	
	
	/**
	 * Generate a random text token of a given length
	 * 
	 * @param n number of characters in this token
	 * @return generated token
	 */
	public static String getRandomToken(int n) {
	      if(n<=0) {
	          return null;
	      }
	      
	      char[] token = new char[n];
	      int c  = 'A';
	      int  r1 = 0;
	      for (int i=0; i < n; i++)
	      {
	        r1 = (int)(Math.random() * 3);
	        switch(r1) {
	          case 0: c = '0' +  (int)(Math.random() * 10); break;
	          case 1: c = 'a' +  (int)(Math.random() * 26); break;
	          case 2: c = 'A' +  (int)(Math.random() * 26); break;
	        }
	        token[i] = (char)c;
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
    public static Integer getParamAsInteger(String paramName, String urlString) {
        // TODO Auto-generated method stub
        int jj = urlString.indexOf(paramName + "=");
        
        String paramValue = null;
        if(jj>=0) {
            int ii = 0;
            for(ii = jj+paramName.length()+1; ii<urlString.length(); ii++) {
               if(!Character.isDigit(urlString.charAt(ii))) {
                   break;
               }
            }
            
            paramValue = urlString.substring(0, ii);
        }
        
        Integer retValue = null;
        
        try {
            retValue = Integer.valueOf(paramValue);
        } catch (NumberFormatException e) {
            retValue = null;
        }
        
        return retValue;
    }
    
    public static Integer getParamAsInteger(String paramValue) {
        Integer retValue = null;
        
        try {
            retValue = Integer.valueOf(paramValue);
        } catch (NumberFormatException e) {
            retValue = null;
        }
        
        return retValue;
    }    
	
}
