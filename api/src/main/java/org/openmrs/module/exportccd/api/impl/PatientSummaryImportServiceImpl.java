/**
 * Copyright 2007 Regenstrief Institute, Inc. All Rights Reserved.
 */
package org.openmrs.module.exportccd.api.impl;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.emf.common.util.EList;
import org.openhealthtools.mdht.uml.cda.AssignedEntity;
import org.openhealthtools.mdht.uml.cda.CDAFactory;
import org.openhealthtools.mdht.uml.cda.ClinicalDocument;
import org.openhealthtools.mdht.uml.cda.Entry;
import org.openhealthtools.mdht.uml.cda.Participant2;
import org.openhealthtools.mdht.uml.cda.ParticipantRole;
import org.openhealthtools.mdht.uml.cda.Performer2;
import org.openhealthtools.mdht.uml.cda.PlayingEntity;
import org.openhealthtools.mdht.uml.cda.StrucDocText;
import org.openhealthtools.mdht.uml.cda.ccd.CCDFactory;
import org.openhealthtools.mdht.uml.cda.ccd.CCDPackage;
import org.openhealthtools.mdht.uml.cda.ccd.ContinuityOfCareDocument;
import org.openhealthtools.mdht.uml.cda.ccd.EncountersSection;
import org.openhealthtools.mdht.uml.cda.util.CDAUtil;
import org.openhealthtools.mdht.uml.hl7.datatypes.CD;
import org.openhealthtools.mdht.uml.hl7.datatypes.CE;
import org.openhealthtools.mdht.uml.hl7.datatypes.DatatypesFactory;
import org.openhealthtools.mdht.uml.hl7.datatypes.ED;
import org.openhealthtools.mdht.uml.hl7.datatypes.II;
import org.openhealthtools.mdht.uml.hl7.datatypes.IVL_TS;
import org.openhealthtools.mdht.uml.hl7.datatypes.PN;
import org.openhealthtools.mdht.uml.hl7.datatypes.ST;
import org.openhealthtools.mdht.uml.hl7.vocab.ActClass;
import org.openhealthtools.mdht.uml.hl7.vocab.EntityClassRoot;
import org.openhealthtools.mdht.uml.hl7.vocab.NullFlavor;
import org.openhealthtools.mdht.uml.hl7.vocab.ParticipationType;
import org.openhealthtools.mdht.uml.hl7.vocab.RoleClassRoot;
import org.openhealthtools.mdht.uml.hl7.vocab.x_ActRelationshipEntry;
import org.openhealthtools.mdht.uml.hl7.vocab.x_DocumentEncounterMood;
import org.openhealthtools.mdht.uml.cda.PatientRole;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;
import org.openmrs.User;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.exportccd.ImportedCCD;
import org.openmrs.module.exportccd.api.PatientSummaryImportService;
import org.openmrs.module.exportccd.api.db.ImportedCCDDAO;
import org.openmrs.module.exportccd.api.db.PatientSummaryExportDAO;

/**
 * Class to implement processing CCD and updating patient information in OpenMRS database
 * 
 * @author hxiao
 */

public class PatientSummaryImportServiceImpl extends BaseOpenmrsService implements PatientSummaryImportService 
{   
	static final String ROOT_SSN = "2.16.840.1.113883.4.1";
	static final String ATTRIBUTE_NAME_SSN = "Social Security Number";
	static final String ATTRIBUTE_NAME_RACE = "Race Code";
	static final String ATTRIBUTE_NAME_TELEPHONE = "Telephone";
	static final String ATTRIBUTE_NAME_MARRIED = "Marrital Status";
	
    private static Log log = LogFactory.getLog(PatientSummaryImportServiceImpl.class);
    static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	private ImportedCCDDAO dao;
    
	
	public Patient consumeCCD(InputStream is) throws Exception {	
		CCDPackage.eINSTANCE.eClass();
		//ContinuityOfCareDocument ccdDocument = (ContinuityOfCareDocument) CDAUtil.load(is);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		org.apache.commons.io.IOUtils.copy(is, baos);
		byte[] bytes = baos.toByteArray();
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		
		ClinicalDocument ccdDocument = CDAUtil.load(bais);
		
		//create OpenMRS Patient
		org.openhealthtools.mdht.uml.cda.Patient ohtPatient = ccdDocument.getPatients().get(0);
		Patient omrsPatient = createOrUpdateOmrsPatient(ohtPatient);
		
		//create OpenMRS encounters
		createOrUpdateEncounters((ContinuityOfCareDocument) ccdDocument, omrsPatient);
		
		bais.reset();		
		saveCCD(bais, omrsPatient);
		//String name = pat.getNames().get(0).getGivens().get(0).getText() + " " + pat.getNames().get(0).getFamilies().get(0).getText();		
		//assertNotNull(name);
		return omrsPatient;
	}
	
    private void saveCCD(InputStream is, Patient omrsPatient) throws Exception {
    	StringBuilder sb = new StringBuilder();
    	try{
        	//read it with BufferedReader
        	BufferedReader br
            	= new BufferedReader(
            		new InputStreamReader(is));
     
     
        	String line;
        	while ((line = br.readLine()) != null) {
        		sb.append(line);
        	}   
    	} catch(IOException e) {
    		log.error("Failed to save CCD to database due to " + e.getMessage(), e);
    		throw e;
    	}
    	ImportedCCD ccd = new ImportedCCD();
    	ccd.setImportedFor(omrsPatient);
    	ccd.setCcdImported(sb.toString());
    	ccd.setImportedBy(Context.getAuthenticatedUser());
    	ccd.setDateImported(new Date());
		dao.saveImportedCCD(ccd);		
	}

	protected Patient createOrUpdateOmrsPatient(org.openhealthtools.mdht.uml.cda.Patient ohtPatient) throws Exception {		    	
		Patient inputPatient = convertToOmrsPatient(ohtPatient);		
		Patient exsitingPatient = findPatientFromDatabase(inputPatient);		
		if (exsitingPatient == null) {
			exsitingPatient = savePatient(inputPatient);
		} else {
			//updatePatient(exsitingPatient, inputPatient);
		}
					
		return exsitingPatient;
    }
    
	private void  createOrUpdateEncounters(ContinuityOfCareDocument ccd, Patient patient) throws Exception
	{
		org.openhealthtools.mdht.uml.cda.Encounter e = ccd.getEncountersSection().getEncounters().get(0);
		Encounter enc = new Encounter();
		enc.setPatient(patient);
		enc.setCreator(Context.getAuthenticatedUser());
		enc.setDateCreated(new Date());
		enc.setEncounterDatetime(sdf.parse(e.getEffectiveTime().getValue()));
		//enc.setEncounterId(e.getIds().get(0).getAssigningAuthorityName()+e.getIds().get(0).getExtension());
		
		//encounter type
		EncounterType et = new EncounterType();
		et.setCreator(Context.getAuthenticatedUser());
		et.setDateCreated(new Date());
		et.setName(e.getTypeId().getExtension());
		et.setDescription(e.getTypeId().getAssigningAuthorityName());		
		enc.setEncounterType(et);

		//encounter location
		Location location = new Location();
		location.setName(e.getIds().get(1).getExtension());
		location.setDescription(e.getIds().get(1).getRoot());		
		enc.setLocation(location);
		
		//encounter participants
		Person pers = new Person();
		PersonName pname = new PersonName();
		PN pn = e.getPerformers().get(0).getAssignedEntity().getAssignedPerson().getNames().get(0);
		pname.setFamilyName(pn.getFamilies().get(0).getText());
		pname.setGivenName(pn.getGivens().get(0).getText());
		//pname.setMiddleName(pn.getGivens().get(1).getText())
		pers.addName(pname);
		enc.setProvider(pers);
		
		//save all
		Context.getPersonService().savePerson(pers);
		Context.getLocationService().saveLocation(location);
		Context.getEncounterService().saveEncounterType(et);
		Context.getEncounterService().saveEncounter(enc);
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
		
		PatientRole patRole = (PatientRole) ohtPatient.eContainer();

		//set patient SSN
		PersonAttributeType ssnType = personService.getPersonAttributeTypeByName(ATTRIBUTE_NAME_SSN);
        if(ssnType != null && !ssnType.isRetired()) {		
			EList<II> ids = patRole.getIds();
			String ssn = null;
			for (II id : ids) {
				if(ROOT_SSN.equals(id.getRoot())) {
					ssn = id.getExtension();
				}
			}
			PersonAttribute ssnAttr = new PersonAttribute();
			ssnAttr.setAttributeType(ssnType);
			ssnAttr.setValue(ssn);
			pat.addAttribute(ssnAttr);
        }
		//set patient DOB
		String dobStr = ohtPatient.getBirthTime().getValue();	
		try{
			Date birthdate = sdf.parse(dobStr);
			pat.setBirthdate(birthdate);
		} catch(ParseException e) {
			log.error("Unable to parse date of birth string: " + dobStr, e);
		}
		
		//set patient Home Address
		String street1 = (patRole.getAddrs()==null || patRole.getAddrs().isEmpty() || patRole.getAddrs().get(0).getStreetAddressLines().size()<1 )? null : patRole.getAddrs().get(0).getStreetAddressLines().get(0).getText();
		String street2 = (patRole.getAddrs()==null || patRole.getAddrs().isEmpty() || patRole.getAddrs().get(0).getStreetAddressLines().size() <= 1)? null : patRole.getAddrs().get(0).getStreetAddressLines().get(1).getText();
		String city = (patRole.getAddrs()==null || patRole.getAddrs().isEmpty() || patRole.getAddrs().get(0).getCities()==null || patRole.getAddrs().get(0).getCities().isEmpty())? null : patRole.getAddrs().get(0).getCities().get(0).getText();
		String state = (patRole.getAddrs()==null || patRole.getAddrs().isEmpty() || patRole.getAddrs().get(0).getStates()==null || patRole.getAddrs().get(0).getStates().isEmpty())? null : patRole.getAddrs().get(0).getStates().get(0).getText();
		String country = (patRole.getAddrs()==null || patRole.getAddrs().isEmpty() || patRole.getAddrs().get(0).getCounties() == null || patRole.getAddrs().get(0).getCounties().isEmpty())? null : patRole.getAddrs().get(0).getCounties().get(0).getText();
		String zip = (patRole.getAddrs()==null || patRole.getAddrs().isEmpty() || patRole.getAddrs().get(0).getPostalCodes()==null || patRole.getAddrs().get(0).getPostalCodes().isEmpty())? null : patRole.getAddrs().get(0).getPostalCodes().get(0).getText();
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
        PersonAttributeType raceType = personService.getPersonAttributeTypeByName(ATTRIBUTE_NAME_RACE);
        if(raceType != null && !raceType.isRetired()) {
			String race = ohtPatient.getRaceCode() == null? null : ohtPatient.getRaceCode().getDisplayName();
			PersonAttribute raceAttr = new PersonAttribute();
			raceAttr.setAttributeType(raceType);
			raceAttr.setValue(race);
			pat.addAttribute(raceAttr);
        }
		
		//set patient telephone numbers
        PersonAttributeType phoneType = personService.getPersonAttributeTypeByName(ATTRIBUTE_NAME_TELEPHONE);
        if(phoneType != null && !phoneType.isRetired()) {
			String telephone = (patRole.getTelecoms()==null || patRole.getTelecoms().isEmpty()) ? null : patRole.getTelecoms().get(0).getValue();
			PersonAttribute phoneAttr = new PersonAttribute();
			phoneAttr.setAttributeType(phoneType);
			phoneAttr.setValue(telephone);
			pat.addAttribute(phoneAttr);
        }
        
		//set patient marital status
        PersonAttributeType marryType = personService.getPersonAttributeTypeByName(ATTRIBUTE_NAME_MARRIED);
        if(marryType != null && !marryType.isRetired()) {
			String marrital = ohtPatient.getMaritalStatusCode().getDisplayName();
			PersonAttribute marriedAttr = new PersonAttribute();
			marriedAttr.setAttributeType(marryType);
			marriedAttr.setValue(marrital);
			pat.addAttribute(marriedAttr);
		}
			
		return pat;
	}
    
	private Patient findPatientFromDatabase(Patient inputPatient) {
		PatientService patientService = Context.getPatientService();
		
		//Patient pat = patientService.getPatientByExample(inputPtient); 
		List<Patient> pats = patientService.getPatients(inputPatient.getGivenName()+ " " + inputPatient.getFamilyName());
		
		Patient patFound = null;
		String inputPatString = inputPatient.getGivenName()+ " " + inputPatient.getMiddleName() + " " + inputPatient.getFamilyName() + " " + inputPatient.getGender() +  " " + inputPatient.getBirthdate().getTime(); 
		for(Patient pat : pats) {
			String patString = pat.getGivenName()+ " " + pat.getMiddleName() + " " + pat.getFamilyName() +  " " + pat.getGender() +  " " + pat.getBirthdate().getTime();
			if(inputPatString.equalsIgnoreCase(patString)) {
				patFound = pat;
				break;
			}
		}
		
	    log.debug("found patient=" + patFound);
		return patFound;
	}
	
    private Patient updatePatient(Patient existingPatient, Patient inputPatient) {
		existingPatient.setAddresses(inputPatient.getAddresses());
		existingPatient.setAttributes(inputPatient.getAttributes());
		existingPatient.setChangedBy(Context.getAuthenticatedUser());
		existingPatient.setDateChanged(new Date());
		//Patient pat = patientService.savePatient(existingPatient);
	    log.debug("updated patient=" + existingPatient);
		return existingPatient;
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

	public ImportedCCDDAO getDao() {
		return dao;
	}
	
	public void setDao(ImportedCCDDAO dao) {
		this.dao = dao;
	}

	@Override
	public ImportedCCD getCCD(Patient pat) throws Exception {
		// TODO Auto-generated method stub
		return dao.getImportedCCD(pat);
	}  
}
