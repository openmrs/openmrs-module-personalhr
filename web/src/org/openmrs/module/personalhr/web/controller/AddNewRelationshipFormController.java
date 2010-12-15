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
import org.springframework.validation.BindException;
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
public class AddNewRelationshipFormController extends SimpleFormController {
    
    protected final Log log = LogFactory.getLog(getClass());
    
    @Override
    protected PhrPatient formBackingObject(HttpServletRequest request) throws Exception {
        log.debug("Entering AddNewRelationshipFormController:formBackingObject");
        Integer patientId = null;
        if (request.getParameter("patientId") != null && !"".equals(request.getParameter("patientId"))) {
            patientId = PersonalhrUtil.getParamAsInteger(request.getParameter("patientId"));
        }
        
        return new PhrPatient(patientId);         
    }

    @Override
    protected void onBindAndValidate(HttpServletRequest request,
            Object commandObject, BindException errors) throws Exception {
        log.debug("Entering AddNewRelationshipFormController:onBindAndValidate");
        PhrPatient phrPatient = (PhrPatient) commandObject;
        List<PhrSharingToken> tokens = phrPatient.getSharingTokens();
        try {
            //validate email address
            for(PhrSharingToken token : tokens) {
              if(PersonalhrUtil.isNullOrEmpty(token.getRelatedPersonEmail())) {
                errors.reject("Email can not be empty");  
              }
              //save the changes to database by calling PersonalhrUtil.getService()
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
        log.debug("Entering AddNewRelationshipFormController:onSubmit");
        
        PhrPatient phrPat = (PhrPatient) commandObject;
        try {
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
