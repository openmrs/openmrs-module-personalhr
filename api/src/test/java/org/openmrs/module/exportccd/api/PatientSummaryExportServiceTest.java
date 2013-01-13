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
package org.openmrs.module.exportccd.api;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

import org.junit.Ignore;
import org.junit.Test;
import org.openhealthtools.mdht.uml.cda.CDAFactory;
import org.openhealthtools.mdht.uml.cda.EntryRelationship;
import org.openhealthtools.mdht.uml.cda.Participant2;
import org.openhealthtools.mdht.uml.cda.ParticipantRole;
import org.openhealthtools.mdht.uml.cda.Patient;
import org.openhealthtools.mdht.uml.cda.PatientRole;
import org.openhealthtools.mdht.uml.cda.PlayingEntity;
import org.openhealthtools.mdht.uml.cda.ccd.AlertObservation;
import org.openhealthtools.mdht.uml.cda.ccd.AlertsSection;
import org.openhealthtools.mdht.uml.cda.ccd.CCDFactory;
import org.openhealthtools.mdht.uml.cda.ccd.ContinuityOfCareDocument;
import org.openhealthtools.mdht.uml.cda.ccd.ProblemAct;
import org.openhealthtools.mdht.uml.cda.ccd.ReactionObservation;
import org.openhealthtools.mdht.uml.hl7.datatypes.CD;
import org.openhealthtools.mdht.uml.hl7.datatypes.CE;
import org.openhealthtools.mdht.uml.hl7.datatypes.CS;
import org.openhealthtools.mdht.uml.hl7.datatypes.DatatypesFactory;
import org.openhealthtools.mdht.uml.hl7.datatypes.II;
import org.openhealthtools.mdht.uml.hl7.datatypes.TS;
import org.openhealthtools.mdht.uml.hl7.vocab.EntityClassRoot;
import org.openhealthtools.mdht.uml.hl7.vocab.ParticipationType;
import org.openhealthtools.mdht.uml.hl7.vocab.PostalAddressUse;
import org.openhealthtools.mdht.uml.hl7.vocab.RoleClassRoot;
import org.openhealthtools.mdht.uml.hl7.vocab.x_ActRelationshipEntryRelationship;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import org.openmrs.module.exportccd.api.PatientSummaryImportService;
/**
 * Tests {@link ${PatientSummaryExportService}}.
 */
public class  PatientSummaryExportServiceTest extends BaseModuleContextSensitiveTest {
	
	@Ignore
	public void shouldSetupContext() {
		assertNotNull(Context.getService(PatientSummaryExportService.class));
	}
	
	@Test
	public void consumeCCD() throws Exception {
		PatientSummaryImportService importService = Context.getService(PatientSummaryImportService.class);
		assertNotNull(importService);
		//InputStream is = new FileInputStream("G:\\COMMON\\everyone\\hxiao\\laf\\innovation\\ccd\\test\\example_ccd.xml");//test\\SampleCDADocument.xml");//CCD.sample.xml");
		//assertNotNull(importService.consumeCCD(is));		
	}	

	private Concept getOpenmrsCodedConceptByName(String displayName) {
		ConceptService cs = Context.getConceptService();
		Concept c = cs.getConceptByName(displayName);
		
		if(c==null) {
			c = new Concept();				
			c.setCreator(Context.getAuthenticatedUser());
			ConceptName cn = new ConceptName();
			cn.setName(displayName);
			cn.setLocale(Context.getLocale());
			cn.setCreator(Context.getAuthenticatedUser());
			c.addName(cn);
			c.setDateCreated(new Date());
			c.setDatatype(cs.getConceptDatatypeByName("Coded"));
			c.setUuid(UUID.randomUUID().toString());
			//c.setConceptClass(cs.getConceptClassByName(className));
			cs.saveConcept(c);
		}
		
		return c;
	}	
	
	public void importCCD() throws Exception {
		//Concept c = getOpenmrsCodedConceptByName("PROCEDURE ADDED");
		// static package registration
		//CCDPackage.eINSTANCE.eClass();

		// load sample continuity of care document from file
		//ContinuityOfCareDocument ccdDocument2 = (ContinuityOfCareDocument) CDAUtil.load(new FileInputStream("G:\\COMMON\\everyone\\hxiao\\laf\\innovation\\ccd\\ccd.xml"));		
	}

	public void createCCD() {

		// create and initialize an instance of the ContinuityOfCareDocument class
		ContinuityOfCareDocument ccdDocument = CCDFactory.eINSTANCE.createContinuityOfCareDocument().init();

		// create a patient role object and add it to the document
		PatientRole patientRole = CDAFactory.eINSTANCE.createPatientRole();
		ccdDocument.addPatientRole(patientRole);
		II id = DatatypesFactory.eINSTANCE.createII();
		patientRole.getIds().add(id);
		id.setRoot("996-756-495");
		id.setExtension("2.16.840.1.113883.19.5");

		// create an address object and add it to patient role
		org.openhealthtools.mdht.uml.hl7.datatypes.AD addr = DatatypesFactory.eINSTANCE.createAD();
		patientRole.getAddrs().add(addr);
		addr.getUses().add(PostalAddressUse.H);
		addr.addStreetAddressLine("1313 Mockingbird Lane");
		addr.addCity("Janesville");
		addr.addState("WI");
		addr.addPostalCode("53545");

		// create a patient object and add it to patient role
		Patient patient = CDAFactory.eINSTANCE.createPatient();
		patientRole.setPatient(patient);
		org.openhealthtools.mdht.uml.hl7.datatypes.PN name = DatatypesFactory.eINSTANCE.createPN();
		patient.getNames().add(name);
		name.addGiven("Henry");
		name.addFamily("Levin");

		CE administrativeGenderCode = DatatypesFactory.eINSTANCE.createCE();

		patient.setAdministrativeGenderCode(administrativeGenderCode);
		administrativeGenderCode.setCode("M");
		administrativeGenderCode.setCodeSystem("2.16.840.1.113883.5.1");

		TS birthTime = DatatypesFactory.eINSTANCE.createTS();
		patient.setBirthTime(birthTime);
		birthTime.setValue("19320924");

		// create and initialize the CCD alerts section
		AlertsSection alertsSection = CCDFactory.eINSTANCE.createAlertsSection().init();
		ccdDocument.addSection(alertsSection);

		// set up the narrative (human-readable) text portion of the alerts section
		StringBuffer buffer = new StringBuffer();
		buffer.append("<table border=\"1\" width=\"100%\">");
		buffer.append("<thead>");
		buffer.append("<tr>");
		buffer.append("<th>Substance</th>");
		buffer.append("<th>Reaction</th>");
		buffer.append("<th>Status</th>");
		buffer.append("</tr>");
		buffer.append("</thead>");
		buffer.append("<tbody>");
		buffer.append("<tr>");
		buffer.append("<td>Penicillin</td>");
		buffer.append("<td>Hives</td>");
		buffer.append("<td>Active</td>");
		buffer.append("</tr>");
		buffer.append("</tbody>");
		buffer.append("</table>");
		alertsSection.createStrucDocText(buffer.toString());

		// create and initialize a CCD problem act
		ProblemAct problemAct = CCDFactory.eINSTANCE.createProblemAct().init();
		alertsSection.addAct(problemAct);

		id = DatatypesFactory.eINSTANCE.createII();
		problemAct.getIds().add(id);
		id.setRoot(UUID.randomUUID().toString());
		// create and initialize an alert observation within the problem act
		AlertObservation alertObservation = CCDFactory.eINSTANCE.createAlertObservation().init();
		problemAct.addObservation(alertObservation);
		((EntryRelationship) alertObservation.eContainer()).setTypeCode(x_ActRelationshipEntryRelationship.SUBJ);

		id = DatatypesFactory.eINSTANCE.createII();
		alertObservation.getIds().add(id);
		id.setRoot(UUID.randomUUID().toString());

		CD code = DatatypesFactory.eINSTANCE.createCD();
		alertObservation.setCode(code);
		code.setCode("ASSERTION");
		code.setCodeSystem("2.16.840.1.113883.5.4");

		CS statusCode = DatatypesFactory.eINSTANCE.createCS();
		alertObservation.setStatusCode(statusCode);
		statusCode.setCode("completed");

		CD value = DatatypesFactory.eINSTANCE.createCD();
		alertObservation.getValues().add(value);
		value.setCode("282100009");
		value.setCodeSystem("2.16.840.1.113883.6.96");
		value.setDisplayName("Adverse reaction to substance");

		// playing entity contains coded information on the substance
		Participant2 participant = CDAFactory.eINSTANCE.createParticipant2();
		alertObservation.getParticipants().add(participant);
		participant.setTypeCode(ParticipationType.CSM);

		ParticipantRole participantRole = CDAFactory.eINSTANCE.createParticipantRole();
		participant.setParticipantRole(participantRole);
		participantRole.setClassCode(RoleClassRoot.MANU);

		PlayingEntity playingEntity = CDAFactory.eINSTANCE.createPlayingEntity();
		participantRole.setPlayingEntity(playingEntity);
		playingEntity.setClassCode(EntityClassRoot.MMAT);
		CE playingEntityCode = DatatypesFactory.eINSTANCE.createCE();
		playingEntity.setCode(playingEntityCode);
		playingEntityCode.setCode("70618");
		playingEntityCode.setCodeSystem("2.16.840.1.113883.6.88");
		playingEntityCode.setDisplayName("Penicillin");


		// reaction observation contains coded information on the adverse reaction
		ReactionObservation reactionObservation = CCDFactory.eINSTANCE.createReactionObservation().init();
		alertObservation.addObservation(reactionObservation);
		((EntryRelationship) reactionObservation.eContainer()).setTypeCode(x_ActRelationshipEntryRelationship.MFST);
		((EntryRelationship) reactionObservation.eContainer()).setInversionInd(Boolean.TRUE);

		code = DatatypesFactory.eINSTANCE.createCD();
		reactionObservation.setCode(code);
		code.setCode("ASSERTION");
		code.setCodeSystem("2.16.840.1.113883.5.4");

		statusCode = DatatypesFactory.eINSTANCE.createCS();
		reactionObservation.setStatusCode(statusCode);
		statusCode.setCode("completed");

		value = DatatypesFactory.eINSTANCE.createCD();
		reactionObservation.getValues().add(value);
		value.setCode("247472004");
		value.setCodeSystem("2.16.840.1.113883.6.96");
		value.setDisplayName("Hives");
	}	
}
