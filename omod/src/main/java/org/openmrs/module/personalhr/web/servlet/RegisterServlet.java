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
package org.openmrs.module.personalhr.web.servlet;

import static org.openmrs.web.WebConstants.GP_ALLOWED_LOGIN_ATTEMPTS_PER_IP;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ContextAuthenticationException;
import org.openmrs.module.personalhr.PersonalhrUtil;
import org.openmrs.module.personalhr.PhrLogEvent;
import org.openmrs.module.personalhr.PhrService.PhrBasicRole;
import org.openmrs.util.LocaleUtility;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.web.OpenmrsCookieLocaleResolver;
import org.openmrs.web.WebConstants;
import org.openmrs.web.user.UserProperties;

/**
 * This servlet accepts the username and password from the login form and authenticates the user to
 * OpenMRS
 * 
 * @see org.openmrs.api.context.Context#authenticate(String, String)
 */
public class RegisterServlet extends HttpServlet {
    
    public static final long serialVersionUID = 134231247523L;
    
    protected static final Log log = LogFactory.getLog(RegisterServlet.class);
    
    /**
     * The mapping from user's IP address to the number of attempts at logging in from that IP
     */
    private final Map<String, Integer> loginAttemptsByIP = new HashMap<String, Integer>();
    
    /**
     * The mapping from user's IP address to the time that they were locked out
     */
    private final Map<String, Date> lockoutDateByIP = new HashMap<String, Date>();
    
    public static final String DEAULT_MRN_FOR_ALL = "TEMPID_WILL_BE_REPLACED";
    
    /**
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException,
                                                                                               IOException {
        final HttpSession httpSession = request.getSession();
        
        final String ipAddress = request.getRemoteAddr();
        Integer loginAttempts = this.loginAttemptsByIP.get(ipAddress);
        if (loginAttempts == null) {
            loginAttempts = 1;
        }
        
        loginAttempts++;
        
        boolean lockedOut = false;
        // look up the allowed # of attempts per IP
        Integer allowedLockoutAttempts = 100;
        
        final String allowedLockoutAttemptsGP = Context.getAdministrationService().getGlobalProperty(
            GP_ALLOWED_LOGIN_ATTEMPTS_PER_IP, "100");
        try {
            allowedLockoutAttempts = Integer.valueOf(allowedLockoutAttemptsGP.trim());
        } catch (final NumberFormatException nfe) {
            log.error("Unable to format '" + allowedLockoutAttemptsGP + "' from global property "
                    + GP_ALLOWED_LOGIN_ATTEMPTS_PER_IP + " as an integer");
        }
        
        // allowing for configurable login attempts here in case network setups are such that all users have the same IP address.
        if ((allowedLockoutAttempts > 0) && (loginAttempts > allowedLockoutAttempts)) {
            lockedOut = true;
            
            final Date lockedOutTime = this.lockoutDateByIP.get(ipAddress);
            if ((lockedOutTime != null) && (new Date().getTime() - lockedOutTime.getTime() > 300000)) {
                lockedOut = false;
                loginAttempts = 0;
                this.lockoutDateByIP.put(ipAddress, null);
            } else {
                // they haven't been locked out before, or they're trying again
                // within the time limit.  Set the locked-out date to right now
                this.lockoutDateByIP.put(ipAddress, new Date());
            }
            
        }
        
        // get the place to redirect to either now, or after they eventually
        // authenticate correctly
        String redirect = determineRedirect(request);
        
        if (lockedOut) {
            httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "auth.login.tooManyAttempts");
        } else {
            try {
                
                String username = request.getParameter("uname");
                final String password = request.getParameter("pw");
                final String sharingToken = request.getParameter("sharingToken");
                
                // only try to authenticate if they actually typed in a username                
                String instituion = getRegistrationInstitution(password);
                
                if (instituion != null) {
                    if ((username == null) || (username.length() == 0)) {
                    	username = DEAULT_MRN_FOR_ALL; //do not need to provide MRN when doing self registration
                    }
                	
                    httpSession.setAttribute("USER_REGISTRATION_MRN", username);
                    httpSession.setAttribute("USER_REGISTRATION_INSTITUTION", instituion);
                    httpSession.setAttribute("loginAttempts", 0);
                    httpSession.setAttribute("loginAttempts", 0);
                    final User user = Context.getAuthenticatedUser();
                    
                    log.debug("Logged in: username=" + username + ", instituion=" + instituion);
                    //update sharing token table
                    if (!PersonalhrUtil.isNullOrEmpty(sharingToken)) {
                        PersonalhrUtil.getService().getSharingTokenDao()
                                .updateSharingToken(user, user.getPerson(), sharingToken);
                    }
                                       
                    redirect = request.getContextPath() + "/phr/user.form";
                    
                    PersonalhrUtil.getService().logEvent(PhrLogEvent.USER_REGISTER, new Date(), null, 
                            httpSession.getId(), null, 
                            "mrn=" + username + "; password=" + password + "; client_ip=" + request.getLocalAddr());
                    
                    httpSession.setAttribute(WebConstants.OPENMRS_CLIENT_IP_HTTPSESSION_ATTR, request.getLocalAddr());
                    httpSession.removeAttribute(WebConstants.OPENMRS_LOGIN_REDIRECT_HTTPSESSION_ATTR);

                    response.sendRedirect(redirect);
                    
                    // unset login attempts by this user because they were
                    // able to successfully log in
                    
                    this.loginAttemptsByIP.remove(ipAddress);
                    
                    return;
                } else {
                    httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "auth.password.invalid");
                    PersonalhrUtil.getService().logEvent(PhrLogEvent.USER_REGISTER_FAILED, new Date(), null, 
                            httpSession.getId(), null, 
                            "mrn=" + username + "; password=" + password + "; client_ip=" + request.getLocalAddr());
                	
                }
            } catch (final ContextAuthenticationException e) {
                // set the error message for the user telling them
                // to try again
                httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "auth.password.invalid");
            }
            
        }
        
        // send the user back the login page because they either
        // had a bad password or are locked out
        this.loginAttemptsByIP.put(ipAddress, loginAttempts);
        httpSession.setAttribute(WebConstants.OPENMRS_LOGIN_REDIRECT_HTTPSESSION_ATTR, redirect);
        response.sendRedirect(request.getContextPath() + "/phr/register.htm");
    }
    
    /**
     * 
     * @param password: password that also indicate the associated institution id
     * @return true if the user is allowed for self registration
     */
    private String getRegistrationInstitution(String password) {
        String registrationPassword = Context.getAdministrationService().getGlobalProperty("personalhr.registration.password");
        String registrationInstitution = Context.getAdministrationService().getGlobalProperty("personalhr.registration.institution");
        if(registrationPassword==null || registrationInstitution==null) {
        	return null;
        }
        
        String[] passwords = registrationPassword.split(",");
        String[] institutions = registrationInstitution.split(",");
        
        if(passwords.length > institutions.length) {
        	return null;
        }
        
        for(int ii=0; ii<passwords.length; ii++) {
        	if(passwords[ii].trim().equals(password)) {
        		return institutions[ii].trim();
        	}
        }
		return null;
	}

	/**
     * Convenience method for pulling the correct page to redirect to out of the request
     * 
     * @param request the current request
     * @return the page to redirect to as determined by parameters in the request
     */
    private String determineRedirect(final HttpServletRequest request) {
        // first option for redirecting is the "redirect" parameter (set on login.jsp from the session attr)
        String redirect = request.getParameter("redirect");
        
        // second option for redirecting is the referrer parameter set at login.jsp
        if ((redirect == null) || redirect.equals("")) {
            redirect = request.getParameter("refererURL");
            if (redirect != null) {
                // checking for a redirect like /openmrs/openmrs (for some reason)
                final int index = redirect.indexOf(request.getContextPath(), 2);
                if (index != -1) {
                    redirect = redirect.substring(index);
                }
            }
        }
        
        // third option for redirecting is the main page of the webapp
        if ((redirect == null) || redirect.equals("")) {
            redirect = request.getContextPath();
        }

        // don't redirect back to the login page on success. (I assume the login page is {something}login.{something}
        else if (redirect.contains("login.")) {
            log.debug("Redirect contains 'login.', redirecting to main page");
            redirect = request.getContextPath();
        }

        // don't redirect to pages outside of openmrs
        else if (!redirect.startsWith(request.getContextPath())) {
            log.debug("redirect is outside of openmrs, redirecting to main page");
            redirect = request.getContextPath();
        }

        // don't redirect back to the initialsetup page
        else if (redirect.endsWith(WebConstants.SETUP_PAGE_URL)) {
            log.debug("redirect is back to the setup page because this is their first ever login");
            redirect = request.getContextPath();
        }

        else if (redirect.contains("/options.form") || redirect.contains("/changePassword.form")
                || redirect.contains("/forgotPassword.form")) {
            log.debug("The user was on a page for setting/changing passwords. Send them to the homepage to reduce confusion");
            redirect = request.getContextPath();
        }
        
        log.debug("Going to use redirect: '" + redirect + "'");
        
        return redirect;
    }
}
