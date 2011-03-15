package org.openmrs.module.personalhr.web.controller;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.personalhr.PersonalhrUtil;
import org.openmrs.module.personalhr.PhrPatient;
import org.openmrs.module.personalhr.PhrSecurityService;
import org.openmrs.module.personalhr.PhrSharingToken;
import org.openmrs.web.WebConstants;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

import org.openmrs.module.messaging.MessagingService;

/**
 * The controller for entering/viewing a form. This should always be set to sessionForm=false.
 * <p/>
 * Handles {@code htmlFormEntry.form} requests. Renders view {@code htmlFormEntry.jsp}.
 * <p/>
 * TODO: This has a bit too much logic in the onSubmit method. Move that into the FormEntrySession.
 */
public class PatientRelationshipsFormController extends SimpleFormController {
    
    protected final Log log = LogFactory.getLog(getClass());
    
    private static final String EMAIL_TEMPLATE="I've shared my health profile with you. \nOPENMRS_PHR_SHARING_LINK\nThe profile is not an attachment -- it's stored securely online. \n\nTo view the profile, accept the sharing request by clicking the link above and then sign in with your username and password. If you do not have a account yet, you will be asked to create one using the e-mail address this sharing request e-mail was sent to. After you create the account, you must click the link above to accept the sharing request. This offer to share expires 30 days from the day it was sent. If you accept the sharing request, you will have ongoing access to view the profile unless the sharer decides to stop sharing it."; 
    
    /**
     * Allows for Integers to be used as values in input tags. Normally, only strings and lists are
     * expected
     * 
     * @see org.springframework.web.servlet.mvc.BaseCommandController#initBinder(javax.servlet.http.HttpServletRequest,
     *      org.springframework.web.bind.ServletRequestDataBinder)
     */
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
        super.initBinder(request, binder);
        binder.registerCustomEditor(java.lang.Integer.class, new CustomNumberEditor(java.lang.Integer.class, true));
    }
    
    @Override
    protected PhrPatient formBackingObject(HttpServletRequest request) throws Exception {
        log.debug("Entering PatientRelationshipsFormController:formBackingObject");
        Integer patientId = null;
        if (request.getParameter("patientId") != null && !"".equals(request.getParameter("patientId"))) {
            patientId = PersonalhrUtil.getParamAsInteger(request.getParameter("patientId"));
        }
        
        return new PhrPatient(patientId);         
    }

    @Override
    protected void onBindAndValidate(HttpServletRequest request,
            Object commandObject, BindException errors) throws Exception {
        String command = request.getParameter("command");
        log.debug("Entering PatientRelationshipsFormController:onBindAndValidate, command=" + command);
        PhrPatient phrPatient = (PhrPatient) commandObject;
        List<PhrSharingToken> tokens = phrPatient.getSharingTokens();
        PhrSharingToken newToken = phrPatient.getNewSharingToken();
        
        log.debug("tokens.size="+tokens.size() + "; new relationship with " + newToken.getRelatedPersonName() + ";" + phrPatient.getPersonName());
        try {
            if("Save Changes".equals(command)) {
                for(PhrSharingToken token : tokens) {
                  //validate email address
                  if(PersonalhrUtil.isNullOrEmpty(token.getRelatedPersonEmail())) {
                    errors.reject("Email can not be empty!");  
                  } else if(!PersonalhrUtil.isValidEmail(token.getRelatedPersonEmail())) {
                    errors.reject("Invalid email address: " + token.getRelatedPersonEmail());  
                  } else {
                    log.debug("token.getRelatedPersonEmail()="+token.getRelatedPersonEmail()); 
                  }  
                  //validate sharing type
                  if(PhrSecurityService.PhrSharingType.SHARE_NOTHING.getValue().equals(token.getShareType())) {
                      errors.reject("Please select the type of information you want to share with the specified person: " + token.getRelatedPersonName());                      
                  } else {
                    log.debug("token.getShareType()="+token.getShareType()+"; PhrSecurityService.PhrSharingType.SHARE_NOTHING.getValue()="+PhrSecurityService.PhrSharingType.SHARE_NOTHING.getValue()); 
                  } 
                  
                }
            } else if("Add".equals(command)) {
                PhrSharingToken token = newToken;

                //validate person name
                if(PersonalhrUtil.isNullOrEmpty(token.getRelatedPersonName())) {
                  errors.reject("Person name can not be empty!");  
                } else {
                  log.debug("token.getRelatedPersonName()="+token.getRelatedPersonName()); 
                }                                
                
                //validate email address
                if(PersonalhrUtil.isNullOrEmpty(token.getRelatedPersonEmail())) {
                  errors.reject("Email can not be empty for added relationship!");  
                } else if(!PersonalhrUtil.isValidEmail(token.getRelatedPersonEmail())) {
                    errors.reject("Invalid email address: "+token.getRelatedPersonEmail());  
                } else {
                  log.debug("token.getRelatedPersonEmail()="+token.getRelatedPersonEmail()); 
                } 
                //validate sharing type
                if(PhrSecurityService.PhrSharingType.SHARE_NOTHING.getValue().equals(token.getShareType())) {
                    errors.reject("Please select the type of information you want to share with the specified person: " + token.getRelatedPersonName());                      
                } else {
                  log.debug("token.getShareType()="+token.getShareType()+"; PhrSecurityService.PhrSharingType.SHARE_NOTHING.getValue()="+PhrSecurityService.PhrSharingType.SHARE_NOTHING.getValue()); 
                }                 
            }                       
        } catch (Exception ex) {
            log.error("Exception during form validation", ex);
            errors.reject("Exception during form validation, see log for more details: " + ex);
        }
    }

    @Override
    protected ModelAndView onSubmit(HttpServletRequest request,
            HttpServletResponse response, Object commandObject, BindException errors)
            throws Exception {
        String command = request.getParameter("command");
        log.debug("Entering PatientRelationshipsFormController:onSubmit, command=" + command);
        
        PhrPatient phrPat = (PhrPatient) commandObject;
        List<PhrSharingToken> tokens = phrPat.getSharingTokens();
        PhrSharingToken newToken = phrPat.getNewSharingToken();
        
        log.debug("onSubmit: tokens.size="+tokens.size() + "; new relationship with " + newToken.getRelatedPersonName() + ";" + phrPat.getPersonName()+";" + newToken.getShareType() + "; " + newToken.getRelationType());
        try {
            if(command != null && command.startsWith("Delete")) {
                Integer id = PersonalhrUtil.getParamAsInteger(command.substring(7));
                if(id!=null && id>0) {
                    phrPat.delete(id);
                }                
            } else if(command != null && (command.startsWith("Save") || command.startsWith("Add"))) {
                phrPat.save();
                
                if(command.startsWith("Add")) {
                    //send email notification to the specified person
                    String email = EMAIL_TEMPLATE;
                    String emailAddress = phrPat.getNewSharingToken().getRelatedPersonEmail();
                    String ip = "172.30.201.24";
                    String token = phrPat.getNewSharingToken().getSharingToken();
                    String url = "https://" + ip + ":8443/" + "openmrs/phr/signUp.form?sharingToken=" + token;
                    email = email.replaceAll("OPENMRS_PHR_SHARING_LINK", url);
                    
                    sendEmail(emailAddress, email);
                    
                    log.debug("\n\nThe following email has been sent to " + emailAddress + ":\n" + email + "\n\n");
                }
            } 
            
            //String results = "Number of relationships changed: " + phrPat.getNumberChanged() + 
            //"; Number of relationships added: " + phrPat.getNumberAdded() +
            //"; Number of relationships deleted: " + phrPat.getNumberDeleted();
            //log.debug(results );
            //request.getSession().setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Saved " + phrPat.getPatient());
            String successView = getSuccessView() + "?patientId=" + phrPat.getPatientId();
            return new ModelAndView(new RedirectView(successView));
            
        } catch (Exception ex) {
            log.error("Exception trying to submit form", ex);
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            errors.reject("Exception! " + ex.getMessage() + "<br/>" + sw.toString());
            return showForm(request, response, errors);
        }
    }

    /**
     * Auto generated method comment
     * 
     * @param emailAddress
     * @param email
     */
    private void sendEmail(String emailAddress, String email) {
        // TODO Auto-generated method stub
        try{
            Context.getService(MessagingService.class).sendMessage(email, emailAddress, org.openmrs.module.messaging.email.EmailProtocol.class);
        } catch (Exception e) {
            log.debug("Unable to send message to " + emailAddress, e);
        }
    }
    
}
