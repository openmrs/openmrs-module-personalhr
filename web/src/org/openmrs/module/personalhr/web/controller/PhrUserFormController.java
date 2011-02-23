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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Person;
import org.openmrs.PersonName;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.PasswordException;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.personalhr.PersonalhrUtil;
import org.openmrs.module.personalhr.PhrSecurityService;
import org.openmrs.module.personalhr.PhrSharingToken;
import org.openmrs.propertyeditor.RoleEditor;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.util.OpenmrsUtil;
import org.openmrs.validator.UserValidator;
import org.openmrs.web.WebConstants;
import org.openmrs.web.user.UserProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;


/**
 * Used for creating/editing User
 */
@Controller
public class PhrUserFormController {
	
	protected static final Log log = LogFactory.getLog(PhrUserFormController.class);
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Role.class, new RoleEditor());
	}

	// the personId attribute is called person_id so that spring MVC doesn't try to bind it to the personId property of user
	@ModelAttribute("user")
	public User formBackingObject(WebRequest request,
	                              @RequestParam(required=false, value="sharingToken") String sharingToken,	                              
	                              @RequestParam(required=false, value="person_id") Integer personId) {
	    log.debug("Entering PhrUserFormController:formBackingObject: sharingToken=" + sharingToken + ", personId=" + personId);
	    	    	    
		String userId = request.getParameter("userId");
		User u = null;
		try {
			u = Context.getUserService().getUser(Integer.valueOf(userId));
		} catch (Exception ex) { }
		if (u == null) {
			u = new User();
	        log.debug("Entering PhrUserFormController:formBackingObject...new User" + u);			
		}
		if (personId != null) {
			u.setPerson(Context.getPersonService().getPerson(personId));
		} else if (u.getPerson() == null) {
			Person p = new Person();
			p.addName(new PersonName());
			u.setPerson(p);
	        log.debug("Entering PhrUserFormController:formBackingObject...setPerson" + p);			
		}
				
		return u;
	}
	
	@ModelAttribute("allRoles")
	public List<Role> getRoles(WebRequest request) {
        log.debug("Entering PhrUserFormController:getRoles...");
		List<Role> roles = Context.getUserService().getAllRoles();
		if (roles == null)
			roles = new Vector<Role>();
		
		for (String s : OpenmrsConstants.AUTO_ROLES()) {
			Role r = new Role(s);
			roles.remove(r);
		}
		return roles;
	}

	@RequestMapping(value="/phr/user.form", method=RequestMethod.GET)
	public String showForm(@RequestParam(required=false, value="userId") Integer userId,
	                       @RequestParam(required=false, value="createNewPerson") String createNewPerson,
                           @RequestParam(required=false, value="sharingToken") String sharingToken,
	                       @ModelAttribute("user") User user,
	                       ModelMap model,
	                       HttpSession session) {

		// the formBackingObject method above sets up user, depending on userId and personId parameters   
        log.debug("Entering PhrUserFormController:showForm: sharingToken=" + sharingToken + ", httpSession=" + session);
        MessageSourceService mss = Context.getMessageSourceService();
        
        PhrSharingToken token = sharingToken==null? null : PersonalhrUtil.getService().getSharingTokenDao().getSharingToken(sharingToken);
        if(!Context.isAuthenticated() && token==null){
            //Not allowed to register without a sharing token
            session.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, mss.getMessage("personalhr.error.valid.invitation"));
            log.error("Failed to register without a valid sharing token");
            return "redirect:/phr/index.htm?noredirect=true";            
        } else if(!Context.isAuthenticated() && token.getRelatedPerson()!=null){
            //Not allowed to register without a sharing token
            session.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, mss.getMessage("personalhr.error.valid.invitation"));
            log.error("Failed to register with a used sharing token");
            return "redirect:/phr/index.htm?noredirect=true";            
        } else if(!Context.isAuthenticated() && (user!=null && user.getUserId() != null)) {
            //Not allowed to modify other user's information
            session.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, mss.getMessage("personalhr.error.other.user"));
            log.error("You're not allowed to view other user's information! user=" + user);
            return "redirect:/phr/index.htm?noredirect=true";                        
        } else if(Context.isAuthenticated()) {
            if(!Context.hasPrivilege(PhrSecurityService.PhrBasicPrivilege.PHR_ADMINISTRATOR_PRIV.getValue())) {
                //Already registered
                session.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, mss.getMessage("personalhr.error.already.registered"));
                log.error("You've already registered!");
                return "redirect:/phr/index.htm?noredirect=true";  
            } 
        }
		
		model.addAttribute("isNewUser", isNewUser(user));
		if (isNewUser(user) || Context.hasPrivilege(OpenmrsConstants.PRIV_EDIT_USER_PASSWORDS))
			model.addAttribute("modifyPasswords", true);
		
		if (createNewPerson != null)
			model.addAttribute("createNewPerson", createNewPerson);
		
		if(!isNewUser(user))
			model.addAttribute("changePassword",new UserProperties(user.getUserProperties()).isSupposedToChangePassword());
		
        if (sharingToken != null)
            model.addAttribute("sharingToken", sharingToken);
        
		// not using the default view name because I'm converting from an existing form
		return "module/personalhr/view/userForm";
	}
	
	/**
	 * @should work for an example
	 */
	@RequestMapping(value="/phr/user.form", method=RequestMethod.POST)
	public String handleSubmission(WebRequest request,
	                               HttpSession httpSession,
	                               ModelMap model,
	                               @RequestParam(required=false, value="action") String action,
	                               @RequestParam(required=false, value="userFormPassword") String password,
	                               @RequestParam(required=false, value="secretQuestion") String secretQuestion,
	                               @RequestParam(required=false, value="secretAnswer") String secretAnswer,
	                               @RequestParam(required=false, value="confirm") String confirm,
	                               @RequestParam(required=false, value="forcePassword") Boolean forcePassword,
	                               @RequestParam(required=false, value="roleStrings") String[] roles,
                                   @RequestParam(required=false, value="createNewPerson") String createNewPerson,
                                   @RequestParam(required=false, value="sharingToken") String sharingToken,
	                               @ModelAttribute("user") User user, BindingResult errors) {
	    
	    if(sharingToken==null) {
	        sharingToken = (String) model.get("sharingToken");
	    }
		
        log.debug("Entering PhrUserFormController:handleSubmission..." + sharingToken);
        //add temporary privileges
        boolean isTemporary = false;
        if(!Context.isAuthenticated()) {                
            Context.authenticate("temporary", "Temporary8");
            Context.addProxyPrivilege(OpenmrsConstants.PRIV_ADD_USERS);
            Context.addProxyPrivilege(OpenmrsConstants.PRIV_EDIT_USERS);
            Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_USERS);
            Context.addProxyPrivilege("PHR Restricted Patient Access");
            isTemporary = true;
            log.debug("Added proxy privileges!");
        }

        UserService us = Context.getUserService();
		MessageSourceService mss = Context.getMessageSourceService();
		
		if (mss.getMessage("User.assumeIdentity").equals(action)) {
			Context.becomeUser(user.getSystemId());
			httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "User.assumeIdentity.success");
			httpSession.setAttribute(WebConstants.OPENMRS_MSG_ARGS, user.getPersonName());
			return "redirect:/phr/index.htm";
				
		} else if (mss.getMessage("User.delete").equals(action)) {
			try {
				Context.getUserService().purgeUser(user);
				httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "User.delete.success");
				return "redirect:/admin/users/user.list";			
			} catch (Exception ex) {
				httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "User.delete.failure");
				log.error("Failed to delete user", ex);
				return "redirect:/phr/user.form?userId="+request.getParameter("userId");
			}
			
		
		} else if (mss.getMessage("User.retire").equals(action)) {
			String retireReason = request.getParameter("retireReason");
			if (!(StringUtils.hasText(retireReason))) {
				errors.rejectValue("retireReason", "User.disableReason.empty");
				return showForm(user.getUserId(), createNewPerson, sharingToken, user, model, httpSession);
			} else {
				us.retireUser(user, retireReason);
				httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "User.retiredMessage");
			}
			
		} else if(mss.getMessage("User.unRetire").equals(action)) {
			us.unretireUser(user);
			httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "User.unRetiredMessage");
		}else {	
			// check if username is already in the database
			if (us.hasDuplicateUsername(user))
				errors.rejectValue("username", "error.username.taken");
			
			// check if password and password confirm are identical
			if (password == null || password.equals("XXXXXXXXXXXXXXX"))
				password = "";
			if (confirm == null || confirm.equals("XXXXXXXXXXXXXXX"))
				confirm = "";
			
			if (!password.equals(confirm))
				errors.reject("error.password.match");
			
			if (password.length() == 0 && isNewUser(user))
				errors.reject("error.password.weak");
			
			//check password strength
			if (password.length() > 0) {
				try {
					OpenmrsUtil.validatePassword(user.getUsername(), password, user.getSystemId());
				}
				catch (PasswordException e) {
					errors.reject(e.getMessage());
				}
			}
			
			Set<Role> newRoles = new HashSet<Role>();
			if (roles != null) {
				for (String r : roles) {
					// Make sure that if we already have a detached instance of this role in the
					// user's roles, that we don't fetch a second copy of that same role from
					// the database, or else hibernate will throw a NonUniqueObjectException.
					Role role = null;
					if (user.getRoles() != null)
						for (Role test : user.getRoles())
							if (test.getRole().equals(r))
								role = test;
					if (role == null) {
						role = us.getRole(r);
						user.addRole(role);
					}
					newRoles.add(role);
				}
			} else {
                Role role = us.getRole("PHR Restricted User");
                newRoles.add(role);			    
                user.addRole(role);
                log.debug("Added PHR Restricted User role only: " + role);
			}
			
			if (user.getRoles() == null)
				newRoles.clear();
			else
				user.getRoles().retainAll(newRoles);
			

			String[] keys = request.getParameterValues("property");
			String[] values = request.getParameterValues("value");
			
			if (keys != null && values != null) {
				for (int x = 0; x < keys.length; x++) {
					String key = keys[x];
					String val = values[x];
					user.setUserProperty(key, val);
				}
			}
							
			new UserProperties(user.getUserProperties()).setSupposedToChangePassword(forcePassword);
			
			UserValidator uv = new UserValidator();
			uv.validate(user, errors);
			
			if (errors.hasErrors()) {
                log.debug("errors validating user: " + errors.getErrorCount() + errors.toString());
				return showForm(user.getUserId(), createNewPerson, sharingToken, user, model, httpSession);
			}
			
			if (isNewUser(user)){			    
			    log.debug("Saving new user " + user.getUsername() + ", sharingToken=" + sharingToken);
			    PhrSharingToken token = PersonalhrUtil.getService().getSharingTokenDao().getSharingToken(sharingToken);
			    
			    //check name matching
			    if(token == null) {
                   httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "Failed to register without a valid sharing token");
                   log.error("Failed to register without a valid sharing token");
                   if(isTemporary) {
                       Context.removeProxyPrivilege(OpenmrsConstants.PRIV_ADD_USERS);
                       Context.removeProxyPrivilege(OpenmrsConstants.PRIV_EDIT_USERS); 
                       Context.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_USERS);
                       Context.removeProxyPrivilege("PHR Restricted Patient Access");
                       Context.logout();
                       log.debug("Removed proxy privileges!");
                  }            
                   return "redirect:/phr/index.htm?noredirect=true";                                                  
                } else  if(token!=null && token.getRelatedPerson() != null) {			        
			        httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "Failed to register with a used sharing token");
		            log.error("Failed to register with a used sharing token");
		            if(isTemporary) {
		                Context.removeProxyPrivilege(OpenmrsConstants.PRIV_ADD_USERS);
		                Context.removeProxyPrivilege(OpenmrsConstants.PRIV_EDIT_USERS); 
		                Context.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_USERS);
		                Context.removeProxyPrivilege("PHR Restricted Patient Access");
		                Context.logout();
		                log.debug("Removed proxy privileges!");
		            }            

		            return "redirect:/phr/index.htm?noredirect=true";            			        
			    } else if(token.getRelatedPersonName().toLowerCase().contains(user.getFamilyName().toLowerCase()) && token.getRelatedPersonName().toLowerCase().contains(user.getGivenName().toLowerCase())) {			        
			        log.debug("PHR Restricted Patient Access=" + Context.getAuthenticatedUser().hasPrivilege("PHR Restricted Patient Access"));;
			        us.saveUser(user, password);
			        
                    token.setRelatedPerson(user.getPerson());
                    token.setChangedBy(user);
                    Date date = new Date();
                    token.setDateChanged(date);
                    token.setActivateDate(date);                    
                    PersonalhrUtil.getService().getSharingTokenDao().savePhrSharingToken(token);
                    httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "User.saved");                    
                    log.debug("New self-registered user created: " + user.getUsername());
			    } else {			        
		            httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Failed to create new user due to name mismatch: " + user.getFamilyName() + ", " + user.getGivenName());
                    log.debug("Failed to create new user due to name mismatch: " + token.getRelatedPersonName() + " vs " + user.getFamilyName() + ", " + user.getGivenName());			        
			    }
	         } else {
				us.saveUser(user, null);
                
				if (!password.equals("") && Context.hasPrivilege(OpenmrsConstants.PRIV_EDIT_USER_PASSWORDS)) {
					if (log.isDebugEnabled())
						log.debug("calling changePassword for user " + user + " by user " + Context.getAuthenticatedUser());
					us.changePassword(user, password);
				}
                log.debug("Existing user " + user.getUsername() + " changed by user " + Context.getAuthenticatedUser().getUsername());
                httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "User.saved");
			}
            
            if (StringUtils.hasLength(secretQuestion) && StringUtils.hasLength(secretAnswer)) {
            	us.changeQuestionAnswer(user, secretQuestion, secretAnswer);
                httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "User.saved");
            }
            
            //remove temporary privileges
            if(isTemporary) {
                Context.removeProxyPrivilege(OpenmrsConstants.PRIV_ADD_USERS);
                Context.removeProxyPrivilege(OpenmrsConstants.PRIV_EDIT_USERS); 
                Context.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_USERS);
                Context.removeProxyPrivilege("PHR Restricted Patient Access");
                Context.logout();
                log.debug("Removed proxy privileges!");
           }            
		}
		return "redirect:/phr/index.htm?noredirect=true";
	}
	/**
	 * Superficially determines if this form is being filled out for a new user (basically just
	 * looks for a primary key (user_id)
	 * 
	 * @param user
	 * @return true/false if this user is new
	 */
	private Boolean isNewUser(User user) {
		return user == null ? true : user.getUserId() == null;
	}
	
}
