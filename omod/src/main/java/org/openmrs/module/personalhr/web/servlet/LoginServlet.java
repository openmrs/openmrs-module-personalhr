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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ContextAuthenticationException;
import org.openmrs.module.personalhr.PersonalhrUtil;
import org.openmrs.module.personalhr.model.PhrLogEvent;
import org.openmrs.module.personalhr.service.PhrService.PhrBasicRole;
import org.openmrs.module.personalhr.service.PhrSharingTokenService;
import org.openmrs.util.LocaleUtility;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.web.OpenmrsCookieLocaleResolver;
import org.openmrs.web.WebConstants;
import org.openmrs.web.user.UserProperties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.openmrs.web.WebConstants.GP_ALLOWED_LOGIN_ATTEMPTS_PER_IP;

/**
 * This servlet accepts the username and password from the login form and authenticates the user to
 * OpenMRS
 * 
 * @see org.openmrs.api.context.Context#authenticate(String, String)
 */
public class LoginServlet extends HttpServlet {
    
    public static final long serialVersionUID = 134231247523L;
    
    protected static final Log log = LogFactory.getLog(LoginServlet.class);
    
    /**
     * The mapping from user's IP address to the number of attempts at logging in from that IP
     */
    private final Map<String, Integer> loginAttemptsByIP = new HashMap<String, Integer>();
    
    /**
     * The mapping from user's IP address to the time that they were locked out
     */
    private final Map<String, Date> lockoutDateByIP = new HashMap<String, Date>();
    
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
                
                final String username = request.getParameter("uname");
                final String password = request.getParameter("pw");
                final String sharingToken = request.getParameter("sharingToken");
                
                // only try to authenticate if they actually typed in a username
                if ((username == null) || (username.length() == 0)) {
                    throw new ContextAuthenticationException("Unable to authenticate with an empty username");
                }
                
                Context.authenticate(username, password);
                
                if (Context.isAuthenticated()) {
                    httpSession.setAttribute("loginAttempts", 0);
                    final User user = Context.getAuthenticatedUser();
                    
                    log.debug("Logged in: username=" + username + ", sharingToken=" + sharingToken);
                    //update sharing token table
                    if (!PersonalhrUtil.isNullOrEmpty(sharingToken)) {
                        Context.getService(PhrSharingTokenService.class)
                                .updateSharingToken(user, user.getPerson(), sharingToken);
                    }
                    
                    String localeString = null;
                    Locale locale = null;
                    // load the user's default locale if possible
                    if (user.getUserProperties() != null) {
                         if (user.getUserProperties().containsKey(OpenmrsConstants.USER_PROPERTY_DEFAULT_LOCALE)) {
                           localeString = user.getUserProperty(OpenmrsConstants.USER_PROPERTY_DEFAULT_LOCALE);
                        }
                    }
                    
                    if(localeString == null) {
                        localeString = "en_US";
                    }
                    if (localeString.length() == 5) {
                        //user's locale is language_COUNTRY (i.e. en_US)
                        final String lang = localeString.substring(0, 2);
                        final String country = localeString.substring(3, 5);
                        locale = new Locale(lang, country);
                    } else {
                        // user's locale is only the language (language plus greater than 2 char country code
                        locale = new Locale(localeString);
                    }
                    
                    final OpenmrsCookieLocaleResolver oclr = new OpenmrsCookieLocaleResolver();
                    oclr.setLocale(request, response, locale);
                    if (new UserProperties(user.getUserProperties()).isSupposedToChangePassword()) {
                        httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "User.password.change");
                        redirect = request.getContextPath() + "/changePassword.form";
                    }
                    
                    // In case the user has no preferences, make sure that the context has some locale set
                    if (Context.getLocale() == null) {
                        Context.setLocale(LocaleUtility.getDefaultLocale());
                    }
                    
                    if (log.isDebugEnabled()) {
                        log.debug("Redirecting after login to: " + redirect);
                        log.debug("Locale address: " + request.getLocalAddr());
                    }
                    
                    if("/openmrs".equals(redirect)) {
                        final String phrRole = PersonalhrUtil.getService().getPhrRole(user);
                        final Integer personId = user.getPerson().getPersonId(); //same as patient id
                        if (PhrBasicRole.PHR_PATIENT.getValue().equals(phrRole)) {
                            if (personId != null) {
                                redirect = request.getContextPath() + "/phr/patientDashboard.form?patientId=" + personId;
                                //PersonalhrUtil.addTemporayPrivileges();
                            } else {
                                log.error("Error: PHR Patient's person id is null!");
                            }
                        } else if (PhrBasicRole.PHR_RESTRICTED_USER.getValue().equals(phrRole)) {
                            if (personId != null) {
                                redirect = request.getContextPath() + "/phr/restrictedUserDashboard.form?personId=" + personId;
                                //PersonalhrUtil.addTemporayPrivileges();
                            } else {
                                log.error("Error: PHR Restricted user's person id is null!");
                            }
                        } else if (PhrBasicRole.PHR_ADMINISTRATOR.getValue().equals(phrRole)) {
                            redirect = request.getContextPath() + "/phr/findPatient.htm";
                            //PersonalhrUtil.addTemporayPrivileges();
                        }
                    }
                    log.debug("PHR LoginServlet redirect to " + redirect);
                    PersonalhrUtil.getService().logEvent(PhrLogEvent.USER_LOGIN, new Date(), user, 
                            httpSession.getId(), null, 
                            "redirect="+redirect+"; client_ip=" + request.getLocalAddr());
                    response.sendRedirect(redirect);
                    
                    httpSession.setAttribute(WebConstants.OPENMRS_CLIENT_IP_HTTPSESSION_ATTR, request.getLocalAddr());
                    httpSession.removeAttribute(WebConstants.OPENMRS_LOGIN_REDIRECT_HTTPSESSION_ATTR);
                    
                    // unset login attempts by this user because they were
                    // able to successfully log in
                    
                    this.loginAttemptsByIP.remove(ipAddress);
                    
                    return;
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
        response.sendRedirect(request.getContextPath() + "/phr/login.htm");
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
