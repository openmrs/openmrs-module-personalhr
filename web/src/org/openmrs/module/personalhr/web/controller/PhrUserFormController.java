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
import org.openmrs.module.personalhr.PhrLogEvent;
import org.openmrs.module.personalhr.PhrService;
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
    public void initBinder(final WebDataBinder binder) {
        binder.registerCustomEditor(Role.class, new RoleEditor());
    }
    
    // the personId attribute is called person_id so that spring MVC doesn't try to bind it to the personId property of user
    @ModelAttribute("user")
    public User formBackingObject(final WebRequest request,
                                  @RequestParam(required = false, value = "sharingToken") final String sharingToken,
                                  @RequestParam(required = false, value = "person_id") final Integer personId) {
        log.debug("Entering PhrUserFormController:formBackingObject: sharingToken=" + sharingToken + ", personId="
                + personId);
        
        final String userId = request.getParameter("userId");
        User u = null;
        try {
            u = Context.getUserService().getUser(Integer.valueOf(userId));
        } catch (final Exception ex) {}
        if (u == null) {
            u = new User();
            log.debug("Entering PhrUserFormController:formBackingObject...new User" + u);
        }
        if (personId != null) {
            Person per = Context.getPersonService().getPerson(personId);
            int count = per.getAttributeMap().size();
            log.debug("attributeMap size = " + count);
            u.setPerson(per);
        } else if (u.getPerson() == null) {
            final Person p = new Person();
            p.addName(new PersonName());
            u.setPerson(p);
            log.debug("Entering PhrUserFormController:formBackingObject...setPerson" + p);
        } else {
            int count = u.getPerson().getAttributeMap().size();
            log.debug("attributeMap size = " + count);            
        }
        
        return u;
    }
    
    @ModelAttribute("allRoles")
    public List<Role> getRoles(final WebRequest request) {
        log.debug("Entering PhrUserFormController:getRoles...");
        List<Role> roles = Context.getUserService().getAllRoles();
        if (roles == null) {
            roles = new Vector<Role>();
        }
        
        for (final String s : OpenmrsConstants.AUTO_ROLES()) {
            final Role r = new Role(s);
            roles.remove(r);
        }
        return roles;
    }
    
    @RequestMapping(value = "/phr/user.form", method = RequestMethod.GET)
    public String showForm(@RequestParam(required = false, value = "userId") final Integer userId,
                           @RequestParam(required = false, value = "createNewPerson") final String createNewPerson,
                           @RequestParam(required = false, value = "sharingToken") final String sharingToken,
                           @ModelAttribute("user") final User user, final ModelMap model, final HttpSession session) {
        
        // the formBackingObject method above sets up user, depending on userId and personId parameters   
        log.debug("Entering PhrUserFormController:showForm: sharingToken=" + sharingToken + ", httpSession=" + session);
        final MessageSourceService mss = Context.getMessageSourceService();
        
        final PhrSharingToken token = sharingToken == null ? null : PersonalhrUtil.getService().getSharingTokenDao()
                .getSharingToken(sharingToken);
        if (!Context.isAuthenticated() && (token == null)) {
            //Not allowed to register without a sharing token
            session.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, mss.getMessage("personalhr.error.valid.invitation"));
            log.error("Failed to register without a valid sharing token");
            return "redirect:/phr/index.htm?noredirect=true";
        } else if (!Context.isAuthenticated() && (token.getRelatedPerson() != null)) {
            //Not allowed to register without a sharing token
            session.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, mss.getMessage("personalhr.error.valid.invitation"));
            log.error("Failed to register with a used sharing token");
            return "redirect:/phr/index.htm?noredirect=true";
        } else if (!Context.isAuthenticated() && ((user != null) && (user.getUserId() != null))) {
            //Not allowed to modify other user's information
            session.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, mss.getMessage("personalhr.error.other.user"));
            log.error("You're not allowed to view other user's information! user=" + user);
            return "redirect:/phr/index.htm?noredirect=true";
        } else if (Context.isAuthenticated()) {
            if (!Context.hasPrivilege(PhrService.PhrBasicPrivilege.PHR_ADMINISTRATOR_PRIV.getValue())) {
                //Already registered
                session.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, mss.getMessage("personalhr.error.already.registered"));
                log.error("You've already registered!");
                return "redirect:/phr/index.htm?noredirect=true";
            }
        }
        
        model.addAttribute("isNewUser", isNewUser(user));
        if (isNewUser(user) || Context.hasPrivilege(OpenmrsConstants.PRIV_EDIT_USER_PASSWORDS)) {
            model.addAttribute("modifyPasswords", true);
        }
        
        if (createNewPerson != null) {
            model.addAttribute("createNewPerson", createNewPerson);
        }
        
        if (!isNewUser(user)) {
            model.addAttribute("changePassword", new UserProperties(user.getUserProperties()).isSupposedToChangePassword());
        }
        
        if (sharingToken != null) {
            model.addAttribute("sharingToken", sharingToken);
        }
        
        // not using the default view name because I'm converting from an existing form
        return "module/personalhr/view/userForm";
    }
    
    /**
     * @should work for an example
     */
    @RequestMapping(value = "/phr/user.form", method = RequestMethod.POST)
    public String handleSubmission(final WebRequest request, final HttpSession httpSession, final ModelMap model,
                                   @RequestParam(required = false, value = "action") final String action,
                                   @RequestParam(required = false, value = "userFormPassword") String password,
                                   @RequestParam(required = false, value = "secretQuestion") final String secretQuestion,
                                   @RequestParam(required = false, value = "secretAnswer") final String secretAnswer,
                                   @RequestParam(required = false, value = "confirm") String confirm,
                                   @RequestParam(required = false, value = "forcePassword") final Boolean forcePassword,
                                   @RequestParam(required = false, value = "roleStrings") final String[] roles,
                                   @RequestParam(required = false, value = "createNewPerson") final String createNewPerson,
                                   @RequestParam(required = false, value = "sharingToken") String sharingToken,
                                   @ModelAttribute("user") final User user, final BindingResult errors) {
        
        if (sharingToken == null) {
            sharingToken = (String) model.get("sharingToken");
        }
        
        log.debug("Entering PhrUserFormController:handleSubmission..." + sharingToken);
        //add temporary privileges
        boolean isTemporary = false;
        boolean isAdministrator = false;
        if (!Context.isAuthenticated()) {
            Context.authenticate("temporary", "Temporary8");
            Context.addProxyPrivilege(OpenmrsConstants.PRIV_ADD_USERS);
            Context.addProxyPrivilege(OpenmrsConstants.PRIV_EDIT_USERS);
            Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_USERS);
            Context.addProxyPrivilege("PHR Restricted Patient Access");
            isTemporary = true;
            log.debug("Added proxy privileges!");
        } else {
          if(PhrService.PhrBasicRole.PHR_ADMINISTRATOR.getValue().equals(PersonalhrUtil.getService().getPhrRole(Context.getAuthenticatedUser()))) {
              isAdministrator = true; 
              Context.addProxyPrivilege(OpenmrsConstants.PRIV_ADD_USERS);
              Context.addProxyPrivilege(OpenmrsConstants.PRIV_EDIT_USERS);
              Context.addProxyPrivilege(OpenmrsConstants.PRIV_DELETE_USERS);
              Context.addProxyPrivilege(OpenmrsConstants.PRIV_PURGE_USERS);
          }
        }
        
        try{
            final UserService us = Context.getUserService();
            final MessageSourceService mss = Context.getMessageSourceService();
            
            if (mss.getMessage("User.assumeIdentity").equals(action)) {
                Context.becomeUser(user.getSystemId());
                httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "User.assumeIdentity.success");
                httpSession.setAttribute(WebConstants.OPENMRS_MSG_ARGS, user.getPersonName());
                return "redirect:/phr/index.htm";
                
            } else if (mss.getMessage("User.delete").equals(action)) {
                try {
                    Context.getUserService().purgeUser(user);
                    httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "User.delete.success");
                    return "redirect:/phr/user.list";
                } catch (final Exception ex) {
                    httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "User.delete.failure");
                    log.error("Failed to delete user", ex);
                    return "redirect:/phr/user.form?userId=" + request.getParameter("userId");
                }
                
            } else if (mss.getMessage("User.retire").equals(action)) {
                final String retireReason = request.getParameter("retireReason");
                if (!(StringUtils.hasText(retireReason))) {
                    errors.rejectValue("retireReason", "User.disableReason.empty");
                    return showForm(user.getUserId(), createNewPerson, sharingToken, user, model, httpSession);
                } else {
                    us.retireUser(user, retireReason);
                    httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "User.retiredMessage");
                }
                
            } else if (mss.getMessage("User.unRetire").equals(action)) {
                us.unretireUser(user);
                httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "User.unRetiredMessage");
            } else {
                // check if username is already in the database
                if (us.hasDuplicateUsername(user)) {
                    errors.rejectValue("username", "error.username.taken");
                }
                
                // check if password and password confirm are identical
                if ((password == null) || password.equals("XXXXXXXXXXXXXXX")) {
                    password = "";
                }
                if ((confirm == null) || confirm.equals("XXXXXXXXXXXXXXX")) {
                    confirm = "";
                }
                
                if (!password.equals(confirm)) {
                    errors.reject("error.password.match");
                }
                
                if ((password.length() == 0) && isNewUser(user)) {
                    errors.reject("error.password.weak");
                }
                
                //check password strength
                if (password.length() > 0) {
                    try {
                        OpenmrsUtil.validatePassword(user.getUsername(), password, user.getSystemId());
                    } catch (final PasswordException e) {
                        errors.reject(e.getMessage());
                    }
                }
                
                final Set<Role> newRoles = new HashSet<Role>();
                if (roles != null) {
                    for (final String r : roles) {
                        // Make sure that if we already have a detached instance of this role in the
                        // user's roles, that we don't fetch a second copy of that same role from
                        // the database, or else hibernate will throw a NonUniqueObjectException.
                        Role role = null;
                        if (user.getRoles() != null) {
                            for (final Role test : user.getRoles()) {
                                if (test.getRole().equals(r)) {
                                    role = test;
                                }
                            }
                        }
                        if (role == null) {
                            role = us.getRole(r);
                            user.addRole(role);
                        }
                        newRoles.add(role);
                    }
                } else {
                    final Role role = us.getRole("PHR Restricted User");
                    newRoles.add(role);
                    user.addRole(role);
                    log.debug("Added PHR Restricted User role only: " + role);
                }
                
                if (user.getRoles() == null) {
                    newRoles.clear();
                } else {
                    user.getRoles().retainAll(newRoles);
                }
                
                final String[] keys = request.getParameterValues("property");
                final String[] values = request.getParameterValues("value");
                
                if ((keys != null) && (values != null)) {
                    for (int x = 0; x < keys.length; x++) {
                        final String key = keys[x];
                        final String val = values[x];
                        user.setUserProperty(key, val);
                    }
                }
                
                new UserProperties(user.getUserProperties()).setSupposedToChangePassword(forcePassword);
                
                final UserValidator uv = new UserValidator();
                uv.validate(user, errors);
                
                if (errors.hasErrors()) {
                    log.debug("errors validating user: " + errors.getErrorCount() + errors.toString());
                    return showForm(user.getUserId(), createNewPerson, sharingToken, user, model, httpSession);
                }
                
                if (isNewUser(user) && !isAdministrator) {
                    log.debug("Saving new user " + user.getUsername() + ", sharingToken=" + sharingToken);
                    final PhrSharingToken token = PersonalhrUtil.getService().getSharingTokenDao().getSharingToken(sharingToken);
                    
                    //check token existence and name matching
                    if (token == null || token.getExpireDate().before(new Date())) {
                        httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR,
                            "Failed to register without a valid sharing token");
                        log.error("Failed to register without a valid sharing token");
                        PersonalhrUtil.getService().logEvent(PhrLogEvent.USER_SIGN_UP, new Date(), null, 
                            httpSession.getId(), null, 
                            "error=Failed to register without a valid sharing token; user_name=" + user.getName());
    
                        if (isTemporary) {
                            Context.removeProxyPrivilege(OpenmrsConstants.PRIV_ADD_USERS);
                            Context.removeProxyPrivilege(OpenmrsConstants.PRIV_EDIT_USERS);
                            Context.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_USERS);
                            Context.removeProxyPrivilege("PHR Restricted Patient Access");
                            Context.logout();
                            log.debug("Removed proxy privileges!");
                        }
                        return "redirect:/phr/index.htm?noredirect=true";
                    } else if ((token != null) && (token.getRelatedPerson() != null)) {
                        httpSession
                                .setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "Failed to register with a used sharing token");
                        log.error("Failed to register with a used sharing token");
                        PersonalhrUtil.getService().logEvent(PhrLogEvent.USER_SIGN_UP, new Date(), null, 
                            httpSession.getId(), null, 
                            "error=Failed to register with a used sharing token; user_name=" + user.getName() + "; sharingToken="+token);
                        if (isTemporary) {
                            Context.removeProxyPrivilege(OpenmrsConstants.PRIV_ADD_USERS);
                            Context.removeProxyPrivilege(OpenmrsConstants.PRIV_EDIT_USERS);
                            Context.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_USERS);
                            Context.removeProxyPrivilege("PHR Restricted Patient Access");
                            Context.logout();
                            log.debug("Removed proxy privileges!");
                        }
                        
                        return "redirect:/phr/index.htm?noredirect=true";
                    } else if (token.getRelatedPersonName().toLowerCase().contains(user.getFamilyName().toLowerCase())
                            && token.getRelatedPersonName().toLowerCase().contains(user.getGivenName().toLowerCase())) {
                        //create a new user by self registration
                        us.saveUser(user, password);
                        
                        token.setRelatedPerson(user.getPerson());
                        token.setChangedBy(user);
                        final Date date = new Date();
                        token.setDateChanged(date);
                        token.setActivateDate(date);
                        PersonalhrUtil.getService().getSharingTokenDao().savePhrSharingToken(token);
                        httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "personalhr.user.signed.up");
                        log.debug("New self-registered user created: " + user.getUsername());
                        PersonalhrUtil.getService().logEvent(PhrLogEvent.USER_SIGN_UP, new Date(), user, 
                            httpSession.getId(), null, 
                            "info=New self-registered user created; user_name=" + user.getName() + "; sharingToken="+token);
                    } else {
                        httpSession.setAttribute(
                            WebConstants.OPENMRS_MSG_ATTR,
                            "Failed to create new user due to name mismatch: " + user.getFamilyName() + ", "
                                    + user.getGivenName());
                        log.debug("Failed to create new user due to name mismatch: " + token.getRelatedPersonName() + " vs "
                                + user.getFamilyName() + ", " + user.getGivenName());
                        PersonalhrUtil.getService().logEvent(PhrLogEvent.USER_SIGN_UP, new Date(), null, 
                            httpSession.getId(), null, 
                            "info=Failed to create new user due to name mismatch; user_name=" + user.getName() + "; sharingToken="+token);
                    }
                } else if (isNewUser(user) && isAdministrator) {
                    //create a new user by PHR Administrator
                    us.saveUser(user, password);                
                } else {
                    //modify an exiting user
                    us.saveUser(user, null);
                    
                    if (!password.equals("") && Context.hasPrivilege(OpenmrsConstants.PRIV_EDIT_USER_PASSWORDS)) {
                        if (log.isDebugEnabled()) {
                            log.debug("calling changePassword for user " + user + " by user " + Context.getAuthenticatedUser());
                        }
                        us.changePassword(user, password);
                    }
                    log.debug("Existing user " + user.getUsername() + " changed by user "
                            + Context.getAuthenticatedUser().getUsername());
                    PersonalhrUtil.getService().logEvent(PhrLogEvent.USER_UPDATE, new Date(), Context.getAuthenticatedUser(), 
                        httpSession.getId(), null, 
                        "info=Existing user updated; user_name=" + user.getName());
                    httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "User.saved");
                }
                
                if (StringUtils.hasLength(secretQuestion) && StringUtils.hasLength(secretAnswer)) {
                    us.changeQuestionAnswer(user, secretQuestion, secretAnswer);
                    httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "User.saved");
                }                
            }
        } finally {
            //remove temporary privileges
            if (isTemporary) {
                Context.removeProxyPrivilege(OpenmrsConstants.PRIV_ADD_USERS);
                Context.removeProxyPrivilege(OpenmrsConstants.PRIV_EDIT_USERS);
                Context.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_USERS);
                Context.removeProxyPrivilege("PHR Restricted Patient Access");
                Context.logout();
                log.debug("Removed proxy privileges for self registration!");
            } else if (isAdministrator) {
                Context.removeProxyPrivilege(OpenmrsConstants.PRIV_ADD_USERS);
                Context.removeProxyPrivilege(OpenmrsConstants.PRIV_EDIT_USERS);
                Context.removeProxyPrivilege(OpenmrsConstants.PRIV_DELETE_USERS);
                Context.removeProxyPrivilege(OpenmrsConstants.PRIV_PURGE_USERS);
                log.debug("Removed proxy privileges for PHR Administrator!");
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
    private Boolean isNewUser(final User user) {
        return user == null ? true : user.getUserId() == null;
    }
    
}
