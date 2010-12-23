package org.openmrs.module.personalhr.web.controller;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.personalhr.PersonalhrUtil;
import org.openmrs.module.personalhr.PhrPatient;
import org.openmrs.module.personalhr.PhrSharingToken;
import org.openmrs.web.WebConstants;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

/**
 * The controller for entering/viewing a form. This should always be set to sessionForm=false.
 * <p/>
 * Handles {@code htmlFormEntry.form} requests. Renders view {@code htmlFormEntry.jsp}.
 * <p/>
 * TODO: This has a bit too much logic in the onSubmit method. Move that into the FormEntrySession.
 */
public class PatientRelationshipsFormController extends SimpleFormController {
    
    protected final Log log = LogFactory.getLog(getClass());
    
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
            //validate email address
            if("Save Changes".equals(command)) {
                for(PhrSharingToken token : tokens) {
                  if(PersonalhrUtil.isNullOrEmpty(token.getRelatedPersonEmail())) {
                    errors.reject("Modified email can not be empty");  
                  } else {
                    log.debug("token.getRelatedPersonEmail()="+token.getRelatedPersonEmail()); 
                  }
                  //save the changes to database by calling PersonalhrUtil.getService()
                }
            } else if("Add".equals(command)) {
                PhrSharingToken token = newToken;
                if(PersonalhrUtil.isNullOrEmpty(token.getRelatedPersonEmail())) {
                  errors.reject("Email can not be empty for added relationship");  
                } else {
                  log.debug("token.getRelatedPersonEmail()="+token.getRelatedPersonEmail()); 
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
        
        log.debug("onSubmit: tokens.size="+tokens.size() + "; new relationship with " + newToken.getRelatedPersonName() + ";" + phrPat.getPersonName());
        try {
            if(command.startsWith("Delete")) {
                Integer id = PersonalhrUtil.getParamAsInteger(command.substring(command.indexOf("Delete ")));
                if(id!=null && id>0) {
                    phrPat.delete(id);
                }                
            } else {
                phrPat.save();
            }
            String results = "Number of relationships changed: " + phrPat.getNumberChanged() + 
            "; Number of relationships added: " + phrPat.getNumberAdded() +
            "; Number of relationships deleted: " + phrPat.getNumberDeleted();
            log.debug(results );
            request.getSession().setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Saved " + phrPat.getPatient());
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
    
}
