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
package org.openmrs.module.exportccd.api.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.crypto.Data;

import org.openmrs.activelist.Allergy;
import org.openmrs.activelist.Problem;
import org.openmrs.api.db.DAOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openhealthtools.mdht.uml.cda.Act;
import org.openhealthtools.mdht.uml.cda.AssignedAuthor;
import org.openhealthtools.mdht.uml.cda.AssignedCustodian;
import org.openhealthtools.mdht.uml.cda.AssignedEntity;
import org.openhealthtools.mdht.uml.cda.AssociatedEntity;
import org.openhealthtools.mdht.uml.cda.AuthoringDevice;
import org.openhealthtools.mdht.uml.cda.Component4;
import org.openhealthtools.mdht.uml.cda.Consumable;
import org.openhealthtools.mdht.uml.cda.Custodian;
import org.openhealthtools.mdht.uml.cda.Author;
import org.openhealthtools.mdht.uml.cda.CDAFactory;
import org.openhealthtools.mdht.uml.cda.CustodianOrganization;
import org.openhealthtools.mdht.uml.cda.Entry;
import org.openhealthtools.mdht.uml.cda.EntryRelationship;
import org.openhealthtools.mdht.uml.cda.InfrastructureRootTypeId;
import org.openhealthtools.mdht.uml.cda.ManufacturedProduct;
import org.openhealthtools.mdht.uml.cda.Material;
import org.openhealthtools.mdht.uml.cda.Observation;
import org.openhealthtools.mdht.uml.cda.ObservationRange;
import org.openhealthtools.mdht.uml.cda.Organization;
import org.openhealthtools.mdht.uml.cda.Organizer;
import org.openhealthtools.mdht.uml.cda.Participant1;
import org.openhealthtools.mdht.uml.cda.Participant2;
import org.openhealthtools.mdht.uml.cda.ParticipantRole;
import org.openhealthtools.mdht.uml.cda.PatientRole;
import org.openhealthtools.mdht.uml.cda.Performer2;
import org.openhealthtools.mdht.uml.cda.Person;
import org.openhealthtools.mdht.uml.cda.PlayingEntity;
import org.openhealthtools.mdht.uml.cda.ReferenceRange;
import org.openhealthtools.mdht.uml.cda.RelatedSubject;
import org.openhealthtools.mdht.uml.cda.StrucDocText;
import org.openhealthtools.mdht.uml.cda.Subject;
import org.openhealthtools.mdht.uml.cda.SubjectPerson;
import org.openhealthtools.mdht.uml.cda.SubstanceAdministration;
import org.openhealthtools.mdht.uml.cda.ccd.AlertsSection;
import org.openhealthtools.mdht.uml.cda.ccd.CCDFactory;
import org.openhealthtools.mdht.uml.cda.ccd.ContinuityOfCareDocument;
import org.openhealthtools.mdht.uml.cda.ccd.EncountersActivity;
import org.openhealthtools.mdht.uml.cda.ccd.EncountersSection;
import org.openhealthtools.mdht.uml.cda.ccd.FamilyHistorySection;
import org.openhealthtools.mdht.uml.cda.ccd.MedicationSection;
import org.openhealthtools.mdht.uml.cda.ccd.MedicationsSection;
import org.openhealthtools.mdht.uml.cda.ccd.PlanOfCareSection;
import org.openhealthtools.mdht.uml.cda.ccd.ProblemSection;
import org.openhealthtools.mdht.uml.cda.ccd.PurposeActivity;
import org.openhealthtools.mdht.uml.cda.ccd.PurposeSection;
import org.openhealthtools.mdht.uml.cda.ccd.ResultsSection;
import org.openhealthtools.mdht.uml.cda.ccd.SocialHistorySection;
import org.openhealthtools.mdht.uml.cda.ccd.VitalSignsSection;
//import org.openhealthtools.mdht.uml.cda.ihe.EncounterActivity;
import org.openhealthtools.mdht.uml.cda.util.CDAUtil;
import org.openhealthtools.mdht.uml.hl7.datatypes.AD;
import org.openhealthtools.mdht.uml.hl7.datatypes.ANY;
import org.openhealthtools.mdht.uml.hl7.datatypes.CD;
import org.openhealthtools.mdht.uml.hl7.datatypes.CE;
import org.openhealthtools.mdht.uml.hl7.datatypes.CR;
import org.openhealthtools.mdht.uml.hl7.datatypes.CS;
import org.openhealthtools.mdht.uml.hl7.datatypes.CV;
import org.openhealthtools.mdht.uml.hl7.datatypes.DatatypesFactory;
import org.openhealthtools.mdht.uml.hl7.datatypes.ED;
import org.openhealthtools.mdht.uml.hl7.datatypes.II;
import org.openhealthtools.mdht.uml.hl7.datatypes.INT;
import org.openhealthtools.mdht.uml.hl7.datatypes.IVL_PQ;
import org.openhealthtools.mdht.uml.hl7.datatypes.IVL_TS;
import org.openhealthtools.mdht.uml.hl7.datatypes.IVXB_TS;
import org.openhealthtools.mdht.uml.hl7.datatypes.ON;
import org.openhealthtools.mdht.uml.hl7.datatypes.PIVL_TS;
import org.openhealthtools.mdht.uml.hl7.datatypes.PN;
import org.openhealthtools.mdht.uml.hl7.datatypes.PQ;
import org.openhealthtools.mdht.uml.hl7.datatypes.SC;
import org.openhealthtools.mdht.uml.hl7.datatypes.ST;
import org.openhealthtools.mdht.uml.hl7.datatypes.SXCM_TS;
import org.openhealthtools.mdht.uml.hl7.datatypes.TEL;
import org.openhealthtools.mdht.uml.hl7.datatypes.TS;
import org.openhealthtools.mdht.uml.hl7.vocab.ActClass;
import org.openhealthtools.mdht.uml.hl7.vocab.ActClassObservation;
import org.openhealthtools.mdht.uml.hl7.vocab.ActMood;
import org.openhealthtools.mdht.uml.hl7.vocab.EntityClassManufacturedMaterial;
import org.openhealthtools.mdht.uml.hl7.vocab.EntityClassRoot;
import org.openhealthtools.mdht.uml.hl7.vocab.NullFlavor;
import org.openhealthtools.mdht.uml.hl7.vocab.ParticipationType;
import org.openhealthtools.mdht.uml.hl7.vocab.RoleClassAssignedEntity;
import org.openhealthtools.mdht.uml.hl7.vocab.RoleClassAssociative;
import org.openhealthtools.mdht.uml.hl7.vocab.RoleClassRoot;
import org.openhealthtools.mdht.uml.hl7.vocab.SetOperator;
import org.openhealthtools.mdht.uml.hl7.vocab.x_ActClassDocumentEntryAct;
import org.openhealthtools.mdht.uml.hl7.vocab.x_ActClassDocumentEntryOrganizer;
import org.openhealthtools.mdht.uml.hl7.vocab.x_ActMoodDocumentObservation;
import org.openhealthtools.mdht.uml.hl7.vocab.x_ActRelationshipEntry;
import org.openhealthtools.mdht.uml.hl7.vocab.x_ActRelationshipEntryRelationship;
import org.openhealthtools.mdht.uml.hl7.vocab.x_DocumentActMood;
import org.openhealthtools.mdht.uml.hl7.vocab.x_DocumentEncounterMood;
import org.openhealthtools.mdht.uml.hl7.vocab.x_DocumentSubject;
import org.openhealthtools.mdht.uml.hl7.vocab.x_DocumentSubstanceMood;
import org.openmrs.Concept;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptNumeric;
import org.openmrs.ConceptSet;
import org.openmrs.DrugOrder;
import org.openmrs.Encounter;
import org.openmrs.EncounterProvider;
import org.openmrs.EncounterRole;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.Provider;
import org.openmrs.Relationship;
import org.openmrs.api.APIException;
import org.openmrs.api.ConceptService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.module.exportccd.CCDSectionEntity;
import org.openmrs.module.exportccd.api.PatientSummaryExportService;
import org.openmrs.module.exportccd.api.db.PatientSummaryExportDAO;
import org.springframework.beans.factory.annotation.Autowired;



/**
 * It is a default implementation of {@link PatientSummaryExportService}.
 */
public class PatientSummaryExportServiceImpl extends BaseOpenmrsService implements PatientSummaryExportService {

	protected final Log log = LogFactory.getLog(this.getClass());

	public static final String vitalSigns = "VitalSigns";

	private PatientSummaryExportDAO dao;



	private CCDSectionEntity entity ;
	
	SimpleDateFormat s= new SimpleDateFormat("yyyyMMdd");

	/**
	 * @param dao the dao to set
	 */
	public void setDao(PatientSummaryExportDAO dao) {
		this.dao = dao;
	}

	/**
	 * @return the dao
	 */
	public PatientSummaryExportDAO getDao() {
		return dao;
	}


	/**
	 * @param dao the dao to set
	 */
	public void setEntity(CCDSectionEntity entity) {
		this.entity = entity;
	}

	/**
	 * @return the dao
	 */
	public CCDSectionEntity getEntity() {
		return entity;
	}

	private CE buildConceptCode(Concept c , String... source)
	{


		Collection<ConceptMap> conceptMap = c.getConceptMappings(); 
		CE codes = DatatypesFactory.eINSTANCE.createCE();
		for(ConceptMap n : conceptMap)
		{

			if(n.getSource().getName().contains("SNOMED"))
			{

				
				codes.setCodeSystem("2.16.840.1.113883.6.96");
				
			}else if(n.getSource().getName().contains("LOINC"))
			{
				
		
					codes.setCodeSystem("2.16.840.1.113883.6.1");
					
				
			}
			else if(n.getSource().getName().contains("RxNorm"))
			{
				
				
					codes.setCodeSystem("2.16.840.1.113883.6.88");
					
				
			}
			else if(n.getSource().getName().contains("C4")||n.getSource().getName().contains("CPT-4"))
			{
				
				
					codes.setCodeSystem("2.16.840.1.113883.6.12");
					
				
			}
			else if(n.getSource().getName().contains("C5")||n.getSource().getName().contains("CPT-5"))
			{
				
				
					codes.setCodeSystem("2.16.840.1.113883.6.82");
					
				
			}
			else if(n.getSource().getName().contains("I9")||n.getSource().getName().contains("ICD9"))
			{
				
				
					codes.setCodeSystem("2.16.840.1.113883.6.42");
					
				
			}
		else if(n.getSource().getName().contains("I10")||n.getSource().getName().contains("ICD10"))
			{
				
				
				codes.setCodeSystem("2.16.840.1.113883.6.3");
				
			
		}
			else if(n.getSource().getName().contains("C2")||n.getSource().getName().contains("CPT-2"))
			{
				
	
					codes.setCodeSystem("2.16.840.1.113883.6.13");
					
				
			}
			else if(n.getSource().getName().contains("FDDX"))
			{
				
	
					codes.setCodeSystem("2.16.840.1.113883.6.63");
					
				
			}
			else if(n.getSource().getName().contains("MEDCIN"))
			{
				
	
					codes.setCodeSystem("2.16.840.1.113883.6.26");
					
				
			}
			codes.setCode(n.getSourceCode());
			codes.setCodeSystemName(n.getSource().getName());
			codes.setDisplayName(n.getConcept().getDisplayString());
			
			
		
			
			
		
		}
		return codes;
	} 

	private CD buildCode(String code , String codeSystem, String displayString, String codeSystemName)
	{
		CD e = DatatypesFactory.eINSTANCE.createCD();
		e.setCode(code);
		e.setCodeSystem(codeSystem);
		e.setDisplayName(displayString);
		e.setCodeSystemName(codeSystemName);
		return e;

	}
	private CE buildCodeCE(String code , String codeSystem, String displayString, String codeSystemName)
	{
		CE e = DatatypesFactory.eINSTANCE.createCE();
		e.setCode(code);
		e.setCodeSystem(codeSystem);
		e.setDisplayName(displayString);
		e.setCodeSystemName(codeSystemName);
		return e;

	}
	private ED buildEDText(String value)
	{
		ED text = DatatypesFactory.eINSTANCE.createED();
		text.addText("<reference value=\""+value+"\"/>");
		return text;
	}

	private   II buildTemplateID(String root , String extension ,String assigningAuthorityName)
	{

		II templateID = DatatypesFactory.eINSTANCE.createII();
		templateID.setAssigningAuthorityName(assigningAuthorityName);
		templateID.setRoot(root);
		templateID.setExtension(extension);
		return templateID;

	}
	private TS  buildEffectiveTime(Date d)
	{
		TS effectiveTime = DatatypesFactory.eINSTANCE.createTS();
		SimpleDateFormat s = new SimpleDateFormat("yyyyMMddhhmmss");
		
		String creationDate = s.format(d);
		String timeOffset = d.getTimezoneOffset()+"";
		
		timeOffset = timeOffset.replace("-", "-0");
		effectiveTime.setValue(creationDate+timeOffset);
		
		return effectiveTime;
	}

	private IVL_TS  buildEffectiveTimeinIVL(Date d , Date d1)
	{
		IVL_TS effectiveTime = DatatypesFactory.eINSTANCE.createIVL_TS();
		
		String creationDate = s.format(d);
		IVXB_TS low = DatatypesFactory.eINSTANCE.createIVXB_TS();
		low.setValue(creationDate);
		effectiveTime.setLow(low);
		IVXB_TS high = DatatypesFactory.eINSTANCE.createIVXB_TS();
		if(d1 != null)
			high.setValue(s.format(d1));
		effectiveTime.setHigh(high);
		return effectiveTime;
	}
	@Override
	public ContinuityOfCareDocument produceCCD(int patientId) {

		ContinuityOfCareDocument ccd = CCDFactory.eINSTANCE.createContinuityOfCareDocument();
		//ccd.init();
		//Document Header 
		//CCD BODY 

		PatientService patientService = Context.getPatientService();
		Patient patient = patientService.getPatient(patientId);
		System.out.println(patient);
		ccd = buildHeader(ccd ,patient);

		ccd=buildAllergies(ccd, patient);

		ccd = buildProblems(ccd, patient);

		ccd = buildMedication(ccd, patient);

		ccd = buildVitalSigns(ccd, patient);
		
		ccd = buildSocialHistory(ccd, patient);
		
		ccd = buildLabResults(ccd, patient);
		
		ccd = buildPlanOfCare(ccd, patient);
		
		ccd = buildFamilyHistory(ccd, patient);


		try {
			CDAUtil.save(ccd,System.out);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}





		return ccd;	


	}



	public boolean saveConceptAsCCDSections(List<Integer> concepts , String category) throws DAOException , APIException
	{
		for(int i = 0 ; i< concepts.size() ; i++)
		{
			entity = new CCDSectionEntity();
			entity.setConcept(Context.getConceptService().getConcept(concepts.get(i)));
			entity.setCategory(category);
			entity.setCcdSectionEntity(concepts.get(i)+category);
			CCDSectionEntity saved  = dao.saveConceptByCategory(entity);

		}

		return true;
	}

	public boolean deleteConceptsByCategory(List<Concept> concepts , String category)
	{
		for (Concept concept : concepts) {

			CCDSectionEntity e = dao.getConceptByCcdSectionEntity(concept.getId() ,category);
			dao.deleteConceptByCategory(e);
		}
		return true;
	}

	public List<Concept> getConceptByCategory(String category)
	{

		List<Concept> concept = dao.getConceptByCategory(category);

		return concept;
	}

	private ContinuityOfCareDocument buildPurposeSummary(ContinuityOfCareDocument ccd)
	{
		PurposeSection purposeSummary = CCDFactory.eINSTANCE.createPurposeSection();


		II purposeSummaryTemplateID = DatatypesFactory.eINSTANCE.createII();
		purposeSummaryTemplateID.setRoot("2.16.840.1.113883.10.20.1.13");
		purposeSummary.getTemplateIds().add(purposeSummaryTemplateID);

		//Snome code for document procedure 
		CE purposeSummaryCode = DatatypesFactory.eINSTANCE.createCE();
		purposeSummaryCode.setCode("48764-5");
		purposeSummaryCode.setCodeSystem("2.16.840.1.113883.6.1");
		purposeSummaryCode.setCodeSystemName("SnomedCt");
		purposeSummaryCode.setDisplayName("SNOMED CT");
		purposeSummary.setCode(purposeSummaryCode);


		ST purposeSummaryTitle = DatatypesFactory.eINSTANCE.createST();
		purposeSummaryTitle.addText("Summary Purpose");
		purposeSummary.setTitle(purposeSummaryTitle);


		StrucDocText sdb = CDAFactory.eINSTANCE.createStrucDocText();
		sdb.addText("Transfer of Care");
		purposeSummary.setText(sdb);
		ccd.addSection(purposeSummary);

		Entry purposeSummaryEntry = CDAFactory.eINSTANCE.createEntry();
		purposeSummary.getEntries().add(purposeSummaryEntry);
		purposeSummaryEntry.setTypeCode(x_ActRelationshipEntry.DRIV);


		Act purposeSummaryAct = CDAFactory.eINSTANCE.createAct();

		purposeSummaryAct.setClassCode(x_ActClassDocumentEntryAct.ACT);
		purposeSummaryAct.setMoodCode(x_DocumentActMood.EVN);
		purposeSummaryEntry.setAct(purposeSummaryAct);

		II purposeSummaryEntryTemplateID = DatatypesFactory.eINSTANCE.createII();
		purposeSummaryEntryTemplateID.setRoot("2.16.840.1.113883.10.20.1.30");
		purposeSummaryAct.getTemplateIds().add(purposeSummaryEntryTemplateID);

		PurposeActivity purposeActivitiy = CCDFactory.eINSTANCE.createPurposeActivity();
		CD purposeActivityCode = DatatypesFactory.eINSTANCE.createCD();
		purposeActivityCode.setCode("23745001");
		purposeActivityCode.setCodeSystem("2.16.840.1.113883.6.9");
		purposeActivityCode.setDisplayName("Documentation Procedure");
		purposeSummaryAct.setCode(purposeActivityCode);

		CS statusCode = DatatypesFactory.eINSTANCE.createCS();
		statusCode.setCode("completed");
		purposeSummaryAct.setStatusCode(statusCode);


		EntryRelationship entryRelationShip = CDAFactory.eINSTANCE.createEntryRelationship();
		entryRelationShip.setTypeCode(x_ActRelationshipEntryRelationship.RSON);
		purposeSummaryAct.getEntryRelationships().add(entryRelationShip);
		Act act2 = CDAFactory.eINSTANCE.createAct();
		act2.setClassCode(x_ActClassDocumentEntryAct.ACT);
		act2.setMoodCode(x_DocumentActMood.EVN);
		entryRelationShip.setAct(act2);

		CE code1 = DatatypesFactory.eINSTANCE.createCE();
		code1.setCode("308292007");
		code1.setCodeSystem("2.16.840.1.113883.6.96");
		code1.setDisplayName("Transfer of care");
		act2.setCode(code1);

		CS statusCode1 = DatatypesFactory.eINSTANCE.createCS();
		statusCode1.setCode("completed");
		act2.setStatusCode(statusCode1);

		purposeSummaryAct.getEntryRelationships().add(entryRelationShip);

		return ccd;

	}
	private   ContinuityOfCareDocument  buildEncounters(ContinuityOfCareDocument ccd, Patient patient)
	{

		EncountersSection encounterSection = CCDFactory.eINSTANCE.createEncountersSection();



		II encounterSectionTemplateID = DatatypesFactory.eINSTANCE.createII();
		encounterSectionTemplateID.setRoot("2.16.840.1.113883.3.88.11.83.127");
		encounterSectionTemplateID.setAssigningAuthorityName("HITSP/C83");
		encounterSection.getTemplateIds().add(encounterSectionTemplateID);

		II encounterSectionTemplateID1 = DatatypesFactory.eINSTANCE.createII();
		encounterSectionTemplateID1.setRoot("1.3.6.1.4.1.19376.1.5.3.1.1.5.3.3");
		encounterSectionTemplateID1.setAssigningAuthorityName("IHE PCC");
		encounterSection.getTemplateIds().add(encounterSectionTemplateID1);

		II encounterSectionTemplateID2 = DatatypesFactory.eINSTANCE.createII();
		encounterSectionTemplateID2.setRoot("2.16.840.1.113883.10.20.1.3");
		encounterSectionTemplateID2.setAssigningAuthorityName("HL7 CCD");
		encounterSection.getTemplateIds().add(encounterSectionTemplateID2);

		//Snome code for document procedure 
		CE encounterSectionCode = DatatypesFactory.eINSTANCE.createCE();
		encounterSectionCode.setCode("46240-8");
		encounterSectionCode.setCodeSystem("2.16.840.1.113883.6.1");
		encounterSectionCode.setCodeSystemName("LOINC");
		encounterSectionCode.setDisplayName("History of encounters");
		encounterSection.setCode(encounterSectionCode);


		ST encounterSectionTitle = DatatypesFactory.eINSTANCE.createST();
		encounterSectionTitle.addText("Encounters");
		encounterSection.setTitle(encounterSectionTitle);




		StringBuffer buffer = new StringBuffer();
		buffer.append("<table border=\"1\" width=\"100%\">");
		buffer.append("<thead>");
		buffer.append("<tr>");
		buffer.append("<th>Encounter Type</th>");
		buffer.append("<th>Clinicial Name</th>");
		buffer.append("<th>Location</th>");
		buffer.append("<th>Date</th>");
		buffer.append("</tr>");
		buffer.append("</thead>");
		buffer.append("<tbody>");

		List<Encounter> encounterList = Context.getEncounterService().getEncountersByPatient(patient);
		List<Entry> encounterEntryList = new ArrayList<Entry>();
		int i=0;
		for(Encounter encounter : encounterList)
		{
			buffer.append("<tr>");
			buffer.append("<td>"+"<content id=\"encounterType"+i+" \">"+encounter.getEncounterType().getName()+"</content></td>");
			Map<EncounterRole, Set<Provider>> encounterProviderMapByRole = encounter.getProvidersByRoles();
			Set<Provider> encounterProviders;
			if (encounterProviderMapByRole.values().iterator().hasNext())
			{
				encounterProviders = encounterProviderMapByRole.values().iterator().next();
				Iterator<Provider> encounterProvideIterator = encounterProviders.iterator();
				if(encounterProvideIterator.hasNext())
				{
					buffer.append("<td>"+encounterProvideIterator.next()+"</td>");
				}
				else
				{
					buffer.append("<td></td>");
				}
				
			}
			buffer.append("<td>"+encounter.getLocation()+"</td>");
			 Date date = encounter.getEncounterDatetime();
			
			 
			buffer.append("<td>"+s.format(date)+"</td>");
			buffer.append("</tr>");
			
			
			//Set of encounters

			Entry encounterEntry = CDAFactory.eINSTANCE.createEntry();
			encounterEntry.setTypeCode(x_ActRelationshipEntry.DRIV);


			org.openhealthtools.mdht.uml.cda.Encounter encounterCCD = CDAFactory.eINSTANCE.createEncounter();
			encounterCCD.setClassCode(ActClass.ENC);
			encounterCCD.setMoodCode(x_DocumentEncounterMood.EVN);
			encounterEntry.setEncounter(encounterCCD);


			II encounterTemplateID = DatatypesFactory.eINSTANCE.createII();
			encounterTemplateID.setRoot("2.16.840.1.113883.3.88.11.83.16");
			encounterTemplateID.setAssigningAuthorityName("HITSP C83");
			encounterCCD.getTemplateIds().add(encounterSectionTemplateID);

			II encounterTemplateID1 = DatatypesFactory.eINSTANCE.createII();
			encounterTemplateID1.setRoot("2.16.840.1.113883.10.20.1.21");
			encounterTemplateID1.setAssigningAuthorityName("CCD");
			encounterCCD.getTemplateIds().add(encounterTemplateID1);

			II encounterTemplateID2 = DatatypesFactory.eINSTANCE.createII();
			encounterTemplateID2.setRoot("1.3.6.1.4.1.19376.1.5.3.1.4.14");
			encounterTemplateID2.setAssigningAuthorityName("IHE PCC");
			encounterCCD.getTemplateIds().add(encounterTemplateID2);




			II encounterID = DatatypesFactory.eINSTANCE.createII();
			encounterID.setRoot(encounter.getUuid());
			encounterCCD.getIds().add(encounterID);

			CD encounterActivityCode = DatatypesFactory.eINSTANCE.createCD();
			encounterActivityCode.setNullFlavor(NullFlavor.UNK);
			ED originalText = DatatypesFactory.eINSTANCE.createED();
			originalText.addText("<reference value=\"#encounterType "+i+"\"/>");	
			encounterActivityCode.setOriginalText(originalText);
			encounterCCD.setCode(encounterActivityCode );


			ED text = DatatypesFactory.eINSTANCE.createED();
			text.addText("<reference value=\"#encounterType"+i+"\"/>");
			encounterCCD.setText(text );

			IVL_TS encounterDate = DatatypesFactory.eINSTANCE.createIVL_TS();
			Date ed = encounter.getDateCreated();
		
			String edate  = s.format(ed);
			encounterDate.setValue(edate);
			encounterCCD.setEffectiveTime(encounterDate );


			Performer2 encounterPerformer = CDAFactory.eINSTANCE.createPerformer2();
			AssignedEntity encounterAssignedEntity = CDAFactory.eINSTANCE.createAssignedEntity();

			II eid = DatatypesFactory.eINSTANCE.createII();
			//setting the uuid of the encounter provider

			Map<EncounterRole, Set<Provider>> encounterProviderMapByRole1 = encounter.getProvidersByRoles();
			Set<Provider> encounterProviders1 = null;
			if (encounterProviderMapByRole1.values().iterator().hasNext())
			{
				encounterProviders1 = encounterProviderMapByRole1.values().iterator().next();
			}
			if(encounterProviders1.size() >0)
			{


				Provider ep = encounterProviders1.iterator().next();
				eid.setRoot(ep.getUuid());
				encounterAssignedEntity.getIds().add(eid);
				AD providerAddress = DatatypesFactory.eINSTANCE.createAD();
				org.openmrs.Person p =  ep.getPerson();
				if(p != null)
				{
					Set<PersonAddress> providerAddSet =p.getAddresses();
					if(! providerAddSet.isEmpty())
					{

						PersonAddress personAddress = providerAddSet.iterator().next();
						providerAddress.addStreetAddressLine(personAddress.getAddress1()+personAddress.getAddress2());
						providerAddress.addCity(personAddress.getCityVillage());
						providerAddress.addCountry(personAddress.getCountry());

					}
				}
				encounterAssignedEntity.getAddrs().add(providerAddress);
				TEL patientTelecom = DatatypesFactory.eINSTANCE.createTEL();
				patientTelecom.setNullFlavor(NullFlavor.UNK);
				encounterAssignedEntity.getTelecoms().add(patientTelecom);
				Person assignedProvider = CDAFactory.eINSTANCE.createPerson();
				PN providerName = DatatypesFactory.eINSTANCE.createPN();
				providerName.addText(ep.getName());
				assignedProvider.getNames().add(providerName);
				encounterAssignedEntity.setAssignedPerson(assignedProvider);
				encounterPerformer.setAssignedEntity(encounterAssignedEntity);



				encounterCCD.getPerformers().add(encounterPerformer);
			}


			Participant2 participant = CDAFactory.eINSTANCE.createParticipant2();
			participant.setTypeCode(ParticipationType.LOC);

			II participantTemplateId = DatatypesFactory.eINSTANCE.createII();
			participantTemplateId.setRoot("2.16.840.1.113883.10.20.1.45");
			participant.getTemplateIds().add(participantTemplateId);

			ParticipantRole participantRole = CDAFactory.eINSTANCE.createParticipantRole();
			participantRole.setClassCode(RoleClassRoot.SDLOC);

			II locationId = DatatypesFactory.eINSTANCE.createII();
			//location uuid should be provided 
			locationId.setRoot(encounter.getLocation().getUuid());
			locationId.setExtension(encounter.getLocation().getName());
			participantRole.getIds().add(locationId);
			PlayingEntity playingEntity = CDAFactory.eINSTANCE.createPlayingEntity();
			playingEntity.setClassCode(EntityClassRoot.PLC);

			PN playingEntityName = DatatypesFactory.eINSTANCE.createPN();
			playingEntityName.addText(Context.getAdministrationService().getImplementationId().getName());
			playingEntity.getNames().add(playingEntityName );
			participantRole.setPlayingEntity(playingEntity );
			participant.setParticipantRole(participantRole );
			//encounter location
			encounterCCD.getParticipants().add(participant); 

			encounterEntryList.add(encounterEntry);
			i++;
		}




		buffer.append("</tbody>");buffer.append("</table>");
		StrucDocText encounterDetails = CDAFactory.eINSTANCE.createStrucDocText();
		encounterDetails.addText(buffer.toString());

		encounterSection.setText(encounterDetails);
		
		encounterSection.getEntries().addAll(encounterEntryList);
		ccd.addSection(encounterSection); 
		return ccd;
	}


	ContinuityOfCareDocument buildAllergies(ContinuityOfCareDocument ccd ,Patient patient)
	{

		AlertsSection allergySection = CCDFactory.eINSTANCE.createAlertsSection();
		allergySection.getTemplateIds().add(buildTemplateID("2.16.840.1.113883.3.88.11.83.102", null, "HITSP/C83"));

		II allergySectionTemplateID1 = DatatypesFactory.eINSTANCE.createII();
		allergySectionTemplateID1.setRoot("1.3.6.1.4.1.19376.1.5.3.1.3.13");
		allergySectionTemplateID1.setAssigningAuthorityName("IHE PCC");
		allergySection.getTemplateIds().add(allergySectionTemplateID1);

		II allergySectionTemplateID2 = DatatypesFactory.eINSTANCE.createII();
		allergySectionTemplateID2.setRoot("2.16.840.1.113883.10.20.1.2");
		allergySectionTemplateID2.setAssigningAuthorityName("HL7 CCD");
		allergySection.getTemplateIds().add(allergySectionTemplateID2);

		//Snome code for document procedure 
		CE allergySectionCode = DatatypesFactory.eINSTANCE.createCE();
		allergySectionCode.setCode("48765-2");
		allergySectionCode.setCodeSystem("2.16.840.1.113883.6.1");
		allergySectionCode.setCodeSystemName("LOINC");
		allergySectionCode.setDisplayName("Allergies, adverse reactions, alerts");
		allergySection.setCode(allergySectionCode);


		ST allergySectionTitle = DatatypesFactory.eINSTANCE.createST();
		allergySectionTitle.addText("Allergies,  Adverse Reactions & Alerts");
		allergySection.setTitle(allergySectionTitle);




		StringBuffer buffer = new StringBuffer();
		buffer.append("<table border=\"1\" width=\"100%\">");
		buffer.append("<thead>");
		buffer.append("<tr>");
		buffer.append("<th>Substance</th>");
		buffer.append("<th>Reaction</th>");
		buffer.append("<th>Date</th>");
		buffer.append("</tr>");
		buffer.append("</thead>");
		buffer.append("<tbody>");

		PatientService patientService = Context.getPatientService();
		List<Allergy>patientAllergyList=patientService.getAllergies(patient);
		List<Entry> allergyEntryList = new ArrayList<Entry>();
		int i = 0;
		for(Allergy patientAllergy : patientAllergyList)
		{
			buffer.append("<tr>");
			buffer.append("<td><content id=\"allergy"+i+" \">"+patientAllergy.getAllergen().getDisplayString()+"</content></td>");
			buffer.append("<td><content id=\"reaction"+i+" \">"+patientAllergy.getReaction().getDisplayString()+"</content></td>");
			 Date date = patientAllergy.getStartDate();
			
		
			buffer.append("<td>"+s.format(date)+"</td>");
			buffer.append("</tr>");
			

			
			//Set of encounters

			Entry allergyEntry = CDAFactory.eINSTANCE.createEntry();
			allergyEntry.setTypeCode(x_ActRelationshipEntry.DRIV);


			Act allergyAct = CDAFactory.eINSTANCE.createAct();
			allergyAct.setClassCode(x_ActClassDocumentEntryAct.ACT);
			allergyAct.setMoodCode(x_DocumentActMood.EVN);
			allergyEntry.setAct(allergyAct);


			II allergyTemplateID = DatatypesFactory.eINSTANCE.createII();
			allergyTemplateID.setRoot("2.16.840.1.113883.3.88.11.83.6");
			allergyTemplateID.setAssigningAuthorityName("HITSP C83");
			allergyAct.getTemplateIds().add(allergyTemplateID);

			II allergyTemplateID1 = DatatypesFactory.eINSTANCE.createII();
			allergyTemplateID1.setRoot("2.16.840.1.113883.10.20.1.27");
			allergyTemplateID1.setAssigningAuthorityName("CCD");
			allergyAct.getTemplateIds().add(allergyTemplateID1);

			II allergyTemplateID2 = DatatypesFactory.eINSTANCE.createII();
			allergyTemplateID2.setRoot("1.3.6.1.4.1.19376.1.5.3.1.4.5.1");
			allergyTemplateID2.setAssigningAuthorityName("IHE PCC");
			allergyAct.getTemplateIds().add(allergyTemplateID2);




			allergyAct.getIds().add(buildID(patientAllergy.getUuid(), ""));

			CD alleryActivityCode = DatatypesFactory.eINSTANCE.createCD();
			alleryActivityCode.setNullFlavor(NullFlavor.NA);
			allergyAct.setCode(alleryActivityCode);

			CS c3 = DatatypesFactory.eINSTANCE.createCS();
			c3.setCode("active");

			allergyAct.setStatusCode(c3);


			allergyAct.setEffectiveTime(buildEffectiveTimeinIVL(patientAllergy.getStartDate(), patientAllergy.getEndDate()));

			EntryRelationship allergyReactionEntry = CDAFactory.eINSTANCE.createEntryRelationship();
			allergyReactionEntry.setTypeCode(x_ActRelationshipEntryRelationship.SUBJ);
			allergyReactionEntry.setInversionInd(false);
			allergyAct.getEntryRelationships().add(allergyReactionEntry);
			Observation allergyReactionObservation = CDAFactory.eINSTANCE.createObservation();
			allergyReactionObservation.setClassCode(ActClassObservation.OBS);
			allergyReactionObservation.setMoodCode(x_ActMoodDocumentObservation.EVN);


			II allergyReactionTemplateID = DatatypesFactory.eINSTANCE.createII();
			allergyReactionTemplateID.setRoot("1.3.6.1.4.1.19376.1.5.3.1.4.6");
			allergyReactionTemplateID.setAssigningAuthorityName("IHE PCC");
			allergyReactionObservation.getTemplateIds().add(allergyReactionTemplateID);

			II allergyReactionTemplateID1 = DatatypesFactory.eINSTANCE.createII();
			allergyReactionTemplateID1.setRoot("1.3.6.1.4.1.19376.1.5.3.1.4.5");
			allergyReactionTemplateID1.setAssigningAuthorityName("IHE PCC");
			allergyReactionObservation.getTemplateIds().add(allergyReactionTemplateID1);

			II allergyReactionTemplateID2 = DatatypesFactory.eINSTANCE.createII();
			allergyReactionTemplateID2.setRoot("2.16.840.1.113883.10.20.1.18");
			allergyReactionTemplateID2.setAssigningAuthorityName("CCD");
			allergyReactionObservation.getTemplateIds().add(allergyReactionTemplateID2);




			allergyReactionObservation.getIds().add(buildID(patientAllergy.getReaction().getUuid(), ""));
			//OpenMRS does not capture the category of allergy eg: food allergy , drug allergy , etc

			allergyReactionObservation.setCode(buildCode("59037007","2.16.840.1.113883.6.96","Drug Intolerance","SNOMED-CT"));
			allergyReactionObservation.setText(buildEDText("#reaction"+i));
			allergyReactionObservation.setEffectiveTime(buildEffectiveTimeinIVL(patientAllergy.getStartDate(),null));
			//CE code = buildConceptCode(patientAllergy.getReaction());
			
			allergyReactionObservation.getValues().add(buildConceptCode(patientAllergy.getAllergen()));
			CS c = DatatypesFactory.eINSTANCE.createCS();
			c.setCode("completed");
			allergyReactionObservation.setStatusCode(c);
			allergyReactionEntry.setObservation(allergyReactionObservation );

			Participant2 allergySubstance = CDAFactory.eINSTANCE.createParticipant2();
			allergySubstance.setTypeCode(ParticipationType.CSM);
			ParticipantRole alleryParticipantRole = CDAFactory.eINSTANCE.createParticipantRole();
			alleryParticipantRole.setClassCode(RoleClassRoot.MANU);
			PlayingEntity allergyPlayingEntity= CDAFactory.eINSTANCE.createPlayingEntity();
			allergyPlayingEntity.setClassCode(EntityClassRoot.MMAT);
			CE c1 = buildConceptCode(patientAllergy.getAllergen());
			c1.setOriginalText(buildEDText("#allergy"+i));
			allergyPlayingEntity.setCode(c1);
			alleryParticipantRole.setPlayingEntity(allergyPlayingEntity);

			allergySubstance.setParticipantRole(alleryParticipantRole );
			allergyReactionObservation.getParticipants().add(allergySubstance );

			EntryRelationship allergyStatusEntry  = CDAFactory.eINSTANCE.createEntryRelationship();
			allergyStatusEntry.setTypeCode(x_ActRelationshipEntryRelationship.REFR);
			Observation statusObservation = CDAFactory.eINSTANCE.createObservation();
			statusObservation.setClassCode(ActClassObservation.OBS);
			statusObservation.setMoodCode(x_ActMoodDocumentObservation.EVN);
			statusObservation.getTemplateIds().add(buildTemplateID("2.16.840.1.113883.10.20.1.39", null, null));
			statusObservation.setCode(buildCode("33999-4", "2.16.840.1.113883.6.1", "Status","LOINC"));
			CS c2 = DatatypesFactory.eINSTANCE.createCS();
			c2.setCode("completed");
			statusObservation.setStatusCode(c2);
			Concept alleryStatus = Context.getConceptService().getConceptByName(patientAllergy.getSeverity().name());
			statusObservation.getValues().add(buildConceptCode(alleryStatus));
			allergyStatusEntry.setObservation(statusObservation);
			allergyReactionObservation.getEntryRelationships().add(allergyStatusEntry  );


			allergyEntryList.add(allergyEntry);
			i++;

		}




		buffer.append("</tbody>");buffer.append("</table>");
		
		StrucDocText allergyDetails = CDAFactory.eINSTANCE.createStrucDocText();
		allergyDetails.addText(buffer.toString());
		allergySection.setText(allergyDetails);
		allergySection.getEntries().addAll(allergyEntryList);
		ccd.addSection(allergySection); 


		return ccd;
	}



	private ContinuityOfCareDocument  buildHeader(ContinuityOfCareDocument ccd , Patient patient)
	{
		ccd.getRealmCodes().clear();
		CS realmCode = DatatypesFactory.eINSTANCE.createCS("US");
		ccd.getRealmCodes().add(realmCode);




		Date d = new Date();
		ccd.setEffectiveTime(buildEffectiveTime(d));
		
		SimpleDateFormat s = new SimpleDateFormat("yyyyMMddhhmmss");
		
		String creationDate = s.format(d);
		ccd.setId(buildID(Context.getAdministrationService().getImplementationId().getImplementationId(), patient.getId()+Context.getAdministrationService().getGlobalProperty("application.name")+creationDate));


		InfrastructureRootTypeId typeId = CDAFactory.eINSTANCE.createInfrastructureRootTypeId();
		typeId.setExtension("POCD_HD000040");
		typeId.setRoot("2.16.840.1.113883.1.3");
		ccd.setTypeId(typeId);

		ccd.getTemplateIds().clear();
		ccd.getTemplateIds().add(buildTemplateID("2.16.840.1.113883.3.27.1776", "","CDA/R2"));

		ccd.getTemplateIds().add(buildTemplateID("2.16.840.1.113883.10.20.3", "", "HL7/CDT Header"));

		ccd.getTemplateIds().add(buildTemplateID("2.16.840.1.113883.3.88.11.32.1", "", "HITSP/C32"));

		ccd.getTemplateIds().add(buildTemplateID("1.3.6.1.4.1.19376.1.5.3.1.1.1", "", "IHE/PCC"));

		ccd.getTemplateIds().add(buildTemplateID("1.3.6.1.4.1.150.2474.11.2.1", "", ""));

		
		ccd.setCode(buildCodeCE("422735006", "2.16.840.1.113883.6.96", "Summary clinical document", "SNOMED CT"));

		ccd.setTitle(buildST("Medical Summary of "+patient.getGivenName()+" "+patient.getFamilyName()+"create on "+d));

		CS languageCode = DatatypesFactory.eINSTANCE.createCS();
		languageCode.setCode("en-US");
		ccd.setLanguageCode(languageCode);

		CE confidentialityCode = DatatypesFactory.eINSTANCE.createCE();
		confidentialityCode.setCode("N");
		ccd.setConfidentialityCode(confidentialityCode);

		PatientRole patientRole = CDAFactory.eINSTANCE.createPatientRole();
		II patientID = DatatypesFactory.eINSTANCE.createII();
		//same as the implementation id
		patientRole.getIds().add(buildID(Context.getAdministrationService().getImplementationId().getImplementationId(), patient.getPatientIdentifier().getIdentifier()));


		Set<PersonAddress> addresses = patient.getAddresses();
		//System.out.print("------------>"+addresses);
		for(PersonAddress address : addresses)
		{

			if(address.isPreferred())
			{
				//	System.out.print("!!!!!!!!!!!!");
				AD patientAddress = DatatypesFactory.eINSTANCE.createAD();
				patientAddress.addStreetAddressLine(address.getAddress1()+address.getAddress2());
				patientAddress.addCity(address.getCityVillage());
				patientAddress.addState(address.getStateProvince());
				patientRole.getAddrs().add(patientAddress);
			}
		}

		TEL patientTelecom = DatatypesFactory.eINSTANCE.createTEL();
		patientTelecom.setNullFlavor(NullFlavor.UNK);
		patientRole.getTelecoms().add(patientTelecom);

		org.openhealthtools.mdht.uml.cda.Patient cdapatient = CDAFactory.eINSTANCE.createPatient();
		patientRole.setPatient(cdapatient);
		PN name = DatatypesFactory.eINSTANCE.createPN();
		name.addGiven(patient.getPersonName().getGivenName());
		name.addFamily(patient.getPersonName().getFamilyName());
		cdapatient.getNames().add(name);


		CE gender = DatatypesFactory.eINSTANCE.createCE();
		gender.setCode(patient.getGender());
		gender.setCodeSystem("2.16.840.1.113883.5.1");
		cdapatient.setAdministrativeGenderCode(gender);

		System.out.print(patient.getAttribute("Civil Status"));
		PersonAttribute civilStatus = patient.getAttribute("Civil Status");
		if(civilStatus != null)
		{
		
		Concept c = Context.getConceptService().getConceptByName(civilStatus.toString());
		Collection<ConceptMap> conceptmapp = c.getConceptMappings();
		for(ConceptMap n : conceptmapp)
		{

			if(n.getSource().getName().equalsIgnoreCase("Snomed ct"))
			{
				CE codes = DatatypesFactory.eINSTANCE.createCE();
				codes.setCode(n.getSourceCode());
				codes.setCodeSystem("2.16.840.1.113883.6.96");
				codes.setCodeSystemName(n.getSource().getName());
				codes.setDisplayName(n.getConcept().getDisplayString());
				cdapatient.setMaritalStatusCode(codes);

			}
		}
		}

		TS dateOfBirth = DatatypesFactory.eINSTANCE.createTS();
		SimpleDateFormat s1 = new SimpleDateFormat("yyyyMMdd");
		Date dobs = patient.getBirthdate();
		String dob = s1.format(dobs);
		dateOfBirth.setValue(dob);
		cdapatient.setBirthTime(dateOfBirth); 


		Organization providerOrganization = CDAFactory.eINSTANCE.createOrganization();
		AD providerOrganizationAddress = DatatypesFactory.eINSTANCE.createAD();
		providerOrganizationAddress.addCounty("");
		providerOrganizationAddress.addState("");
		providerOrganization.getAddrs().add(providerOrganizationAddress);

		ON organizationName = DatatypesFactory.eINSTANCE.createON();
		organizationName.addText(Context.getAdministrationService().getImplementationId().getName());
		providerOrganization.getNames().add(organizationName);

		TEL providerOrganizationTelecon = DatatypesFactory.eINSTANCE.createTEL();
		providerOrganizationTelecon.setNullFlavor(NullFlavor.UNK);
		providerOrganization.getTelecoms().add(providerOrganizationTelecon);

		patientRole.setProviderOrganization(providerOrganization);

		ccd.addPatientRole(patientRole);

		Author author = CDAFactory.eINSTANCE.createAuthor();
		author.setTime(buildEffectiveTime(d));
		//in this case we consider the assigned author is the one generating the document i.e the logged in user exporting the document
		AssignedAuthor assignedAuthor = CDAFactory.eINSTANCE.createAssignedAuthor();
		II authorId = DatatypesFactory.eINSTANCE.createII();
		authorId.setRoot(Context.getAdministrationService().getImplementationId().getImplementationId());
		authorId.setExtension(Context.getAdministrationService().getImplementationId().getName());
		assignedAuthor.getIds().add(authorId);


		Organization representedOrganization = CDAFactory.eINSTANCE.createOrganization();
		AD representedOrganizationAddress = DatatypesFactory.eINSTANCE.createAD();
		representedOrganizationAddress.addCounty("");
		representedOrganizationAddress.addState("");
		ON implName = DatatypesFactory.eINSTANCE.createON();
		implName.addText(Context.getAdministrationService().getImplementationId().getName());
		representedOrganization.getNames().add(implName);

		assignedAuthor.getAddrs().add(representedOrganizationAddress);
		assignedAuthor.getTelecoms().add(providerOrganizationTelecon);

		Person assignedPerson = CDAFactory.eINSTANCE.createPerson();
		PN assignedPersonName = DatatypesFactory.eINSTANCE.createPN();
		assignedPersonName.addText("Auto-generated");
		assignedPerson.getNames().add(assignedPersonName);


		AuthoringDevice authoringDevice = CDAFactory.eINSTANCE.createAuthoringDevice();
		SC authoringDeviceName = DatatypesFactory.eINSTANCE.createSC();
		authoringDeviceName.addText(Context.getAdministrationService().getGlobalProperty("application.name"));
		authoringDevice.setSoftwareName(authoringDeviceName);
		assignedAuthor.setAssignedAuthoringDevice(authoringDevice);

		assignedAuthor.setAssignedPerson(assignedPerson);
		assignedAuthor.setRepresentedOrganization(representedOrganization);
		author.setAssignedAuthor(assignedAuthor);
		ccd.getAuthors().add(author);

		ccd = buildEncounters(ccd , patient);


		List<Relationship> relationShips= Context.getPersonService().getRelationshipsByPerson(patient);
		List<Participant1> participantList = new ArrayList<Participant1>(relationShips.size());
		for (int i = 0; i< relationShips.size();i++) {
			Participant1 e = CDAFactory.eINSTANCE.createParticipant1();

			e.setTypeCode(ParticipationType.IND);
			II pid1 = DatatypesFactory.eINSTANCE.createII();
			pid1.setAssigningAuthorityName("HITSP/C83");
			pid1.setRoot("2.16.840.1.113883.3.88.11.83.3");

			II pid2 = DatatypesFactory.eINSTANCE.createII();
			pid2.setAssigningAuthorityName("IHE/PCC");
			pid2.setRoot("1.3.6.1.4.1.19376.1.5.3.1.2.4");

			e.getTemplateIds().add(pid1);
			e.getTemplateIds().add(pid2);

			IVL_TS time = DatatypesFactory.eINSTANCE.createIVL_TS();
			time.setNullFlavor(NullFlavor.UNK);
			e.setTime(time);
			Relationship relationship = relationShips.get(i);
			AssociatedEntity patientRelationShip = CDAFactory.eINSTANCE.createAssociatedEntity();
			patientRelationShip.setClassCode(RoleClassAssociative.PRS);
			CE relationShipCode = DatatypesFactory.eINSTANCE.createCE();
			relationShipCode.setCodeSystemName("Snomed CT");
			relationShipCode.setCodeSystem("2.16.840.1.113883.6.96");
			Person associatedPerson = CDAFactory.eINSTANCE.createPerson();
			PN associatedPersonName = DatatypesFactory.eINSTANCE.createPN();
			Iterator<PersonAddress> patientAddressIterator = null;

			switch (relationship.getRelationshipType().getId()) {
			case 1:
				//Under care of doctor snomed code 305450004
				relationShipCode.setCode("305450004");
				relationShipCode.setDisplayName("Doctor");
				associatedPersonName.addFamily(relationship.getPersonA().getFamilyName());
				associatedPersonName.addGiven(relationship.getPersonA().getGivenName());
				patientAddressIterator = relationship.getPersonB().getAddresses().iterator();
				break;
			case 2:

				relationShipCode.setCode("375005");
				relationShipCode.setDisplayName("Sibling");
				associatedPersonName.addFamily(relationship.getPersonA().getFamilyName());
				associatedPersonName.addGiven(relationship.getPersonA().getGivenName());
				patientAddressIterator = relationship.getPersonA().getAddresses().iterator();
				break;
			case 3:
				if(patient.getId() == relationship.getPersonA().getId())
				{
					relationShipCode.setCode("67822003");
					relationShipCode.setDisplayName("Child");
					associatedPersonName.addFamily(relationship.getPersonB().getFamilyName());
					associatedPersonName.addGiven(relationship.getPersonB().getGivenName());
					patientAddressIterator = relationship.getPersonB().getAddresses().iterator();
				}else
				{
					relationShipCode.setCode("40683002");
					relationShipCode.setDisplayName("Parent");
					associatedPersonName.addFamily(relationship.getPersonA().getFamilyName());
					associatedPersonName.addGiven(relationship.getPersonA().getGivenName());
					patientAddressIterator = relationship.getPersonA().getAddresses().iterator();

				}
				break;
			case 4:
				if(patient.getId() == relationship.getPersonA().getId())
				{
					if(relationship.getPersonB().getGender().equalsIgnoreCase("M"))
						relationShipCode.setCode("83559000");
					else
						relationShipCode.setCode("34581001");
					relationShipCode.setDisplayName("Neice/Nephew");
					associatedPersonName.addFamily(relationship.getPersonB().getFamilyName());
					associatedPersonName.addGiven(relationship.getPersonB().getGivenName());
					patientAddressIterator = relationship.getPersonB().getAddresses().iterator();
				}else
				{
					if(relationship.getPersonA().getGender().equalsIgnoreCase("M"))
						relationShipCode.setCode("38048003");
					else
						relationShipCode.setCode("25211005");
					relationShipCode.setDisplayName("Aunt/Uncle");
					associatedPersonName.addFamily(relationship.getPersonA().getFamilyName());
					associatedPersonName.addGiven(relationship.getPersonA().getGivenName());
					patientAddressIterator = relationship.getPersonB().getAddresses().iterator();

				}	

				break;

			}

			patientRelationShip.setCode(relationShipCode);
			AD associatedPersonAddress = DatatypesFactory.eINSTANCE.createAD();

			if(patientAddressIterator.hasNext())
			{
				PersonAddress padd = patientAddressIterator.next();
				associatedPersonAddress.addStreetAddressLine(padd.getAddress1()+ padd.getAddress2())	;
			}

			patientRelationShip.getAddrs().add(associatedPersonAddress);
			associatedPerson.getNames().add(associatedPersonName );
			patientRelationShip.setAssociatedPerson(associatedPerson );
			e.setAssociatedEntity(patientRelationShip);
			participantList.add(e);

		}
		ccd.getParticipants().addAll(participantList);

		//the steward of the document 
		Custodian custodian = CDAFactory.eINSTANCE.createCustodian();
		AssignedCustodian assignedCustodian = CDAFactory.eINSTANCE.createAssignedCustodian();
		CustodianOrganization custodianOrganization = CDAFactory.eINSTANCE.createCustodianOrganization();
		II custodianId = DatatypesFactory.eINSTANCE.createII();
		String custodianUuid = Context.getAdministrationService().getImplementationId().getImplementationId();
		custodianId.setRoot(custodianUuid);
		custodianId.setExtension(Context.getAdministrationService().getImplementationId().getName());
		custodianOrganization.getIds().add(custodianId);

		custodianOrganization.setAddr(providerOrganizationAddress);
		custodianOrganization.setName(organizationName);
		custodianOrganization.setTelecom(providerOrganizationTelecon);
		assignedCustodian.setRepresentedCustodianOrganization(custodianOrganization);
		custodian.setAssignedCustodian(assignedCustodian);
		ccd.setCustodian(custodian);
		//assignedCustodian.setRepresentedCustodianOrganization(arg0);*/
		return ccd;


	}

	private ContinuityOfCareDocument buildProblems(ContinuityOfCareDocument ccd , Patient patient)
	{
		ProblemSection problemSection = CCDFactory.eINSTANCE.createProblemSection();
		ccd.addSection(problemSection);
		problemSection.getTemplateIds().add(buildTemplateID("2.16.840.1.113883.3.88.11.83.103", "", "HITSP/C83"));
		problemSection.getTemplateIds().add(buildTemplateID("1.3.6.1.4.1.19376.1.5.3.1.3.6", "", "IHE PCC"));
		problemSection.getTemplateIds().add(buildTemplateID("2.16.840.1.113883.10.20.1.11", "", "HL7 CCD"));
		problemSection.setCode(buildCodeCE("11450-4", "2.16.840.1.113883.6.1", "Problem list", "LOINC"));
		problemSection.setTitle(buildST("Problems"));

		CE problemCode = DatatypesFactory.eINSTANCE.createCE();
		problemCode.setNullFlavor(NullFlavor.NA);
	//	problemSection.setCode(problemCode);
		StringBuffer buffer = new StringBuffer();
		buffer.append("<table border=\"1\" width=\"100%\">");
		buffer.append("<thead>");
		buffer.append("<tr>");
		buffer.append("<th>Problem</th>");
		buffer.append("<th>Effective Date</th>");
		buffer.append("<th>Status</th>");
		buffer.append("</tr>");
		buffer.append("</thead>");
		buffer.append("<tbody>");

		PatientService patientService = Context.getPatientService();
		List<Problem>patientProblemList=patientService.getProblems(patient);
		List<Entry> problemEntryList = new ArrayList<Entry>();
		for(Problem patientProblem : patientProblemList)
		{
			buffer.append("<tr>");
			buffer.append("<td><content ID=\""+patientProblem.getProblem()+"\">"+patientProblem.getProblem().getDisplayString()+"</content></td>");
			 Date date = patientProblem.getStartDate();
		
			buffer.append("<td>"+s.format(date)+"</td>");
			//technically we should nt ne using the modifier for problem status but for first pass its ok 
			buffer.append("<td>"+patientProblem.getModifier().getText()+"</td>");
			buffer.append("</tr>");
		
			Entry problemEntry = CDAFactory.eINSTANCE.createEntry() ;
			problemEntry.setTypeCode(x_ActRelationshipEntry.DRIV);

			Act problemAct = CDAFactory.eINSTANCE.createAct();
			problemAct.setClassCode(x_ActClassDocumentEntryAct.ACT);
			problemAct.setMoodCode(x_DocumentActMood.EVN);
			problemEntry.setAct(problemAct);
			problemAct.getTemplateIds().add(buildTemplateID("2.16.840.1.113883.3.88.11.83.7", null, "HITSP C83"));
			problemAct.getTemplateIds().add(buildTemplateID("2.16.840.1.113883.10.20.1.27", null, "CCD"));
			problemAct.getTemplateIds().add(buildTemplateID("1.3.6.1.4.1.19376.1.5.3.1.4.5.2", null, "IHE PCC"));
			problemAct.getTemplateIds().add(buildTemplateID("1.3.6.1.4.1.19376.1.5.3.1.4.5.1", null, "IHE PCC"));
			problemAct.getIds().add(buildID(patientProblem.getUuid(), ""));
			problemSection.getEntries().add(problemEntry );
			problemAct.setEffectiveTime(buildEffectiveTimeinIVL(patientProblem.getStartDate(), patientProblem.getEndDate()));

			CS statusCode = DatatypesFactory.eINSTANCE.createCS();
			statusCode.setCode("completed");
			problemAct.setStatusCode(statusCode);
			CD problemActCode = DatatypesFactory.eINSTANCE.createCD();
			problemActCode.setNullFlavor(NullFlavor.NA);
			problemAct.setCode(problemActCode);
			EntryRelationship problemEntryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
			problemAct.getEntryRelationships().add(problemEntryRelationship);
			problemEntryRelationship.setTypeCode(x_ActRelationshipEntryRelationship.SUBJ);
			Observation problemObservation = CDAFactory.eINSTANCE.createObservation();
			problemObservation.setClassCode(ActClassObservation.OBS);
			problemObservation.setMoodCode(x_ActMoodDocumentObservation.EVN);
			problemObservation.getTemplateIds().add(buildTemplateID("2.16.840.1.113883.10.20.1.28", null, "CCD"));
			problemObservation.getTemplateIds().add(buildTemplateID("1.3.6.1.4.1.19376.1.5.3.1.4.5", null, "IHE PCC"));
			problemObservation.getIds().add(buildID(patientProblem.getUuid(), ""));
			//needs to be changed later
			problemObservation.setCode(buildCode("64572001", "2.16.840.1.113883.6.96", "Condition", "SNOMED-CT"));
			problemObservation.setText(buildEDText("#"+patientProblem.getProblem()));
			problemEntryRelationship.setObservation(problemObservation );
			CS statusCodeObservation = DatatypesFactory.eINSTANCE.createCS();
			statusCodeObservation.setCode("completed");
			problemObservation.setStatusCode(statusCodeObservation);
			problemObservation.setEffectiveTime(buildEffectiveTimeinIVL(patientProblem.getStartDate(), patientProblem.getEndDate()));
			CE code = buildConceptCode(patientProblem.getProblem());
			problemObservation.getValues().add(buildCode(code.getCode(),code.getCodeSystem(),code.getDisplayName(),code.getCodeSystemName()));
			EntryRelationship problemObsEntryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
			problemObsEntryRelationship.setTypeCode(x_ActRelationshipEntryRelationship.REFR);
			Observation statusObservation = CDAFactory.eINSTANCE.createObservation();
			statusObservation.setClassCode(ActClassObservation.OBS);
			statusObservation.setMoodCode(x_ActMoodDocumentObservation.EVN);
			statusObservation.getTemplateIds().add(buildTemplateID("2.16.840.1.113883.10.20.1.50", "", ""));
			statusObservation.setCode(buildCode("33999-4", "2.16.840.1.113883.6.1", "Status", "LOINC"));
			problemObsEntryRelationship.setObservation(statusObservation );
			//needs to comefrom the admin screen
			if(patientProblem.getModifier().equals(patientProblem.getModifier().HISTORY_OF))
			{
				statusObservation.getValues().add(buildCode("90734009", "2.16.840.1.113883.6.96", "Chronic", "Snomed CT"));
			}
			else
			{
				statusObservation.getValues().add(buildCode("415684004", "2.16.840.1.113883.6.96", "Rule Out", "Snomed CT"));
			}
			CS obsStatusObsCode = DatatypesFactory.eINSTANCE.createCS();
			obsStatusObsCode.setCode("completed");
			statusObservation.setStatusCode(obsStatusObsCode);
			problemObservation.getEntryRelationships().add(problemObsEntryRelationship );

			problemEntryList.add(problemEntry);

		}
		buffer.append("</tbody>");buffer.append("</table>");
		StrucDocText problemDetails = CDAFactory.eINSTANCE.createStrucDocText();
		problemDetails.addText(buffer.toString());
		problemSection.setText(problemDetails);

		problemSection.getEntries().addAll(problemEntryList);
		return ccd;

	}

	private ST buildST(String title)
	{
		ST displayTitle = DatatypesFactory.eINSTANCE.createST();
		displayTitle.addText(title);
		return displayTitle;

	}

	private II buildID(String root , String extension)
	{
		II id = DatatypesFactory.eINSTANCE.createII();
		//same as the implementation id
		id.setRoot(root);
		id.setExtension(extension);
		return id;

	}


	private ContinuityOfCareDocument buildMedication(ContinuityOfCareDocument ccd , Patient patient)
	{
		MedicationsSection medicationSection = CCDFactory.eINSTANCE.createMedicationsSection();
		ccd.addSection(medicationSection);
		medicationSection.getTemplateIds().add(buildTemplateID("2.16.840.1.113883.3.88.11.83.112", "", "HITSP/C83"));
		medicationSection.getTemplateIds().add(buildTemplateID("1.3.6.1.4.1.19376.1.5.3.1.3.19", "", "IHE PCC"));
		medicationSection.getTemplateIds().add(buildTemplateID("2.16.840.1.113883.10.20.1.8", "", "HL7 CCD"));

		medicationSection.setCode(buildCodeCE("10160-0", "2.16.840.1.113883.6.1", "History of medication use", "LOINC"));
		medicationSection.setTitle(buildST("Medication"));
		StringBuffer buffer = new StringBuffer();
		buffer.append("<table border=\"1\" width=\"100%\">");
		buffer.append("<thead>");
		buffer.append("<tr>");
		buffer.append("<th>Medication</th>");
		buffer.append("<th>Effective Date</th>");
		buffer.append("<th>Dose</th>");
		buffer.append("<th>Days</th>");
		buffer.append("</tr>");
		buffer.append("</thead>");
		buffer.append("<tbody>");

		List<DrugOrder> drugOrders = Context.getOrderService().getDrugOrdersByPatient(patient);		
		List<Entry> drugOrderEntryList = new ArrayList<Entry>();
		int i = 0;
		for(DrugOrder drugOrder : drugOrders)
		{
			buffer.append("<tr>");
			buffer.append("<td><content ID=\"drug"+i+"\">"+drugOrder.getDrug().getName()+"</content></td>");
			Date date = drugOrder.getStartDate();
			
			buffer.append("<td>"+s.format(date)+"</td>");
			//technically we should nt ne using the modifier for problem status but for first pass its ok 
			buffer.append("<td>"+drugOrder.getDose()+"</td>");
			buffer.append("<td>"+drugOrder.getFrequency()+"</td>");
			buffer.append("</tr>");
		
			Entry medicationEntry = CDAFactory.eINSTANCE.createEntry();
			medicationEntry.setTypeCode(x_ActRelationshipEntry.DRIV);

			SubstanceAdministration medicationSubstance = CDAFactory.eINSTANCE.createSubstanceAdministration();
			medicationSubstance.setClassCode(ActClass.SBADM);
			medicationSubstance.setMoodCode(x_DocumentSubstanceMood.EVN);
			medicationSubstance.getTemplateIds().add(buildTemplateID("1.3.6.1.4.1.19376.1.5.3.1.4.7.1", "", "IHE PCC"));
			medicationSubstance.getTemplateIds().add(buildTemplateID("1.3.6.1.4.1.19376.1.5.3.1.4.7", "", "IHE PCC"));
			medicationSubstance.getTemplateIds().add(buildTemplateID("2.16.840.1.113883.10.20.1.24", "", "CCD"));
			medicationSubstance.getIds().add(buildID(drugOrder.getDrug().getUuid(), ""));
			CS statusCode = DatatypesFactory.eINSTANCE.createCS();
			statusCode.setCode("completed");
			medicationSubstance.setStatusCode(statusCode);

			String frequency = drugOrder.getFrequency();
			String value = frequency.split("x")[0];
			String medicationFrequency = value.split("/")[0];

			IVL_TS e  = DatatypesFactory.eINSTANCE.createIVL_TS();
			IVXB_TS startDate = DatatypesFactory.eINSTANCE.createIVXB_TS();
			SimpleDateFormat s = new SimpleDateFormat("yyyyMMddhhmmss");
			String creationDate = s.format(drugOrder.getStartDate());
			startDate.setValue(creationDate);
			e.setLow(startDate);
			medicationSubstance.getEffectiveTimes().add(e);

			PIVL_TS e1 = DatatypesFactory.eINSTANCE.createPIVL_TS();
			e1.setOperator(SetOperator.A);
			PQ period = DatatypesFactory.eINSTANCE.createPQ();
			period.setUnit("h");
			period.setValue(24.00/Double.parseDouble(medicationFrequency));
			e1.setPeriod(period );
			medicationSubstance.getEffectiveTimes().add(e1);






			medicationSubstance.setText(buildEDText("#drug"+i));

			IVL_PQ dose = DatatypesFactory.eINSTANCE.createIVL_PQ();
			dose.setUnit(drugOrder.getUnits());
			dose.setValue(drugOrder.getDose());
			medicationSubstance.setDoseQuantity(dose );
			i++;
			Consumable consumable = CDAFactory.eINSTANCE.createConsumable();
			consumable.setTypeCode(ParticipationType.CSM);
			ManufacturedProduct manufacturedProduct = CDAFactory.eINSTANCE.createManufacturedProduct();
			manufacturedProduct.getTemplateIds().add(buildTemplateID("2.16.840.1.113883.3.88.11.83.8.2", "", "HITSP C83"));
			manufacturedProduct.getTemplateIds().add(buildTemplateID("2.16.840.1.113883.10.20.1.53", "", "CCD"));
			manufacturedProduct.getTemplateIds().add(buildTemplateID("1.3.6.1.4.1.19376.1.5.3.1.4.7.2", "", "IHE PCC"));
			Material manufacturedMaterial = CDAFactory.eINSTANCE.createMaterial();
			manufacturedMaterial.setClassCode(EntityClassManufacturedMaterial.MMAT);
			CE materialCode = buildConceptCode(drugOrder.getConcept(), "RxNorm");
			materialCode.setOriginalText(buildEDText("#drug"+i));
			manufacturedMaterial.setCode(materialCode);

			manufacturedProduct.setManufacturedMaterial(manufacturedMaterial );
			consumable.setManufacturedProduct(manufacturedProduct );
			medicationSubstance.setConsumable(consumable );

			EntryRelationship medicationStatus = CDAFactory.eINSTANCE.createEntryRelationship();
			medicationStatus.setTypeCode(x_ActRelationshipEntryRelationship.REFR);
			Observation medicationStatusObs= CDAFactory.eINSTANCE.createObservation();
			medicationStatus.setObservation(medicationStatusObs);
			medicationStatusObs.setMoodCode(x_ActMoodDocumentObservation.EVN);
			medicationStatusObs.setClassCode(ActClassObservation.OBS);
			medicationStatusObs.getTemplateIds().add(buildTemplateID("2.16.840.1.113883.10.20.1.47", "", ""));
			medicationStatusObs.setCode(buildCode("33999-4", "2.16.840.1.113883.6.1", "Status", "LOINC"));
			medicationStatusObs.getValues().add(buildCodeCE("55561003", "2.16.840.1.113883.6.96", "Active" , "SNOMED-CT" ));
			medicationSubstance.getEntryRelationships().add(medicationStatus );
			medicationEntry.setSubstanceAdministration(medicationSubstance );

















			drugOrderEntryList.add(medicationEntry);


			i++;

		}
		buffer.append("</tbody>");buffer.append("</table>");
		StrucDocText medicationDetails = CDAFactory.eINSTANCE.createStrucDocText();
		medicationDetails.addText(buffer.toString());
		medicationSection.setText(medicationDetails);

		medicationSection.getEntries().addAll(drugOrderEntryList);
		return ccd;

	}


	private ContinuityOfCareDocument buildPlanOfCare(ContinuityOfCareDocument ccd , Patient patient )
	{

		PlanOfCareSection section = CCDFactory.eINSTANCE.createPlanOfCareSection();
		ccd.addSection(section);
		section.getTemplateIds().add(buildTemplateID("2.16.840.1.113883.3.88.11.83.124", "", "HITSP/C83"));
		section.getTemplateIds().add(buildTemplateID("1.3.6.1.4.1.19376.1.5.3.1.3.31", "", "IHE PCC"));
		section.getTemplateIds().add(buildTemplateID("2.16.840.1.113883.10.20.2.7", "", "HL7 CCD"));
		section.getTemplateIds().add(buildTemplateID("2.16.840.1.113883.10.20.1.10", "", "HL7 CCD"));
		section.setCode(buildCodeCE("18776-5", "2.16.840.1.113883.6.1", "Treatment plan", "LOINC"));
		section.setTitle(buildST("Plan Of Care"));
		StringBuffer buffer = new StringBuffer();
		buffer.append("<table border=\"1\" width=\"100%\">");
		buffer.append("<thead>");
		buffer.append("<tr>");
		buffer.append("<th>Planned Activity</th>");
		buffer.append("<th>Planned Date</th>");
		List<Concept> labResultsList= dao.getConceptByCategory("PlanOfCare");
		List <Obs> listOfObservations = new ArrayList<Obs>();
		//check if a concept is set of a single Concept 
		for (Concept concept : labResultsList) {
		
			if(concept.isSet())
			{
				List<Concept> conceptSet = concept.getSetMembers();
				
				for (Concept conceptSet2 : conceptSet) {
					
					listOfObservations.addAll(Context.getObsService().getObservationsByPersonAndConcept(patient, conceptSet2));
					
				}
			}else
			{
				listOfObservations.addAll(Context.getObsService().getObservationsByPersonAndConcept(patient, concept));
			
			}
		}
		buffer.append("</tr>");
		buffer.append("</thead>");
		buffer.append("<tbody>");
		for (Obs obs : listOfObservations) {
			buffer.append("<tr>");
			buffer.append("<td><content id = \""+obs.getConcept().getDisplayString()+"\">"+obs.getConcept().getDisplayString()+"</content></td>");
			int type = obs.getConcept().getDatatype().getId();
			switch(type)
			{
			case 1:
				buffer.append("<td>"+obs.getValueNumeric()+"</td>");
				break;

			case 2:
				buffer.append("<td>"+obs.getValueCoded().getDisplayString()+"</td>");
				break;


			case 3:
				buffer.append("<td>"+obs.getValueText()+"</td>");
				break;

			case 6:
				
				buffer.append("<td>"+s.format(obs.getValueDate())+"</td>");
				break;

			case 7:
				buffer.append("<td>"+obs.getValueTime()+"</td>");
				break;

			case 8:
				buffer.append("<td>"+s.format(obs.getValueDatetime())+"</td>");
				break;

			case 10:
				buffer.append("<td>"+obs.getValueBoolean()+"</td>");
				
				break;

			case 13:
				buffer.append("<td>"+obs.getValueComplex()+"</td>");
					break;				
			}
			buffer.append("</tr>");
			
			Entry labResultEntry = CDAFactory.eINSTANCE.createEntry();
			labResultEntry.setTypeCode(x_ActRelationshipEntry.DRIV);
			Observation observation = CDAFactory.eINSTANCE.createObservation();
			observation.setClassCode(ActClassObservation.OBS);
			observation.setMoodCode(x_ActMoodDocumentObservation.RQO);
			observation.getTemplateIds().add(buildTemplateID("2.16.840.1.113883.10.20.1.25", "", "HITSP C83"));
			observation.getIds().add(buildID(obs.getUuid(), ""));
			observation.setCode(buildConceptCode(obs.getConcept(),"SNOMED","LOINC"));
			observation.setText(buildEDText("#"+obs.getConcept().getDisplayString()));
			CS statusCode1 = DatatypesFactory.eINSTANCE.createCS();
			statusCode1.setCode("new");
			observation.setStatusCode(statusCode1);
			observation.setEffectiveTime(buildEffectiveTimeinIVL(obs.getObsDatetime(), null));
			EntryRelationship entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
			entryRelationship.setInversionInd(false);
			entryRelationship.setTypeCode(x_ActRelationshipEntryRelationship.REFR);
			Act act = CDAFactory.eINSTANCE.createAct();
			act.setClassCode(x_ActClassDocumentEntryAct.ACT);
			act.setMoodCode(x_DocumentActMood.EVN);
			act.getTemplateIds().add(buildTemplateID("1.3.6.1.4.1.19376.1.5.3.1.4.4.1", "", ""));
			if(obs.getEncounter() != null)
			act.getIds().add(buildID(obs.getEncounter().getUuid(), ""));
			
			CD code = DatatypesFactory.eINSTANCE.createCD();
			code.setNullFlavor(NullFlavor.UNK);
			act.setCode(code);
			entryRelationship.setAct(act );
			///observation.getEntryRelationships().add(entryRelationship);
			labResultEntry.setObservation(observation);
			
			section.getEntries().add(labResultEntry);
		}
				
				
				
			


		
		buffer.append("</tbody>");buffer.append("</table>");
		StrucDocText details = CDAFactory.eINSTANCE.createStrucDocText();
		details.addText(buffer.toString());

		section.setText(details);

		return ccd;

	}

	private ContinuityOfCareDocument buildVitalSigns(ContinuityOfCareDocument ccd , Patient patient)
	{

		VitalSignsSection section =  CCDFactory.eINSTANCE.createVitalSignsSection();
		ccd.addSection(section);
		section.getTemplateIds().add(buildTemplateID("2.16.840.1.113883.3.88.11.83.119", "", "HITSP/C83"));
		section.getTemplateIds().add(buildTemplateID("1.3.6.1.4.1.19376.1.5.3.1.1.5.3.2", "", "IHE PCC"));
		section.getTemplateIds().add(buildTemplateID("1.3.6.1.4.1.19376.1.5.3.1.3.25", "", "IHE PCC"));
		section.getTemplateIds().add(buildTemplateID("2.16.840.1.113883.10.20.1.16", "", "HL7 CCD"));

		section.setCode(buildCodeCE("8716-3", "2.16.840.1.113883.6.1", "Vital signs", "LOINC"));
		section.setTitle(buildST("Vital Signs"));
		StringBuffer buffer = new StringBuffer();
		buffer.append("<table border=\"1\" width=\"100%\">");
		buffer.append("<thead>");
		buffer.append("<tr>");
		buffer.append("<th>Date</th>");
		List<Concept> vitalSignsList= dao.getConceptByCategory("VitalSigns");
		List <Obs> listOfObservations = new ArrayList<Obs>();
		Map <String, String> vitalSignData = new HashMap<String, String>();
		Set<Concept> observedConceptList = new HashSet();
		Set <Date> dateSet = new HashSet<Date>();
		for (Concept concept : vitalSignsList) {
			if(concept.isSet())
			{
				List<Concept> conceptSet = concept.getSetMembers();
				System.out.println(conceptSet);
				for (Concept conceptSet2 : conceptSet) {
					
					listOfObservations.addAll(Context.getObsService().getObservationsByPersonAndConcept(patient, conceptSet2));
					observedConceptList.add(conceptSet2);
				}
			}else
			{
				listOfObservations.addAll(Context.getObsService().getObservationsByPersonAndConcept(patient, concept));
				observedConceptList.add(concept);
			}
		}

		for (Obs obs : listOfObservations) {
			Date dateCreated = obs.getObsDatetime();

			dateSet.add(dateCreated);
			if(vitalSignData.containsKey(dateCreated))
			{
				String data = vitalSignData.get(dateCreated);
				data = data+","+obs.getId();
				vitalSignData.put(dateCreated+obs.getConcept().getId().toString(), data);


			}else
			{
				vitalSignData.put(dateCreated+obs.getConcept().getId().toString(), obs.getId().toString());

			}


		}
		System.out.println(vitalSignData);
		SortedSet<Date> sortedSet = new TreeSet<Date>(Collections.reverseOrder());
		sortedSet.addAll(dateSet);
		
		for (Date date : sortedSet) {
			
			buffer.append("<th>"+s.format(date)+"</th>");
		}
		buffer.append("</tr>");
		buffer.append("</thead>");
		buffer.append("<tbody>");
//		SortedSet<Concept> sortedConcepts = new TreeSet<Concept>(observedConceptList);
		for (Concept concept : observedConceptList) {

			buffer.append("<tr>");
			buffer.append("<td><content Id= \""+concept.getDisplayString()+"\">"+concept.getDisplayString()+"</content></td>");
			
			for (Date date : sortedSet) {
				if(vitalSignData.containsKey(date+""+concept))
				{
					ConceptNumeric c =  Context.getConceptService().getConceptNumeric(concept.getId());
					String obsId = vitalSignData.get(date+""+concept);
					Obs obs = Context.getObsService().getObs(Integer.parseInt(obsId));
					buffer.append("<td>"+obs.getValueNumeric()+c.getUnits()+"</td>");
					Entry vitalSignEntry = CDAFactory.eINSTANCE.createEntry();
					vitalSignEntry.setTypeCode(x_ActRelationshipEntry.DRIV);
					Organizer organizer = CDAFactory.eINSTANCE.createOrganizer();
					organizer.setClassCode(x_ActClassDocumentEntryOrganizer.CLUSTER);
					organizer.setMoodCode(ActMood.EVN);
					organizer.getTemplateIds().add(buildTemplateID("1.3.6.1.4.1.19376.1.5.3.1.4.13.1", "", "IHE PCC"));
					organizer.getTemplateIds().add(buildTemplateID("2.16.840.1.113883.10.20.1.32", "", "CCD"));
					organizer.getTemplateIds().add(buildTemplateID("2.16.840.1.113883.10.20.1.35", "", "CCD"));
					//cluster of observations
					organizer.getIds().add(buildID(obs.getUuid(), ""));
					organizer.setCode(buildCode("46680005", "2.16.840.1.113883.6.96", "Vital signs", "SNOMED-CT"));
					CS statusCode = DatatypesFactory.eINSTANCE.createCS();
					statusCode.setCode("completed");
					organizer.setStatusCode(statusCode);
					organizer.setEffectiveTime(buildEffectiveTimeinIVL(date,null));
					Component4 component = CDAFactory.eINSTANCE.createComponent4();
					Observation observation = CDAFactory.eINSTANCE.createObservation();
					observation.setClassCode(ActClassObservation.OBS);
					observation.setMoodCode(x_ActMoodDocumentObservation.EVN);
					observation.getTemplateIds().add(buildTemplateID("2.16.840.1.113883.3.88.11.83.14", "", "HITSP C83"));
					observation.getTemplateIds().add(buildTemplateID("2.16.840.1.113883.10.20.1.31", "", "CCD"));
					observation.getTemplateIds().add(buildTemplateID("1.3.6.1.4.1.19376.1.5.3.1.4.13", "", "IHE PCC"));
					observation.getIds().add(buildID(obs.getUuid(), ""));
					observation.setCode(buildConceptCode(concept,"SNOMED","LOINC"));
					observation.setText(buildEDText("#"+concept.getDisplayString()));
					CS statusCode1 = DatatypesFactory.eINSTANCE.createCS();
					statusCode1.setCode("completed");
					observation.setStatusCode(statusCode1);
					observation.setEffectiveTime(buildEffectiveTimeinIVL(obs.getObsDatetime(), null));
					PQ unit = DatatypesFactory.eINSTANCE.createPQ();
										unit.setUnit(c.getUnits());
					unit.setValue(obs.getValueNumeric());
					observation.getValues().add(unit);

					ReferenceRange conceptRange = CDAFactory.eINSTANCE.createReferenceRange();
					ObservationRange observationRange = CDAFactory.eINSTANCE.createObservationRange();
					observationRange.setNullFlavor(NullFlavor.UNK);
					conceptRange.setObservationRange(observationRange );
					observation.getReferenceRanges().add(conceptRange);
					component.setObservation(observation );
					organizer.getComponents().add(component );

					vitalSignEntry.setOrganizer(organizer);
					section.getEntries().add(vitalSignEntry);
				}
				else
				{
					buffer.append("<td></td>");
				}
			}
			buffer.append("</tr>");


		}	
		buffer.append("</tbody>");
		buffer.append("</table>");
		StrucDocText details = CDAFactory.eINSTANCE.createStrucDocText();
		details.addText(buffer.toString());

		section.setText(details);

		return ccd;

	}


	private ContinuityOfCareDocument buildLabResults(ContinuityOfCareDocument ccd , Patient patient )
	{

		ResultsSection section = CCDFactory.eINSTANCE.createResultsSection();
		ccd.addSection(section);
		section.getTemplateIds().add(buildTemplateID("2.16.840.1.113883.3.88.11.83.122", "", "HITSP/C83"));
		section.getTemplateIds().add(buildTemplateID("1.3.6.1.4.1.19376.1.5.3.1.3.28", "", "IHE PCC"));

		section.setCode(buildCodeCE("30954-2", "2.16.840.1.113883.6.1", "Relevant diagnostic tests/laboratory data", "LOINC"));
		section.setTitle(buildST("Results"));
		StringBuffer buffer = new StringBuffer();
		buffer.append("<table border=\"1\" width=\"100%\">");
		buffer.append("<thead>");
		buffer.append("<tr>");
		buffer.append("<th>Date</th>");
		List<Concept> labResultsList= dao.getConceptByCategory("LabResults");
		List <Obs> listOfObservations = new ArrayList<Obs>();
		Map <String, String> labResultData = new HashMap<String, String>();
		List<Concept> observedConceptList = new ArrayList<Concept>();
		Set <Date> dateSet = new HashSet<Date>();
		for (Concept concept : labResultsList) {
			if(concept.isSet())
			{
				List<Concept> conceptSet = concept.getSetMembers();
				System.out.println(conceptSet);
				for (Concept conceptSet2 : conceptSet) {
					
					listOfObservations.addAll(Context.getObsService().getObservationsByPersonAndConcept(patient, conceptSet2));
					observedConceptList.add(conceptSet2);
				}
			}else
			{
				listOfObservations.addAll(Context.getObsService().getObservationsByPersonAndConcept(patient, concept));
				observedConceptList.add(concept);
			}
		}

		for (Obs obs : listOfObservations) {
			Date dateCreated = obs.getObsDatetime();

			dateSet.add(dateCreated);
			if(labResultData.containsKey(dateCreated))
			{
				String data = labResultData.get(dateCreated);
				data = data+","+obs.getId();
				labResultData.put(dateCreated+obs.getConcept().getId().toString(), data);


			}else
			{
				labResultData.put(dateCreated+obs.getConcept().getId().toString(), obs.getId().toString());

			}


		}
		System.out.println(labResultData);
		for (Date date : dateSet) {
		
			buffer.append("<th>"+s.format(date)+"</th>");
		}
		buffer.append("</tr>");
		buffer.append("</thead>");
		buffer.append("<tbody>");
		for (Concept concept : observedConceptList) {

			buffer.append("<tr>");
			buffer.append("<td><content Id= \""+concept.getDisplayString()+"\">"+concept.getDisplayString()+"</content></td>");
			for (Date date : dateSet) {
				if(labResultData.containsKey(date+""+concept))
				{
					String obsId = labResultData.get(date+""+concept);
					Obs obs = Context.getObsService().getObs(Integer.parseInt(obsId));
					int type = obs.getConcept().getDatatype().getId();
					ConceptNumeric c =  Context.getConceptService().getConceptNumeric(concept.getId());
					
					switch(type)
					{
					case 1:
						buffer.append("<td>"+obs.getValueNumeric()+c.getUnits()+"</td>");
						break;

					case 2:
						buffer.append("<td>"+obs.getValueCoded().getDisplayString()+c.getUnits()+"</td>");
						break;


					case 3:
						buffer.append("<td>"+obs.getValueText()+c.getUnits()+"</td>");
						break;

					case 6:
						buffer.append("<td>"+s.format(obs.getValueDate())+"</td>");
						break;

					case 7:
						buffer.append("<td>"+obs.getValueTime()+c.getUnits()+"</td>");
						break;

					case 8:
						buffer.append("<td>"+s.format(obs.getValueDatetime())+"</td>");
						break;

					case 10:
						buffer.append("<td>"+obs.getValueBoolean()+c.getUnits()+"</td>");

						break;

					case 13:
						buffer.append("<td>"+obs.getValueComplex()+c.getUnits()+"</td>");
 						break;				
					}
				
					Entry labResultEntry = CDAFactory.eINSTANCE.createEntry();
					labResultEntry.setTypeCode(x_ActRelationshipEntry.DRIV);
					Organizer organizer = CDAFactory.eINSTANCE.createOrganizer();
					organizer.setClassCode(x_ActClassDocumentEntryOrganizer.CLUSTER);
					organizer.setMoodCode(ActMood.EVN);
					organizer.getTemplateIds().add(buildTemplateID("2.16.840.1.113883.10.20.1.32", "", ""));
					//cluster of observations
					organizer.getIds().add(buildID(obs.getUuid(), ""));
					organizer.setCode(buildCode("56850-1", "2.16.840.1.113883.6.1", "Interpretation and review of laboratory results", "LOINC"));
					CS statusCode = DatatypesFactory.eINSTANCE.createCS();
					statusCode.setCode("completed");
					organizer.setStatusCode(statusCode);
					organizer.setEffectiveTime(buildEffectiveTimeinIVL(date,null));
					Component4 component = CDAFactory.eINSTANCE.createComponent4();
					Observation observation = CDAFactory.eINSTANCE.createObservation();
					observation.setClassCode(ActClassObservation.OBS);
					observation.setMoodCode(x_ActMoodDocumentObservation.EVN);
					observation.getTemplateIds().add(buildTemplateID("2.16.840.1.113883.3.88.11.83.15", "", "HITSP C83"));
					observation.getTemplateIds().add(buildTemplateID("2.16.840.1.113883.10.20.1.31", "", "CCD"));
					observation.getTemplateIds().add(buildTemplateID("1.3.6.1.4.1.19376.1.5.3.1.4.13", "", "IHE PCC"));
					observation.getIds().add(buildID(obs.getUuid(), ""));
					observation.setCode(buildConceptCode(concept,"SNOMED","LOINC"));
					observation.setText(buildEDText("#"+concept.getDisplayString()));
					CS statusCode1 = DatatypesFactory.eINSTANCE.createCS();
					statusCode1.setCode("completed");
					observation.setStatusCode(statusCode1);
					observation.setEffectiveTime(buildEffectiveTimeinIVL(obs.getObsDatetime(), null));
					PQ unit = DatatypesFactory.eINSTANCE.createPQ();
					unit.setUnit(c.getUnits());
					unit.setValue(obs.getValueNumeric());
					observation.getValues().add(unit);

					ReferenceRange conceptRange = CDAFactory.eINSTANCE.createReferenceRange();
					ObservationRange observationRange = CDAFactory.eINSTANCE.createObservationRange();
					observationRange.setNullFlavor(NullFlavor.UNK);
					conceptRange.setObservationRange(observationRange );
					observation.getReferenceRanges().add(conceptRange);
					component.setObservation(observation );
					organizer.getComponents().add(component );

					labResultEntry.setOrganizer(organizer);
					section.getEntries().add(labResultEntry);
				}
				else
				{
					buffer.append("<td></td>");
				}
			}
			buffer.append("</tr>");


		}	

		buffer.append("</tbody>");buffer.append("</table>");
		StrucDocText details = CDAFactory.eINSTANCE.createStrucDocText();
		String s = new String(buffer.toString().getBytes(), Charset.forName("UTF-8"));
		details.addText(s);
		
		section.setText(details);

		return ccd;

	}

	/*private ContinuityOfCareDocument buildVitalSigns(ContinuityOfCareDocument ccd , Patient patient)
	{

		VitalSignsSection section =  CCDFactory.eINSTANCE.createVitalSignsSection();
		ccd.addSection(section);
		section.getTemplateIds().add(buildTemplateID("2.16.840.1.113883.3.88.11.83.119", "", "HITSP/C83"));
		section.getTemplateIds().add(buildTemplateID("1.3.6.1.4.1.19376.1.5.3.1.1.5.3.2", "", "IHE PCC"));
		section.getTemplateIds().add(buildTemplateID("1.3.6.1.4.1.19376.1.5.3.1.3.25", "", "IHE PCC"));
		section.getTemplateIds().add(buildTemplateID("2.16.840.1.113883.10.20.1.16", "", "HL7 CCD"));

		section.setCode(buildCodeCE("8716-3", "2.16.840.1.113883.6.1", "Vital signs", "LOINC"));
		section.setTitle(buildST("Vital Signs"));
		StringBuffer buffer = new StringBuffer();
		buffer.append("<table border=\"1\" width=\"100%\">");
		//buffer.append("<thead>");
		buffer.append("<tr>");
		buffer.append("<th>Date</th>");
		List<Concept> vitalSignsList= dao.getConceptByCategory("VitalSigns");
		List <Obs> listOfObservations = new ArrayList<Obs>();
		Map <String, String> vitalSignData = new HashMap<String, String>();
		List<Concept> observedConceptList = new ArrayList<Concept>();
		Set <Date> dateSet = new HashSet<Date>();
		for (Concept concept : vitalSignsList) {
			if(concept.isSet())
			{
				List<Concept> conceptSet = concept.getSetMembers();
				System.out.println(conceptSet);
				for (Concept conceptSet2 : conceptSet) {
					
					listOfObservations.addAll(Context.getObsService().getObservationsByPersonAndConcept(patient, conceptSet2));
					observedConceptList.add(conceptSet2);
				}
			}else
			{
				listOfObservations.addAll(Context.getObsService().getObservationsByPersonAndConcept(patient, concept));
				observedConceptList.add(concept);
			}
		}

		for (Obs obs : listOfObservations) {
			Date dateCreated = obs.getObsDatetime();

			dateSet.add(dateCreated);
			if(vitalSignData.containsKey(dateCreated))
			{
				String data = vitalSignData.get(dateCreated);
				data = data+","+obs.getId();
				vitalSignData.put(dateCreated+obs.getConcept().getId().toString(), data);


			}else
			{
				vitalSignData.put(dateCreated+obs.getConcept().getId().toString(), obs.getId().toString());

			}


		}
		System.out.println(vitalSignData);
		for (Date date : dateSet) {
			buffer.append("<th>"+date+"</th>");
		}

		for (Concept concept : observedConceptList) {

			buffer.append("<tr>");
			buffer.append("<td contentId= "+concept.getDisplayString()+">"+concept.getDisplayString()+"</td>");
			for (Date date : dateSet) {
				if(vitalSignData.containsKey(date+""+concept))
				{
					String obsId = vitalSignData.get(date+""+concept);
					Obs obs = Context.getObsService().getObs(Integer.parseInt(obsId));
					buffer.append("<td>"+obs.getValueNumeric()+"</td>");
					Entry vitalSignEntry = CDAFactory.eINSTANCE.createEntry();
					vitalSignEntry.setTypeCode(x_ActRelationshipEntry.DRIV);
					Organizer organizer = CDAFactory.eINSTANCE.createOrganizer();
					organizer.setClassCode(x_ActClassDocumentEntryOrganizer.CLUSTER);
					organizer.setMoodCode(ActMood.EVN);
					organizer.getTemplateIds().add(buildTemplateID("1.3.6.1.4.1.19376.1.5.3.1.4.13.1", "", "IHE PCC"));
					organizer.getTemplateIds().add(buildTemplateID("2.16.840.1.113883.10.20.1.32", "", "CCD"));
					organizer.getTemplateIds().add(buildTemplateID("2.16.840.1.113883.10.20.1.35", "", "CCD"));
					//cluster of observations
					organizer.getIds().add(buildID("", ""));
					organizer.setCode(buildCode("46680005", "2.16.840.1.113883.6.96", "Vital signs", "SNOMED-CT"));
					CS statusCode = DatatypesFactory.eINSTANCE.createCS();
					statusCode.setCode("completed");
					organizer.setStatusCode(statusCode);
					organizer.setEffectiveTime(buildEffectiveTimeinIVL(date,null));
					Component4 component = CDAFactory.eINSTANCE.createComponent4();
					Observation observation = CDAFactory.eINSTANCE.createObservation();
					observation.setClassCode(ActClassObservation.OBS);
					observation.setMoodCode(x_ActMoodDocumentObservation.EVN);
					observation.getTemplateIds().add(buildTemplateID("2.16.840.1.113883.3.88.11.83.14", "", "HITSP C83"));
					observation.getTemplateIds().add(buildTemplateID("2.16.840.1.113883.10.20.1.31", "", "CCD"));
					observation.getTemplateIds().add(buildTemplateID("1.3.6.1.4.1.19376.1.5.3.1.4.13", "", "IHE PCC"));
					observation.getIds().add(buildID(obs.getUuid(), ""));
					observation.setCode(buildConceptCode(concept,"SNOMED","LOINC"));
					observation.setText(buildEDText("#"+concept.getDisplayString()));
					CS statusCode1 = DatatypesFactory.eINSTANCE.createCS();
					statusCode1.setCode("completed");
					observation.setStatusCode(statusCode1);
					observation.setEffectiveTime(buildEffectiveTimeinIVL(obs.getObsDatetime(), null));
					PQ unit = DatatypesFactory.eINSTANCE.createPQ();
					ConceptNumeric c =  Context.getConceptService().getConceptNumeric(concept.getId());
					unit.setUnit(c.getUnits());
					unit.setValue(obs.getValueNumeric());
					observation.getValues().add(unit);

					ReferenceRange conceptRange = CDAFactory.eINSTANCE.createReferenceRange();
					ObservationRange observationRange = CDAFactory.eINSTANCE.createObservationRange();
					observationRange.setNullFlavor(NullFlavor.UNK);
					conceptRange.setObservationRange(observationRange );
					observation.getReferenceRanges().add(conceptRange);
					component.setObservation(observation );
					organizer.getComponents().add(component );

					vitalSignEntry.setOrganizer(organizer);
					section.getEntries().add(vitalSignEntry);
				}
				else
				{
					buffer.append("<td></td>");
				}
			}
			buffer.append("</tr>");


		}	

		StrucDocText details = CDAFactory.eINSTANCE.createStrucDocText();
		details.addText(buffer.toString());

		section.setText(details);




		return ccd;
	}*/


	private ContinuityOfCareDocument buildSocialHistory(ContinuityOfCareDocument ccd , Patient patient)
	{
		SocialHistorySection section =  CCDFactory.eINSTANCE.createSocialHistorySection();
		ccd.addSection(section);
		section.getTemplateIds().add(buildTemplateID("2.16.840.1.113883.10.20.1.15", "", "HITSP/C83"));
		section.setCode(buildCodeCE("29762-2", "2.16.840.1.113883.6.1", "Social History", "LOINC"));
		section.setTitle(buildST("Social History"));
		StrucDocText details = CDAFactory.eINSTANCE.createStrucDocText();
		StringBuffer buffer = new StringBuffer();
		buffer.append("<table border=\"1\" width=\"100%\">");
		buffer.append("<thead>");
		buffer.append("<tr>");
		buffer.append("<th>Social History Element</th>");
		buffer.append("<th>Values</th>");
		buffer.append("<th>Date</th>");
		buffer.append("</tr>");
		buffer.append("</thead>");
		buffer.append("<tbody>");
		List<Concept> socialHistoryList= dao.getConceptByCategory("SocialHistory");
		List<Obs> obsList = new ArrayList<Obs>();
		for (Concept concept : socialHistoryList) {
			obsList.addAll(Context.getObsService().getObservationsByPersonAndConcept(patient, concept));	
		}
		for (Obs obs : obsList) {
			buffer.append("<tr>");
			buffer.append("<td> <content ID = \""+obs.getConceptDescription().getDescription()+"\" >"+obs.getConceptDescription().getDescription()+"</content>");
			
			buffer.append("</td>");
			buffer.append("<td>");
			int type = obs.getConcept().getDatatype().getId();
			String value = "";
			switch(type)
			{
			case 1:
				value = obs.getValueNumeric().toString();
				buffer.append(obs.getValueNumeric());
				break;

			case 2:
				value = obs.getValueCoded().getDisplayString();
				buffer.append(obs.getValueCoded().getDisplayString());
				break;


			case 3:
				value = obs.getValueText();
				
				buffer.append(obs.getValueText());
				break;

			case 6:
				value = obs.getValueDate().toString();
				buffer.append(obs.getValueDate());
				break;

			case 7:
				value = obs.getValueTime().toString();
				buffer.append(obs.getValueTime());
				break;

			case 8:
				value = obs.getValueDatetime().toString();
				buffer.append(obs.getValueDatetime());
				break;

			case 10:
				value = obs.getValueAsBoolean().toString();
				buffer.append(obs.getValueBoolean());

				break;

			case 13:
				value = obs.getValueComplex();
				buffer.append(obs.getValueComplex());
				break;				
			}
			buffer.append("</td>");
		
			buffer.append("<td>"+s.format(obs.getObsDatetime())+"</td>");
			buffer.append("</tr>");

			Entry entry = CDAFactory.eINSTANCE.createEntry();
			entry.setTypeCode(x_ActRelationshipEntry.DRIV);

			Observation observation = CDAFactory.eINSTANCE.createObservation();
			observation.setClassCode(ActClassObservation.OBS);
			observation.setMoodCode(x_ActMoodDocumentObservation.EVN);
			observation.getTemplateIds().add(buildTemplateID("2.16.840.1.113883.10.20.1.33", "",""));
			observation.getIds().add(buildID(obs.getUuid(), ""));
			observation.setCode(buildConceptCode(obs.getConcept(), "SNOMED","AMPATH","LOINC"));
			CS statusCode = DatatypesFactory.eINSTANCE.createCS();
			statusCode.setCode("completed");
			observation.setStatusCode(statusCode);
			observation.setEffectiveTime(buildEffectiveTimeinIVL(obs.getObsDatetime(), null));
			ST value1 = buildST(value);
			observation.getValues().add(value1);
			EntryRelationship entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
			entryRelationship.setTypeCode(x_ActRelationshipEntryRelationship.SUBJ);
			entryRelationship.setInversionInd(true);
			Observation eObservation = CDAFactory.eINSTANCE.createObservation();
			eObservation.setClassCode(ActClassObservation.OBS);
			eObservation.setMoodCode(x_ActMoodDocumentObservation.EVN);
			eObservation.getTemplateIds().add(buildTemplateID("2.16.840.1.113883.10.20.1.41", "", ""));
			eObservation.setCode(buildCode("ASSERTION", "2.16.840.1.113883.5.4", "", ""));
			CS statusCode1 = DatatypesFactory.eINSTANCE.createCS();
			statusCode1.setCode("completed");
			eObservation.setStatusCode(statusCode1);
			CD value2 = buildCode("404684003", "2.16.840.1.113883.6.96", "Clinical Finding", "");

			CR qualifier = DatatypesFactory.eINSTANCE.createCR();
			CV episodicity = DatatypesFactory.eINSTANCE.createCV();

			qualifier.setName(episodicity);
			qualifier.setValue(buildCode("288527008", "2.16.840.1.113883.6.96", "New episode", ""));
			value2.getQualifiers().add(qualifier);
			eObservation.getValues().add(value2);
			EntryRelationship e = CDAFactory.eINSTANCE.createEntryRelationship();
			e.setTypeCode(x_ActRelationshipEntryRelationship.SAS);
			Observation eObservation2 = CDAFactory.eINSTANCE.createObservation();
			eObservation2.setClassCode(ActClassObservation.OBS);
			eObservation2.setMoodCode(x_ActMoodDocumentObservation.EVN);
			//which id should i pass obs or concept uuid ???
			eObservation2.getIds().add(buildID(obs.getConcept().getUuid(), ""));
			eObservation2.setCode(buildConceptCode(obs.getConcept(), "SNOMED"));
			e.setObservation(eObservation2);
			eObservation.getEntryRelationships().add(e);
			entryRelationship.setObservation(eObservation);

			observation.getEntryRelationships().add(entryRelationship); 
			entry.setObservation(observation );
			section.getEntries().add(entry);
		}
		buffer.append("</tbody>");buffer.append("</table>");
		details.addText(buffer .toString());

		section.setText(details);
		return ccd;
	}


	@SuppressWarnings("deprecation")
	private ContinuityOfCareDocument buildFamilyHistory(ContinuityOfCareDocument ccd , Patient patient)
	{
		
		FamilyHistorySection section =  CCDFactory.eINSTANCE.createFamilyHistorySection();
		ccd.addSection(section);
		section.getTemplateIds().add(buildTemplateID("2.16.840.1.113883.3.88.11.83.125", "", "HITSP/C83"));
		section.getTemplateIds().add(buildTemplateID("2.16.840.1.113883.10.20.1.4", "", "CCD"));
		section.getTemplateIds().add(buildTemplateID("1.3.6.1.4.1.19376.1.5.3.1.3.14", "", "IHE PCC"));
		
		section.setCode(buildCodeCE("10157-6", "2.16.840.1.113883.6.1", "History of family member diseases", "LOINC"));
		section.setTitle(buildST("Family History"));
		StrucDocText details = CDAFactory.eINSTANCE.createStrucDocText();
		StringBuffer buffer = new StringBuffer();
		ConceptService cs = Context.getConceptService();
		List <Obs> l = Context.getObsService().getObservationsByPersonAndConcept(patient, cs.getConcept(160593));
		
		for (Obs obs : l) {
			
			List<Obs> familyHistory = Context.getObsService().findObsByGroupId(obs.getId());
			System.out.println(familyHistory);
			String relation = "";
			String diagnosis = "";
			String inCare= "";
			String age = "";
			for (Obs obs2 : familyHistory) {
				switch(obs2.getConcept().getId())
				{
				case 1560:
					
					
					relation = obs2.getValueCoded().getDisplayString();
					
					break;
					
				case 160617:
					age = obs2.getValueNumeric().toString();
					break ;
					
			
				case 160592:
					diagnosis = obs2.getValueCoded().getDisplayString();
					break;
					
				}
				
				
			}
			buffer.append("<paragraph>"+relation+"</paragraph>");
			buffer.append("<table border=\"1\" width=\"100%\">");
			buffer.append("<thead>");
			buffer.append("<tr>");
			buffer.append("<th>Age</th>");
			buffer.append("<th>Diagnosis</th>");
		
			buffer.append("</tr>");
			buffer.append("</thead>");
			buffer.append("<tbody>");
			buffer.append("<tr>");
			buffer.append("<td>"+age+"</td>");

			buffer.append("<td> <content id=\""+diagnosis+"\">"+diagnosis+"</content></td>");
			buffer.append("</tr>");
			buffer.append("</tbody>");
			buffer.append("</table>");
			
			Entry entry = CDAFactory.eINSTANCE.createEntry();
			entry.setTypeCode(x_ActRelationshipEntry.DRIV);
			Organizer organizer = CDAFactory.eINSTANCE.createOrganizer();
			organizer.setMoodCode(ActMood.EVN);
			organizer.setClassCode(x_ActClassDocumentEntryOrganizer.CLUSTER);
			organizer.getTemplateIds().add(buildTemplateID("2.16.840.1.113883.3.88.11.83.18", "", "HITSP C83"));
			organizer.getTemplateIds().add(buildTemplateID("2.16.840.1.113883.10.20.1.23", "", "CCD"));
			organizer.getTemplateIds().add(buildTemplateID("1.3.6.1.4.1.19376.1.5.3.1.4.15", "", "IHE PCC"));
			CS statusCode = DatatypesFactory.eINSTANCE.createCS();
			statusCode.setCode("completed");
			organizer.setStatusCode(statusCode);
			Subject subject = CDAFactory.eINSTANCE.createSubject();
			RelatedSubject relatedSubject = CDAFactory.eINSTANCE.createRelatedSubject();
			relatedSubject.setClassCode(x_DocumentSubject.PRS);
			relatedSubject.setCode(buildConceptCode(cs.getConcept(relation), "Snomed"));
			AD address = DatatypesFactory.eINSTANCE.createAD();
			relatedSubject.getAddrs().add(address );
			TEL tel = DatatypesFactory.eINSTANCE.createTEL();
			tel.setNullFlavor(NullFlavor.UNK);
			SubjectPerson subjectPerson = CDAFactory.eINSTANCE.createSubjectPerson();
			PN name = DatatypesFactory.eINSTANCE.createPN();
			subjectPerson.getNames().add(name );
			CE gender = DatatypesFactory.eINSTANCE.createCE();
			gender.setNullFlavor(NullFlavor.UNK);
			subjectPerson.setAdministrativeGenderCode(gender );
			TS birthTime = DatatypesFactory.eINSTANCE.createTS();
			birthTime.setNullFlavor(NullFlavor.UNK);
			subjectPerson.setBirthTime(birthTime );
			relatedSubject.setSubject(subjectPerson );
			relatedSubject.getTelecoms().add(tel);
			subject.setRelatedSubject(relatedSubject );
			organizer.setSubject(subject);
			Component4 obsComp = CDAFactory.eINSTANCE.createComponent4();
			Observation co = CDAFactory.eINSTANCE.createObservation();
			co.setClassCode(ActClassObservation.OBS);
			co.setMoodCode(x_ActMoodDocumentObservation.EVN);
			co.getTemplateIds().add(buildTemplateID("2.16.840.1.113883.10.20.1.22", "", "CCD"));
			co.getTemplateIds().add(buildTemplateID("1.3.6.1.4.1.19376.1.5.3.1.4.13", "", "IHE PCC"));
			co.getTemplateIds().add(buildTemplateID("1.3.6.1.4.1.19376.1.5.3.1.4.13.3", "", "IHE PCC"));
			co.getIds().add(buildID(obs.getUuid(), ""));
			co.setCode(buildConceptCode(cs.getConcept(diagnosis), "SNOMED"));
			co.setText(buildEDText("#"+diagnosis));
			CS statusCode1 = DatatypesFactory.eINSTANCE.createCS();
			statusCode1.setCode("completed");
			co.setStatusCode(statusCode1);
			IVL_TS et = DatatypesFactory.eINSTANCE.createIVL_TS();
			et.setNullFlavor(NullFlavor.UNK);
			co.setEffectiveTime(et);
			obsComp.setObservation(co );
			EntryRelationship e = CDAFactory.eINSTANCE.createEntryRelationship();
			e.setTypeCode(x_ActRelationshipEntryRelationship.SUBJ);
			e.setInversionInd(true);
			Observation ob = CDAFactory.eINSTANCE.createObservation();
			ob.setClassCode(ActClassObservation.OBS);
			ob.setMoodCode(x_ActMoodDocumentObservation.EVN);
			ob.getTemplateIds().add(buildTemplateID("2.16.840.1.113883.10.20.1.38", "", ""));
			ob.setCode(buildCode("397659008", "2.16.840.1.113883.6.96", "Age", "SNOMED-CT"));
			CS statusCode2 = DatatypesFactory.eINSTANCE.createCS();
			statusCode2.setCode("completed");
			ob.setStatusCode(statusCode2);
			INT age1 = DatatypesFactory.eINSTANCE.createINT();
			age1.setValue((int)Float.parseFloat(age));
			ob.getValues().add(age1);
			e.setObservation(ob );
			co.getEntryRelationships().add(e );
			organizer.getComponents().add(obsComp );
			entry.setOrganizer(organizer );
			section.getEntries().add(entry);
			
		}
		
		
		details.addText(buffer .toString());

		section.setText(details);
		return ccd ; 
	}

	

}


