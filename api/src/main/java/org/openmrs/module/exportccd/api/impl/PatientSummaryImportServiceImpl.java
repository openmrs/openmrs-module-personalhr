/**
 * Copyright 2007 Regenstrief Institute, Inc. All Rights Reserved.
 */
package org.openmrs.module.exportccd.api.impl;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.emf.common.util.EList;
import org.openhealthtools.mdht.uml.cda.ClinicalDocument;
import org.openhealthtools.mdht.uml.cda.ccd.CCDPackage;
import org.openhealthtools.mdht.uml.cda.util.CDAUtil;
import org.openhealthtools.mdht.uml.hl7.datatypes.II;
import org.openhealthtools.mdht.uml.cda.PatientRole;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonName;
import org.openmrs.User;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.exportccd.api.PatientSummaryImportService;

/**
 * Class to implement processing CCD and updating patient information in OpenMRS database
 * 
 * @author hxiao
 */

public class PatientSummaryImportServiceImpl extends BaseOpenmrsService implements PatientSummaryImportService 
{   
	static final String ROOT_SSN = "2.16.840.1.113883.4.1";
	static final String ATTRIBUTE_NAME_SSN = "Social Security Number";
	static final String ATTRIBUTE_NAME_RACE = "Race";
	static final String ATTRIBUTE_NAME_TELEPHONE = "Telephone";
	static final String ATTRIBUTE_NAME_MARRIED = "Marrital Status";
	
    private static Log log = LogFactory.getLog(PatientSummaryImportServiceImpl.class);
    static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    
	
	public Patient consumeCCD(InputStream is) throws Exception {	
		CCDPackage.eINSTANCE.eClass();
		//ContinuityOfCareDocument ccdDocument = (ContinuityOfCareDocument) CDAUtil.load(is);
		ClinicalDocument ccdDocument = CDAUtil.load(is);
		
		org.openhealthtools.mdht.uml.cda.Patient ohtPatient = ccdDocument.getPatients().get(0);
		Patient omrsPatient = createOrUpdateOmrsPatient(ohtPatient);
		//String name = pat.getNames().get(0).getGivens().get(0).getText() + " " + pat.getNames().get(0).getFamilies().get(0).getText();		
		//assertNotNull(name);
		return omrsPatient;
	}
	
    protected Patient createOrUpdateOmrsPatient(org.openhealthtools.mdht.uml.cda.Patient ohtPatient) throws Exception {		    	
		Patient inputPatient = convertToOmrsPatient(ohtPatient);		
		Patient exsitingPatient = findPatientFromDatabase(inputPatient);		
		if (exsitingPatient == null) {
			exsitingPatient = savePatient(inputPatient);
		} else {
			updatePatient(exsitingPatient, inputPatient);
		}
					
		return exsitingPatient;
    }
    
	private Patient convertToOmrsPatient(org.openhealthtools.mdht.uml.cda.Patient ohtPatient) {
		Patient pat = new Patient();
	    PersonService personService = Context.getPersonService();
	    //PatientService patientService = Context.getPatientService();
		
		//set patient names		
		String familyName = (ohtPatient.getNames().isEmpty() || ohtPatient.getNames().get(0).getFamilies().isEmpty())? null : ohtPatient.getNames().get(0).getFamilies().get(0).getText();		
		String givenName = (ohtPatient.getNames().isEmpty() || ohtPatient.getNames().get(0).getGivens().isEmpty())? null : ohtPatient.getNames().get(0).getGivens().get(0).getText();
		String middleName = (ohtPatient.getNames().isEmpty() || ohtPatient.getNames().get(0).getGivens().size() <= 1) ? null : ohtPatient.getNames().get(0).getGivens().get(1).getText();
		PersonName pn = new PersonName();
		pn.setFamilyName(familyName);
		pn.setGivenName(givenName);
		pn.setMiddleName(middleName);
		pat.addName(pn);
		
		//set patient sex
		String sex = ohtPatient.getAdministrativeGenderCode().getDisplayName();
		pat.setGender(sex);
		
		//set patient SSN
		PatientRole patRole = (PatientRole) ohtPatient.eContainer();
		EList<II> ids = patRole.getIds();
		String ssn = null;
		for (II id : ids) {
			if(ROOT_SSN.equals(id.getRoot())) {
				ssn = id.getExtension();
			}
		}
		PersonAttribute ssnAttr = new PersonAttribute();
		ssnAttr.setAttributeType(personService.getPersonAttributeTypeByName(ATTRIBUTE_NAME_SSN));
		ssnAttr.setValue(ssn);
		pat.addAttribute(ssnAttr);
		
		//set patient DOB
		String dobStr = ohtPatient.getBirthTime().getValue();	
		try{
			Date birthdate = sdf.parse(dobStr);
			pat.setBirthdate(birthdate);
		} catch(ParseException e) {
			log.error("Unable to parse date of birth string: " + dobStr, e);
		}
		
		//set patient Home Address
		String street1 = (patRole.getAddrs()==null || patRole.getAddrs().get(0).getStreetAddressLines().size()<1 )? null : patRole.getAddrs().get(0).getStreetAddressLines().get(0).getText();
		String street2 = (patRole.getAddrs()==null || patRole.getAddrs().get(0).getStreetAddressLines().size() <= 1)? null : patRole.getAddrs().get(0).getStreetAddressLines().get(1).getText();
		String city = (patRole.getAddrs()==null || patRole.getAddrs().get(0).getCities()==null || patRole.getAddrs().get(0).getCities().isEmpty())? null : patRole.getAddrs().get(0).getCities().get(0).getText();
		String state = (patRole.getAddrs()==null || patRole.getAddrs().get(0).getStates()==null || patRole.getAddrs().get(0).getStates().isEmpty())? null : patRole.getAddrs().get(0).getStates().get(0).getText();
		String country = (patRole.getAddrs()==null || patRole.getAddrs().get(0).getCounties() == null || patRole.getAddrs().get(0).getCounties().isEmpty())? null : patRole.getAddrs().get(0).getCounties().get(0).getText();
		String zip = (patRole.getAddrs()==null || patRole.getAddrs().get(0).getPostalCodes()==null || patRole.getAddrs().get(0).getPostalCodes().isEmpty())? null : patRole.getAddrs().get(0).getPostalCodes().get(0).getText();
        PersonAddress address = new PersonAddress();
        User ncdUser = Context.getUserContext().getAuthenticatedUser();
        Date now = new Date();
        address.setCreator(ncdUser);
        address.setDateCreated(now);
        address.setAddress1(street1);
        address.setAddress2(street2);
        address.setCityVillage(city);
        address.setCountry(country);
        address.setPostalCode(zip);
        address.setStateProvince(state);        
        address.setPerson(pat);
        pat.addAddress(address);	
        
		//get patient race
        if(personService.getPersonAttributeTypeByName(ATTRIBUTE_NAME_RACE) != null && !personService.getPersonAttributeTypeByName(ATTRIBUTE_NAME_RACE).isRetired()) {
			String race = ohtPatient.getRaceCode() == null? null : ohtPatient.getRaceCode().getDisplayName();
			PersonAttribute raceAttr = new PersonAttribute();
			raceAttr.setAttributeType(personService.getPersonAttributeTypeByName(ATTRIBUTE_NAME_RACE));
			raceAttr.setValue(race);
			pat.addAttribute(raceAttr);
        }
		
		//set patient telephone numbers
        if(personService.getPersonAttributeTypeByName(ATTRIBUTE_NAME_TELEPHONE) != null && !personService.getPersonAttributeTypeByName(ATTRIBUTE_NAME_TELEPHONE).isRetired()) {
			String telephone = (patRole.getTelecoms()==null || patRole.getTelecoms().isEmpty()) ? null : patRole.getTelecoms().get(0).getValue();
			PersonAttribute phoneAttr = new PersonAttribute();
			phoneAttr.setAttributeType(personService.getPersonAttributeTypeByName(ATTRIBUTE_NAME_TELEPHONE));
			phoneAttr.setValue(telephone);
			pat.addAttribute(phoneAttr);
        }
        
		//set patient marital status
        if(personService.getPersonAttributeTypeByName(ATTRIBUTE_NAME_MARRIED) != null && !personService.getPersonAttributeTypeByName(ATTRIBUTE_NAME_MARRIED).isRetired()) {
			String marrital = ohtPatient.getMaritalStatusCode().getDisplayName();
			PersonAttribute marriedAttr = new PersonAttribute();
			marriedAttr.setAttributeType(personService.getPersonAttributeTypeByName(ATTRIBUTE_NAME_MARRIED));
			marriedAttr.setValue(marrital);
			pat.addAttribute(marriedAttr);
		}
			
		return pat;
	}
    
	private Patient findPatientFromDatabase(Patient inputPtient) {
		PatientService patientService = Context.getPatientService();
		
		Patient pat = patientService.getPatientByExample(inputPtient);
		
	    log.debug("found patient=" + pat);
		return pat;
	}
	
    private Patient updatePatient(Patient existingPatient, Patient inputPatient) {
		PatientService patientService = Context.getPatientService();
		existingPatient.setAddresses(inputPatient.getAddresses());
		existingPatient.setAttributes(inputPatient.getAttributes());
		existingPatient.setChangedBy(Context.getAuthenticatedUser());
		existingPatient.setDateChanged(new Date());
		Patient pat = patientService.savePatient(existingPatient);
	    log.debug("updated patient=" + pat);
		return pat;
	}


   private Patient savePatient(Patient inputPatient) {

       //PersonService personService = Context.getPersonService();
       PatientService patientService = Context.getPatientService();

       // Find the "OpenMRS Identification Number" type
       PatientIdentifierType patientIdType = 
           patientService.getPatientIdentifierTypeByName("OpenMRS Identification Number");

       // Find the "unknown location" location
       Location unknownLocation = Context.getLocationService().getLocation(1);
       
       // Add it. The mrngen module wraps PatientService.savePatient
       // and replaces the identifier value.
       PatientIdentifier patientId = new PatientIdentifier();
       User ncdUser = Context.getUserContext().getAuthenticatedUser();
       Date now = new Date();
       patientId.setCreator(ncdUser);
       patientId.setDateCreated(now);
       patientId.setIdentifierType(patientIdType);
       patientId.setIdentifier("TEMPID_WILL_BE_REPLACED");
       patientId.setLocation(unknownLocation);
       inputPatient.addIdentifier(patientId);
       
       Patient pat = patientService.savePatient(inputPatient);
       
       log.debug("saved patient=" + pat);
       return pat;
   }  
}
