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
package org.openmrs.module.personalhr.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;

/**
 * This filter checks if an authenticated user has been flagged by the admin to change his password
 * on first/subsequent login. It will intercept any requests made to a *.html or a *.form to force
 * the user to change his password.
 */
public class PhrSecurityFilterOld implements Filter {
    
    protected final Log log = LogFactory.getLog(getClass());
    
    private String excludeURL;
    
    private String allowedURL;
    
    private String changePasswordForm;
    
    private FilterConfig config;
    
    private String[] excludedURLs;
    
    private String[] allowedURLs;
    
    /**
     * @see javax.servlet.Filter#destroy()
     */
    @Override
    public void destroy() {
    }
    
    /**
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
     *      javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
                                                                                                               throws IOException,
                                                                                                               ServletException {
        final String requestURI = ((HttpServletRequest) request).getRequestURI();
        this.log.debug("Entering PhrSecurityFilter.doFilter: " + requestURI);
        
        if (Context.isAuthenticated() && shouldNotAllowAccessToUrl(requestURI) && isPhrUserForNotAllowedUrl(requestURI)) {
            this.config.getServletContext().getRequestDispatcher(this.changePasswordForm).forward(request, response);
        } else {
            chain.doFilter(request, response);
        }
    }
    
    /**
     * Auto generated method comment
     * 
     * @return
     */
    private boolean isPhrUserForNotAllowedUrl(final String requestURI) {
        boolean notAllowed = false;
        this.log.debug("Entering PhrSecurityFilter.isPhrUserForNotAllowedUrl: " + requestURI);
        
        //is a PHR User or not?
        if (Context.getAuthenticatedUser().hasRole("PHR Administrator")
                || Context.getAuthenticatedUser().hasRole("PHR Patient")
                || Context.getAuthenticatedUser().hasRole("PHR Restricted User")) {
            notAllowed = true;
            for (final String url : this.allowedURLs) {
                if (requestURI.contains(url)) {
                    notAllowed = false;
                    break;
                }
            }
        }
        
        this.log.debug("Exiting PhrSecurityFilter.isPhrUserForNotAllowedUrl: " + notAllowed);
        
        return notAllowed;
    }
    
    /**
     * Method to check if the request url is an excluded url.
     * 
     * @param requestURI
     * @param excludeURL
     * @return
     */
    private boolean shouldNotAllowAccessToUrl(final String requestURI) {
        
        for (final String url : this.excludedURLs) {
            if (requestURI.contains(url)) {
                this.log.debug("shouldNotAllowAccessToUrl: " + false + " due to " + url);
                return false;
            }
        }
        
        this.log.debug("shouldNotAllowAccessToUrl: " + true);
        return true;
    }
    
    /**
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    @Override
    public void init(final FilterConfig config) throws ServletException {
        this.config = config;
        this.excludeURL = config.getInitParameter("excludeURL");
        this.excludedURLs = this.excludeURL.split(",");
        this.allowedURL = config.getInitParameter("allowedURL");
        this.allowedURLs = this.allowedURL.split(",");
        this.changePasswordForm = config.getInitParameter("changePasswordForm");
    }
    
}
