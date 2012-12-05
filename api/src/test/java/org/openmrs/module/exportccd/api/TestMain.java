/*******************************************************************************
 * Copyright (c) 2009, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.openmrs.module.exportccd.api;

import static org.junit.Assert.assertNotNull;

import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.emf.common.util.Diagnostic;
import org.junit.Test;
import org.openhealthtools.mdht.uml.cda.AssignedEntity;
import org.openhealthtools.mdht.uml.cda.CDAFactory;
import org.openhealthtools.mdht.uml.cda.ClinicalDocument;
import org.openhealthtools.mdht.uml.cda.Informant12;
import org.openhealthtools.mdht.uml.cda.SubstanceAdministration;
import org.openhealthtools.mdht.uml.cda.ccd.CCDFactory;
import org.openhealthtools.mdht.uml.cda.ccd.CCDPackage;
import org.openhealthtools.mdht.uml.cda.ccd.ContinuityOfCareDocument;
import org.openhealthtools.mdht.uml.cda.ccd.EpisodeObservation;
import org.openhealthtools.mdht.uml.cda.ccd.ProblemAct;
import org.openhealthtools.mdht.uml.cda.ccd.ProblemHealthStatusObservation;
import org.openhealthtools.mdht.uml.cda.ccd.ProblemObservation;
import org.openhealthtools.mdht.uml.cda.ccd.ProblemSection;
import org.openhealthtools.mdht.uml.cda.ccd.ProblemStatusObservation;
import org.openhealthtools.mdht.uml.cda.ccd.PurposeActivity;
import org.openhealthtools.mdht.uml.cda.ccd.PurposeSection;
import org.openhealthtools.mdht.uml.cda.ccd.ResultObservation;
import org.openhealthtools.mdht.uml.cda.ccd.ResultOrganizer;
import org.openhealthtools.mdht.uml.cda.ccd.ResultsSection;
import org.openhealthtools.mdht.uml.cda.util.BasicValidationHandler;
import org.openhealthtools.mdht.uml.cda.util.CDAUtil;
import org.openhealthtools.mdht.uml.cda.util.ValidationResult;
import org.openhealthtools.mdht.uml.hl7.datatypes.PN;
import org.openhealthtools.mdht.uml.hl7.vocab.x_ActRelationshipEntryRelationship;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.PersonName;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.exportccd.api.PatientSummaryImportService;

public class TestMain {
    static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss.SSSZZZZZ");
    
	public static void main(String[] args) throws Exception {
		ContinuityOfCareDocument ccd = importCCD();
		createOrUpdateEncounters(ccd, null, null);
	}
	
	private static void  createOrUpdateEncounters(ContinuityOfCareDocument ccd, Patient patient, User usr) throws Exception
	{
		org.openhealthtools.mdht.uml.cda.Encounter e = ccd.getEncountersSection().getEncounters().get(0);
		Encounter enc = new Encounter();
		enc.setPatient(patient);
		enc.setCreator(usr);
		enc.setDateCreated(new Date());
		enc.setEncounterDatetime(sdf.parse(e.getEffectiveTime().getLow().getValue()));
		enc.setUuid(e.getIds().get(0).getRoot()+"."+e.getIds().get(0).getExtension());
		
		//encounter type
		EncounterType et = new EncounterType();
		et.setCreator(usr);
		et.setDateCreated(new Date());
		et.setName(e.getCode().getDisplayName());
		et.setDescription(e.getCode().getCodeSystemName() + " code: " + e.getCode().getCode());		
		enc.setEncounterType(et);

		//encounter location
		Location location = new Location();
		location.setName(e.getParticipants().get(0).getTypeCode().getName().equals("LOC") ? e.getParticipants().get(0).getParticipantRole().getPlayingEntity().getNames().get(0).getText() : null);
		location.setDescription(e.getParticipants().get(0).getTypeCode().getName().equals("LOC") ? e.getParticipants().get(0).getParticipantRole().getScopingEntity().getDesc().getText() : null);		
		enc.setLocation(location);
		
		//encounter participants
		Person pers = new Person();
		PersonName pname = new PersonName();
		PN pn = e.getPerformers().get(0).getAssignedEntity().getAssignedPerson().getNames().get(0);
		pname.setFamilyName(pn.getFamilies().isEmpty() ? null :pn.getFamilies().get(0).getText());
		pname.setGivenName(pn.getGivens().isEmpty() ? null : pn.getGivens().get(0).getText());
		//pname.setMiddleName(pn.getGivens().get(1).getText())
		pers.addName(pname);
		enc.setProvider(pers);
		
		//save all
		Context.getPersonService().savePerson(pers);
		Context.getLocationService().saveLocation(location);
		Context.getEncounterService().saveEncounterType(et);
		Context.getEncounterService().saveEncounter(enc);
	}
		


	public static ContinuityOfCareDocument importCCD() throws Exception {
		// static package registration
		CCDPackage.eINSTANCE.eClass();

		// load sample continuity of care document from file
		ContinuityOfCareDocument ccdDocument = (ContinuityOfCareDocument) CDAUtil.load(new FileInputStream("G:\\COMMON\\everyone\\hxiao\\laf\\innovation\\ccd\\ccd_added.xml"));
		
		return ccdDocument;
	}
	
	public static void createCCD() throws Exception {
		ContinuityOfCareDocument doc = CCDFactory.eINSTANCE.createContinuityOfCareDocument().init();

		Informant12 informant = CDAFactory.eINSTANCE.createInformant12();
		AssignedEntity assignedEntity = CDAFactory.eINSTANCE.createAssignedEntity();
		informant.setAssignedEntity(assignedEntity);
		doc.getInformants().add(informant);

		PurposeSection purposeSection = CCDFactory.eINSTANCE.createPurposeSection().init();
		doc.addSection(purposeSection);
		PurposeActivity purposeActivity = CCDFactory.eINSTANCE.createPurposeActivity().init();
		purposeSection.addAct(purposeActivity);
		SubstanceAdministration purposeReason = CDAFactory.eINSTANCE.createSubstanceAdministration();
		purposeActivity.addSubstanceAdministration(purposeReason);
		// should produce validation error for incorrect reason type
		// Organizer purposeReason = CDAFactory.eINSTANCE.createOrganizer();
		// purposeActivity.addOrganizer(purposeReason);
		purposeActivity.getEntryRelationships().get(0).setTypeCode(x_ActRelationshipEntryRelationship.RSON);

		ProblemAct problemAct = CCDFactory.eINSTANCE.createProblemAct().init();
		ProblemObservation problemObservation = CCDFactory.eINSTANCE.createProblemObservation().init();
		ProblemStatusObservation problemStatus = CCDFactory.eINSTANCE.createProblemStatusObservation().init();
		ProblemHealthStatusObservation problemHealthStatus = CCDFactory.eINSTANCE.createProblemHealthStatusObservation().init();
		EpisodeObservation episodeObservation = CCDFactory.eINSTANCE.createEpisodeObservation().init();

		ProblemSection problemSection = CCDFactory.eINSTANCE.createProblemSection().init();
		doc.addSection(problemSection);
		problemSection.addAct(problemAct);
		problemAct.addObservation(problemObservation);

		problemSection.addObservation(problemStatus);
		problemSection.addObservation(problemHealthStatus);
		problemSection.addObservation(episodeObservation);

		ResultsSection resultsSection = CCDFactory.eINSTANCE.createResultsSection().init();
		doc.addSection(resultsSection);
		ResultOrganizer resultOrganizer = CCDFactory.eINSTANCE.createResultOrganizer().init();
		resultsSection.addOrganizer(resultOrganizer);
		ResultObservation resultObservation = CCDFactory.eINSTANCE.createResultObservation().init();
		resultOrganizer.addObservation(resultObservation);

		System.out.println("***** Generate CCD *****");
		CDAUtil.save(doc, System.out);

		System.out.println("\n\n***** Validate generated CCD *****");
		validate(doc);

		System.out.println("\n***** Validate sample CCD *****");
		ValidationResult result = new ValidationResult();
		@SuppressWarnings("unused")
		ClinicalDocument sampleCCD = CDAUtil.load(new FileInputStream("samples/SampleCCDDocument.xml"), result);
		for (Diagnostic diagnostic : result.getErrorDiagnostics()) {
			System.out.println("ERROR: " + diagnostic.getMessage());
		}
		for (Diagnostic diagnostic : result.getWarningDiagnostics()) {
			System.out.println("WARNING: " + diagnostic.getMessage());
		}
	}

	private static void validate(ClinicalDocument clinicalDocument) throws Exception {
		boolean valid = CDAUtil.validate(clinicalDocument, new BasicValidationHandler() {
			@Override
			public void handleError(Diagnostic diagnostic) {
				System.out.println("ERROR: " + diagnostic.getMessage());
			}

			@Override
			public void handleWarning(Diagnostic diagnostic) {
				System.out.println("WARNING: " + diagnostic.getMessage());
			}
			// @Override
			// public void handleInfo(Diagnostic diagnostic) {
			// System.out.println("INFO: " + diagnostic.getMessage());
			// }
		});

		if (valid) {
			System.out.println("Document is valid");
		} else {
			System.out.println("Document is invalid");
		}
	}
}
