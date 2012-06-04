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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Attributable;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.APIException;
import org.openmrs.api.DuplicateIdentifierException;
import org.openmrs.api.IdentifierNotUniqueException;
import org.openmrs.api.InsufficientIdentifiersException;
import org.openmrs.api.InvalidCheckDigitException;
import org.openmrs.api.InvalidIdentifierFormatException;
import org.openmrs.api.PasswordException;
import org.openmrs.api.PatientIdentifierException;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.UserService;
import org.openmrs.api.PersonService.ATTR_VIEW_TYPE;
import org.openmrs.api.context.Context;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.messaging.MessagingAddressService;
import org.openmrs.module.messaging.domain.MessagingAddress;
import org.openmrs.module.personalhr.PersonalhrUtil;
import org.openmrs.module.personalhr.PhrLogEvent;
import org.openmrs.module.personalhr.PhrService;
import org.openmrs.module.personalhr.PhrSharingToken;
import org.openmrs.propertyeditor.RoleEditor;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.util.OpenmrsUtil;
import org.openmrs.util.OpenmrsConstants.PERSON_TYPE;
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
    private static final String NOTIFICATION_TEMPLATE = "Dear OPENMRS_PHR_RELATED_PERSON,\n\nThank you for registering with the Personal Cancer Toolkit. You are now able to access this toolkit through the following link:\n\nOPENMRS_URL\n\nYour user details are as follows: \n\nUsername: OPENMRS_USERNAME\n\nPassword: OPENMRS_PASSWORD\n\nIf you have any questions or require further clarification, please contact the site administrator here:\n\ncancertoolkit-l@listserv.regenstrief.org\n\nThank You!\nSincerely,\nThe Personal Cancer Toolkit Development Team";
    
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
            //session.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, mss.getMessage("personalhr.error.valid.invitation"));
            //log.error("Failed to register without a valid sharing token");
            //return "redirect:/phr/index.htm?noredirect=true";

        	String mrn = (String) session.getAttribute("USER_REGISTRATION_MRN");
            String insitution = (String) session.getAttribute("USER_REGISTRATION_INSTITUTION");
            if(insitution == null || mrn == null) {
	        	//Ask for a study-assigned patient id and password to continue
	            return "redirect:/phr/register.htm";
            } 
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
                                   @ModelAttribute("user") final User user, final BindingResult errors) throws Exception {
        
        if (sharingToken == null) {
            sharingToken = (String) model.get("sharingToken");
        }
        
        String emailEntered = request.getParameter("Email");
    	String mrn = (String) httpSession.getAttribute("USER_REGISTRATION_MRN");
        String institution = (String) httpSession.getAttribute("USER_REGISTRATION_INSTITUTION");
        
        log.debug("Entering PhrUserFormController:handleSubmission..." + sharingToken);
        //add temporary privileges
        boolean isTemporary = false;
        boolean isAdministrator = false;
        if (!Context.isAuthenticated()) {
            Context.authenticate("temporary", "Temporary8");
            Context.addProxyPrivilege(OpenmrsConstants.PRIV_ADD_USERS);
            Context.addProxyPrivilege(OpenmrsConstants.PRIV_EDIT_USERS);
            Context.addProxyPrivilege(OpenmrsConstants.PRIV_EDIT_PERSONS);
            Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_USERS);
            Context.addProxyPrivilege(OpenmrsConstants.PRIV_EDIT_USER_PASSWORDS);
            Context.addProxyPrivilege("PHR Restricted Patient Access");
            Context.addProxyPrivilege("PHR Single Patient Access");
            Context.addProxyPrivilege(OpenmrsConstants.PRIV_ADD_PATIENTS);
            Context.addProxyPrivilege(OpenmrsConstants.PRIV_EDIT_PATIENTS);
            Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PATIENTS);
            isTemporary = true;
            log.debug("Added proxy privileges!");
        } else {
          if(PhrService.PhrBasicRole.PHR_ADMINISTRATOR.getValue().equals(PersonalhrUtil.getService().getPhrRole(Context.getAuthenticatedUser()))) {
              isAdministrator = true; 
              Context.addProxyPrivilege(OpenmrsConstants.PRIV_ADD_USERS);
              Context.addProxyPrivilege(OpenmrsConstants.PRIV_EDIT_USERS);
              Context.addProxyPrivilege(OpenmrsConstants.PRIV_DELETE_USERS);
              Context.addProxyPrivilege(OpenmrsConstants.PRIV_PURGE_USERS);
              Context.addProxyPrivilege(OpenmrsConstants.PRIV_EDIT_PERSONS);
              Context.addProxyPrivilege(OpenmrsConstants.PRIV_EDIT_USER_PASSWORDS);
          }
        }
        
        try{
            final UserService us = Context.getUserService();
            final MessageSourceService mss = Context.getMessageSourceService();
            
            if (mss.getMessage("User.assumeIdentity").equals(action)) {
                Context.becomeUser(user.getSystemId());
                httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "User.assumeIdentity.success");
                httpSession.setAttribute(WebConstants.OPENMRS_MSG_ARGS, user.getPersonName());
                removeTemporaryPrivileges(isTemporary, isAdministrator);
                return "redirect:/phr/index.htm";
                
            } else if (mss.getMessage("User.delete").equals(action)) {
                try {
                    Context.getUserService().purgeUser(user);
                    httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "User.delete.success");
                    removeTemporaryPrivileges(isTemporary, isAdministrator);
                    return "redirect:/phr/user.list";
                } catch (final Exception ex) {
                    httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "User.delete.failure");
                    log.error("Failed to delete user", ex);
                    removeTemporaryPrivileges(isTemporary, isAdministrator);
                    return "redirect:/phr/user.form?userId=" + request.getParameter("userId");
                }
                
            } else if (mss.getMessage("User.retire").equals(action)) {
                final String retireReason = request.getParameter("retireReason");
                if (!(StringUtils.hasText(retireReason))) {
                    errors.rejectValue("retireReason", "User.disableReason.empty");
                    removeTemporaryPrivileges(isTemporary, isAdministrator);
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
                } else { //user is doing a self registration when roles = null
                    Role role = us.getRole("PHR Restricted User");
                    if(institution != null && mrn != null) { //if a patient is doing a self registration
                    	role = us.getRole("PHR Patient");
                    }
                    newRoles.add(role);
                    user.addRole(role);
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
                    removeTemporaryPrivileges(isTemporary, isAdministrator);
                    return showForm(user.getUserId(), createNewPerson, sharingToken, user, model, httpSession);
                }
                
                // look for person attributes (including email entered) in the request and save to user
            	setPersonAttribute(user, request, errors);
                                
                if (isNewUser(user) && !isAdministrator) {
                    log.debug("Saving new user " + user.getUsername() + ", sharingToken=" + sharingToken);
                    final PhrSharingToken token = PersonalhrUtil.getService().getSharingTokenDao().getSharingToken(sharingToken);
                    
                    if(institution == null || mrn == null) { //if a patient is not doing a self registration
	                    if (token == null || token.getExpireDate().before(new Date())) { //check token existence and name matching
	                        httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR,
	                            "Failed to register without a valid sharing token");
	                        log.error("Failed to register without a valid sharing token");
	                        PersonalhrUtil.getService().logEvent(PhrLogEvent.USER_SIGN_UP, new Date(), null, 
	                            httpSession.getId(), null, 
	                            "error=Failed to register without a valid sharing token; user_name=" + user.getName());
	    
	                        removeTemporaryPrivileges(isTemporary, isAdministrator);
	                        return "redirect:/phr/index.htm?noredirect=true";
	                    } else if ((token != null) && (token.getRelatedPerson() != null)) {
	                        httpSession
	                                .setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "Failed to register with a used sharing token");
	                        log.error("Failed to register with a used sharing token");
	                        PersonalhrUtil.getService().logEvent(PhrLogEvent.USER_SIGN_UP, new Date(), null, 
	                            httpSession.getId(), null, 
	                            "error=Failed to register with a used sharing token; user_name=" + user.getName() + "; sharingToken="+token);
	                        removeTemporaryPrivileges(isTemporary, isAdministrator);	                        
	                        return "redirect:/phr/index.htm?noredirect=true";
	                    } 
                    
	                    if (emailEntered != null && token.getRelatedPersonEmail().equalsIgnoreCase(emailEntered)) {                                               	                                                                        
	                        //create a new user by self registration
	                        us.saveUser(user, password);
	                        
	                        //update sharing token
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
	                        
	                        //save email to messaging service
	                        Integer addressId = saveEmail(user.getPerson(), emailEntered);
	                        
	                        //set default messaging alert address
	                        boolean shouldAlert = true;
	                        PersonalhrUtil.setMessagingAlertSettings(user.getPerson(), shouldAlert, addressId);
	                        
	                        //send email notification
	                        final String deployUrl= Context.getRuntimeProperties().getProperty("deployment.url");//"https://65.111.248.164:8443/"; //"172.30.201.24";
	                        final String url = deployUrl + "/openmrs/phr/index.htm";
	                        final String passwordOption= Context.getAdministrationService().getGlobalProperty("personalhr.show.password");
	                                  
	                        String notification = NOTIFICATION_TEMPLATE;
	                        notification = notification.replaceAll("OPENMRS_PHR_RELATED_PERSON", user.getPerson().getGivenName());
	                        notification = notification.replaceAll("OPENMRS_USERNAME", user.getUsername());
	                        notification = notification.replaceAll("OPENMRS_PASSWORD", showPassword(password, passwordOption));
	                        notification = notification.replaceAll("OPENMRS_URL", url);
	                        
	                        PersonalhrUtil.sendEmail(emailEntered, notification);
	                    } else {
	                        httpSession.setAttribute(
	                            WebConstants.OPENMRS_MSG_ATTR,
	                            "Failed to create new user due to email mismatch: " + emailEntered);
	                        log.debug("Failed to create new user due to email mismatch: " + token.getRelatedPersonEmail() + " vs "
	                                + emailEntered);
	                        PersonalhrUtil.getService().logEvent(PhrLogEvent.USER_SIGN_UP, new Date(), null, 
	                            httpSession.getId(), null, 
	                            "info=Failed to create new user due to email mismatch: " + token.getRelatedPersonEmail() + "vs " + emailEntered + "; sharingToken="+token);
	                    }
                    } else { //patient is doing a self registration                                                                                              
                        //create a new user by self registration
                        us.saveUser(user, password);
                        
                        //create a patient object for this user if a patient is doing a self registration
                        if(institution != null && mrn != null) {
                        	savePatient(user, institution, mrn, httpSession);
                        }
                        
                        //log event and success message
                        httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "personalhr.user.signed.up");
                        log.debug("New self-registered user created: " + user.getUsername());
                        PersonalhrUtil.getService().logEvent(PhrLogEvent.USER_SIGN_UP, new Date(), user, 
                            httpSession.getId(), null, 
                            "info=New self-registered user created; user_name=" + user.getName() + "; sharingToken="+token);
                        
                        //save email to messaging service
                        Integer addressId = saveEmail(user.getPerson(), emailEntered);
                        
                        //set default messaging alert address
                        boolean shouldAlert = true;
                        PersonalhrUtil.setMessagingAlertSettings(user.getPerson(), shouldAlert, addressId);
                        
                        //send email notification
                        final String deployUrl= Context.getRuntimeProperties().getProperty("deployment.url");
                        final String url = deployUrl + "/openmrs/phr/index.htm";
                        final String passwordOption= Context.getAdministrationService().getGlobalProperty("personalhr.show.password");
                                  
                        String notification = NOTIFICATION_TEMPLATE;
                        notification = notification.replaceAll("OPENMRS_PHR_RELATED_PERSON", user.getPerson().getGivenName());
                        notification = notification.replaceAll("OPENMRS_USERNAME", user.getUsername());
                        notification = notification.replaceAll("OPENMRS_PASSWORD", showPassword(password, passwordOption));
                        notification = notification.replaceAll("OPENMRS_URL", url);
                        
                        PersonalhrUtil.sendEmail(emailEntered, notification);
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
        	removeTemporaryPrivileges(isTemporary, isAdministrator);
        }
        return "redirect:/phr/index.htm?noredirect=true";
    }
    
    /**
     * Auto generated method comment
     * 
     * @param password full length password
     * @param passwordOption length of password to expose
     * @return
     */
    private String showPassword(String password, String passwordOption) {
        if(passwordOption==null || password==null) {
            return password;
        }
        try{
            int numToShow = Integer.parseInt(passwordOption); 
            StringBuffer sb = new StringBuffer();
            for(int ii=0; ii<password.length(); ii++) {
                if(ii<numToShow) {
                    sb.append(password.charAt(ii));
                } else {
                    sb.append('*');
                }
            }
            return sb.toString();
            
        } catch (NumberFormatException e) {
            return password;
        }                
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
    
    /**
     * Auto generated method comment
     * 
     * @param newPatient
     * @param email
     */
    private Integer saveEmail(final Person newPerson, final String email) {
        try {
            final MessagingAddressService mas = Context.getService(MessagingAddressService.class);
            MessagingAddress ma = new MessagingAddress(email, newPerson, org.openmrs.module.messaging.email.EmailProtocol.class);
            ma.setPreferred(true);
            List<MessagingAddress> addresses = mas.findMessagingAddresses(null, org.openmrs.module.messaging.email.EmailProtocol.class, newPerson, false);
            if(addresses != null &&  !addresses.isEmpty()) {
                for(MessagingAddress addr : addresses) {
                    if(addr.getPreferred()) {
                        addr.setAddress(email);
                        ma = addr;
                        break;
                    }
                }
            } 
            mas.saveMessagingAddress(ma);

            List<MessagingAddress> addresses2 = mas.findMessagingAddresses(null, org.openmrs.module.messaging.email.EmailProtocol.class, newPerson, false);
            if(addresses2 != null &&  !addresses2.isEmpty() && addresses2.get(0) != null) {
                return addresses2.get(0).getId();
            } else {            
                return null;
            }
        } catch (final Exception e) {
            this.log.debug("Unable to save email address to messaging_addresses table " + email, e);
            return null;
        } catch (final NoClassDefFoundError e) {
            this.log.debug("Messaging module is not found, cannot save " + email, e);
            return null;
        }         
    }    

    protected boolean savePatient(User user, String institution, String mrn, final HttpSession httpSession) throws Exception {
                
        log.debug("\nNOW GOING THROUGH ONSUBMIT METHOD.......................................\n\n");
        boolean isError = false;
        
        if (Context.isAuthenticated()) {
            PatientService ps = Context.getPatientService();
            PersonService personService = Context.getPersonService();
                        
            Patient patient = new Patient(user.getPerson());
            
            PatientIdentifier pid = new PatientIdentifier();
			PatientIdentifierType pit = Context.getPatientService().getPatientIdentifierTypeByName(institution);
			if (pit == null) {
				log.error("Can't find PatientIdentifierType named '" + institution + "'");
				isError = true;
			}            
            pid.setIdentifierType(pit);
            pid.setIdentifier(mrn);
            pid.setLocation(Context.getLocationService().getLocation(institution)); //assuming location name and identifier name are the same
            pid.setPreferred(true);
            patient.addIdentifier(pid);
            
            
            Patient newPatient = null;
            
            if (!isError) {
                // save or add the patient
                try {
            		Context.clearSession();                	
                    newPatient = ps.savePatient(patient);
                }
                catch (InvalidIdentifierFormatException iife) {
                    log.error(iife);
                    patient.removeIdentifier(iife.getPatientIdentifier());
                    httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "PatientIdentifier.error.formatInvalid");
                    //errors = new BindException(new InvalidIdentifierFormatException(msa.getMessage("PatientIdentifier.error.formatInvalid")), "givenName");
                    isError = true;
                }
                catch (InvalidCheckDigitException icde) {
                    log.error(icde);
                    patient.removeIdentifier(icde.getPatientIdentifier());
                    httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "PatientIdentifier.error.checkDigit");
                    //errors = new BindException(new InvalidCheckDigitException(msa.getMessage("PatientIdentifier.error.checkDigit")), "givenName");
                    isError = true;
                }
                catch (IdentifierNotUniqueException inue) {
                    log.error(inue);
                    patient.removeIdentifier(inue.getPatientIdentifier());
                    httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "PatientIdentifier.error.notUnique");
                    //errors = new BindException(new IdentifierNotUniqueException(msa.getMessage("PatientIdentifier.error.notUnique")), "givenName");
                    isError = true;
                }
                catch (DuplicateIdentifierException die) {
                    log.error(die);
                    patient.removeIdentifier(die.getPatientIdentifier());
                    httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "PatientIdentifier.error.duplicate");
                    //errors = new BindException(new DuplicateIdentifierException(msa.getMessage("PatientIdentifier.error.duplicate")), "givenName");
                    isError = true;
                }
                catch (InsufficientIdentifiersException iie) {
                    log.error(iie);
                    patient.removeIdentifier(iie.getPatientIdentifier());
                    httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR,
                        "PatientIdentifier.error.insufficientIdentifiers");
                    //errors = new BindException(new InsufficientIdentifiersException(msa.getMessage("PatientIdentifier.error.insufficientIdentifiers")), "givenName");
                    isError = true;
                }
                catch (PatientIdentifierException pie) {
                    log.error(pie);
                    patient.removeIdentifier(pie.getPatientIdentifier());
                    httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, pie.getMessage());
                    //errors = new BindException(new PatientIdentifierException(msa.getMessage("PatientIdentifier.error.general")), "givenName");
                    isError = true;
                }
                
            }            
        }
        
        return isError;
    }    
    
    void removeTemporaryPrivileges(boolean isTemporary, boolean isAdministrator) {
        if (isTemporary) {
            Context.removeProxyPrivilege(OpenmrsConstants.PRIV_ADD_USERS);
            Context.removeProxyPrivilege(OpenmrsConstants.PRIV_EDIT_USERS);
            Context.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_USERS);
            Context.removeProxyPrivilege("PHR Restricted Patient Access");
            Context.removeProxyPrivilege(OpenmrsConstants.PRIV_EDIT_PERSONS);
            Context.removeProxyPrivilege(OpenmrsConstants.PRIV_EDIT_USER_PASSWORDS);
            Context.removeProxyPrivilege("PHR Single Patient Access");
            Context.removeProxyPrivilege(OpenmrsConstants.PRIV_ADD_PATIENTS);
            Context.removeProxyPrivilege(OpenmrsConstants.PRIV_EDIT_PATIENTS);
            Context.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PATIENTS);
            Context.logout();
            log.debug("Removed proxy privileges for self registration!");
        } else if (isAdministrator) {
            Context.removeProxyPrivilege(OpenmrsConstants.PRIV_ADD_USERS);
            Context.removeProxyPrivilege(OpenmrsConstants.PRIV_EDIT_USERS);
            Context.removeProxyPrivilege(OpenmrsConstants.PRIV_DELETE_USERS);
            Context.removeProxyPrivilege(OpenmrsConstants.PRIV_PURGE_USERS);
            Context.removeProxyPrivilege(OpenmrsConstants.PRIV_EDIT_PERSONS);
            Context.removeProxyPrivilege(OpenmrsConstants.PRIV_EDIT_USER_PASSWORDS);
            log.debug("Removed proxy privileges for PHR Administrator!");
        }                	    	
    }
    
    private void setPersonAttribute(User user, WebRequest request, BindingResult errors) {
        // look for person attributes (including email entered) in the request and save to user
        for (final PersonAttributeType type : Context.getPersonService().getPersonAttributeTypes(PERSON_TYPE.USER,
            ATTR_VIEW_TYPE.LISTING)) {
            final String paramName = type.getName();
            final String value = request.getParameter(paramName);
            
            this.log.debug("paramName=" + paramName);
            
            // if there is an error displaying the attribute, the value will be null
            if (value != null) {
                final PersonAttribute attribute = new PersonAttribute(type, value);
                try {
                    final Object hydratedObject = attribute.getHydratedObject();
                    if ((hydratedObject == null) || "".equals(hydratedObject.toString())) {
                        // if null is returned, the value should be blanked out
                        attribute.setValue("");
                    } else if (hydratedObject instanceof Attributable) {
                        attribute.setValue(((Attributable) hydratedObject).serialize());
                    } else if (!hydratedObject.getClass().getName().equals(type.getFormat())) {
                        // if the classes doesn't match the format, the hydration failed somehow
                        // TODO change the PersonAttribute.getHydratedObject() to not swallow all errors?
                        throw new APIException();
                    }
                } catch (final APIException e) {
                    errors.rejectValue("attributeMap[" + type.getName() + "]", "Invalid value for " + type.getName()
                            + ": '" + value + "'");
                    this.log.warn("Got an invalid value: " + value + " while setting personAttributeType id #"
                            + paramName, e);
                    
                    // setting the value to empty so that the user can reset the value to something else
                    attribute.setValue("");
                    
                }
                user.getPerson().addAttribute(attribute);
            }   
        }
    }
}