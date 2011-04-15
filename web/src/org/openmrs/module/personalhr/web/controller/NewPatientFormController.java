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

import java.text.NumberFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Attributable;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.api.APIException;
import org.openmrs.api.DuplicateIdentifierException;
import org.openmrs.api.IdentifierNotUniqueException;
import org.openmrs.api.InsufficientIdentifiersException;
import org.openmrs.api.InvalidCheckDigitException;
import org.openmrs.api.InvalidIdentifierFormatException;
import org.openmrs.api.PatientIdentifierException;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.PersonService.ATTR_VIEW_TYPE;
import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.MessagingAddressService;
import org.openmrs.module.messaging.domain.MessagingAddress;
import org.openmrs.module.personalhr.PersonalhrUtil;
import org.openmrs.propertyeditor.ConceptEditor;
import org.openmrs.propertyeditor.LocationEditor;
import org.openmrs.util.OpenmrsConstants;
import org.openmrs.util.OpenmrsConstants.PERSON_TYPE;
import org.openmrs.util.OpenmrsUtil;
import org.openmrs.web.WebConstants;
import org.openmrs.web.controller.person.PersonFormController;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

/**
 * This controller is used for the "mini"/"new"/"short" patient form. Only key/important attributes
 * for the patient are displayed and allowed to be edited
 * 
 * @see org.openmrs.web.controller.patient.PatientFormController
 */
public class NewPatientFormController extends SimpleFormController {
    
    /** Logger for this class and subclasses */
    protected final Log log = LogFactory.getLog(getClass());
    
    public void setShortPatientValidator(final ShortPatientValidator shortPatientValidator) {
        super.setValidator(shortPatientValidator);
    }
    
    // identifiers submitted with the form.  Stored here so that they can
    // be redisplayed for the user after an error
    Set<PatientIdentifier> newIdentifiers = new HashSet<PatientIdentifier>();
    
    String pref = "";
    
    /**
     * Allows for other Objects to be used as values in input tags. Normally, only strings and lists
     * are expected
     * 
     * @see org.springframework.web.servlet.mvc.BaseCommandController#initBinder(javax.servlet.http.HttpServletRequest,
     *      org.springframework.web.bind.ServletRequestDataBinder)
     */
    @Override
    protected void initBinder(final HttpServletRequest request, final ServletRequestDataBinder binder) throws Exception {
        super.initBinder(request, binder);
        
        final NumberFormat nf = NumberFormat.getInstance(Context.getLocale());
        binder.registerCustomEditor(java.lang.Integer.class, new CustomNumberEditor(java.lang.Integer.class, nf, true));
        binder.registerCustomEditor(java.util.Date.class, new CustomDateEditor(Context.getDateFormat(), true, 10));
        binder.registerCustomEditor(Location.class, new LocationEditor());
        binder.registerCustomEditor(Concept.class, "causeOfDeath", new ConceptEditor());
    }
    
    @Override
    protected ModelAndView processFormSubmission(final HttpServletRequest request, final HttpServletResponse response,
                                                 final Object obj, final BindException errors) throws Exception {
        
        this.newIdentifiers = new HashSet<PatientIdentifier>();
        
        final ShortPatientModel shortPatient = (ShortPatientModel) obj;
        
        this.log.debug("\nNOW GOING THROUGH PROCESSFORMSUBMISSION METHOD.......................................\n\n");
        
        if (Context.isAuthenticated()) {
            final PatientService ps = Context.getPatientService();
            final MessageSourceAccessor msa = getMessageSourceAccessor();
            
            final String action = request.getParameter("action");
            if ((action == null) || action.equals(msa.getMessage("general.save"))) {
                
                final String[] identifiers = request.getParameterValues("identifier");
                final String[] types = request.getParameterValues("identifierType");
                final String[] locs = request.getParameterValues("location");
                this.pref = request.getParameter("preferred");
                if (this.pref == null) {
                    this.pref = "";
                }
                
                if (this.log.isDebugEnabled()) {
                    this.log.debug("identifiers: " + identifiers);
                    if (identifiers != null) {
                        for (final String s : identifiers) {
                            this.log.debug(s);
                        }
                    }
                    
                    this.log.debug("types: " + types);
                    if (types != null) {
                        for (final String s : types) {
                            this.log.debug(s);
                        }
                    }
                    
                    this.log.debug("locations: " + locs);
                    if (locs != null) {
                        for (final String s : locs) {
                            this.log.debug(s);
                        }
                    }
                    
                    this.log.debug("preferred: " + this.pref);
                }
                
                // loop over the identifiers to create the patient.identifiers set
                if (identifiers != null) {
                    for (int i = 0; i < identifiers.length; i++) {
                        // arguments for the spring error messages
                        final String id = identifiers[i].trim();
                        final String[] args = { id };
                        
                        // add the new identifier only if they put in some identifier string
                        if (id.length() > 0) {
                            
                            // set up the actual identifier java object
                            PatientIdentifierType pit = null;
                            if ((types[i] == null) || types[i].equals("")) {
                                final String msg = getMessageSourceAccessor().getMessage(
                                    "PatientIdentifier.identifierType.null", args);
                                errors.reject(msg);
                            } else {
                                pit = ps.getPatientIdentifierType(Integer.valueOf(types[i]));
                            }
                            
                            Location loc = null;
                            if ((locs[i] == null) || locs[i].equals("")) {
                                final String msg = getMessageSourceAccessor().getMessage("PatientIdentifier.location.null",
                                    args);
                                errors.reject(msg);
                            } else {
                                loc = Context.getLocationService().getLocation(Integer.valueOf(locs[i]));
                            }
                            
                            final PatientIdentifier pi = new PatientIdentifier(id, pit, loc);
                            pi.setPreferred(this.pref.equals(id + types[i]));
                            this.newIdentifiers.add(pi);
                            
                            if (this.log.isDebugEnabled()) {
                                this.log.debug("Creating patient identifier with identifier: " + id);
                                this.log.debug("and type: " + types[i]);
                                this.log.debug("and location: " + locs[i]);
                            }
                            
                        }
                    }
                }
            }
            
        }
        
        // skip calling super.processFormSubmission so that setting up the page is done
        // again in the onSubmit method
        
        return onSubmit(request, response, shortPatient, errors);
    }
    
    /**
     * The onSubmit function receives the form/command object that was modified by the input form
     * and saves it to the db
     * 
     * @see org.springframework.web.servlet.mvc.SimpleFormController#onSubmit(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse, java.lang.Object,
     *      org.springframework.validation.BindException)
     */
    @Override
    @SuppressWarnings("unchecked")
    protected ModelAndView onSubmit(final HttpServletRequest request, final HttpServletResponse response, final Object obj,
                                    final BindException errors) throws Exception {
        
        final HttpSession httpSession = request.getSession();
        
        this.log.debug("\nNOW GOING THROUGH ONSUBMIT METHOD.......................................\n\n");
        
        if (Context.isAuthenticated()) {
            final PatientService ps = Context.getPatientService();
            final PersonService personService = Context.getPersonService();
            
            final ShortPatientModel shortPatient = (ShortPatientModel) obj;
            final String view = getFormView();
            boolean isError = errors.hasErrors(); // account for possible errors in the processFormSubmission method
            
            if (isError) {
                httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, errors.getFieldError().getCode());
            }
            
            final String action = request.getParameter("action");
            final MessageSourceAccessor msa = getMessageSourceAccessor();
            if ((action != null) && action.equals(msa.getMessage("general.cancel"))) {
                httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "general.canceled");
                return new ModelAndView(new RedirectView("addPerson.htm?personType=patient"));
            }
            
            Patient patient = null;
            if (shortPatient.getPatientId() != null) {
                patient = ps.getPatient(shortPatient.getPatientId());
                if (patient == null) {
                    try {
                        final Person p = personService.getPerson(shortPatient.getPatientId());
                        Context.clearSession(); // so that this Person doesn't cause hibernate to think the new Patient is in the cache already (only needed until #725 is fixed)
                        patient = new Patient(p);
                    } catch (final ObjectRetrievalFailureException noUserEx) {
                        // continue;
                    }
                }
            }
            
            if (patient == null) {
                patient = new Patient();
            }
            
            boolean duplicate = false;
            final PersonName newName = shortPatient.getName();
            
            if (this.log.isDebugEnabled()) {
                this.log.debug("Checking new name: " + newName.toString());
            }
            
            for (final PersonName pn : patient.getNames()) {
                if ((((pn.getGivenName() == null) && (newName.getGivenName() == null)) || OpenmrsUtil.nullSafeEquals(
                    pn.getGivenName(), newName.getGivenName()))
                        && (((pn.getMiddleName() == null) && (newName.getMiddleName() == null)) || OpenmrsUtil
                                .nullSafeEquals(pn.getMiddleName(), newName.getMiddleName()))
                        && (((pn.getFamilyName() == null) && (newName.getFamilyName() == null)) || OpenmrsUtil
                                .nullSafeEquals(pn.getFamilyName(), newName.getFamilyName()))) {
                    duplicate = true;
                }
            }
            
            // if this is a new name, add it to the patient
            if (!duplicate) {
                // set the current name to "non-preferred"
                if (patient.getPersonName() != null) {
                    patient.getPersonName().setPreferred(false);
                }
                
                // add the new name
                newName.setPersonNameId(null);
                newName.setPreferred(true);
                newName.setUuid(null);
                patient.addName(newName);
            }
            
            if (this.log.isDebugEnabled()) {
                this.log.debug("The address to add/check: " + shortPatient.getAddress());
            }
            
            if ((shortPatient.getAddress() != null) && !shortPatient.getAddress().isBlank()) {
                duplicate = false;
                for (final PersonAddress pa : patient.getAddresses()) {
                    if (pa.toString().equals(shortPatient.getAddress().toString())) {
                        duplicate = true;
                        pa.setPreferred(true);
                    } else {
                        pa.setPreferred(false);
                    }
                }
                
                if (this.log.isDebugEnabled()) {
                    this.log.debug("The duplicate address:  " + duplicate);
                }
                
                if (!duplicate) {
                    final PersonAddress newAddress = (PersonAddress) shortPatient.getAddress().clone();
                    newAddress.setPersonAddressId(null);
                    newAddress.setPreferred(true);
                    newAddress.setUuid(null);
                    patient.addAddress(newAddress);
                }
            }
            if (this.log.isDebugEnabled()) {
                this.log.debug("patient addresses: " + patient.getAddresses());
            }
            
            // set or unset the preferred bit for the old identifiers if needed
            if (patient.getIdentifiers() == null) {
                patient.setIdentifiers(new LinkedHashSet<PatientIdentifier>());
            }
            
            for (final PatientIdentifier pi : patient.getIdentifiers()) {
                pi.setPreferred(this.pref.equals(pi.getIdentifier() + pi.getIdentifierType().getPatientIdentifierTypeId()));
            }
            
            String email = null;
            // look for person attributes in the request and save to patient
            for (final PersonAttributeType type : personService.getPersonAttributeTypes(PERSON_TYPE.PATIENT,
                ATTR_VIEW_TYPE.VIEWING)) {
                final String paramName = type.getPersonAttributeTypeId().toString();
                final String value = request.getParameter(paramName);
                
                this.log.debug("paramName=" + paramName);
                if ("9".equalsIgnoreCase(paramName)) {
                    if (PersonalhrUtil.isNullOrEmpty(value)) {
                        //errors.reject("Email address cannot be empty!");
                        httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "Email address cannot be empty!");
                        isError = true;
                    } else if (!PersonalhrUtil.isValidEmail(value)) {
                        //errors.reject("Invalid email address: " + value);
                        httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "Invalid email address: " + value);
                        isError = true;
                    } else {
                        //store email address to messaging_addresses table
                        email = value;
                    }
                }
                
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
                    patient.addAttribute(attribute);
                }
            }
            
            if (this.newIdentifiers.isEmpty()) {
                final PatientIdentifier ident = new PatientIdentifier();
                ident.setIdentifier(PersonalhrUtil.getRandomIdentifer());
                ident.setIdentifierType(new PatientIdentifierType(1));
                ident.setLocation(new Location(1));
                this.newIdentifiers.add(ident);
            }
            // add the new identifiers.  First remove them so that things like
            // changes to preferred status and location are persisted
            for (final PatientIdentifier identifier : this.newIdentifiers) {
                identifier.setPatient(patient);
                for (final PatientIdentifier currentIdentifier : patient.getActiveIdentifiers()) {
                    patient.removeIdentifier(currentIdentifier);
                }
            }
            
            patient.addIdentifiers(this.newIdentifiers);
            
            // find which identifiers they removed and void them
            // must create a new list so that the updated identifiers in
            // the newIdentifiers list are hashed correctly
            final List<PatientIdentifier> newIdentifiersList = new Vector<PatientIdentifier>();
            newIdentifiersList.addAll(this.newIdentifiers);
            for (final PatientIdentifier identifier : patient.getIdentifiers()) {
                if (!newIdentifiersList.contains(identifier)) {
                    // mark the "removed" identifiers as voided
                    identifier.setVoided(true);
                    identifier.setVoidReason("Removed from new patient screen");
                }
            }
            
            // set the other patient attributes
            patient.setBirthdate(shortPatient.getBirthdate());
            patient.setBirthdateEstimated(shortPatient.getBirthdateEstimated());
            patient.setGender(shortPatient.getGender());
            
            patient.setDead(shortPatient.getDead());
            if (patient.isDead()) {
                patient.setDeathDate(shortPatient.getDeathDate());
                patient.setCauseOfDeath(shortPatient.getCauseOfDeath());
            } else {
                patient.setDeathDate(null);
                patient.setCauseOfDeath(null);
            }
            
            Patient newPatient = null;
            
            if (!isError) {
                // save or add the patient
                try {
                    newPatient = ps.savePatient(patient);
                } catch (final InvalidIdentifierFormatException iife) {
                    this.log.error(iife);
                    patient.removeIdentifier(iife.getPatientIdentifier());
                    httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "PatientIdentifier.error.formatInvalid");
                    //errors = new BindException(new InvalidIdentifierFormatException(msa.getMessage("PatientIdentifier.error.formatInvalid")), "givenName");
                    isError = true;
                } catch (final InvalidCheckDigitException icde) {
                    this.log.error(icde);
                    patient.removeIdentifier(icde.getPatientIdentifier());
                    httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "PatientIdentifier.error.checkDigit");
                    //errors = new BindException(new InvalidCheckDigitException(msa.getMessage("PatientIdentifier.error.checkDigit")), "givenName");
                    isError = true;
                } catch (final IdentifierNotUniqueException inue) {
                    this.log.error(inue);
                    patient.removeIdentifier(inue.getPatientIdentifier());
                    httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "PatientIdentifier.error.notUnique");
                    //errors = new BindException(new IdentifierNotUniqueException(msa.getMessage("PatientIdentifier.error.notUnique")), "givenName");
                    isError = true;
                } catch (final DuplicateIdentifierException die) {
                    this.log.error(die);
                    patient.removeIdentifier(die.getPatientIdentifier());
                    httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "PatientIdentifier.error.duplicate");
                    //errors = new BindException(new DuplicateIdentifierException(msa.getMessage("PatientIdentifier.error.duplicate")), "givenName");
                    isError = true;
                } catch (final InsufficientIdentifiersException iie) {
                    this.log.error(iie);
                    patient.removeIdentifier(iie.getPatientIdentifier());
                    httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR,
                        "PatientIdentifier.error.insufficientIdentifiers");
                    //errors = new BindException(new InsufficientIdentifiersException(msa.getMessage("PatientIdentifier.error.insufficientIdentifiers")), "givenName");
                    isError = true;
                } catch (final PatientIdentifierException pie) {
                    this.log.error(pie);
                    patient.removeIdentifier(pie.getPatientIdentifier());
                    httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, pie.getMessage());
                    //errors = new BindException(new PatientIdentifierException(msa.getMessage("PatientIdentifier.error.general")), "givenName");
                    isError = true;
                }
                
                // update the death reason
                if (patient.getDead()) {
                    this.log.debug("Patient is dead, so let's make sure there's an Obs for it");
                    // need to make sure there is an Obs that represents the patient's cause of death, if applicable
                    
                    final String codProp = Context.getAdministrationService().getGlobalProperty("concept.causeOfDeath");
                    final Concept causeOfDeath = Context.getConceptService().getConcept(codProp);
                    
                    if (causeOfDeath != null) {
                        final List<Obs> obssDeath = Context.getObsService().getObservationsByPersonAndConcept(patient,
                            causeOfDeath);
                        if (obssDeath != null) {
                            if (obssDeath.size() > 1) {
                                this.log.error("Multiple causes of death (" + obssDeath.size() + ")?  Shouldn't be...");
                            } else {
                                Obs obsDeath = null;
                                if (obssDeath.size() == 1) {
                                    // already has a cause of death - let's edit it.
                                    this.log.debug("Already has a cause of death, so changing it");
                                    
                                    obsDeath = obssDeath.iterator().next();
                                    
                                } else {
                                    // no cause of death obs yet, so let's make one
                                    this.log.debug("No cause of death yet, let's create one.");
                                    
                                    obsDeath = new Obs();
                                    obsDeath.setPerson(patient);
                                    obsDeath.setConcept(causeOfDeath);
                                    
                                    // Get default location
                                    final Location loc = Context.getLocationService().getDefaultLocation();
                                    
                                    // TODO person healthcenter if ( loc == null ) loc = patient.getHealthCenter();
                                    if (loc != null) {
                                        obsDeath.setLocation(loc);
                                    } else {
                                        this.log.error("Could not find a suitable location for which to create this new Obs");
                                    }
                                }
                                
                                // put the right concept and (maybe) text in this obs
                                Concept currCause = patient.getCauseOfDeath();
                                if (currCause == null) {
                                    // set to NONE
                                    this.log.debug("Current cause is null, attempting to set to NONE");
                                    final String noneConcept = Context.getAdministrationService().getGlobalProperty(
                                        "concept.none");
                                    currCause = Context.getConceptService().getConcept(noneConcept);
                                }
                                
                                if (currCause != null) {
                                    this.log.debug("Current cause is not null, setting to value_coded");
                                    obsDeath.setValueCoded(currCause);
                                    obsDeath.setValueCodedName(currCause.getName()); // ABKTODO: presume current locale?
                                    
                                    Date dateDeath = patient.getDeathDate();
                                    if (dateDeath == null) {
                                        dateDeath = new Date();
                                    }
                                    obsDeath.setObsDatetime(dateDeath);
                                    
                                    // check if this is an "other" concept - if so, then we need to add value_text
                                    final String otherConcept = Context.getAdministrationService().getGlobalProperty(
                                        "concept.otherNonCoded");
                                    final Concept conceptOther = Context.getConceptService().getConcept(otherConcept);
                                    if (conceptOther != null) {
                                        if (conceptOther.equals(currCause)) {
                                            // seems like this is an other concept - let's try to get the "other" field info
                                            final String otherInfo = ServletRequestUtils.getStringParameter(request,
                                                "causeOfDeath_other", "");
                                            this.log.debug("Setting value_text as " + otherInfo);
                                            obsDeath.setValueText(otherInfo);
                                        } else {
                                            this.log.debug("New concept is NOT the OTHER concept, so setting to blank");
                                            obsDeath.setValueText("");
                                        }
                                    } else {
                                        this.log.debug("Don't seem to know about an OTHER concept, so deleting value_text");
                                        obsDeath.setValueText("");
                                    }
                                    
                                    if (!StringUtils.hasText(obsDeath.getVoidReason())) {
                                        obsDeath.setVoidReason(Context.getMessageSourceService().getMessage(
                                            "general.default.changeReason"));
                                    }
                                    Context.getObsService().saveObs(obsDeath, obsDeath.getVoidReason());
                                } else {
                                    this.log.debug("Current cause is still null - aborting mission");
                                }
                            }
                        }
                    } else {
                        this.log.debug("Cause of death is null - should not have gotten here without throwing an error on the form.");
                    }
                    
                }
                
            }
            
            // save the relationships to the database
            if (!isError && !errors.hasErrors()) {
                final Map<String, Relationship> relationships = getRelationshipsMap(patient, request);
                for (final Relationship relationship : relationships.values()) {
                    // if the user added a person to this relationship, save it
                    if ((relationship.getPersonA() != null) && (relationship.getPersonB() != null)) {
                        personService.saveRelationship(relationship);
                    }
                }
                
                //save email to messaging_addresses table
                saveEmail(newPatient, email);
            }
            
            // redirect if an error occurred
            if (isError || errors.hasErrors()) {
                this.log.error("Had an error during processing. Redirecting to " + this.getSuccessView());
                
                final Map<String, Object> model = new HashMap<String, Object>();
                model.put(getCommandName(), new ShortPatientModel(patient));
                
                // evict from session so that nothing temporarily added here is saved
                Context.evictFromSession(patient);
                
                //return this.showForm(request, response, errors, model);
                return new ModelAndView(new RedirectView(this.getSuccessView() + "?patientId=" + patient.getPatientId()));
            } else {
                httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "Patient.saved");
                this.log.debug("Patient saved! Redirect to " + this.getSuccessView() + "?patientId="
                        + newPatient.getPatientId());
                return new ModelAndView(new RedirectView(this.getSuccessView() + "?patientId=" + newPatient.getPatientId()));
            }
        } else {
            return new ModelAndView(new RedirectView(getFormView()));
        }
    }
    
    /**
     * Auto generated method comment
     * 
     * @param newPatient
     * @param email
     */
    private void saveEmail(final Patient newPatient, final String email) {
        // TODO Auto-generated method stub
        // TODO Auto-generated method stub
        try {
            final MessagingAddressService mas = Context.getService(MessagingAddressService.class);
            final MessagingAddress ma = new MessagingAddress(email, newPatient);
            ma.setProtocol(org.openmrs.module.messaging.email.EmailProtocol.class);
            mas.saveMessagingAddress(ma);
        } catch (final Exception e) {
            this.log.debug("Unable to save email address to messaging_addresses table " + email, e);
        } catch (final NoClassDefFoundError e) {
            this.log.debug("Messaging module is not found, cannot save " + email, e);
        }
        
    }
    
    /**
     * This is called prior to displaying a form for the first time. It tells Spring the
     * form/command object to load into the request
     * 
     * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected Object formBackingObject(final HttpServletRequest request) throws ServletException {
        this.log.debug("Start NewPatientFormController:formBackingObject...");
        this.newIdentifiers = new HashSet<PatientIdentifier>();
        Patient p = null;
        Integer id = null;
        
        if (Context.isAuthenticated()) {
            final PatientService ps = Context.getPatientService();
            String patientId = request.getParameter("patientId");
            if (!StringUtils.hasText(patientId) && (request.getAttribute("org.openmrs.portlet.patientId") != null)) {
                patientId = request.getAttribute("org.openmrs.portlet.patientId").toString();
            }
            
            if (StringUtils.hasText(patientId)) {
                try {
                    id = Integer.valueOf(patientId);
                    p = ps.getPatient(id);
                } catch (final NumberFormatException numberError) {
                    this.log.warn("Invalid patientId supplied: '" + patientId + "'", numberError);
                } catch (final ObjectRetrievalFailureException noUserEx) {
                    // continue
                }
            }
            
            if (p == null) {
                try {
                    final Person person = Context.getPersonService().getPerson(id);
                    if (person != null) {
                        p = new Patient(person);
                    }
                } catch (final ObjectRetrievalFailureException noPersonEx) {
                    this.log.warn("There is no patient or person with id: '" + id + "'", noPersonEx);
                    throw new ServletException("There is no patient or person with id: '" + id + "'");
                }
            }
        }
        
        this.log.debug("Patient=" + p + ", id: " + id);
        
        ShortPatientModel patient = new ShortPatientModel(p);
        
        final String name = request.getParameter("addName");
        if ((p == null) && (name != null)) {
            final String gender = request.getParameter("addGender");
            final String date = request.getParameter("addBirthdate");
            final String age = request.getParameter("addAge");
            
            p = new Patient();
            PersonFormController.getMiniPerson(p, name, gender, date, age);
            
            patient = new ShortPatientModel(p);
        }
        
        if (patient.getAddress() == null) {
            final PersonAddress pa = new PersonAddress();
            pa.setPreferred(true);
            patient.setAddress(pa);
        }
        
        return patient;
    }
    
    /**
     * Called prior to form display. Allows for data to be put in the request to be used in the view
     * 
     * @see org.springframework.web.servlet.mvc.SimpleFormController#referenceData(javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected Map<String, Object> referenceData(final HttpServletRequest request) throws Exception {
        
        final Map<String, Object> map = new HashMap<String, Object>();
        
        // the list of identifiers to display
        // this is a hashset so that the comparison is one with .equals() instead of .compareTo
        final Set<PatientIdentifier> identifiers = new HashSet<PatientIdentifier>();
        
        Patient patient = null;
        String causeOfDeathOther = "";
        
        if (Context.isAuthenticated()) {
            final PatientService ps = Context.getPatientService();
            final String patientId = request.getParameter("patientId");
            if ((patientId != null) && !patientId.equals("")) {
                
                // our current patient
                patient = ps.getPatient(Integer.valueOf(patientId));
                
                if (patient != null) {
                    
                    // only show non-voided identifiers
                    identifiers.addAll(patient.getActiveIdentifiers());
                    // get 'other' cause of death
                    final String propCause = Context.getAdministrationService().getGlobalProperty("concept.causeOfDeath");
                    final Concept conceptCause = Context.getConceptService().getConcept(propCause);
                    if ((conceptCause != null) && (patient.getPatientId() != null)) {
                        final List<Obs> obssDeath = Context.getObsService().getObservationsByPersonAndConcept(patient,
                            conceptCause);
                        
                        if (obssDeath.size() == 1) {
                            final Obs obsDeath = obssDeath.iterator().next();
                            causeOfDeathOther = obsDeath.getValueText();
                            if (causeOfDeathOther == null) {
                                this.log.debug("cod is null, so setting to empty string");
                                causeOfDeathOther = "";
                            } else {
                                this.log.debug("cod is valid: " + causeOfDeathOther);
                            }
                        } else {
                            this.log.debug("obssDeath is wrong size: " + obssDeath.size());
                        }
                    } else {
                        this.log.debug("No concept cause found");
                    }
                    // end get 'other' cause of death
                }
            }
            
            // set up the property for the relationships
            
            // {'3a':Relationship#234, '7b':Relationship#9488}
            final Map<String, Relationship> relationships = getRelationshipsMap(patient, request);
            map.put("relationships", relationships);
        }
        
        // give them both the just-entered identifiers and the patient's current identifiers
        for (final PatientIdentifier identifier : this.newIdentifiers) {
            // add the patient object to the new identifier list so
            // that the .equals method works correctly in the next loop
            identifier.setPatient(patient);
        }
        
        if (this.pref.length() > 0) {
            for (final PatientIdentifier pi : identifiers) {
                pi.setPreferred(this.pref.equals(pi.getIdentifier() + pi.getIdentifierType().getPatientIdentifierTypeId()));
            }
        }
        
        if (Context.isAuthenticated()) {
            map.put("defaultLocation",
                Context.getAuthenticatedUser().getUserProperty(OpenmrsConstants.USER_PROPERTY_DEFAULT_LOCATION));
        }
        map.put("identifiers", identifiers);
        map.put("causeOfDeathOther", causeOfDeathOther);
        
        return map;
    }
    
    /**
     * Convenience method to fetch the relationships to display on the page. First the database is
     * queried for the user demanded relationships to show on the new patient form. @see
     * OpenmrsConstants#GLOBAL_PROPERTY_NEWPATIENTFORM_RELATIONSHIPS Each 3a, 6b relationship
     * defined there is pulled from the db and put into the map. If one doesn't exist in the db yet,
     * a relationship stub is created. If '3a' or '6b' exist as parameters in the given
     * <code>request</code>, that parameter value is put into the returned map
     * 
     * @param person The person to match against
     * @param request the current request with or without 3a-named parameters in it
     * @return Map from relation string to defined relationship object {'3a':obj, '7b':obj}
     */
    private Map<String, Relationship> getRelationshipsMap(final Person person, final HttpServletRequest request) {
        final Map<String, Relationship> relationshipMap = new LinkedHashMap<String, Relationship>();
        
        // gp is in the form "3a, 7b, 4a"
        String relationshipsString = Context.getAdministrationService().getGlobalProperty(
            OpenmrsConstants.GLOBAL_PROPERTY_NEWPATIENTFORM_RELATIONSHIPS, "");
        relationshipsString = relationshipsString.trim();
        if (relationshipsString.length() > 0) {
            final String[] showRelations = relationshipsString.split(",");
            // iterate over strings like "3a"
            for (String showRelation : showRelations) {
                showRelation = showRelation.trim();
                
                boolean aIsToB = true;
                if (showRelation.endsWith("b")) {
                    aIsToB = false;
                }
                
                // trim out the trailing a or b char
                String showRelationId = showRelation.replace("a", "");
                showRelationId = showRelationId.replace("b", "");
                
                final RelationshipType relationshipType = Context.getPersonService().getRelationshipType(
                    Integer.valueOf(showRelationId));
                
                // flag to know if we need to create a stub relationship
                boolean relationshipFound = false;
                
                if ((person != null) && (person.getPersonId() != null)) {
                    if (aIsToB) {
                        final List<Relationship> relationships = Context.getPersonService().getRelationships(null, person,
                            relationshipType);
                        if (relationships.size() > 0) {
                            relationshipMap.put(showRelation, relationships.get(0));
                            relationshipFound = true;
                        }
                    } else {
                        final List<Relationship> relationships = Context.getPersonService().getRelationships(person, null,
                            relationshipType);
                        if (relationships.size() > 0) {
                            relationshipMap.put(showRelation, relationships.get(0));
                            relationshipFound = true;
                        }
                    }
                }
                
                // if no relationship was found, create a stub one now
                if (relationshipFound == false) {
                    final Relationship relationshipStub = new Relationship();
                    relationshipStub.setRelationshipType(relationshipType);
                    if (aIsToB) {
                        relationshipStub.setPersonB(person);
                    } else {
                        relationshipStub.setPersonA(person);
                    }
                    
                    relationshipMap.put(showRelation, relationshipStub);
                }
                
                // check the request to see if a parameter exists in there
                // that matches to the user desired relation.  Overwrite
                // any previous data if found
                final String submittedPersonId = request.getParameter(showRelation);
                if ((submittedPersonId != null) && (submittedPersonId.length() > 0)) {
                    final Person submittedPerson = Context.getPersonService().getPerson(Integer.valueOf(submittedPersonId));
                    if (aIsToB) {
                        relationshipMap.get(showRelation).setPersonA(submittedPerson);
                    } else {
                        relationshipMap.get(showRelation).setPersonB(submittedPerson);
                    }
                }
                
            }
            
        }
        
        return relationshipMap;
    }
    
}
