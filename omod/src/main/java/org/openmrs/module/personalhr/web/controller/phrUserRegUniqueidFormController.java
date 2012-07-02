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
package org.openmrs.module.personalhr.web.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.util.PrivilegeConstants;
import org.openmrs.web.WebConstants;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;


public class phrUserRegUniqueidFormController extends SimpleFormController {
	
	/** Logger for this class and subclasses */
	protected static final Log log = LogFactory.getLog(phrUserRegUniqueidFormController.class);
	
	
	protected Object formBackingObject(HttpServletRequest request) throws ServletException {
		
		return "";
	}
	
	/**
	 * The mapping from user's IP address to the number of attempts at logging in from that IP
	 */
	private Map<String, Integer> loginAttemptsByIP = new HashMap<String, Integer>();
	
	/**
	 * The mapping from user's IP address to the time that they were locked out
	 */
	private Map<String, Date> lockoutDateByIP = new HashMap<String, Date>();
	
	
	protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object obj,
	        BindException errors) throws Exception {
		
		HttpSession httpSession = request.getSession();
		
		String uniqueid = request.getParameter("uid");
		//Start
		String captcha = request.getParameter("captcha");
		System.out.println(captcha);
		//end
		
		String ipAddress = request.getLocalAddr();
		Integer forgotPasswordAttempts = loginAttemptsByIP.get(ipAddress);
		if (forgotPasswordAttempts == null)
			forgotPasswordAttempts = 1;
		
		boolean lockedOut = false;
		
		if (forgotPasswordAttempts > 5) {
			lockedOut = true;
			
			Date lockedOutTime = lockoutDateByIP.get(ipAddress);
			if (lockedOutTime != null && System.currentTimeMillis() - lockedOutTime.getTime() > 300000) {
				lockedOut = false;
				forgotPasswordAttempts = 0;
				lockoutDateByIP.put(ipAddress, null);
			} else {
				lockoutDateByIP.put(ipAddress, new Date());
			}
			
		}
		
		if (lockedOut) {
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "auth.forgotPassword.tooManyAttempts");
		} else {
		
			forgotPasswordAttempts++;
			
			String secretAnswer = request.getParameter("secretAnswer");
			if (secretAnswer == null) {
				
				User user = null;
				
				try {
					Context.addProxyPrivilege(PrivilegeConstants.VIEW_USERS);
					
					if (uniqueid != null && uniqueid.length() > 0 && Integer.parseInt(uniqueid) > 0 && captcha.equals(ImageMap.image[0]))
						user = Context.getUserService().getUser(Integer.parseInt(uniqueid));
				}
				finally {
					Context.removeProxyPrivilege(PrivilegeConstants.VIEW_USERS);
				}
				
				if (user == null || user.getSecretQuestion() == null || user.getSecretQuestion().equals("")) {
					httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "auth.question.empty");
				} else {
					httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "auth.question.fill");
					request.setAttribute("secretQuestion", user.getSecretQuestion());
					
					forgotPasswordAttempts = 0;
				}
				
			} else if (secretAnswer != null) {
				
				User user = null;
				
				try {
					Context.addProxyPrivilege(PrivilegeConstants.VIEW_USERS);
					user = Context.getUserService().getUser(Integer.parseInt(uniqueid));
				}
				finally {
					Context.removeProxyPrivilege(PrivilegeConstants.VIEW_USERS);
				}
				
				if (user == null || user.getSecretQuestion() == null || user.getSecretQuestion().equals("")) {
					httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "auth.question.empty");
				} else if (user.getSecretQuestion() != null && Context.getUserService().isSecretAnswer(user, secretAnswer)) {
					
					String randomPassword = "";
					for (int i = 0; i < 8; i++) {
						randomPassword += String.valueOf((Math.random() * (127 - 48) + 48));
					}
					
					try {
						Context.addProxyPrivilege(PrivilegeConstants.EDIT_USER_PASSWORDS);
						Context.getUserService().changePassword(user, randomPassword);
					}
					finally {
						Context.removeProxyPrivilege(PrivilegeConstants.EDIT_USER_PASSWORDS);
					}
					String username = user.getUsername();
					httpSession.setAttribute("resetPassword", randomPassword);
					httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Fill in the boxes for your login details.");
					Context.authenticate(username, randomPassword);
					httpSession.setAttribute("loginAttempts", 0);
					return new ModelAndView(new RedirectView(request.getContextPath() + "/phr/phrOptions.form#Change Login Info"));
				} else {
					httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "Invalid unique-id and secret answer combination. Please try again.");
					httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "auth.question.fill");
					request.setAttribute("secretQuestion", user.getSecretQuestion());
				}
			}
		}
		
		loginAttemptsByIP.put(ipAddress, forgotPasswordAttempts);
		request.setAttribute("uid", uniqueid);
		return showForm(request, response, errors);
	}
	
}
